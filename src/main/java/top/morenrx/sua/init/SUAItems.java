package top.morenrx.sua.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilteredUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeWrapper;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.upgrades.drink.DrinkUpgrade;
import top.morenrx.sua.upgrades.drink.DrinkUpgradeContainer;
import top.morenrx.sua.upgrades.drink.DrinkUpgradeWrapper;
import top.morenrx.sua.upgrades.ender_chest.EnderChestUpgrade;
import top.morenrx.sua.upgrades.network_deposit.NetworkDepositUpgrade;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgrade;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgradeContainer;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgradeWrapper;
import top.morenrx.sua.upgrades.network_pickup.NetworkPickupUpgrade;
import top.morenrx.sua.upgrades.network_pickup.NetworkPickupUpgradeContainer;
import top.morenrx.sua.upgrades.network_pickup.NetworkPickupUpgradeWrapper;
import top.morenrx.sua.upgrades.network_restock.NetworkRestockUpgrade;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgrade;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgradeContainer;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgradeWrapper;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgrade;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeContainer;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeWrapper;
import top.morenrx.sua.upgrades.voiding.SuperVoidUpgrade;

public class SUAItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SophUpgradeAddons.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SophUpgradeAddons.MODID);

    public static final DeferredItem<Item> MOD_ICON = ITEMS.register("mod_icon", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NETWORK_MAGNET_UPGRADE = ITEMS.register("network_magnet_upgrade", () -> new NetworkMagnetUpgrade(
            SUAConfig.INSTANCE.networkMagnetUpgrade.enable::get,
            SUAConfig.INSTANCE.networkMagnetUpgrade.filterSlots::get,
            SUAConfig.INSTANCE.networkMagnetUpgrade.magnetRange::get));
    public static final DeferredItem<Item> NETWORK_PICKUP_UPGRADE = ITEMS.register("network_pickup_upgrade", () -> new NetworkPickupUpgrade(
            SUAConfig.INSTANCE.networkPickupUpgrade.enable::get,
            SUAConfig.INSTANCE.networkPickupUpgrade.filterSlots::get));
    public static final DeferredItem<Item> NETWORK_DEPOSIT_UPGRADE = ITEMS.register("network_deposit_upgrade", () -> new NetworkDepositUpgrade(
            SUAConfig.INSTANCE.networkDepositUpgrade.enable::get,
            SUAConfig.INSTANCE.networkDepositUpgrade.filterSlots::get));
    public static final DeferredItem<Item> NETWORK_RESTOCK_UPGRADE = ITEMS.register("network_restock_upgrade", () -> new NetworkRestockUpgrade(
            SUAConfig.INSTANCE.networkRestockUpgrade.enable::get,
            SUAConfig.INSTANCE.networkRestockUpgrade.filterSlots::get));
    public static final DeferredItem<Item> SUPER_VOID_UPGRADE = ITEMS.register("super_void_upgrade", () -> new SuperVoidUpgrade(
            SUAConfig.INSTANCE.superVoidUpgrade.enable::get));
    public static final DeferredItem<Item> ENDER_CHEST_UPGRADE = ITEMS.register("ender_chest_upgrade", () -> new EnderChestUpgrade(
            SUAConfig.INSTANCE.endChestUpgrade.enable::get));
    public static final DeferredItem<Item> POTION_CHARM_UPGRADE = ITEMS.register("potion_charm_upgrade", () -> new PotionCharmUpgrade(
            SUAConfig.INSTANCE.potionCharmUpgradeConfig.enable::get,
            SUAConfig.INSTANCE.potionCharmUpgradeConfig.slots::get));
    public static final DeferredItem<Item> ADVANCED_POTION_CHARM_UPGRADE = ITEMS.register("advanced_potion_charm_upgrade", () -> new PotionCharmUpgrade(
            SUAConfig.INSTANCE.advancedPotionCharmUpgradeConfig.enable::get,
            SUAConfig.INSTANCE.advancedPotionCharmUpgradeConfig.slots::get));
    public static final DeferredItem<Item> DRINK_UPGRADE = ITEMS.register("drink_upgrade", () -> new DrinkUpgrade(
            SUAConfig.INSTANCE.drinkUpgrade.enable::get,
            SUAConfig.INSTANCE.drinkUpgrade.filterSlots::get));
    public static final DeferredItem<Item> ADVANCED_DRINK_UPGRADE = ITEMS.register("advanced_drink_upgrade", () -> new DrinkUpgrade(
            SUAConfig.INSTANCE.advancedDrinkUpgrade.enable::get,
            SUAConfig.INSTANCE.advancedDrinkUpgrade.filterSlots::get));
    public static final DeferredItem<Item> SALVAGING_UPGRADE = ITEMS.register("salvaging_upgrade", () -> new SalvagingUpgrade(
            SUAConfig.INSTANCE.salvagingUpgradeConfig.enable::get,
            SUAConfig.INSTANCE.salvagingUpgradeConfig.filterSlots::get));
    public static final DeferredItem<Item> ADVANCED_SALVAGING_UPGRADE = ITEMS.register("advanced_salvaging_upgrade", () -> new SalvagingUpgrade(
            SUAConfig.INSTANCE.advancedSalvagingUpgradeConfig.enable::get,
            SUAConfig.INSTANCE.advancedSalvagingUpgradeConfig.filterSlots::get));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("main", () ->
            CreativeModeTab.builder()
                    .icon(() -> MOD_ICON.get().getDefaultInstance())
                    .title(Component.translatable("tab.soph_upgrade_addons.main"))
                    .displayItems((parameters, output) -> {
                        ITEMS.getEntries().forEach(item -> {
                            if (item != MOD_ICON) output.accept(item.get());
                        });
                    })
                    .build()
    );

    public static final UpgradeContainerType<NetworkMagnetUpgradeWrapper, NetworkMagnetUpgradeContainer> BASIC_NETWORK_MAGNET_TYPE = new UpgradeContainerType<>(NetworkMagnetUpgradeContainer::new);
    public static final UpgradeContainerType<NetworkPickupUpgradeWrapper, NetworkPickupUpgradeContainer> BASIC_NETWORK_PICKUP_TYPE = new UpgradeContainerType<>(NetworkPickupUpgradeContainer::new);
    public static final UpgradeContainerType<DepositUpgradeWrapper, DepositUpgradeContainer> BASIC_NETWORK_DEPOSIT_TYPE = new UpgradeContainerType<>(DepositUpgradeContainer::new);
    public static final UpgradeContainerType<RestockUpgradeWrapper, ContentsFilteredUpgradeContainer<RestockUpgradeWrapper>> BASIC_NETWORK_RESTOCK_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
    public static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> SUPER_VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
    public static final UpgradeContainerType<DrinkUpgradeWrapper, DrinkUpgradeContainer> BASIC_DRINK_TYPE = new UpgradeContainerType<>(DrinkUpgradeContainer::new);
    public static final UpgradeContainerType<DrinkUpgradeWrapper, DrinkUpgradeContainer> ADVANCED_DRINK_TYPE = new UpgradeContainerType<>(DrinkUpgradeContainer::new);
    public static final UpgradeContainerType<PotionCharmUpgradeWrapper, PotionCharmUpgradeContainer> BASIC_POTION_CHARM_TYPE = new UpgradeContainerType<>(PotionCharmUpgradeContainer::new);
    public static final UpgradeContainerType<PotionCharmUpgradeWrapper, PotionCharmUpgradeContainer> ADVANCED_POTION_CHARM_TYPE = new UpgradeContainerType<>(PotionCharmUpgradeContainer::new);
    public static final UpgradeContainerType<SalvagingUpgradeWrapper, SalvagingUpgradeContainer> BASIC_SALVAGING_UPGRADE_TYPE = new UpgradeContainerType<>(SalvagingUpgradeContainer::new);
    public static final UpgradeContainerType<SalvagingUpgradeWrapper, SalvagingUpgradeContainer> ADVANCED_SALVAGING_UPGRADE_TYPE = new UpgradeContainerType<>(SalvagingUpgradeContainer::new);

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(SUAItems::registerContainers);
        EnderChestUpgrade.init();
    }

    private static void registerContainers(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.MENU)) return;
        UpgradeContainerRegistry.register(NETWORK_MAGNET_UPGRADE.getId(), BASIC_NETWORK_MAGNET_TYPE);
        UpgradeContainerRegistry.register(NETWORK_PICKUP_UPGRADE.getId(), BASIC_NETWORK_PICKUP_TYPE);
        UpgradeContainerRegistry.register(NETWORK_DEPOSIT_UPGRADE.getId(), BASIC_NETWORK_DEPOSIT_TYPE);
        UpgradeContainerRegistry.register(NETWORK_RESTOCK_UPGRADE.getId(), BASIC_NETWORK_RESTOCK_TYPE);
        UpgradeContainerRegistry.register(SUPER_VOID_UPGRADE.getId(), SUPER_VOID_TYPE);
        UpgradeContainerRegistry.register(DRINK_UPGRADE.getId(), BASIC_DRINK_TYPE);
        UpgradeContainerRegistry.register(ADVANCED_DRINK_UPGRADE.getId(), ADVANCED_DRINK_TYPE);
        UpgradeContainerRegistry.register(POTION_CHARM_UPGRADE.getId(), BASIC_POTION_CHARM_TYPE);
        UpgradeContainerRegistry.register(ADVANCED_POTION_CHARM_UPGRADE.getId(), ADVANCED_POTION_CHARM_TYPE);
        UpgradeContainerRegistry.register(SALVAGING_UPGRADE.getId(), BASIC_SALVAGING_UPGRADE_TYPE);
        UpgradeContainerRegistry.register(ADVANCED_SALVAGING_UPGRADE.getId(), ADVANCED_SALVAGING_UPGRADE_TYPE);
    }
}
