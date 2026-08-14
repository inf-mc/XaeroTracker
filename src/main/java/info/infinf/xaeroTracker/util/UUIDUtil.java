package info.infinf.xaeroTracker.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDUtil {
    @Contract("_ -> new")
    public static int @NotNull [] uuidToIntArray(@NotNull UUID uuid) {
        var most = uuid.getMostSignificantBits();
        var least = uuid.getLeastSignificantBits();
        return new int[]{(int)(most >> 32), (int)most, (int)(least >> 32), (int)least};
    }
}
