package top.morenrx.sua.init;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPButtonDefinitions;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeGuiManager;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import top.morenrx.sua.upgrades.drink.DrinkUpgradeContainer;
import top.morenrx.sua.upgrades.drink.DrinkUpgradeTab;
import top.morenrx.sua.upgrades.network_deposit.NetworkDepositUpgradeTab;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgradeContainer;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgradeTab;
import top.morenrx.sua.upgrades.network_pickup.NetworkPickupUpgradeContainer;
import top.morenrx.sua.upgrades.network_pickup.NetworkPickupUpgradeTab;
import top.morenrx.sua.upgrades.network_restock.NetworkRestockUpgradeTab;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgradeContainer;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgradeTab;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeContainer;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeTab;
import top.morenrx.sua.upgrades.voiding.SuperVoidUpgradeTab;

public class SUAClient {

    public static void init(IEventBus modBus) {
        modBus.addListener(SUAClient::onMenuScreensRegister);
    }

    private static void onMenuScreensRegister(RegisterMenuScreensEvent event) {
        UpgradeGuiManager.registerTab(SUAItems.BASIC_NETWORK_MAGNET_TYPE, (NetworkMagnetUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new NetworkMagnetUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.networkMagnetUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
        UpgradeGuiManager.registerTab(SUAItems.BASIC_NETWORK_PICKUP_TYPE, (NetworkPickupUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new NetworkPickupUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.networkPickupUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
        UpgradeGuiManager.registerTab(SUAItems.BASIC_NETWORK_DEPOSIT_TYPE, NetworkDepositUpgradeTab.Basic::new);
        UpgradeGuiManager.registerTab(SUAItems.BASIC_NETWORK_RESTOCK_TYPE, NetworkRestockUpgradeTab.Basic::new);
        UpgradeGuiManager.registerTab(SUAItems.SUPER_VOID_TYPE, (VoidUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new SuperVoidUpgradeTab(container, position, base, SUAConfig.INSTANCE.superVoidUpgrade.slotsInRow.get()));
        UpgradeGuiManager.registerTab(SUAItems.BASIC_DRINK_TYPE, (DrinkUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new DrinkUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.drinkUpgrade.slotsInRow.get()));
        UpgradeGuiManager.registerTab(SUAItems.ADVANCED_DRINK_TYPE, (DrinkUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new DrinkUpgradeTab.Advanced(container, position, base, SUAConfig.INSTANCE.advancedDrinkUpgrade.slotsInRow.get()));
        UpgradeGuiManager.registerTab(SUAItems.BASIC_POTION_CHARM_TYPE, (PotionCharmUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new PotionCharmUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.potionCharmUpgradeConfig.slotsInRow.get()));
        UpgradeGuiManager.registerTab(SUAItems.ADVANCED_POTION_CHARM_TYPE, (PotionCharmUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new PotionCharmUpgradeTab.Advanced(container, position, base, SUAConfig.INSTANCE.advancedPotionCharmUpgradeConfig.slotsInRow.get()));
        UpgradeGuiManager.registerTab(SUAItems.BASIC_SALVAGING_UPGRADE_TYPE, (SalvagingUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new SalvagingUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.salvagingUpgradeConfig.slotsInRow.get()));
        UpgradeGuiManager.registerTab(SUAItems.ADVANCED_SALVAGING_UPGRADE_TYPE, (SalvagingUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new SalvagingUpgradeTab.Advanced(container, position, base, SUAConfig.INSTANCE.advancedSalvagingUpgradeConfig.slotsInRow.get()));
    }
}
