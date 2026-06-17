package top.morenrx.sua.upgrades.drink;

import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinition;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.UV;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicControl;
import top.morenrx.sua.util.SUAUtils;

import java.util.Map;

public class DrinkUpgradeTab extends UpgradeSettingsTab<DrinkUpgradeContainer> {
    private static final int THIRST_LEVEL_SIZE = 3;
    public static final ButtonDefinition.Toggle<Integer> THIRST_LEVEL = ButtonDefinitions.createToggleButtonDefinition(
            Map.of(
                    DrinkUpgrade.Data.THIRST_LEVEL_ANY, SUAUtils.Gui.getButtonStateData(new UV(64, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("thirst_level_any"), Dimension.SQUARE_16, new Position(1, 1)),
                    DrinkUpgrade.Data.THIRST_LEVEL_HALF, SUAUtils.Gui.getButtonStateData(new UV(48, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("thirst_level_half"), Dimension.SQUARE_16, new Position(1, 1)),
                    DrinkUpgrade.Data.THIRST_LEVEL_FULL, SUAUtils.Gui.getButtonStateData(new UV(32, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("thirst_level_full"), Dimension.SQUARE_16, new Position(1, 1))
            ));

    public static final ButtonDefinition.Toggle<Boolean> DRINK_FOR_HURT = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    GuiHelper.getButtonStateData(new UV(96, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("drink_for_hurt"), Dimension.SQUARE_16, new Position(1, 1)),
                    GuiHelper.getButtonStateData(new UV(112, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("drink_ignore_hurt"), Dimension.SQUARE_16, new Position(1, 1))
            ));
    private static final int PURITY_SIZE = 4;
    public static final ButtonDefinition.Toggle<Integer> PURITY = ButtonDefinitions.createToggleButtonDefinition(
            Map.of(
                    DrinkUpgrade.Data.PURITY_DIRTY, SUAUtils.Gui.getButtonStateData(new UV(128, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("purity_dirty"), Dimension.SQUARE_16, new Position(1, 1)),
                    DrinkUpgrade.Data.PURITY_SLIGHTLY_DIRTY, SUAUtils.Gui.getButtonStateData(new UV(112, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("purity_slightly_dirst"), Dimension.SQUARE_16, new Position(1, 1)),
                    DrinkUpgrade.Data.PURITY_ACCEPTABLE, SUAUtils.Gui.getButtonStateData(new UV(96, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("purity_acceptable"), Dimension.SQUARE_16, new Position(1, 1)),
                    DrinkUpgrade.Data.PURITY_PURIFIED, SUAUtils.Gui.getButtonStateData(new UV(80, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("purity_purified"), Dimension.SQUARE_16, new Position(1, 1))
            ));


    protected FilterLogicControl<FilterLogic, FilterLogicContainer<FilterLogic>> filterLogicControl;

    protected DrinkUpgradeTab(DrinkUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);
    }

    @Override
    protected void moveSlotsToTab() {
        filterLogicControl.moveSlotsToView();
    }

    public static class Basic extends DrinkUpgradeTab {
        public Basic(DrinkUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("drink"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("drink"));
            filterLogicControl = addHideableChild(new FilterLogicControl.Basic(screen, new Position(x + 3, y + 24), getContainer().getFilterLogicContainer(),
                    slotsPerRow));
        }
    }

    public static class Advanced extends DrinkUpgradeTab {
        public Advanced(DrinkUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("advanced_drink"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("advanced_drink"));
            addHideableChild(new ToggleButton<>(new Position(x + 3, y + 24), THIRST_LEVEL,
                    button -> getContainer().setDrinkAtThirstLevel((getContainer().getDrinkAtThirstLevel() + 1) % THIRST_LEVEL_SIZE),
                    () -> getContainer().getDrinkAtThirstLevel()));
            addHideableChild(new ToggleButton<>(new Position(x + 21, y + 24), DRINK_FOR_HURT,
                    button -> getContainer().setDrinkForHurt(!getContainer().shouldDrinkForHurt()),
                    () -> getContainer().shouldDrinkForHurt()));
            addHideableChild(new ToggleButton<>(new Position(x + 39, y + 24), PURITY,
                    button -> getContainer().setPurity((getContainer().shouldPurity() + 1) % PURITY_SIZE),
                    () -> getContainer().shouldPurity()));

            filterLogicControl = addHideableChild(new FilterLogicControl.Advanced(screen, new Position(x + 3, y + 44), getContainer().getFilterLogicContainer(),
                    slotsPerRow));
        }
    }
}