package top.morenrx.sbua.upgrades.rsmagnet;

import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeGuiManager;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sbua.SBUAConfig;
import top.morenrx.sbua.SBUAInit;
import top.morenrx.sbua.init.SBUAItems;
import top.morenrx.sbua.util.ModCompat;

import java.util.List;
import java.util.function.IntSupplier;

@Mod.EventBusSubscriber(modid = SBUAInit.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RSMagnetUpgrade extends UpgradeItemBase<RSMagnetUpgradeWrapper> {
    public static final UpgradeType<RSMagnetUpgradeWrapper> TYPE = new UpgradeType<>(RSMagnetUpgradeWrapper::new);
    private static final UpgradeContainerType<RSMagnetUpgradeWrapper, RSMagnetUpgradeContainer> CONTAINER_TYPE = new UpgradeContainerType<>(RSMagnetUpgradeContainer::new);

    private final IntSupplier radius;
    private final IntSupplier filterSlotCount;


    public RSMagnetUpgrade() {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.radius = SBUAConfig.INSTANCE.RSMagnetUpgrade.magnetRange::get;
        this.filterSlotCount = SBUAConfig.INSTANCE.RSMagnetUpgrade.filterSlots::get;
    }

    @Override
    public @NotNull UpgradeType<RSMagnetUpgradeWrapper> getType() {
        return TYPE;
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

        if (!ModCompat.REFINED_STORAGE || !SBUAConfig.INSTANCE.RSMagnetUpgrade.enable.get()) {
            return InteractionResultHolder.pass(stack);
        }

        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("pos")) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide()) {
            tag.remove("pos");
            tag.remove("dim");
            player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.rs_magnet_upgrade.clear"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player == null || !ModCompat.REFINED_STORAGE || !SBUAConfig.INSTANCE.RSMagnetUpgrade.enable.get()) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof ControllerBlockEntity) || blockEntity.getLevel() == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            long pos = blockEntity.getBlockPos().asLong();
            String dimensionKey = blockEntity.getLevel().dimension().location().toString();

            CompoundTag tag = context.getItemInHand().getOrCreateTag();
            tag.putLong("pos", pos);
            tag.putString("dim", dimensionKey);

            player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.rs_magnet_upgrade.linker"));
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!ModCompat.REFINED_STORAGE || !SBUAConfig.INSTANCE.RSMagnetUpgrade.enable.get()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.rs_magnet_upgrade.tooltip.disable"));
            return;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.rs_magnet_upgrade.tooltip.unlinked").withStyle(ChatFormatting.DARK_AQUA));
        } else {
            long pos = tag.getLong("pos");
            String dim = tag.getString("dim");
            if (pos == 0 || dim.isEmpty()) {
                tooltip.add(Component.translatable("item.soph_upgrade_addons.rs_magnet_upgrade.tooltip.unlinked").withStyle(ChatFormatting.DARK_AQUA));
            } else {
                BlockPos blockPos = BlockPos.of(pos);
                String[] split = dim.split(":");
                String dimKey = "dimension." + split[0] + split[1];
                tooltip.add(Component.translatable("item.soph_upgrade_addons.rs_magnet_upgrade.tooltip.linked")
                        .append(I18n.exists(dimKey) ? Component.translatable(dimKey) : Component.literal(split[1]))
                        .append(String.format(" %d, %d, %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()))
                        .withStyle(ChatFormatting.AQUA)
                );
            }
        }
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        ResourceLocation id = SBUAItems.RS_MAGNET_UPGRADE.getId();
        if (id != null) {
            UpgradeContainerRegistry.register(id, CONTAINER_TYPE);
        }
    }


    @Mod.EventBusSubscriber(modid = SBUAInit.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            UpgradeGuiManager.registerTab(
                    RSMagnetUpgrade.CONTAINER_TYPE,
                    (RSMagnetUpgradeContainer container, Position position, StorageScreenBase<?> base) ->
                            new RSMagnetUpgradeTab.Advanced(
                                    container, position, base,
                                    SBUAConfig.INSTANCE.RSMagnetUpgrade.slotsInRow.get(),
                                    SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE
                            )
            );
        }
    }
}
