package top.morenrx.sua.upgrades.network_pickup;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.helper.NetworkUpgradeHelper;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;

import java.util.List;
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
        return enable.getAsBoolean();
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
        return NetworkUpgradeHelper.handleUse(level, player, hand, this::isEnable);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        return NetworkUpgradeHelper.handleUseOn(context, this::isEnable);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        NetworkUpgradeHelper.appendHoverText(stack, worldIn, tooltip, flagIn, this::isEnable);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.RARE;
    }
}
