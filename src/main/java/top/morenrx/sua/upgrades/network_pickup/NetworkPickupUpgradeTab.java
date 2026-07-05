package top.morenrx.sua.upgrades.network_pickup;

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
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterControl;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterType;
import top.morenrx.sua.init.SUACompat;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;
import top.morenrx.sua.util.SUAUtils;

import java.util.HashMap;
import java.util.Map;

public class NetworkPickupUpgradeTab extends UpgradeSettingsTab<NetworkPickupUpgradeContainer> {

    private static final ButtonDefinition.Toggle<Boolean> ENABLE_VOID = ButtonDefinitions.createToggleButtonDefinition(
            ButtonDefinitions.getBooleanStateData(
                    SUAUtils.Gui.getButtonStateData(new UV(0, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("enable_void"), Dimension.SQUARE_16, new Position(1, 1)),
                    SUAUtils.Gui.getButtonStateData(new UV(16, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("do_not_enable_void"), Dimension.SQUARE_16, new Position(1, 1))
            ));

    private static final Map<String, ToggleButton.StateData> NETWORK_TYPE_BUTTON_STATES = new HashMap<>();

    static {
        if (SUACompat.REFINED_STORAGE.getAsBoolean()) {
            NETWORK_TYPE_BUTTON_STATES.put(
                    NetworkStorageProvider.Type.RS,
                    SUAUtils.Gui.getButtonStateData(new UV(144, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("use_rs"), Dimension.SQUARE_16, new Position(1, 1)));

        }
        if (SUACompat.APPLIED_ENERGISTICS.getAsBoolean()) {
            NETWORK_TYPE_BUTTON_STATES.put(
                    NetworkStorageProvider.Type.AE,
                    SUAUtils.Gui.getButtonStateData(new UV(160, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("use_ae"), Dimension.SQUARE_16, new Position(1, 1)));

        }
        if (SUACompat.TOMS_STORAGE.getAsBoolean()) {
            NETWORK_TYPE_BUTTON_STATES.put(
                    NetworkStorageProvider.Type.TOM,
                    SUAUtils.Gui.getButtonStateData(new UV(176, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("use_tom"), Dimension.SQUARE_16, new Position(1, 1)));

        }
        if (SUACompat.BEYOND_DIMENSIONS.getAsBoolean()) {
            NETWORK_TYPE_BUTTON_STATES.put(
                    NetworkStorageProvider.Type.BD,
                    SUAUtils.Gui.getButtonStateData(new UV(192, 0), SBPTranslationHelper.INSTANCE.translUpgradeButton("use_bd"), Dimension.SQUARE_16, new Position(1, 1)));

        }
    }

    private static final ButtonDefinition.Toggle<String> NETWORK_TYPE = ButtonDefinitions.createToggleButtonDefinition(NETWORK_TYPE_BUTTON_STATES);


    protected ContentsFilterControl filterLogicControl;


    protected NetworkPickupUpgradeTab(NetworkPickupUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);

        addHideableChild(new ToggleButton<>(new Position(x + 3, y + 24), ENABLE_VOID,
                button -> getContainer().setEnableVoid(!getContainer().shouldEnableVoid()),
                () -> getContainer().shouldEnableVoid()));

        if (NETWORK_TYPE_BUTTON_STATES.size() > 1)
            addHideableChild(new ToggleButton<>(new Position(x + 21, y + 24), NETWORK_TYPE,
                    button -> getContainer().setNetworkType(NetworkStorageProvider.get().nextNetworkType(getContainer().shouldNetworkType())),
                    () -> getContainer().shouldNetworkType()));
    }

    @Override
    protected void moveSlotsToTab() {
        filterLogicControl.moveSlotsToView();
    }

    public static class Basic extends NetworkPickupUpgradeTab {
        public Basic(NetworkPickupUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow, ButtonDefinition.Toggle<ContentsFilterType> contentsFilterButton) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("network_pickup"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("network_pickup"));
            filterLogicControl = addHideableChild(new ContentsFilterControl.Advanced(screen, new Position(x + 3, y + 44), getContainer().getFilterLogicContainer(),
                    slotsPerRow, contentsFilterButton));
        }
    }
}