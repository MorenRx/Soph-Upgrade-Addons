package top.morenrx.sbua.upgrades.rs_magnet;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sbua.SBUAConfig;
import top.morenrx.sbua.upgrades.base.ISBUAItemConfig;
import top.morenrx.sbua.init.SBUACompat;

import java.util.List;
import java.util.function.IntSupplier;

public class RSMagnetUpgrade extends UpgradeItemBase<RSMagnetUpgradeWrapper> implements ISBUAItemConfig {
    public static final UpgradeType<RSMagnetUpgradeWrapper> TYPE = new UpgradeType<>(RSMagnetUpgradeWrapper::new);

    private final IntSupplier radius;
    private final IntSupplier filterSlotCount;


    public RSMagnetUpgrade() {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.radius = SBUAConfig.INSTANCE.rsMagnetUpgrade.magnetRange::get;
        this.filterSlotCount = SBUAConfig.INSTANCE.rsMagnetUpgrade.filterSlots::get;
    }

    @Override
    public @NotNull UpgradeType<RSMagnetUpgradeWrapper> getType() {
        return TYPE;
    }

    @Override
    public boolean isEnable() {
        return SBUACompat.REFINED_STORAGE && SBUAConfig.INSTANCE.rsMagnetUpgrade.enable.get();
    }

    @Override
    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    public int getFilterSlotCount() {
        return this.filterSlotCount.getAsInt();
    }

    public int getRadius() {
        return this.radius.getAsInt();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isEnable()) return InteractionResultHolder.pass(stack);

        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("pos")) return InteractionResultHolder.pass(stack);

        if (!level.isClientSide()) {
            tag.remove("pos");
            tag.remove("dim");
            tag.remove("player");
            player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.rs.clear"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null || !isEnable()) return InteractionResult.PASS;

        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof INetworkNodeProxy<?>) || blockEntity.getLevel() == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            long pos = blockEntity.getBlockPos().asLong();
            String dimensionKey = blockEntity.getLevel().dimension().location().toString();

            CompoundTag tag = context.getItemInHand().getOrCreateTag();
            tag.putLong("pos", pos);
            tag.putString("dim", dimensionKey);
            tag.putUUID("player", player.getUUID());
            player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.rs.linker"));
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (!isEnable()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.tooltip.disable").withStyle(ChatFormatting.RED));
            return;
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.rs_magnet_upgrade.tooltip.unlinked").withStyle(ChatFormatting.DARK_AQUA));
            return;
        }

        long pos = tag.getLong("pos");
        String dim = tag.getString("dim");
        if (pos == 0 || dim.isEmpty()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.rs_magnet_upgrade.tooltip.unlinked").withStyle(ChatFormatting.DARK_AQUA));
            return;
        }

        BlockPos blockPos = BlockPos.of(pos);
        String[] split = dim.split(":");
        String dimKey = "dimension." + split[0] + "." + split[1];
        tooltip.add(Component.translatable("item.soph_upgrade_addons.rs_magnet_upgrade.tooltip.linked")
                .append(I18n.exists(dimKey) ? Component.translatable(dimKey) : Component.literal(split[1]))
                .append(String.format(" %d, %d, %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()))
                .withStyle(ChatFormatting.AQUA)
        );
    }
}
