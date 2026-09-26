package top.morenrx.sua.upgrades.compat.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.util.Action;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;

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
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate) {
        BlockEntity blockEntity = getTargetBlockEntity(upgradeStack, serverLevel);
        if (!(blockEntity instanceof INetworkNodeProxy<?> proxy)) return toInsert;
        INetwork network = proxy.getNode().getNetwork();
        if (network == null || !network.canRun()) return toInsert;

        ItemStack remaining = network.insertItem(toInsert, toInsert.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
        if (!simulate) {
            network.getItemStorageTracker().changed(player, toInsert.copy());
        }
        return remaining;
    }

    @Override
    public NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        if (!(blockEntity instanceof INetworkNodeProxy<?> proxy)) return null;
        INetwork network = proxy.getNode().getNetwork();
        if (network == null || !network.canRun()) return null;

        return (stack, player, simulate) -> {
            ItemStack remaining = network.insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
            if (!simulate) {
                network.getItemStorageTracker().changed(player, stack.copy());
            }
            return remaining;
        };
    }
}
