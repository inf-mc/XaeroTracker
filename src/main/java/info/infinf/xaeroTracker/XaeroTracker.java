package info.infinf.xaeroTracker;

import info.infinf.xaeroTracker.Commands.Executor;
import info.infinf.xaeroTracker.util.MessageUtil;
import info.infinf.xaeroTracker.util.PlayerUtil;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.*;

public final class XaeroTracker extends JavaPlugin implements Listener {
    public static final @NotNull String MINIMAP_PACKET_ID = "xaerominimap:main";
    public static final @NotNull String WORLD_MAP_PACKET_ID = "xaeroworldmap:main";

    public final @NotNull Map<@NotNull Player, @NotNull PlayerData> playerData = new HashMap<>();

    public FilePlayerList trackIgnoreList;
    public FilePlayerList trackBypassList;
    public boolean shouldSendLevelId;
    public int levelId;
    public long syncCooldown;

    private TranslationStore.StringBased<MessageFormat> translationStore;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        var conf = getConfig();
        shouldSendLevelId = conf.getBoolean("should-send-level-id", true);
        if (!conf.contains("level-id")) {
            conf.set("level-id", new Random().nextInt());
            saveConfig();
        }
        levelId = conf.getInt("level-id");
        syncCooldown = conf.getInt("sync-cooldown", 250);

        trackIgnoreList = new FilePlayerList(this, getDataPath().resolve("track_ignore_list.yml").toFile());
        trackBypassList = new FilePlayerList(this, getDataPath().resolve("track_bypass_list.yml").toFile());

        var messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(
                this, MINIMAP_PACKET_ID, this::onMinimapMessageReceived);
        messenger.registerIncomingPluginChannel(
                this, WORLD_MAP_PACKET_ID, this::onWorldMapMessageReceived);
        messenger.registerOutgoingPluginChannel(this, MINIMAP_PACKET_ID);
        messenger.registerOutgoingPluginChannel(this, WORLD_MAP_PACKET_ID);

        getServer().getPluginManager().registerEvents(this, this);
        var cmd = getCommand("xaerotracker");
        if (cmd != null) {
            cmd.setExecutor(new Executor(this));
            cmd.setPermission("xaerotracker");
            cmd.setUsage(
                    """
                    /xt toggleTracked
                    /xt toggleTracked <player name>
                    /xt toggleTrackEveryone
                    /xt toggleTrackEveryone <player name>
                    """
            );
        }

