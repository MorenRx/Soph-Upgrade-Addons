package top.morenrx.sua.upgrades.network_deposit;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.init.SUACompat;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class NetworkDepositUpgrade extends DepositUpgradeItem implements ISUAItemConfig {
    public static final UpgradeType<DepositUpgradeWrapper> TYPE = new UpgradeType<>(DepositUpgradeWrapper::new);
    private final BooleanSupplier enable;
    public NetworkDepositUpgrade(BooleanSupplier enable, IntSupplier filterSlots) {
        super(filterSlots);
        this.enable = enable;
    }

    @Override
    public boolean isEnable() {
        return SUACompat.REFINED_STORAGE.getAsBoolean() && enable.getAsBoolean();
    }

    @Override
    public @NotNull UpgradeType<DepositUpgradeWrapper> getType() {
        return TYPE;
    }

    @Override
    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (!isEnable()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.tooltip.disable").withStyle(ChatFormatting.RED));
            return;
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.RARE;
    }
}
