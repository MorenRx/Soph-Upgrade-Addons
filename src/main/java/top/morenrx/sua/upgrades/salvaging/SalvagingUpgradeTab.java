package top.morenrx.sua.upgrades.salvaging;

import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicControl;
import top.morenrx.sua.helper.client.SalvagingClientHelper;
import top.morenrx.sua.upgrades.salvaging.gui.OverlayToggleButton;

public class SalvagingUpgradeTab extends UpgradeSettingsTab<SalvagingUpgradeContainer> {

    protected FilterLogicControl<FilterLogic, FilterLogicContainer<FilterLogic>> filterLogicControl;

    protected SalvagingUpgradeTab(SalvagingUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);
    }

    @Override
    protected void moveSlotsToTab() {
        filterLogicControl.moveSlotsToView();
    }

    public static class Basic extends SalvagingUpgradeTab {
        public Basic(SalvagingUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("salvaging"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("salvaging"));
            addHideableChild(new ToggleButton<>(new Position(x + 3, y + 24), ButtonDefinitions.WORK_IN_GUI,
                    button -> getContainer().setWorkInGUI(!getContainer().shouldWorkInGUI()), getContainer()::shouldWorkInGUI));
            addHideableChild(new ToggleButton<>(new Position(x + 21, y + 24), SalvagingClientHelper.SALVAGING_OTHER,
                    button -> getContainer().setSalvagingOther(!getContainer().shouldSalvagingOther()),
                    getContainer()::shouldSalvagingOther));
            filterLogicControl = addHideableChild(new FilterLogicControl.Basic(screen, new Position(x + 3, y + 44), getContainer().getFilterLogicContainer(),
                    slotsPerRow));
        }
    }

    public static class Advanced extends SalvagingUpgradeTab {
        public Advanced(SalvagingUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("advanced_salvaging"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("advanced_salvaging"));
            addHideableChild(new ToggleButton<>(new Position(x + 3, y + 24), ButtonDefinitions.WORK_IN_GUI,
                    button -> getContainer().setWorkInGUI(!getContainer().shouldWorkInGUI()),
                    getContainer()::shouldWorkInGUI));
            addHideableChild(new ToggleButton<>(new Position(x + 21, y + 24), SalvagingClientHelper.SALVAGING_EQUIPMENT,
                    button -> getContainer().setSalvagingEquipment(!getContainer().shouldSalvagingEquipment()),
                    getContainer()::shouldSalvagingEquipment));
            addHideableChild(new ToggleButton<>(new Position(x + 39, y + 24), SalvagingClientHelper.SALVAGING_GEM,
                    button -> getContainer().setSalvagingGem(!getContainer().shouldSalvagingGem()),
                    getContainer()::shouldSalvagingGem));
            addHideableChild(new ToggleButton<>(new Position(x + 57, y + 24), SalvagingClientHelper.SALVAGING_OTHER,
                    button -> getContainer().setSalvagingOther(!getContainer().shouldSalvagingOther()),
                    getContainer()::shouldSalvagingOther));
            int rarityFilterX = slotsPerRow * 18 + 2;
            for (int i = 0; i < SalvagingClientHelper.EQUIPMENT_RARITY.size(); i++) {
                int mask = 1 << i;
                addHideableChild(new OverlayToggleButton<>(new Position(x + 3 + rarityFilterX, y + 24 + (i * 18)), SalvagingClientHelper.EQUIPMENT_RARITY.get(i),
                        button -> getContainer().setEquipmentRarityMask(getContainer().shouldEquipmentRarityMask() ^ mask),
                        () -> getContainer().shouldSalvagingEquipment() && (getContainer().shouldEquipmentRarityMask() & mask) != 0));
            }
            for (int i = 0; i < SalvagingClientHelper.GEM_RARITY.size(); i++) {
                int mask = 1 << i;
                addHideableChild(new OverlayToggleButton<>(new Position(x + 3 + rarityFilterX + 18, y + 24 + (i * 18)), SalvagingClientHelper.GEM_RARITY.get(i),
                        button -> getContainer().setGemRarityMask(getContainer().shouldGemRarityMask() ^ mask),
                        () -> getContainer().shouldSalvagingGem() && (getContainer().shouldGemRarityMask() & mask) != 0));
            }

            filterLogicControl = addHideableChild(new FilterLogicControl.Advanced(screen, new Position(x + 3, y + 44), getContainer().getFilterLogicContainer(),
                    slotsPerRow));
        }
    }
}