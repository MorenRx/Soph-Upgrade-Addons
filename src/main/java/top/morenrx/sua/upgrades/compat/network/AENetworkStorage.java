package top.morenrx.sua.upgrades.compat.network;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;

public class AENetworkStorage implements INetworkStorage {

    @Override
    public String getName() {
        return NetworkStorageProvider.Data.AE;
    }

    @Override
    public boolean canBindBlock(BlockEntity blockEntity) {
        if (blockEntity == null) return false;
        if (blockEntity instanceof IInWorldGridNodeHost) return true;
        return blockEntity.getCapability(Capabilities.IN_WORLD_GRID_NODE_HOST).isPresent();
    }

    @Override
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate) {
        BlockEntity blockEntity = getTargetBlockEntity(upgradeStack, serverLevel);
        if (blockEntity == null) return toInsert;

        IInWorldGridNodeHost host = (blockEntity instanceof IInWorldGridNodeHost h) ? h : blockEntity.getCapability(Capabilities.IN_WORLD_GRID_NODE_HOST).orElse(null);
        if (host == null) return toInsert;

        IGridNode gridNode = host.getGridNode(Direction.UP);
        if (gridNode == null) return toInsert;

        IGrid grid = gridNode.getGrid();
        MEStorage inventory = grid.getStorageService().getInventory();
        long amount = inventory.insert(AEItemKey.of(toInsert), toInsert.getCount(), simulate ? Actionable.SIMULATE : Actionable.MODULATE, IActionSource.ofPlayer(player));
        if (amount == toInsert.getCount()) return ItemStack.EMPTY;
        if (amount == 0) return toInsert;

        ItemStack copy = toInsert.copy();
        copy.setCount(copy.getCount() - (int) amount);
        return copy;
    }

    @Override
    public NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        if (blockEntity == null) return null;
        IInWorldGridNodeHost host = (blockEntity instanceof IInWorldGridNodeHost h) ? h : blockEntity.getCapability(Capabilities.IN_WORLD_GRID_NODE_HOST).orElse(null);
        if (host == null) return null;

        IGridNode gridNode = host.getGridNode(Direction.UP);
        if (gridNode == null) return null;

        IGrid grid = gridNode.getGrid();
        MEStorage inventory = grid.getStorageService().getInventory();

        return (stack, player, simulate) -> {
            long amount = inventory.insert(AEItemKey.of(stack), stack.getCount(), simulate ? Actionable.SIMULATE : Actionable.MODULATE, IActionSource.ofPlayer(player));
            if (amount == stack.getCount()) return ItemStack.EMPTY;
            if (amount == 0) return stack;

            ItemStack copy = stack.copy();
            copy.setCount(copy.getCount() - (int) amount);
            return copy;
        };
    }
}
