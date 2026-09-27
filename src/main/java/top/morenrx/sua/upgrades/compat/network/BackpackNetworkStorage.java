package top.morenrx.sua.upgrades.compat.network;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.data.NetworkLocation;

import java.util.List;

public class BackpackNetworkStorage implements INetworkStorage {

    @Override
    public String getName() {
        return NetworkStorageProvider.Data.BACKPACK;
    }

    @Override
    public boolean canBindBlock(BlockEntity blockEntity) {
        return false;
    }

    @Override
    public InteractionResult onBindBlock(UseOnContext context, BlockEntity blockEntity, ItemStack upgradeStack) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean hasBinding(ItemStack upgradeStack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack upgradeStack, List<Component> tooltip) {
    }

    @Override
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate, @Nullable NetworkLocation location) {
        if (storageWrapper == null) return toInsert;
        return storageWrapper.getInventoryForUpgradeProcessing().insertItem(toInsert, simulate);
    }
}
