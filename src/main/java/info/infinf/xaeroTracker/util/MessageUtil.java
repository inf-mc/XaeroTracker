package info.infinf.xaeroTracker.util;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;

import java.io.IOException;

public class MessageUtil {
    public static byte[] getLevelIdMessage(int levelId) {
        // 1 (MessageType) + 4 (LevelId)
        var msg = Unpooled.buffer(5);

        msg.writeByte(0);
        msg.writeInt(levelId);
        msg.capacity(msg.writerIndex());

        return msg.array();
    }

    public static byte[] getHandshakeMessage() {
        // 1 (MessageType) + 4 (Protocol version)
        var msg = Unpooled.buffer(5);

        msg.writeByte(1);
        msg.writeInt(3);
        msg.capacity(msg.writerIndex());

        return msg.array();
    }

    public static byte[] getTrackPlayerMessage(Player pl) {
        // 1 (MessageType) + 1 (CompoundTagType)
        // 24 (6 Tags, 1 byte for TagType, 2 for TagName length, 1 for TagName, 4 bytes per Tag) +
        // 1 (boolean tag) + 24 (3 double tags) + 20 (UUID int array) +
        // 22 (dimension ResourceLocation minecraft:the_nether)
        var msg = Unpooled.buffer(93);

        msg.writeByte(2);

//        CompoundTag nbt = new CompoundTag();
//        nbt.putBoolean("r", false);
//        nbt.putIntArray("i", UUIDUtil.uuidToIntArray(pl.getUniqueId()));
//        nbt.putDouble("x", pl.getX());
//        nbt.putDouble("y", pl.getY());
//        nbt.putDouble("z", pl.getZ());
//        nbt.putString("d", pl.getWorld().getKey().toString());
//        FriendlyByteBuf.writeNbt(msg, nbt);

        // ugly implementation of nbt io for compatibility, don't care about it
        try (var wrappedMsg = new ByteBufOutputStream(msg)) {
            var uuidArray = UUIDUtil.uuidToIntArray(pl.getUniqueId());
            wrappedMsg.writeByte(10);
            wrappedMsg.writeByte(1);
            wrappedMsg.writeUTF("r");
            wrappedMsg.writeByte(0);
            wrappedMsg.writeByte(11);
            wrappedMsg.writeUTF("i");
            wrappedMsg.writeInt(uuidArray.length);
            for (var i: uuidArray) {
                wrappedMsg.writeInt(i);
            }
            wrappedMsg.writeByte(6);
            wrappedMsg.writeUTF("x");
            wrappedMsg.writeDouble(pl.getX());
            wrappedMsg.writeByte(6);
            wrappedMsg.writeUTF("y");
            wrappedMsg.writeDouble(pl.getY());
            wrappedMsg.writeByte(6);
            wrappedMsg.writeUTF("z");
            wrappedMsg.writeDouble(pl.getZ());
            wrappedMsg.writeByte(8);
            wrappedMsg.writeUTF("d");
            wrappedMsg.writeUTF(pl.getWorld().getKey().toString());
            wrappedMsg.writeByte(0);
        } catch (IOException ignored) {
            // impossible
        }

        msg.capacity(msg.writerIndex());

        return msg.array();
    }

    public static byte[] getUntrackPlayerMessage(Player pl) {
        // 1 (MessageType) + 1 (CompoundTagType)
        // 8 (2 Tags, 1 byte for TagType, 2 for TagName length, 1 for TagName, 4 bytes per Tag) +
        // 1 (boolean tag) + 20 (UUID int array)
        var msg = Unpooled.buffer(31);

        msg.writeByte(2);

//        CompoundTag nbt = new CompoundTag();
//        nbt.putBoolean("r", true);
//        nbt.putIntArray("i", UUIDUtil.uuidToIntArray(pl.getUniqueId()));
//        FriendlyByteBuf.writeNbt(msg, nbt);

        // ugly implementation of nbt io for compatibility, don't care about it
        try (var wrappedMsg = new ByteBufOutputStream(msg)) {
            var uuidArray = UUIDUtil.uuidToIntArray(pl.getUniqueId());
            wrappedMsg.writeByte(10);
            wrappedMsg.writeByte(1);
            wrappedMsg.writeUTF("r");
            wrappedMsg.writeByte(1);
            wrappedMsg.writeByte(11);
            wrappedMsg.writeUTF("i");
            wrappedMsg.writeInt(uuidArray.length);
            for (var i: uuidArray) {
                wrappedMsg.writeInt(i);
            }
            wrappedMsg.writeByte(0);
        } catch (IOException ignored) {
            // impossible
        }

        msg.capacity(msg.writerIndex());

        return msg.array();
    }

    public static byte[] getTrackResetMessage() {
        // 1 (MessageType)
        var msg = Unpooled.buffer(1);

        msg.writeByte(3);
        msg.capacity(msg.writerIndex());

        return msg.array();
    }
}
