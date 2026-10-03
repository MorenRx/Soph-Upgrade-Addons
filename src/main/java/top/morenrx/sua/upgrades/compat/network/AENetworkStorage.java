package top.morenrx.sua.upgrades.compat.network;

import appeng.api.config.Actionable;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.data.NetworkLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AENetworkStorage implements INetworkStorage {

    @Override
    public String getName() {
        return NetworkStorageProvider.Data.AE;
    }

    private static @Nullable IInWorldGridNodeHost getNodeHost(BlockEntity blockEntity) {
        if (blockEntity == null) return null;
        if (blockEntity instanceof IInWorldGridNodeHost h) return h;
        if (blockEntity.getLevel() == null) return null;

        return GridHelper.getNodeHost(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    @Override
    public boolean canBindBlock(BlockEntity blockEntity) {
        if (blockEntity == null) return false;
        if (blockEntity instanceof IInWorldGridNodeHost) return true;
        return getNodeHost(blockEntity) != null;
    }

    @Override
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate, @Nullable NetworkLocation location) {
        BlockEntity blockEntity = location != null ? location.getBlockEntity(serverLevel) : getTargetBlockEntity(upgradeStack, serverLevel);
        if (blockEntity == null) return toInsert;

        IInWorldGridNodeHost host = getNodeHost(blockEntity);
        if (host == null) return toInsert;

        IGridNode gridNode = host.getGridNode(Direction.UP);
        if (gridNode == null) return toInsert;

        IGrid grid = gridNode.getGrid();
        if (grid == null) return toInsert;

        MEStorage inventory = grid.getStorageService().getInventory();
        long amount = inventory.insert(AEItemKey.of(toInsert), toInsert.getCount(), simulate ? Actionable.SIMULATE : Actionable.MODULATE, IActionSource.ofPlayer(player));
        if (amount == toInsert.getCount()) return ItemStack.EMPTY;
        if (amount == 0) return toInsert;

        ItemStack copy = toInsert.copy();
        copy.setCount(copy.getCount() - (int) amount);
        return copy;
    }

    public static @Nullable IGridNode findGridNode(IInWorldGridNodeHost host) {
        IGridNode node = host.getGridNode(null);
        if (node != null) return node;
        for (Direction dir : Direction.values()) {
            node = host.getGridNode(dir);
            if (node != null) return node;
        }
        return null;
    }

    @Override
    public NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        if (blockEntity == null) return null;
        IInWorldGridNodeHost host = getNodeHost(blockEntity);
        if (host == null) return null;

        IGridNode gridNode = findGridNode(host);
        if (gridNode == null) return null;

        IGrid grid = gridNode.getGrid();
        if (grid == null) return null;
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

    @Override
    public NetworkExtractHandler getRestockExtractHandler(BlockEntity blockEntity) {
        if (blockEntity == null) return null;
        IInWorldGridNodeHost host = getNodeHost(blockEntity);
        if (host == null) return null;

        IGridNode gridNode = findGridNode(host);
        if (gridNode == null) return null;

        IGrid grid = gridNode.getGrid();
        if (grid == null) return null;
        MEStorage inventory = grid.getStorageService().getInventory();

        return (filterStack, maxAmount, player, simulate) -> {
            long extracted = inventory.extract(AEItemKey.of(filterStack), maxAmount, simulate ? Actionable.SIMULATE : Actionable.MODULATE, IActionSource.ofPlayer(player));
            if (extracted <= 0) return ItemStack.EMPTY;
            ItemStack copy = filterStack.copy();
            copy.setCount((int) extracted);
            return copy;
        };
    }

    @Override
    public void forEachStoredItem(BlockEntity blockEntity, Predicate<ItemStack> consumer) {
        if (blockEntity == null) return;
        IInWorldGridNodeHost host = getNodeHost(blockEntity);
        if (host == null) return;

        IGridNode gridNode = findGridNode(host);
        if (gridNode == null) return;

        IGrid grid = gridNode.getGrid();
        if (grid == null) return;

        MEStorage inventory = grid.getStorageService().getInventory();
        KeyCounter counter = inventory.getAvailableStacks();
        List<ItemStack> snapshot = new ArrayList<>();
        for (var entry : counter) {
            if (entry.getKey() instanceof AEItemKey itemKey) {
                ItemStack stack = itemKey.toStack();
                if (!stack.isEmpty()) snapshot.add(stack);
            }
        }

        for (ItemStack stack : snapshot) {
            if (!consumer.test(stack)) break;
        }
    }
}
