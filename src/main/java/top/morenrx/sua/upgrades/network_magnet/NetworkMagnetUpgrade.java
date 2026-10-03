package top.morenrx.sua.upgrades.network_magnet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sua.helper.NetworkUpgradeHelper;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class NetworkMagnetUpgrade extends UpgradeItemBase<NetworkMagnetUpgradeWrapper> implements ISUAItemConfig {
    public static final UpgradeType<NetworkMagnetUpgradeWrapper> TYPE = new UpgradeType<>(NetworkMagnetUpgradeWrapper::new);
    private final BooleanSupplier enable;
    private final IntSupplier radius, filterSlots;

    public static class Data {
        public static final String KEY_PICKUP_ITEMS = "pickupItems";
        public static final String KEY_PICKUP_XP = "pickupXp";
        public static final String KEY_ENABLE_VOID = "enableVoid";
        public static final String KEY_NETWORK_TYPE = "networkType";

        public static final String KEY_PREVENT_REMOTE_MOVEMENT = "PreventRemoteMovement";
        public static final String KEY_ALLOW_MACHINE_MOVEMENT = "AllowMachineRemoteMovement";
    }


    public NetworkMagnetUpgrade(BooleanSupplier enable, IntSupplier filterSlots, IntSupplier radius) {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.enable = enable;
        this.filterSlots = filterSlots;
        this.radius = radius;
    }

    @Override
    public @NotNull UpgradeType<NetworkMagnetUpgradeWrapper> getType() {
        return TYPE;
    }

    @Override
    public boolean isEnable() {
        return enable.getAsBoolean() && NetworkStorageProvider.get().hasExternalStorages();
    }

    @Override
    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    public int getFilterSlotCount() {
        return this.filterSlots.getAsInt();
    }

    public int getRadius() {
        return this.radius.getAsInt();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return NetworkUpgradeHelper.handleUse(level, player, hand, this::isEnable);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        return NetworkUpgradeHelper.handleUseOn(context, this::isEnable);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        NetworkUpgradeHelper.appendHoverText(stack, context, tooltip, flagIn, this::isEnable);
    }
}
