package top.morenrx.sua.upgrades.compat.network;

import com.tom.storagemod.tile.StorageTerminalBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import top.morenrx.sua.access.tomstorage.IStorageTerminalBlockEntityAccess;

public class TomNetworkStorage implements INetworkStorage {

    @Override
    public String getName() {
        return NetworkStorageProvider.Data.TOM;
    }

    @Override
    public boolean canBindBlock(BlockEntity blockEntity) {
        return blockEntity instanceof StorageTerminalBlockEntity;
    }

    @Override
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate) {
        BlockEntity blockEntity = getTargetBlockEntity(upgradeStack, serverLevel);
        if (!(blockEntity instanceof StorageTerminalBlockEntity terminal)) return toInsert;
        if (!terminal.canInteractWith(player)) return toInsert;

        return ((IStorageTerminalBlockEntityAccess) terminal).sua$pushStack(toInsert, simulate);
    }

    @Override
    public NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        if (!(blockEntity instanceof StorageTerminalBlockEntity terminal)) return null;

        return (stack, player, simulate) -> {
            if (!terminal.canInteractWith(player)) return stack;
            return ((IStorageTerminalBlockEntityAccess) terminal).sua$pushStack(stack, simulate);
        };
    }
}
