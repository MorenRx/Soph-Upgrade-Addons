package top.morenrx.sbua.upgrades.voiding;

import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicControl;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeTab;

public class SuperVoidUpgradeTab extends VoidUpgradeTab {

    public SuperVoidUpgradeTab(VoidUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow) {
        super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("super_void"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("super_void"));
        this.filterLogicControl = this.addHideableChild(new FilterLogicControl.Advanced(screen, new Position(this.x + 3, this.y + 44), this.getContainer().getFilterLogicContainer(), slotsPerRow));
    }
}
