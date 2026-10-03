package top.morenrx.sua.helper;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class NetworkStorageHelper {

    public static @Nullable CompoundTag getStorageTag(ItemStack stack, String storageName) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        CompoundTag root = customData.copyTag();
        if (root.contains(storageName, Tag.TAG_COMPOUND)) {
            return root.getCompound(storageName);
        }
        return null;
    }

    public static CompoundTag getOrCreateStorageTag(ItemStack stack, String storageName) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag root = customData != null ? customData.copyTag() : new CompoundTag();
        if (root.contains(storageName, Tag.TAG_COMPOUND)) {
            return root.getCompound(storageName);
        }
        CompoundTag newSub = new CompoundTag();
        root.put(storageName, newSub);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        return newSub;
    }

    public static void modifyStorageTag(ItemStack stack, String storageName, Consumer<CompoundTag> modifier) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> {
            CompoundTag sub = root.contains(storageName, Tag.TAG_COMPOUND) ? root.getCompound(storageName) : new CompoundTag();
            modifier.accept(sub);
            root.put(storageName, sub);
        });
    }

    public static void saveStorageTag(ItemStack stack, String storageName, CompoundTag storageTag) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> {
            root.put(storageName, storageTag);
        });
    }

    public static void clearStorageTag(ItemStack stack, String storageName) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> {
            root.remove(storageName);
        });
    }
}
