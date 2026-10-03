package top.morenrx.sua.upgrades.compat.network;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
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

public class RSNetworkStorage implements INetworkStorage {

    @Override
    public String getName() {
        return NetworkStorageProvider.Data.RS;
    }

    @Nullable
    private static NetworkNodeContainerProvider getContainerProvider(@Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof AbstractNetworkNodeContainerBlockEntity<?> nodeEntity) {
            return nodeEntity.getContainerProvider();
        }
        if (blockEntity != null && blockEntity.getLevel() != null) {
            return Platform.INSTANCE.getContainerProviderSafely(blockEntity.getLevel(), blockEntity.getBlockPos(), null);
        }
        return null;
    }

    @Override
    public boolean canBindBlock(BlockEntity blockEntity) {
        return getContainerProvider(blockEntity) != null;
    }

    @Nullable
    private static Network getNetwork(BlockEntity blockEntity) {
        NetworkNodeContainerProvider provider = getContainerProvider(blockEntity);
        if (provider == null) return null;
        for (var container : provider.getContainers()) {
            Network network = container.getNode().getNetwork();
            if (network != null) return network;
        }
        return null;
    }

    @Override
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate, @Nullable NetworkLocation location) {
        BlockEntity blockEntity = location != null ? location.getBlockEntity(serverLevel) : getTargetBlockEntity(upgradeStack, serverLevel);
        Network network = getNetwork(blockEntity);
        if (network == null) return toInsert;

        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);

        ItemResource itemResource = ItemResource.ofItemStack(toInsert);
        Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
        Actor actor = player != null ? player::getScoreboardName : Actor.EMPTY;

        long inserted = storage.insert(itemResource, toInsert.getCount(), action, actor);
        if (inserted == toInsert.getCount()) return ItemStack.EMPTY;
        if (inserted <= 0) return toInsert;

        ItemStack copy = toInsert.copy();
        copy.setCount(copy.getCount() - (int) inserted);
        return copy;
    }

    @Override
    public NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        Network network = getNetwork(blockEntity);
        if (network == null) return null;
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);

        return (stack, player, simulate) -> {
            ItemResource itemResource = ItemResource.ofItemStack(stack);
            Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
            Actor actor = player != null ? player::getScoreboardName : Actor.EMPTY;

            long inserted = storage.insert(itemResource, stack.getCount(), action, actor);
            if (inserted == stack.getCount()) return ItemStack.EMPTY;
            if (inserted <= 0) return stack;

            ItemStack copy = stack.copy();
            copy.setCount(copy.getCount() - (int) inserted);
            return copy;
        };
    }

    @Override
    public NetworkExtractHandler getRestockExtractHandler(BlockEntity blockEntity) {
        Network network = getNetwork(blockEntity);
        if (network == null) return null;
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);

        return (filterStack, maxAmount, player, simulate) -> {
            ItemResource itemResource = ItemResource.ofItemStack(filterStack);
            Action action = simulate ? Action.SIMULATE : Action.EXECUTE;
            Actor actor = player != null ? player::getScoreboardName : Actor.EMPTY;

            long extracted = storage.extract(itemResource, maxAmount, action, actor);
            if (extracted <= 0) return ItemStack.EMPTY;
            return itemResource.toItemStack(extracted);
        };
    }

    @Override
    public void forEachStoredItem(BlockEntity blockEntity, Predicate<ItemStack> consumer) {
        Network network = getNetwork(blockEntity);
        if (network == null) return;
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);

        List<ItemStack> snapshot = new ArrayList<>();
        for (var resourceAmount : storage.getAll()) {
            if (resourceAmount.resource() instanceof ItemResource itemResource) {
                ItemStack stack = itemResource.toItemStack(resourceAmount.amount());
                if (!stack.isEmpty()) snapshot.add(stack);
            }
        }

        for (ItemStack stack : snapshot) {
            if (!consumer.test(stack)) break;
        }
    }
}
