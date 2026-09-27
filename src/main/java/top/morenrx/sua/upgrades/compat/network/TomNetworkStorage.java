package top.morenrx.sua.upgrades.compat.network;

import com.tom.storagemod.tile.StorageTerminalBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.data.NetworkLocation;
import top.morenrx.sua.mixin.common.tomstorage.IStorageTerminalBlockEntityAccess;

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
        if (terminal instanceof IStorageTerminalBlockEntityAccess access) {
            IItemHandler handler = access.sua$getItemHandler();
            if (handler == null) {
                access.sua$setUpdateItems(true);
                terminal.updateServer();
                handler = access.sua$getItemHandler();
            }
            return handler;
        }
        return null;
    }

    public static ItemStack pushStack(StorageTerminalBlockEntity terminal, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        IItemHandler handler = getTerminalItemHandler(terminal);
        if (handler != null) return ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
        return stack;
    }

    public static ItemStack pullStack(StorageTerminalBlockEntity terminal, ItemStack filterStack, int maxAmount, boolean simulate) {
        if (filterStack.isEmpty() || maxAmount <= 0) return ItemStack.EMPTY;
        IItemHandler handler = getTerminalItemHandler(terminal);
        if (handler == null) return ItemStack.EMPTY;

        int remainingNeeded = maxAmount;
        ItemStack extracted = ItemStack.EMPTY;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (inSlot.isEmpty() || !ItemHandlerHelper.canItemStacksStack(inSlot, filterStack)) continue;

            ItemStack pulled = handler.extractItem(i, remainingNeeded, simulate);
            if (pulled.isEmpty()) continue;

            if (extracted.isEmpty()) {
                extracted = pulled.copy();
            } else {
                extracted.grow(pulled.getCount());
            }

            remainingNeeded -= pulled.getCount();
            if (remainingNeeded <= 0) break;
        }
        return extracted;
    }

    @Override
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate, @Nullable NetworkLocation location) {
        BlockEntity blockEntity = location != null ? location.getBlockEntity(serverLevel) : getTargetBlockEntity(upgradeStack, serverLevel);
        if (!(blockEntity instanceof StorageTerminalBlockEntity terminal)) return toInsert;
        if (!terminal.canInteractWith(player)) return toInsert;

        return pushStack(terminal, toInsert, simulate);
    }

    public static boolean isTerminalAccessible(StorageTerminalBlockEntity terminal, Player player) {
        if (terminal.canInteractWith(player)) return true;
        return player.level() == terminal.getLevel() && player.distanceToSqr(terminal.getBlockPos().getX() + 0.5, terminal.getBlockPos().getY() + 0.5, terminal.getBlockPos().getZ() + 0.5) <= 36.0;
    }

    @Override
    public NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        if (!(blockEntity instanceof StorageTerminalBlockEntity terminal)) return null;

        return (stack, player, simulate) -> {
            if (!isTerminalAccessible(terminal, player)) return stack;
            return pushStack(terminal, stack, simulate);
        };
    }

    @Override
    public NetworkExtractHandler getRestockExtractHandler(BlockEntity blockEntity) {
        if (!(blockEntity instanceof StorageTerminalBlockEntity terminal)) return null;

        return (filterStack, maxAmount, player, simulate) -> {
            if (!isTerminalAccessible(terminal, player)) return ItemStack.EMPTY;
            return pullStack(terminal, filterStack, maxAmount, simulate);
        };
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
