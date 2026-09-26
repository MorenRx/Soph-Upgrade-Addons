package top.morenrx.sua.upgrades.compat.network;

import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.api.storage.key.KeyAmount;
import com.wintercogs.beyonddimensions.api.storage.key.impl.ItemStackKey;
import com.wintercogs.beyonddimensions.common.block.entity.NetedBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import top.morenrx.sua.helper.NetworkStorageHelper;

import java.util.List;

public class BDNetworkStorage implements INetworkStorage {

    public static class Data {
        public static final String KEY_ID = "id";
    }

    @Override
    public String getName() {
        return NetworkStorageProvider.Data.BD;
    }

    @Override
    public boolean canBindBlock(BlockEntity blockEntity) {
        return blockEntity instanceof NetedBlockEntity;
    }

    @Override
    public InteractionResult onBindBlock(UseOnContext context, BlockEntity blockEntity, ItemStack upgradeStack) {
        if (!(blockEntity instanceof NetedBlockEntity netedBlockEntity)) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (!level.isClientSide() && player != null) {
            int netId = netedBlockEntity.getNetId();
            if (netId == DimensionsNet.NO_PRIMARY_NET_ID) {
                return InteractionResult.FAIL;
            }
            CompoundTag tag = NetworkStorageHelper.getOrCreateStorageTag(upgradeStack, getName());
            tag.putInt(Data.KEY_ID, netId);
            DimensionsNet net = netedBlockEntity.getNet();
            Component netName = net != null ? net.getNetworkName() : Component.literal("#" + netId);
            player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.network.bd.linker", netName, netId));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public InteractionResultHolder<ItemStack> onBindAir(Level level, Player player, InteractionHand hand, ItemStack upgradeStack) {
        if (level.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(upgradeStack, true);
        }
        DimensionsNet primaryNet = DimensionsNet.getPrimaryNetFromPlayer(player);
        if (primaryNet == null || primaryNet.deleted) {
            player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.network.bd.linker_fail").withStyle(ChatFormatting.RED));
            return InteractionResultHolder.fail(upgradeStack);
        }
        int netId = primaryNet.getId();
        CompoundTag tag = NetworkStorageHelper.getOrCreateStorageTag(upgradeStack, getName());
        tag.putInt(Data.KEY_ID, netId);
        Component netName = primaryNet.getNetworkName();
        player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.network.bd.linker", netName, netId));
        return InteractionResultHolder.sidedSuccess(upgradeStack, false);
    }

    @Override
    public boolean hasBinding(ItemStack upgradeStack) {
        CompoundTag tag = NetworkStorageHelper.getStorageTag(upgradeStack, getName());
        return tag != null && tag.contains(Data.KEY_ID) && tag.getInt(Data.KEY_ID) != DimensionsNet.NO_PRIMARY_NET_ID;
    }

    @Override
    public void appendTooltip(ItemStack upgradeStack, List<Component> tooltip) {
        CompoundTag tag = NetworkStorageHelper.getStorageTag(upgradeStack, getName());
        if (tag != null && tag.contains(Data.KEY_ID)) {
            int netId = tag.getInt(Data.KEY_ID);
            if (netId != DimensionsNet.NO_PRIMARY_NET_ID) {
                MutableComponent linkedComponent = Component
                        .translatable("item.soph_upgrade_addons.network_pickup_upgrade.tooltip.linked.bd")
                        .withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(" #" + netId));
                tooltip.add(linkedComponent);
            }
        }
    }

    @Override
    public ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate) {
        CompoundTag tag = NetworkStorageHelper.getStorageTag(upgradeStack, getName());
        if (tag == null || !tag.contains(Data.KEY_ID)) return toInsert;
        int netId = tag.getInt(Data.KEY_ID);
        if (netId == DimensionsNet.NO_PRIMARY_NET_ID) return toInsert;

        DimensionsNet net = DimensionsNet.getNetFromId(netId);
        if (net == null || net.deleted) return toInsert;

        KeyAmount remaining = net.getUnifiedStorage().insert(new ItemStackKey(toInsert), toInsert.getCount(), simulate);
        if (remaining.isEmpty()) return ItemStack.EMPTY;
        ItemStack copy = toInsert.copy();
        copy.setCount((int) remaining.amount());
        return copy;
    }

    @Override
    public NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        if (!(blockEntity instanceof NetedBlockEntity netedBlockEntity)) return null;
        DimensionsNet net = netedBlockEntity.getNet();
        if (net == null) return null;
        return (stack, player, simulate) -> {
            KeyAmount remaining = net.getUnifiedStorage().insert(new ItemStackKey(stack), stack.getCount(), simulate);
            if (remaining.isEmpty()) return ItemStack.EMPTY;
            ItemStack copy = stack.copy();
            copy.setCount((int) remaining.amount());
            return copy;
        };
    }
}
