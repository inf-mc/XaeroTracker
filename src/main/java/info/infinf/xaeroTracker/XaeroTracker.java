package info.infinf.xaeroTracker;

import info.infinf.xaeroTracker.util.MessageUtil;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class XaeroTracker extends JavaPlugin implements Listener {
    public static final @NotNull String MINIMAP_PACKET_ID = "xaerominimap:main";
    public static final @NotNull String WORLD_MAP_PACKET_ID = "xaeroworldmap:main";

    public final Map<Player, PlayerData> playerData = new HashMap<>();
    public boolean shouldSendLevelId;
    public int levelId;
    public long syncCooldown;

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

        var messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(
                this, MINIMAP_PACKET_ID, this::onMinimapMessageReceived);
        messenger.registerIncomingPluginChannel(
                this, WORLD_MAP_PACKET_ID, this::onWorldMapMessageReceived);
        messenger.registerOutgoingPluginChannel(this, MINIMAP_PACKET_ID);
        messenger.registerOutgoingPluginChannel(this, WORLD_MAP_PACKET_ID);

        this.getServer().getPluginManager().registerEvents(this, this);
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
        track(pl);
        playerData.put(pl, new PlayerData(System.currentTimeMillis()));
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
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!e.hasExplicitlyChangedPosition()) {
            return;
        }

        var pl = e.getPlayer();
        var current = System.currentTimeMillis();
        var data = playerData.computeIfAbsent(pl, (ignored) -> new PlayerData());
        if (current - data.lastSyncTime >= syncCooldown) {
            track(pl);
            data.lastSyncTime = current;
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

    /**
     * Sync location of pl to other players on server
     *
     * @param pl
     */
    public void track(Player pl) {
        byte[] msg;
        msg = MessageUtil.getTrackPlayerMessage(pl);

        var server = pl.getServer();

        for(var other: server.getOnlinePlayers()) {
            if (other == pl) {
                continue;
            }
            var data = playerData.get(other);
            if (data != null) {
                sendModderOneChannel(other, data, msg);
            }
        }
    }

    /**
     * Sync other players' location to pl
     *
     * @param pl
     * @param channel
     */
    public void trackOthers(Player pl, String channel) {
        for (var other: pl.getServer().getOnlinePlayers()) {
            if (other != pl) {
                pl.sendPluginMessage(this, channel, MessageUtil.getTrackPlayerMessage(other));
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
    }
}

