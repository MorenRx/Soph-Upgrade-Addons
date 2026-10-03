package top.morenrx.sua.helper.client;

import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinition;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.UV;
import top.morenrx.sua.init.SUACompat;
import top.morenrx.sua.upgrades.salvaging.gui.OverlayToggleButton;
import top.morenrx.sua.util.SUAUtils;

import java.util.ArrayList;
import java.util.List;

public class SalvagingClientHelper {
    public static final List<OverlayToggleButton.StateData<Boolean>> EQUIPMENT_RARITY = new ArrayList<>();
    public static final List<OverlayToggleButton.StateData<Boolean>> GEM_RARITY = new ArrayList<>();
    public static final ButtonDefinition.Toggle<Boolean> SALVAGING_EQUIPMENT = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    SUAUtils.Gui.getButtonStateData(new UV(48, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_equipment_enable"), Dimension.SQUARE_16, new Position(1, 1)),
                    SUAUtils.Gui.getButtonStateData(new UV(64, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_equipment_disable"), Dimension.SQUARE_16, new Position(1, 1))
            ));
    public static final ButtonDefinition.Toggle<Boolean> SALVAGING_GEM = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    SUAUtils.Gui.getButtonStateData(new UV(0, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_gem_enable"), Dimension.SQUARE_16, new Position(1, 1)),
                    SUAUtils.Gui.getButtonStateData(new UV(16, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_gem_disable"), Dimension.SQUARE_16, new Position(1, 1))
            ));
    public static final ButtonDefinition.Toggle<Boolean> SALVAGING_OTHER = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    SUAUtils.Gui.getButtonStateData(new UV(96, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_other_enable"), Dimension.SQUARE_16, new Position(1, 1)),
                    SUAUtils.Gui.getButtonStateData(new UV(112, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("salvaging_other_disable"), Dimension.SQUARE_16, new Position(1, 1))
            ));

    static {
        if (SUACompat.APOTHEOSIS.getAsBoolean()) {
            for (LootRarity lootRarity : RarityRegistry.getSortedRarities()) {
                EQUIPMENT_RARITY.add(new OverlayToggleButton.StateData<>(
                        ButtonDefinitions.createToggleButtonDefinition(ButtonDefinitions.getBooleanStateData(
                                SUAUtils.Gui.getButtonStateData(new UV(48, 16),
                                        Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment_rarity_enable")).append(Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment"), Component.translatable("rarity." + RarityRegistry.INSTANCE.getKey(lootRarity)))).withStyle(Style.EMPTY.withColor(lootRarity.color())),
                                        Dimension.SQUARE_16, new Position(1, 1)),
                                SUAUtils.Gui.getButtonStateData(new UV(64, 16),
                                        Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment_rarity_disable")).append(Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("equipment"), Component.translatable("rarity." + RarityRegistry.INSTANCE.getKey(lootRarity)))).withStyle(Style.EMPTY.withColor(lootRarity.color())),
                                        Dimension.SQUARE_16, new Position(1, 1))
                        )),
                        SUAUtils.Gui.getTextureBlitData(new UV(80, 16), Dimension.SQUARE_16, new Position(1, 1)),
                        lootRarity.color()::getValue
                ));
            }
            for (Purity purity : Purity.values()) {
                GEM_RARITY.add(new OverlayToggleButton.StateData<>(
                        ButtonDefinitions.createToggleButtonDefinition(ButtonDefinitions.getBooleanStateData(
                                SUAUtils.Gui.getButtonStateData(new UV(0, 16),
                                        Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("gem_rarity_enable")).append(Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("gem_with_purity"), purity.toComponent())).withStyle(Style.EMPTY.withColor(purity.getColor())),
                                        Dimension.SQUARE_16, new Position(1, 1)),
                                SUAUtils.Gui.getButtonStateData(new UV(16, 16),
                                        Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("gem_rarity_disable")).append(Component.translatable(SBPTranslationHelper.INSTANCE.translUpgradeButton("gem_with_purity"), purity.toComponent())).withStyle(Style.EMPTY.withColor(purity.getColor())),
                                        Dimension.SQUARE_16, new Position(1, 1))
                        )),
                        SUAUtils.Gui.getTextureBlitData(new UV(32, 16), Dimension.SQUARE_16, new Position(1, 1)),
                        purity.getColor()::getValue
                ));
            }
        }
    }
}
