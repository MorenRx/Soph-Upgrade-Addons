package top.morenrx.sua.upgrades.compat.network;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
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
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.helper.NetworkStorageHelper;

import java.util.List;

public interface INetworkStorage {
    class Data {
        public static final String KEY_POS = "pos";
        public static final String KEY_DIM = "dim";
    }

    String getName();

    boolean canBindBlock(BlockEntity blockEntity);

    default InteractionResult onBindBlock(UseOnContext context, BlockEntity blockEntity, ItemStack upgradeStack) {
        if (!canBindBlock(blockEntity)) return InteractionResult.PASS;
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (!level.isClientSide() && player != null) {
            long pos = blockEntity.getBlockPos().asLong();
            String dimensionKey = blockEntity.getLevel().dimension().location().toString();
            CompoundTag tag = NetworkStorageHelper.getOrCreateStorageTag(upgradeStack, getName());
            tag.putString(Data.KEY_DIM, dimensionKey);
            tag.putLong(Data.KEY_POS, pos);
            player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.network." + getName() + ".linker"));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    default InteractionResultHolder<ItemStack> onBindAir(Level level, Player player, InteractionHand hand, ItemStack upgradeStack) {
        return InteractionResultHolder.pass(upgradeStack);
    }

    default void clearBinding(ItemStack upgradeStack) {
        NetworkStorageHelper.clearStorageTag(upgradeStack, getName());
    }

    default boolean hasBinding(ItemStack upgradeStack) {
        CompoundTag tag = NetworkStorageHelper.getStorageTag(upgradeStack, getName());
        return tag != null && tag.contains(Data.KEY_POS) && !tag.getString(Data.KEY_DIM).isEmpty();
    }

    default void appendTooltip(ItemStack upgradeStack, List<Component> tooltip) {
        CompoundTag tag = NetworkStorageHelper.getStorageTag(upgradeStack, getName());
        if (tag != null && tag.contains(Data.KEY_POS) && !tag.getString(Data.KEY_DIM).isEmpty()) {
            long pos = tag.getLong(Data.KEY_POS);
            String dim = tag.getString(Data.KEY_DIM);
            BlockPos blockPos = BlockPos.of(pos);
            String[] split = dim.split(":");
            String dimKey = "dimension." + split[0] + "." + split[1];
            MutableComponent linkedComponent = Component
                    .translatable("item.soph_upgrade_addons.network_pickup_upgrade.tooltip.linked." + getName())
                    .withStyle(ChatFormatting.AQUA);
            linkedComponent.append(Component.translatableWithFallback(dimKey, split[1]))
                    .append(String.format(" %d, %d, %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            tooltip.add(linkedComponent);
        }
    }

    ItemStack insert(IStorageWrapper storageWrapper, ItemStack upgradeStack, ServerLevel serverLevel, Player player, ItemStack toInsert, boolean simulate);

    default boolean isValidDepositBlock(BlockEntity blockEntity) {
        return canBindBlock(blockEntity);
    }

    default NetworkInsertHandler getDepositInsertHandler(BlockEntity blockEntity) {
        return null;
    }

    default @Nullable BlockEntity getTargetBlockEntity(ItemStack upgradeStack, ServerLevel currentLevel) {
        CompoundTag tag = NetworkStorageHelper.getStorageTag(upgradeStack, getName());
        if (tag == null || !tag.contains(Data.KEY_POS) || tag.getString(Data.KEY_DIM).isEmpty()) return null;
        long pos = tag.getLong(Data.KEY_POS);
        String dim = tag.getString(Data.KEY_DIM);
        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, SophUpgradeAddons.parse(dim));
        ServerLevel targetLevel = currentLevel.getServer().getLevel(dimKey);
        if (targetLevel == null) return null;
        return targetLevel.getBlockEntity(BlockPos.of(pos));
    }

    @FunctionalInterface
    interface NetworkInsertHandler {
        ItemStack insert(ItemStack stack, Player player, boolean simulate);
    }
}
