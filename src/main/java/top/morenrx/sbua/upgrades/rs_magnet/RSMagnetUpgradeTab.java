package top.morenrx.sbua.upgrades.rsmagnet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinition;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.*;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterControl;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterType;

public class RSMagnetUpgradeTab extends UpgradeSettingsTab<RSMagnetUpgradeContainer> {
    private static final ButtonDefinition.Toggle<Boolean> PICKUP_ITEMS = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    GuiHelper.getButtonStateData(new UV(128, 48), TranslationHelper.INSTANCE.translUpgradeButton("pickup_items"), Dimension.SQUARE_16, new Position(1, 1)),
                    GuiHelper.getButtonStateData(new UV(144, 48), TranslationHelper.INSTANCE.translUpgradeButton("do_not_pickup_items"), Dimension.SQUARE_16, new Position(1, 1))
            ));

    private static final ButtonDefinition.Toggle<Boolean> PICKUP_XP = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    GuiHelper.getButtonStateData(new UV(96, 48), Dimension.SQUARE_16, new Position(1, 1), Component.translatable(TranslationHelper.INSTANCE.translUpgradeButton("pickup_xp")), Component.translatable(TranslationHelper.INSTANCE.translUpgradeButton("pickup_xp.detail")).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC)),
                    GuiHelper.getButtonStateData(new UV(112, 48), TranslationHelper.INSTANCE.translUpgradeButton("do_not_pickup_xp"), Dimension.SQUARE_16, new Position(1, 1))
            ));

    protected ContentsFilterControl filterLogicControl;

    protected RSMagnetUpgradeTab(RSMagnetUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);

        addHideableChild(new ToggleButton<>(new Position(x + 3, y + 24), PICKUP_ITEMS,
                button -> getContainer().setPickupItems(!getContainer().shouldPickupItems()),
                () -> getContainer().shouldPickupItems()));
        addHideableChild(new ToggleButton<>(new Position(x + 21, y + 24), PICKUP_XP,
                button -> getContainer().setPickupXp(!getContainer().shouldPickupXp()),
                () -> getContainer().shouldPickupXp()));
    }

    @Override
    protected void moveSlotsToTab() {
        filterLogicControl.moveSlotsToView();
    }

    public static class Advanced extends RSMagnetUpgradeTab {
        public Advanced(RSMagnetUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow, ButtonDefinition.Toggle<ContentsFilterType> contentsFilterButton) {
            super(upgradeContainer, position, screen, Component.translatable("gui.soph_upgrade_addons.rs_magnet_upgrade"), Component.translatable("gui.soph_upgrade_addons.rs_magnet_upgrade.tooltip"));
            filterLogicControl = addHideableChild(new ContentsFilterControl.Advanced(screen, new Position(x + 3, y + 44), getContainer().getFilterLogicContainer(),
                    slotsPerRow, contentsFilterButton));
        }
    }
}