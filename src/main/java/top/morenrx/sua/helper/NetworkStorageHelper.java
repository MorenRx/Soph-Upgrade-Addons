package top.morenrx.sua.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NetworkStorageHelper {

    public static @Nullable CompoundTag getStorageTag(ItemStack stack, String storageName) {
        CompoundTag root = stack.getTag();
        if (root == null) return null;

        if (root.contains(storageName, Tag.TAG_COMPOUND)) {
            return root.getCompound(storageName);
        }
        return null;
    }

    public static CompoundTag getOrCreateStorageTag(ItemStack stack, String storageName) {
        CompoundTag root = stack.getOrCreateTag();
        if (root.contains(storageName, Tag.TAG_COMPOUND)) {
            return root.getCompound(storageName);
        }
        CompoundTag newSub = new CompoundTag();
        root.put(storageName, newSub);
        return newSub;
    }

    public static void clearStorageTag(ItemStack stack, String storageName) {
        CompoundTag root = stack.getTag();
        if (root != null) {
            root.remove(storageName);
        }
    }
}
