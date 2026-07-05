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
        public static final String KEY_NBT_DIM = "_dim";
        public static final String KEY_NBT_POS = "_pos";
    }

    public @Nullable NetworkLocation getNetworkLocation(@NotNull ServerLevel level, @NotNull ItemStack upgradeStack) {
        CompoundTag nbt = upgradeStack.getOrCreateTag();
        return NetworkLocation.create(level, nbt.getString(name + Data.KEY_NBT_DIM), nbt.getLong(name + Data.KEY_NBT_POS));
    }

    public interface InsertHandler {
        ItemStack insert(ItemStack stack, Player player, boolean simulate);
    }
}
