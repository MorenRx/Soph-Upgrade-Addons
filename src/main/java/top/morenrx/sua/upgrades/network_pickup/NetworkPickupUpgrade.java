package top.morenrx.sua.upgrades.network_pickup;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageHandler;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class NetworkPickupUpgrade extends UpgradeItemBase<NetworkPickupUpgradeWrapper> implements ISUAItemConfig {
    public static final UpgradeType<NetworkPickupUpgradeWrapper> TYPE = new UpgradeType<>(NetworkPickupUpgradeWrapper::new);
    private final BooleanSupplier enable;
    private final IntSupplier filterSlots;

    public static class Data {
        public static final String KEY_ENABLE_VOID = "enableVoid";
        public static final String KEY_NETWORK_TYPE = "networkType";
    }

    public NetworkPickupUpgrade(BooleanSupplier enable, IntSupplier filterSlots) {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.enable = enable;
        this.filterSlots = filterSlots;
    }

    @Override
    public boolean isEnable() {
        return enable.getAsBoolean() && !NetworkStorageProvider.get().getNetworkStorageHandlers().isEmpty();
    }

    public int getFilterSlotCount() {
        return filterSlots.getAsInt();
    }

    @Override
    public @NotNull UpgradeType<NetworkPickupUpgradeWrapper> getType() {
        return TYPE;
    }

    @Override
    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching() || !isEnable()) return InteractionResultHolder.pass(stack);

        if (!level.isClientSide()) {
            NetworkStorageProvider.get().getNetworkStorageHandlers().forEach((name, handler) -> handler.removeNetworkLocation(stack));
            player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.network.clear"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null || !player.isCrouching() || !isEnable()) return InteractionResult.PASS;

        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return InteractionResult.PASS;
        }

        AtomicReference<InteractionResult> interactionResult = new AtomicReference<>(InteractionResult.PASS);
        NetworkStorageProvider.get().getNetworkStorageHandlers().forEach((name, handler) -> {
            if (handler.blockValidGetter().apply(blockEntity)) {
                if (!level.isClientSide()) {
                    long pos = blockEntity.getBlockPos().asLong();
                    String dimensionKey = blockEntity.getLevel().dimension().location().toString();

                    CompoundTag nbt = context.getItemInHand().getOrCreateTag();
                    nbt.putString(name + NetworkStorageHandler.Data.KEY_NBT_DIM, dimensionKey);
                    nbt.putLong(name + NetworkStorageHandler.Data.KEY_NBT_POS, pos);
                    player.sendSystemMessage(Component.translatable("message.soph_upgrade_addons.network." + name + ".linker"));
                }
                interactionResult.set(InteractionResult.sidedSuccess(level.isClientSide()));
            }
        });

        return interactionResult.get();
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
            tooltip.add(Component.translatable("item.soph_upgrade_addons.network_pickup_upgrade.tooltip.unlinked").withStyle(ChatFormatting.DARK_AQUA));
            return;
        }

        List<Component> components = new ArrayList<>();
        NetworkStorageProvider.get().getNetworkStorageHandlers().forEach((name, handler) -> {
            int netId = tag.getInt(name + NetworkStorageHandler.Data.KEY_NBT_ID);
            String dim = tag.getString(name + NetworkStorageHandler.Data.KEY_NBT_DIM);
            long pos = tag.getLong(name + NetworkStorageHandler.Data.KEY_NBT_POS);
            if (pos != 0 && !dim.isEmpty()) {
                BlockPos blockPos = BlockPos.of(pos);
                String[] split = dim.split(":");
                String dimKey = "dimension." + split[0] + "." + split[1];
                MutableComponent linkedComponent = Component
                        .translatable("item.soph_upgrade_addons.network_pickup_upgrade.tooltip.linked." + name)
                        .withStyle(ChatFormatting.AQUA);

                if (netId != 0) {
                    linkedComponent
                            .append(String.valueOf(netId));
                } else {
                    linkedComponent
                            .append(I18n.exists(dimKey) ? Component.translatable(dimKey) : Component.literal(split[1]))
                            .append(String.format(" %d, %d, %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                }

                components.add(linkedComponent);
            }
        });

        if (components.isEmpty()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.network_pickup_upgrade.tooltip.unlinked").withStyle(ChatFormatting.DARK_AQUA));
        } else {
            tooltip.addAll(components);
        }
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.RARE;
    }
}
