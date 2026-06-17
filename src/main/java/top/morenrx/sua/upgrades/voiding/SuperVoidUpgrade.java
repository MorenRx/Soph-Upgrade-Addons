package top.morenrx.sua.upgrades.voiding;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.init.SUAConfig;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;

import java.util.List;
import java.util.function.BooleanSupplier;

public class SuperVoidUpgrade extends VoidUpgradeItem implements ISUAItemConfig {

    private final BooleanSupplier enable;
    public SuperVoidUpgrade(BooleanSupplier enable) {
        super(Config.SERVER.advancedVoidUpgrade, Config.SERVER.maxUpgradesPerStorage);
        this.enable = enable;
    }

    @Override
    public int getFilterSlotCount() {
        return SUAConfig.INSTANCE.superVoidUpgrade.filterSlots.get();
    }

    @Override
    public boolean isEnable() {
        return enable.getAsBoolean();
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
