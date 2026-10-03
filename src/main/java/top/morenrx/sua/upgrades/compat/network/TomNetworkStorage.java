package top.morenrx.sua.upgrades.compat.network;

import com.tom.storagemod.block.entity.StorageTerminalBlockEntity;
import com.tom.storagemod.inventory.IInventoryAccess;
import com.tom.storagemod.inventory.NetworkInventory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.access.tomstorage.IStorageTerminalBlockEntityAccess;
import top.morenrx.sua.data.NetworkLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TomNetworkStorage implements INetworkStorage {

    @Override
    public String getName() {
        return NetworkStorageProvider.Data.TOM;
    }

    @Override
    public boolean canBindBlock(BlockEntity blockEntity) {
        return blockEntity instanceof StorageTerminalBlockEntity;
    }

    public static @Nullable IItemHandler getTerminalItemHandler(StorageTerminalBlockEntity terminal) {
        if (terminal.isRemoved() || terminal.getLevel() == null || !(terminal instanceof IStorageTerminalBlockEntityAccess access))
            return null;

        NetworkInventory netInv = access.sua$getItemCache();
        if (netInv == null) return null;

        IInventoryAccess invAccess = netInv.getAccess(terminal.getLevel(), terminal.getBlockPos());
        if (invAccess == null) return null;

        IItemHandler handler = invAccess.getPlatformHandler();
        if (handler == null || handler == EmptyItemHandler.INSTANCE || handler.getSlots() <= 0)
            return null;


        return handler;
    }

    public static ItemStack pushStack(StorageTerminalBlockEntity terminal, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        IItemHandler handler = getTerminalItemHandler(terminal);
        if (handler == null) return stack;

        return ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
    }

    public static ItemStack pullStack(StorageTerminalBlockEntity terminal, ItemStack filterStack, int maxAmount, boolean simulate) {
        if (filterStack.isEmpty() || maxAmount <= 0) return ItemStack.EMPTY;

        IItemHandler handler = getTerminalItemHandler(terminal);
        if (handler == null) return ItemStack.EMPTY;

        int remainingNeeded = maxAmount;
        ItemStack result = ItemStack.EMPTY;

        for (int i = 0; i < handler.getSlots() && remainingNeeded > 0; i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (inSlot.isEmpty() || !ItemStack.isSameItemSameComponents(inSlot, filterStack))
                continue;

            ItemStack extracted = handler.extractItem(i, remainingNeeded, simulate);
            if (extracted.isEmpty()) continue;

            if (result.isEmpty()) {
                result = extracted.copy();
            } else {
                result.grow(extracted.getCount());
            }
            remainingNeeded -= extracted.getCount();
        }

        return result;
    }

    @Override
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate, @Nullable NetworkLocation location) {
        BlockEntity blockEntity = location != null ? location.getBlockEntity(serverLevel) : getTargetBlockEntity(upgradeStack, serverLevel);
        if (!(blockEntity instanceof StorageTerminalBlockEntity terminal))
            return toInsert;

        return pushStack(terminal, toInsert, simulate);
    }

    @Override
    public NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        if (!(blockEntity instanceof StorageTerminalBlockEntity terminal)) return null;

        return (stack, player, simulate) -> pushStack(terminal, stack, simulate);
    }

    @Override
    public NetworkExtractHandler getRestockExtractHandler(BlockEntity blockEntity) {
        if (!(blockEntity instanceof StorageTerminalBlockEntity terminal)) return null;

        return (filterStack, maxAmount, player, simulate) -> pullStack(terminal, filterStack, maxAmount, simulate);
    }

    @Override
    public void forEachStoredItem(BlockEntity blockEntity, Predicate<ItemStack> consumer) {
        if (!(blockEntity instanceof StorageTerminalBlockEntity terminal)) return;

        IItemHandler handler = getTerminalItemHandler(terminal);
        if (handler == null) return;

        List<ItemStack> snapshot = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) snapshot.add(stack.copy());
        }

        for (ItemStack stack : snapshot) {
            if (!consumer.test(stack)) break;
        }
    }
}
