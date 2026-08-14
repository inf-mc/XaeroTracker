package info.infinf.xaeroTracker.util;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

public final class PlayerUtil {
    private PlayerUtil() {
    }

    @SuppressWarnings("deprecation") // Common compatibility convention used by vanish plugins.
    public static boolean isVanished(@NotNull Player pl) {
        for (MetadataValue meta : pl.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }
        return false;
    }
}
