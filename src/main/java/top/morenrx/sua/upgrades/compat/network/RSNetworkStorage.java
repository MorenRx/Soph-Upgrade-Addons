package top.morenrx.sua.upgrades.compat.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.data.NetworkLocation;

import java.util.*;
import java.util.function.Predicate;

public class RSNetworkStorage implements INetworkStorage {

    @Override
    public String getName() {
        return NetworkStorageProvider.Data.RS;
    }

    @Override
    public boolean canBindBlock(BlockEntity blockEntity) {
        return blockEntity instanceof INetworkNodeProxy<?>;
    }

    @Override
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate, @Nullable NetworkLocation location) {
        BlockEntity blockEntity = location != null ? location.getBlockEntity(serverLevel) : getTargetBlockEntity(upgradeStack, serverLevel);
        if (!(blockEntity instanceof INetworkNodeProxy<?> proxy)) return toInsert;

        INetwork network = proxy.getNode().getNetwork();
        if (network == null || !network.canRun()) return toInsert;

        ItemStack remaining = network.insertItem(toInsert, toInsert.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
        if (!simulate) network.getItemStorageTracker().changed(player, toInsert.copy());
        return remaining;
    }

    @Override
    public NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        if (!(blockEntity instanceof INetworkNodeProxy<?> proxy)) return null;
        INetwork network = proxy.getNode().getNetwork();
        if (network == null || !network.canRun()) return null;

        return (stack, player, simulate) -> {
            ItemStack remaining = network.insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
            if (!simulate) network.getItemStorageTracker().changed(player, stack.copy());
            return remaining;
        };
    }

    @Override
    public NetworkExtractHandler getRestockExtractHandler(BlockEntity blockEntity) {
        if (!(blockEntity instanceof INetworkNodeProxy<?> proxy)) return null;
        INetwork network = proxy.getNode().getNetwork();
        if (network == null || !network.canRun()) return null;

        return (filterStack, maxAmount, player, simulate) -> {
            Action action = simulate ? Action.SIMULATE : Action.PERFORM;
            int compareFlags = filterStack.hasTag() ? IComparer.COMPARE_NBT : 0;

            ItemStack extracted = network.extractItem(filterStack, maxAmount, compareFlags, action);
            if (extracted.isEmpty() && compareFlags != 0)
                extracted = network.extractItem(filterStack, maxAmount, 0, action);

            if (!extracted.isEmpty() && !simulate)
                network.getItemStorageTracker().changed(player, extracted.copy());
            return extracted;
        };
    }

    @Override
    public void forEachStoredItem(BlockEntity blockEntity, Predicate<ItemStack> consumer) {
        if (!(blockEntity instanceof INetworkNodeProxy<?> proxy)) return;
        INetwork network = proxy.getNode().getNetwork();
        if (network == null || !network.canRun()) return;

        List<IStorage<ItemStack>> storages = network.getItemStorageCache().getStorages();
        if (storages == null) return;

        List<ItemStack> snapshot = new ArrayList<>();
        Set<ItemStackKey> seen = new HashSet<>();

        for (IStorage<ItemStack> storage : storages) {
            if (storage.getAccessType() == AccessType.INSERT) continue;
            Collection<ItemStack> stacks = storage.getStacks();
            if (stacks == null || stacks.isEmpty()) continue;

            for (ItemStack stack : stacks) {
                if (stack != null && !stack.isEmpty() && stack.getCount() > 0 && seen.add(ItemStackKey.of(stack)))
                    snapshot.add(stack);
            }
        }

        for (ItemStack stack : snapshot) {
            if (!consumer.test(stack)) break;
        }
    }
}
