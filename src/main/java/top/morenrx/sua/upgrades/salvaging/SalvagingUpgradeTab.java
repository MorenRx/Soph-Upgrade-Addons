package top.morenrx.sua.upgrades.salvaging;

import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import top.morenrx.sua.init.SUACompat;
import top.morenrx.sua.upgrades.salvaging.gui.OverlayToggleButton;
import top.morenrx.sua.util.SUAUtils;

import java.util.ArrayList;
import java.util.List;

public class SalvagingUpgradeTab extends UpgradeSettingsTab<SalvagingUpgradeContainer> {
    private static final List<OverlayToggleButton.StateData<Boolean>> EQUIPMENT_RARITY = new ArrayList<>();
    private static final List<OverlayToggleButton.StateData<Boolean>> GEM_RARITY = new ArrayList<>();
    private static final ButtonDefinition.Toggle<Boolean> SALVAGING_EQUIPMENT = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    SUAUtils.Gui.getButtonStateData(new UV(48, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_equipment_enable"), Dimension.SQUARE_16, new Position(1, 1)),
                    SUAUtils.Gui.getButtonStateData(new UV(64, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_equipment_disable"), Dimension.SQUARE_16, new Position(1, 1))
            ));
    private static final ButtonDefinition.Toggle<Boolean> SALVAGING_GEM = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    SUAUtils.Gui.getButtonStateData(new UV(0, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_gem_enable"), Dimension.SQUARE_16, new Position(1, 1)),
                    SUAUtils.Gui.getButtonStateData(new UV(16, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_gem_disable"), Dimension.SQUARE_16, new Position(1, 1))
            ));
    private static final ButtonDefinition.Toggle<Boolean> SALVAGING_OTHER = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    SUAUtils.Gui.getButtonStateData(new UV(96, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_other_enable"), Dimension.SQUARE_16, new Position(1, 1)),
                    SUAUtils.Gui.getButtonStateData(new UV(112, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_other_disable"), Dimension.SQUARE_16, new Position(1, 1))
            ));

    static {
        if (SUACompat.APOTHEOSIS.getAsBoolean()) {
            for (DynamicHolder<LootRarity> orderedRarity : RarityRegistry.INSTANCE.getOrderedRarities()) {
                LootRarity lootRarity = orderedRarity.get();
                EQUIPMENT_RARITY.add(new OverlayToggleButton.StateData<>(
                        ButtonDefinitions.createToggleButtonDefinition(ButtonDefinitions.getBooleanStateData(
                                SUAUtils.Gui.getButtonStateData(new UV(48, 16),
                                        Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment_rarity_enable")).append(Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment"), Component.translatable("rarity." + RarityRegistry.INSTANCE.getKey(lootRarity)))).withStyle(Style.EMPTY.withColor(lootRarity.getColor())),
                                        Dimension.SQUARE_16, new Position(1, 1)),
                                SUAUtils.Gui.getButtonStateData(new UV(64, 16),
                                        Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment_rarity_disable")).append(Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment"), Component.translatable("rarity." + RarityRegistry.INSTANCE.getKey(lootRarity)))).withStyle(Style.EMPTY.withColor(lootRarity.getColor())),
                                        Dimension.SQUARE_16, new Position(1, 1))
                        )),
                        SUAUtils.Gui.getTextureBlitData(new UV(80, 16), Dimension.SQUARE_16, new Position(1, 1)),
                        lootRarity.getColor()::getValue
                ));
                GEM_RARITY.add(new OverlayToggleButton.StateData<>(
                        ButtonDefinitions.createToggleButtonDefinition(ButtonDefinitions.getBooleanStateData(
                                SUAUtils.Gui.getButtonStateData(new UV(0, 16),
                                        Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("gem_rarity_enable")).append(Component.translatable("item.apotheosis.gem." + RarityRegistry.INSTANCE.getKey(lootRarity), Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("gem")))).withStyle(Style.EMPTY.withColor(lootRarity.getColor())),
                                        Dimension.SQUARE_16, new Position(1, 1)),
                                SUAUtils.Gui.getButtonStateData(new UV(16, 16),
                                        Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("gem_rarity_disable")).append(Component.translatable("item.apotheosis.gem." + RarityRegistry.INSTANCE.getKey(lootRarity), Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("gem")))).withStyle(Style.EMPTY.withColor(lootRarity.getColor())),
                                        Dimension.SQUARE_16, new Position(1, 1))
                        )),
                        SUAUtils.Gui.getTextureBlitData(new UV(32, 16), Dimension.SQUARE_16, new Position(1, 1)),
                        lootRarity.getColor()::getValue
                ));
            }
        }
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
            addHideableChild(new ToggleButton<>(new Position(x + 21, y + 24), SALVAGING_EQUIPMENT,
                    button -> getContainer().setSalvagingEquipment(!getContainer().shouldSalvagingEquipment()),
                    getContainer()::shouldSalvagingEquipment));
            addHideableChild(new ToggleButton<>(new Position(x + 39, y + 24), SALVAGING_GEM,
                    button -> getContainer().setSalvagingGem(!getContainer().shouldSalvagingGem()),
                    getContainer()::shouldSalvagingGem));
            addHideableChild(new ToggleButton<>(new Position(x + 57, y + 24), SALVAGING_OTHER,
                    button -> getContainer().setSalvagingOther(!getContainer().shouldSalvagingOther()),
                    getContainer()::shouldSalvagingOther));
            int rarityFilterX = slotsPerRow * 18 + 2;
            for (int i = 0; i < EQUIPMENT_RARITY.size(); i++) {
                int mask = 1 << i;
                addHideableChild(new OverlayToggleButton<>(new Position(x + 3 + rarityFilterX, y + 24 + (i * 18)), EQUIPMENT_RARITY.get(i),
                        button -> getContainer().setEquipmentRarityMask(getContainer().shouldEquipmentRarityMask() ^ mask),
                        () -> getContainer().shouldSalvagingEquipment() && (getContainer().shouldEquipmentRarityMask() & mask) != 0));
                addHideableChild(new OverlayToggleButton<>(new Position(x + 3 + rarityFilterX + 18, y + 24 + (i * 18)), GEM_RARITY.get(i),
                        button -> getContainer().setGemRarityMask(getContainer().shouldGemRarityMask() ^ mask),
                        () -> getContainer().shouldSalvagingGem() && (getContainer().shouldGemRarityMask() & mask) != 0));
            }

            filterLogicControl = addHideableChild(new FilterLogicControl.Advanced(screen, new Position(x + 3, y + 44), getContainer().getFilterLogicContainer(),
                    slotsPerRow));
        }
    }
}