package info.infinf.xaeroTracker.util;

import java.util.UUID;

public class UUIDUtil {
    public static int[] uuidToIntArray(UUID uuid) {
        var most = uuid.getMostSignificantBits();
        var least = uuid.getLeastSignificantBits();
        return new int[]{(int)(most >> 32), (int)most, (int)(least >> 32), (int)least};
    }
}
