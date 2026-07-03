package top.morenrx.sua.upgrades.network_pickup;

import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinition;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterControl;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilteredUpgradeContainer;

public class NetworkPickupUpgradeTab extends UpgradeSettingsTab<ContentsFilteredUpgradeContainer<NetworkPickupUpgradeWrapper>> {
    protected ContentsFilterControl filterLogicControl;

    protected NetworkPickupUpgradeTab(ContentsFilteredUpgradeContainer<NetworkPickupUpgradeWrapper> upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);
    }

    @Override
    protected void moveSlotsToTab() {
        filterLogicControl.moveSlotsToView();
    }

    public static class Basic extends NetworkPickupUpgradeTab {
        public Basic(ContentsFilteredUpgradeContainer<NetworkPickupUpgradeWrapper> upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow, ButtonDefinition.Toggle<ContentsFilterType> contentsFilterButton) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("network_pickup"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("network_pickup"));
            filterLogicControl = addHideableChild(new ContentsFilterControl.Advanced(screen, new Position(x + 3, y + 24), getContainer().getFilterLogicContainer(),
                    slotsPerRow, contentsFilterButton));
        }
    }
}