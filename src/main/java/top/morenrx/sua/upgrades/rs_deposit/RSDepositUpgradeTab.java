package top.morenrx.sua.upgrades.rs_deposit;

import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositFilterLogicControl;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import top.morenrx.sua.init.SUAConfig;

public class RSDepositUpgradeTab extends UpgradeSettingsTab<DepositUpgradeContainer> {
    protected DepositFilterLogicControl filterLogicControl;

    protected RSDepositUpgradeTab(DepositUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);
    }

    @Override
    protected void moveSlotsToTab() {
        filterLogicControl.moveSlotsToView();
    }

    public static class Basic extends DepositUpgradeTab {
        public Basic(DepositUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("rs_deposit"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("rs_deposit"));
            filterLogicControl = addHideableChild(new DepositFilterLogicControl.Advanced(screen, new Position(x + 3, y + 24), getContainer().getFilterLogicContainer(),
                    SUAConfig.INSTANCE.rsDepositUpgrade.slotsInRow.get()));
        }
    }
}