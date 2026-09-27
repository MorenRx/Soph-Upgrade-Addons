package top.morenrx.sua.upgrades.network_restock;

import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPButtonDefinitions;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeTab;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterControl;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilteredUpgradeContainer;
import top.morenrx.sua.init.SUAConfig;

public class NetworkRestockUpgradeTab extends RestockUpgradeTab {
    protected NetworkRestockUpgradeTab(ContentsFilteredUpgradeContainer<RestockUpgradeWrapper> upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);
    }

    public static class Basic extends RestockUpgradeTab {
        public Basic(ContentsFilteredUpgradeContainer<RestockUpgradeWrapper> upgradeContainer, Position position, StorageScreenBase<?> screen) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("network_restock"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("network_restock"));
            filterLogicControl = addHideableChild(new ContentsFilterControl.Advanced(screen, new Position(x + 3, y + 24), getContainer().getFilterLogicContainer(),
                    SUAConfig.INSTANCE.networkRestockUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
        }
    }
}
