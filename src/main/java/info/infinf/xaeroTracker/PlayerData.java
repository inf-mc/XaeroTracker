package info.infinf.xaeroTracker;

import org.jetbrains.annotations.Nullable;

public class PlayerData {
    private int worldMapNetworkVersion;
    private int miniMapNetworkVersion;
    public long lastSyncTime;
    @Nullable public String channel;

    public PlayerData() {
        this(0, 0, 0);
    }

    public PlayerData(long lastSyncTime) {
        this(0, 0, lastSyncTime);
    }

    public PlayerData(int worldMapNetworkVersion, int miniMapNetworkVersion, long lastSyncTime) {
        this.worldMapNetworkVersion = worldMapNetworkVersion;
        this.miniMapNetworkVersion = miniMapNetworkVersion;
        this.lastSyncTime = lastSyncTime;
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
        if (miniMapNetworkVersion == 3) {
            channel = XaeroTracker.MINIMAP_PACKET_ID;
        } else if (worldMapNetworkVersion == 3) {
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

    @Override
    public String toString() {
        return "[PlayerData: worldMapNetworkVersion=" + worldMapNetworkVersion +
                ", minimapNetworkVersion=" + miniMapNetworkVersion +
                ", lastSyncTime=" + lastSyncTime + " ]";
    }
}
