package top.morenrx.sua.helper.client;

import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinition;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.UV;
import top.morenrx.sua.init.SUACompat;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;
import top.morenrx.sua.util.SUAUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class NetworkStorageClientHelper {
    private static ButtonDefinition.Toggle<String> NETWORK_TYPE_BUTTON = null;
    private static Map<String, ToggleButton.StateData> BUTTON_STATES = null;

    public static Map<String, ToggleButton.StateData> getButtonStates() {
        if (BUTTON_STATES == null) {
            BUTTON_STATES = new LinkedHashMap<>();

            BUTTON_STATES.put(
                    NetworkStorageProvider.Data.BACKPACK,
                    GuiHelper.getButtonStateData(new UV(80, 16), SBPTranslationHelper.INSTANCE.translUpgradeButton("use_backpack"), Dimension.SQUARE_16, new Position(1, 1))
            );

            if (SUACompat.REFINED_STORAGE.getAsBoolean()) {
                BUTTON_STATES.put(
                        NetworkStorageProvider.Data.RS,
                        SUAUtils.Gui.getButtonStateData(new UV(144, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("use_rs"), Dimension.SQUARE_16, new Position(1, 1))
                );
            }
            if (SUACompat.APPLIED_ENERGISTICS.getAsBoolean()) {
                BUTTON_STATES.put(
                        NetworkStorageProvider.Data.AE,
                        SUAUtils.Gui.getButtonStateData(new UV(160, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("use_ae"), Dimension.SQUARE_16, new Position(1, 1))
                );
            }
            if (SUACompat.TOMS_STORAGE.getAsBoolean()) {
                BUTTON_STATES.put(
                        NetworkStorageProvider.Data.TOM,
                        SUAUtils.Gui.getButtonStateData(new UV(176, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("use_tom"), Dimension.SQUARE_16, new Position(1, 1))
                );
            }
            if (SUACompat.BEYOND_DIMENSIONS.getAsBoolean()) {
                BUTTON_STATES.put(
                        NetworkStorageProvider.Data.BD,
                        SUAUtils.Gui.getButtonStateData(new UV(192, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("use_bd"), Dimension.SQUARE_16, new Position(1, 1))
                );
            }
        }
        return BUTTON_STATES;
    }

    public static ButtonDefinition.Toggle<String> getNetworkTypeButtonDefinition() {
        if (NETWORK_TYPE_BUTTON == null) {
            NETWORK_TYPE_BUTTON = ButtonDefinitions.createToggleButtonDefinition(getButtonStates());
        }
        return NETWORK_TYPE_BUTTON;
    }
}
