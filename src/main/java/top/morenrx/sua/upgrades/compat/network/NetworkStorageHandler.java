package top.morenrx.sua.upgrades.compat.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.data.NetworkLocation;

import java.util.function.Function;

public record NetworkStorageHandler(
        String name,
        Function<BlockEntity, Boolean> blockValidGetter,
        Function<BlockEntity, NetworkStorageHandler.InsertHandler> insertHandlerGetter
) {

    public static class Data {
        public static final String KEY_NBT_ID = "_id";
        public static final String KEY_NBT_DIM = "_dim";
        public static final String KEY_NBT_POS = "_pos";
    }

    public @Nullable NetworkLocation getNetworkLocation(@NotNull ServerLevel level, @NotNull ItemStack upgradeStack) {
        CompoundTag nbt = upgradeStack.getOrCreateTag();
        Integer id = nbt.contains(name + Data.KEY_NBT_ID) ? nbt.getInt(name + Data.KEY_NBT_ID) : null;
        String dim = nbt.contains(name + Data.KEY_NBT_DIM) ? nbt.getString(name + Data.KEY_NBT_DIM) : null;
        Long pos = nbt.contains(name + Data.KEY_NBT_POS) ? nbt.getLong(name + Data.KEY_NBT_POS) : null;
        return NetworkLocation.create(level, id, dim, pos);
    }

    public void removeNetworkLocation(@NotNull ItemStack upgradeStack) {
        CompoundTag nbt = upgradeStack.getOrCreateTag();
        nbt.remove(name + Data.KEY_NBT_ID);
        nbt.remove(name + Data.KEY_NBT_POS);
        nbt.remove(name + Data.KEY_NBT_DIM);
    }

    public interface InsertHandler {
        ItemStack insert(ItemStack stack, Player player, boolean simulate);
    }
}
