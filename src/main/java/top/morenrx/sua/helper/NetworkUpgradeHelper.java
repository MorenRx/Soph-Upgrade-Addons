package top.morenrx.sua.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class NetworkUpgradeHelper {

    public static InteractionResultHolder<ItemStack> handleUse(Level level, Player player, InteractionHand hand, BooleanSupplier isEnable) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isEnable.getAsBoolean()) return InteractionResultHolder.pass(stack);

        if (player.isCrouching()) {
            InteractionResultHolder<ItemStack> bindAirResult = NetworkStorageProvider.get().onBindAir(level, player, hand, stack);
            if (bindAirResult.getResult().consumesAction()) {
                return bindAirResult;
            }
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide()) {
            NetworkStorageProvider.get().clearBindings(stack);
            player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.network.clear"));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static InteractionResult handleUseOn(UseOnContext context, BooleanSupplier isEnable) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null || !player.isCrouching() || !isEnable.getAsBoolean()) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return InteractionResult.PASS;
        }

        return NetworkStorageProvider.get().onBindBlock(context, blockEntity, context.getItemInHand());
    }

    public static void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn, BooleanSupplier isEnable) {
        if (!isEnable.getAsBoolean()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.tooltip.disable").withStyle(ChatFormatting.RED));
            return;
        }

        List<Component> linkedTooltips = new ArrayList<>();
        NetworkStorageProvider.get().appendTooltips(stack, linkedTooltips);

        if (linkedTooltips.isEmpty()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.network_pickup_upgrade.tooltip.unlinked").withStyle(ChatFormatting.DARK_AQUA));
        } else {
            tooltip.addAll(linkedTooltips);
        }
    }
}
