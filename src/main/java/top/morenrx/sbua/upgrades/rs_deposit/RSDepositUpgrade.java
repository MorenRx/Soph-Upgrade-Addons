package top.morenrx.sbua.upgrades.rs_deposit;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sbua.SBUAConfig;
import top.morenrx.sbua.upgrades.base.ISBUAItemConfig;
import top.morenrx.sbua.init.SBUACompat;

import java.util.List;

public class RSDepositUpgrade extends DepositUpgradeItem implements ISBUAItemConfig {
    public static final UpgradeType<DepositUpgradeWrapper> TYPE = new UpgradeType<>(DepositUpgradeWrapper::new);

    public RSDepositUpgrade() {
        super(SBUAConfig.INSTANCE.rsDepositUpgrade.filterSlots::get);
    }

    @Override
    public boolean isEnable() {
        return SBUACompat.REFINED_STORAGE && SBUAConfig.INSTANCE.rsDepositUpgrade.enable.get();
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
}
