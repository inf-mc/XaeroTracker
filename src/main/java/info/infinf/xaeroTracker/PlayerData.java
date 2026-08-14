package info.infinf.xaeroTracker;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

public class PlayerData {
    private int worldMapNetworkVersion;
    private int miniMapNetworkVersion;
    public long lastSyncTime;
    public boolean lastShouldTrack;
    @Nullable public String channel;
    @Nullable public BukkitTask syncSchedule;

    public PlayerData() {
        this(0, 0, 0, false);
    }

    public PlayerData(int worldMapNetworkVersion, int miniMapNetworkVersion, long lastSyncTime, boolean lastShouldTrack) {
        this.worldMapNetworkVersion = worldMapNetworkVersion;
        this.miniMapNetworkVersion = miniMapNetworkVersion;
        this.lastSyncTime = lastSyncTime;
        this.lastShouldTrack = lastShouldTrack;
    }

    public int getWorldMapNetworkVersion() {
        return worldMapNetworkVersion;
    }

    public int getMiniMapNetworkVersion() {
        return miniMapNetworkVersion;
    }

    public void setMiniMapNetworkVersion(int miniMapNetworkVersion) {
        this.miniMapNetworkVersion = miniMapNetworkVersion;
        decideChannel();
    }

    public void setWorldMapNetworkVersion(int worldMapNetworkVersion) {
        this.worldMapNetworkVersion = worldMapNetworkVersion;
        decideChannel();
    }

    private void decideChannel() {
        if (miniMapNetworkVersion == XaeroTracker.SUPPORTED_NETWORK_VERSION) {
            channel = XaeroTracker.MINIMAP_PACKET_ID;
        } else if (worldMapNetworkVersion == XaeroTracker.SUPPORTED_NETWORK_VERSION) {
            channel = XaeroTracker.WORLD_MAP_PACKET_ID;
        } else {
            channel = null;
        }
    }

    public boolean hasWorldMap() {
        return worldMapNetworkVersion != 0;
    }

    public boolean hasMiniMap() {
        return miniMapNetworkVersion != 0;
    }

    public void clearSyncSchedule() {
        if (syncSchedule != null) {
            syncSchedule.cancel();
            syncSchedule = null;
        }
    }

    @Override
    public String toString() {
        return "[PlayerData: worldMapNetworkVersion=" + worldMapNetworkVersion +
                ", minimapNetworkVersion=" + miniMapNetworkVersion +
                ", lastSyncTime=" + lastSyncTime + " ]";
    }
}
