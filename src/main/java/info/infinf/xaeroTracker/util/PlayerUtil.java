package info.infinf.xaeroTracker.util;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class PlayerUtil {
    public static boolean isVanished(Player pl) {
        for (MetadataValue meta : pl.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }
        return false;
    }
}
