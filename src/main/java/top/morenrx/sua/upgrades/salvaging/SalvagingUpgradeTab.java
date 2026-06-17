package top.morenrx.sua.upgrades.salvaging;

import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinition;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.UV;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicControl;
import top.morenrx.sua.util.SUAUtils;

import java.util.HashMap;
import java.util.Map;

public class SalvagingUpgradeTab extends UpgradeSettingsTab<SalvagingUpgradeContainer> {
    private static final int RARITY_COUNT = RarityRegistry.INSTANCE.getOrderedRarities().size();
    private static final ButtonDefinition.Toggle<Integer> EQUIPMENT_RARITY;
    private static final ButtonDefinition.Toggle<Integer> GEM_RARITY;

    private static final ButtonDefinition.Toggle<Boolean> SALVAGING_OTHER = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    SUAUtils.Gui.getButtonStateData(new UV(0, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_other_enable"), Dimension.SQUARE_16, new Position(1, 1)),
                    SUAUtils.Gui.getButtonStateData(new UV(0, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_other_disable"), Dimension.SQUARE_16, new Position(1, 1))
            ));

    static {
        Map<Integer, ToggleButton.StateData> equipmentRarityMap = new HashMap<>();
        Map<Integer, ToggleButton.StateData> gemRarityMap = new HashMap<>();
        equipmentRarityMap.put(0, SUAUtils.Gui.getButtonStateData(new UV(0, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment_all"), Dimension.SQUARE_16, new Position(1, 1)));
        gemRarityMap.put(0, SUAUtils.Gui.getButtonStateData(new UV(0, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("gem_all"), Dimension.SQUARE_16, new Position(1, 1)));
        equipmentRarityMap.put(1, SUAUtils.Gui.getButtonStateData(new UV(0, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment_disable"), Dimension.SQUARE_16, new Position(1, 1)));
        gemRarityMap.put(1, SUAUtils.Gui.getButtonStateData(new UV(0, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("gem_disable"), Dimension.SQUARE_16, new Position(1, 1)));
        for (int i = 0; i < RARITY_COUNT; ++i) {
            equipmentRarityMap.put(i + 2, SUAUtils.Gui.getButtonStateData(new UV(0, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment_rarity_" + i), Dimension.SQUARE_16, new Position(1, 1)));
            gemRarityMap.put(i + 2, SUAUtils.Gui.getButtonStateData(new UV(0, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("gem_rarity_" + i), Dimension.SQUARE_16, new Position(1, 1)));
        }

        EQUIPMENT_RARITY = ButtonDefinitions.createToggleButtonDefinition(equipmentRarityMap);
        GEM_RARITY = ButtonDefinitions.createToggleButtonDefinition(gemRarityMap);
    }

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
            addHideableChild(new ToggleButton<>(new Position(x + 21, y + 24), SALVAGING_OTHER,
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
            addHideableChild(new ToggleButton<>(new Position(x + 21, y + 24), EQUIPMENT_RARITY,
                    button -> getContainer().setEquipmentRarity((getContainer().shouldEquipmentRarity() + 1) % (RARITY_COUNT + 1)),
                    getContainer()::shouldEquipmentRarity));
            addHideableChild(new ToggleButton<>(new Position(x + 39, y + 24), GEM_RARITY,
                    button -> getContainer().setGemRarity((getContainer().shouldGemRarity() + 1) % (RARITY_COUNT + 1)),
                    getContainer()::shouldGemRarity));
            addHideableChild(new ToggleButton<>(new Position(x + 57, y + 24), SALVAGING_OTHER,
                    button -> getContainer().setSalvagingOther(!getContainer().shouldSalvagingOther()),
                    getContainer()::shouldSalvagingOther));
            filterLogicControl = addHideableChild(new FilterLogicControl.Advanced(screen, new Position(x + 3, y + 44), getContainer().getFilterLogicContainer(),
                    slotsPerRow));
        }
    }
}