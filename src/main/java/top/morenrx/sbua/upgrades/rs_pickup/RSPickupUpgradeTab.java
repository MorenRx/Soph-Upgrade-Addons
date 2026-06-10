package top.morenrx.sbua.upgrades.rs_pickup;

import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinition;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterControl;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilteredUpgradeContainer;

public class RSPickupUpgradeTab extends UpgradeSettingsTab<ContentsFilteredUpgradeContainer<RSPickupUpgradeWrapper>> {
    protected ContentsFilterControl filterLogicControl;

    protected RSPickupUpgradeTab(ContentsFilteredUpgradeContainer<RSPickupUpgradeWrapper> upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);
    }

    @Override
    protected void moveSlotsToTab() {
        filterLogicControl.moveSlotsToView();
    }

    public static class Basic extends RSPickupUpgradeTab {
        public Basic(ContentsFilteredUpgradeContainer<RSPickupUpgradeWrapper> upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow, ButtonDefinition.Toggle<ContentsFilterType> contentsFilterButton) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("rs_pickup"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("rs_pickup"));
            filterLogicControl = addHideableChild(new ContentsFilterControl.Advanced(screen, new Position(x + 3, y + 24), getContainer().getFilterLogicContainer(),
                    slotsPerRow, contentsFilterButton));
        }
    }
}