        translationStore = TranslationStore.messageFormat(Key.key("xaerotracker:lang"));
        translationStore.registerAll(
                Locale.US,
                ResourceBundle.getBundle("locales.lang", Locale.US, UTF8ResourceBundleControl.get()),
                true);
        translationStore.registerAll(
                Locale.SIMPLIFIED_CHINESE,
                ResourceBundle.getBundle("locales.lang", Locale.SIMPLIFIED_CHINESE, UTF8ResourceBundleControl.get()),
                true);
        translationStore.defaultLocale(Locale.US);
        GlobalTranslator.translator().addSource(translationStore);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var pl = e.getPlayer();
        try {
            var addChannel = pl.getClass().getMethod("addChannel", String.class);
            addChannel.invoke(pl, MINIMAP_PACKET_ID);
            addChannel.invoke(pl, WORLD_MAP_PACKET_ID);

            sendHandshakeInfo(pl);
            if (shouldSendLevelId) {
                sendLevelId(pl);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            // won't add any channel to this player
        }
        pl.getWorld().getName();
        var data = new PlayerData();
        playerData.put(pl, data);
        track(pl, data);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        if (!shouldSendLevelId) {
            return;
        }
        var pl = e.getPlayer();
        var data = playerData.get(pl);
        if (data != null) {
            sendModderBothChannels(pl, data, MessageUtil.getLevelIdMessage(levelId));
            track(pl, data);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!e.hasExplicitlyChangedPosition()) {
            return;
        }

        var pl = e.getPlayer();
        var data = playerData.get(pl);
        if (data == null) {
            return;
        }
        var current = System.currentTimeMillis();
        if (current - data.lastSyncTime >= syncCooldown) {
            track(pl, data);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var pl = e.getPlayer();
        untrack(pl);
        playerData.remove(pl);
    }

    public void onMinimapMessageReceived(@NotNull String channel, @NotNull Player pl, byte @NotNull [] message) {
        var buf = Unpooled.wrappedBuffer(message);
        if (buf.readByte() == 1) {
            var version = buf.readInt();
            var data = playerData.computeIfAbsent(pl, (ignored) -> new PlayerData());
            data.setMiniMapNetworkVersion(version);
        }
        pl.sendPluginMessage(this, MINIMAP_PACKET_ID, MessageUtil.getTrackResetMessage());
        trackOthers(pl, MINIMAP_PACKET_ID);
    }

    public void onWorldMapMessageReceived(@NotNull String channel, @NotNull Player pl, byte @NotNull [] message) {
        var buf = Unpooled.wrappedBuffer(message);
        if (buf.readByte() == 1) {
            var version = buf.readInt();
            var data = playerData.computeIfAbsent(pl, (ignored) -> new PlayerData());
            data.setWorldMapNetworkVersion(version);
        }
        pl.sendPluginMessage(this, WORLD_MAP_PACKET_ID, MessageUtil.getTrackResetMessage());
        trackOthers(pl, WORLD_MAP_PACKET_ID);
    }

    public boolean shouldBeTracked(Player pl) {
        return !trackIgnoreList.contains(pl.getName()) &&
                !pl.isInvisible() &&
                pl.getGameMode() != GameMode.SPECTATOR &&
                !PlayerUtil.isVanished(pl);
    }

    /**
     * Should other track pl ignoring {@link #shouldBeTracked(Player)}
     *
     * @param pl
     * @param other
     * @return Should other track pl ignoring {@link #shouldBeTracked(Player)}
     */
    public boolean shouldBeTracked(Player pl, Player other) {
        return trackBypassList.contains(other.getName()) ||
                other.hasPermission(new Permission("xaerotracker.tracker." + pl.getName(), PermissionDefault.FALSE));
    }

    /**
     * Sync location of pl to other players on server
     *
     * @param pl
     */
    public void track(@NotNull Player pl, @NotNull PlayerData plData) {
        var msg = MessageUtil.getTrackPlayerMessage(pl);
        var server = pl.getServer();
        var shouldTrack = shouldBeTracked(pl);
        byte[] untrackMsg = null;

        for (var other: server.getOnlinePlayers()) {
            if (other == pl) {
                continue;
            }

            var otherData = playerData.get(other);
            if (otherData == null) {
                continue;
            }

            if (!shouldTrack && !shouldBeTracked(pl, other)) {
                if (plData.lastShouldTrack) {
                    if (untrackMsg == null) {
                        untrackMsg = MessageUtil.getUntrackPlayerMessage(pl);
                    }
                    sendModderOneChannel(other, otherData, untrackMsg);
                }
                continue;
            }
            sendModderOneChannel(other, otherData, msg);
        }
        plData.lastShouldTrack = shouldTrack;
        plData.lastSyncTime = System.currentTimeMillis();
    }

    /**
     * Sync other players' location to pl
     *
     * @param pl
     * @param channel
     */
    public void trackOthers(Player pl, String channel) {
        for (var other: pl.getServer().getOnlinePlayers()) {
            if (other != pl && (shouldBeTracked(other) || shouldBeTracked(other, pl))) {
                pl.sendPluginMessage(this, channel, MessageUtil.getTrackPlayerMessage(other));
            }
        }
    }

    /**
     * Send untrack message of all players who shouldn't be tracked to pl
     *
     * @param pl
     */
    public void hideUntracked(Player pl) {
        var data = playerData.get(pl);
        if (data ==null) {
            return;
        }

        for(var other: pl.getServer().getOnlinePlayers()) {
            if (other != pl && !shouldBeTracked(other) && !shouldBeTracked(pl, other)) {
                sendModderOneChannel(pl, data, MessageUtil.getUntrackPlayerMessage(other));
            }
        }
    }

    /**
     * Remove the track of pl
     *
     * @param pl
     */
    public void untrack(Player pl) {
        var msg = MessageUtil.getUntrackPlayerMessage(pl);
        for(var other: pl.getServer().getOnlinePlayers()) {
            if (other == pl) {
                continue;
            }
            var data = playerData.get(other);
            if (data != null) {
                sendModderOneChannel(other, data, msg);
            }
        }
    }

    public void sendHandshakeInfo(Player pl) {
        send(pl, MessageUtil.getHandshakeMessage());
    }

    /**
     * Only if level id is sent can client show tracked players
     *
     * @param pl
     */
    public void sendLevelId(Player pl) {
        send(pl, MessageUtil.getLevelIdMessage(levelId));
    }

    /**
     * Send message to both minimap and world map channel
     *
     * @param pl
     * @param msg
     */
    public void send(Player pl, byte[] msg) {
        pl.sendPluginMessage(this, WORLD_MAP_PACKET_ID, msg);
        pl.sendPluginMessage(this, MINIMAP_PACKET_ID, msg);
    }

    public void sendModderBothChannels(Player pl, @NotNull PlayerData data, byte[] msg) {
        if (data.hasMiniMap()) {
            pl.sendPluginMessage(this, MINIMAP_PACKET_ID, msg);
        }
        if (data.hasWorldMap()) {
            pl.sendPluginMessage(this, WORLD_MAP_PACKET_ID, msg);
        }
    }

    /**
     * Send message to the channel decided by PlayerData
     *
     * @param pl
     * @param msg
     */
    public void sendModderOneChannel(Player pl, @NotNull PlayerData data, byte[] msg) {
        if (data.channel != null) {
            pl.sendPluginMessage(this, data.channel, msg);
        }
    }

    @Override
    public void onDisable() {
        playerData.clear();
        var messenger = Bukkit.getMessenger();
        messenger.unregisterIncomingPluginChannel(this);
        messenger.unregisterOutgoingPluginChannel(this);
        GlobalTranslator.translator().removeSource(translationStore);
        translationStore = null;
        trackIgnoreList = null;
        trackBypassList = null;
    }
}

