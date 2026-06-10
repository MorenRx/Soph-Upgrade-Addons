package top.morenrx.sbua.upgrades.voiding;

import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeItem;
import top.morenrx.sbua.SBUAConfig;
import top.morenrx.sbua.upgrades.base.ISBUAItemConfig;

public class SuperVoidUpgrade extends VoidUpgradeItem implements ISBUAItemConfig {

    public SuperVoidUpgrade() {
        super(Config.SERVER.advancedVoidUpgrade, Config.SERVER.maxUpgradesPerStorage);
    }

    @Override
    public int getFilterSlotCount() {
        return SBUAConfig.INSTANCE.superVoidUpgrade.filterSlots.get();
    }

    @Override
    public boolean isEnable() {
        return SBUAConfig.INSTANCE.superVoidUpgrade.enable.get();
    }
}
