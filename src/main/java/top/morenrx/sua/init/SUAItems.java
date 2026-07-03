package top.morenrx.sua.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPButtonDefinitions;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeGuiManager;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilteredUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeWrapper;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.upgrades.drink.DrinkUpgrade;
import top.morenrx.sua.upgrades.drink.DrinkUpgradeContainer;
import top.morenrx.sua.upgrades.drink.DrinkUpgradeTab;
import top.morenrx.sua.upgrades.drink.DrinkUpgradeWrapper;
import top.morenrx.sua.upgrades.ender_chest.EnderChestUpgrade;
import top.morenrx.sua.upgrades.network_deposit.NetworkDepositUpgrade;
import top.morenrx.sua.upgrades.network_deposit.NetworkDepositUpgradeTab;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgrade;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgradeContainer;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgradeTab;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgradeWrapper;
import top.morenrx.sua.upgrades.network_pickup.NetworkPickupUpgrade;
import top.morenrx.sua.upgrades.network_pickup.NetworkPickupUpgradeTab;
import top.morenrx.sua.upgrades.network_pickup.NetworkPickupUpgradeWrapper;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgrade;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgradeContainer;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgradeTab;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgradeWrapper;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgrade;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeContainer;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeTab;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeWrapper;
import top.morenrx.sua.upgrades.voiding.SuperVoidUpgrade;
import top.morenrx.sua.upgrades.voiding.SuperVoidUpgradeTab;


public class SUAItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SophUpgradeAddons.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SophUpgradeAddons.MODID);


    public static final RegistryObject<Item> MOD_ICON = ITEMS.register("mod_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NETWORK_MAGNET_UPGRADE = ITEMS.register("network_magnet_upgrade", () -> new NetworkMagnetUpgrade(
            SUAConfig.INSTANCE.networkMagnetUpgrade.enable::get,
            SUAConfig.INSTANCE.networkMagnetUpgrade.filterSlots::get,
            SUAConfig.INSTANCE.networkMagnetUpgrade.magnetRange::get));
    public static final RegistryObject<Item> NETWORK_PICKUP_UPGRADE = ITEMS.register("network_pickup_upgrade", () -> new NetworkPickupUpgrade(
            SUAConfig.INSTANCE.networkPickupUpgrade.enable::get,
            SUAConfig.INSTANCE.networkPickupUpgrade.filterSlots::get));
    public static final RegistryObject<Item> NETWORK_DEPOSIT_UPGRADE = ITEMS.register("network_deposit_upgrade", () -> new NetworkDepositUpgrade(
            SUAConfig.INSTANCE.networkDepositUpgrade.enable::get,
            SUAConfig.INSTANCE.networkDepositUpgrade.filterSlots::get));
    public static final RegistryObject<Item> SUPER_VOID_UPGRADE = ITEMS.register("super_void_upgrade", () -> new SuperVoidUpgrade(
            SUAConfig.INSTANCE.superVoidUpgrade.enable::get));
    public static final RegistryObject<Item> ENDER_CHEST_UPGRADE = ITEMS.register("ender_chest_upgrade", () -> new EnderChestUpgrade(
            SUAConfig.INSTANCE.endChestUpgrade.enable::get));
    public static final RegistryObject<Item> POTION_CHARM_UPGRADE = ITEMS.register("potion_charm_upgrade", () -> new PotionCharmUpgrade(
            SUAConfig.INSTANCE.potionCharmUpgradeConfig.enable::get,
            SUAConfig.INSTANCE.potionCharmUpgradeConfig.slots::get));
    public static final RegistryObject<Item> ADVANCED_POTION_CHARM_UPGRADE = ITEMS.register("advanced_potion_charm_upgrade", () -> new PotionCharmUpgrade(
            SUAConfig.INSTANCE.advancedPotionCharmUpgradeConfig.enable::get,
            SUAConfig.INSTANCE.advancedPotionCharmUpgradeConfig.slots::get));
    public static final RegistryObject<Item> DRINK_UPGRADE = ITEMS.register("drink_upgrade", () -> new DrinkUpgrade(
            SUAConfig.INSTANCE.drinkUpgrade.enable::get,
            SUAConfig.INSTANCE.drinkUpgrade.filterSlots::get));
    public static final RegistryObject<Item> ADVANCED_DRINK_UPGRADE = ITEMS.register("advanced_drink_upgrade", () -> new DrinkUpgrade(
            SUAConfig.INSTANCE.advancedDrinkUpgrade.enable::get,
            SUAConfig.INSTANCE.advancedDrinkUpgrade.filterSlots::get));
    public static final RegistryObject<Item> SALVAGING_UPGRADE = ITEMS.register("salvaging_upgrade", () -> new SalvagingUpgrade(
            SUAConfig.INSTANCE.salvagingUpgradeConfig.enable::get,
            SUAConfig.INSTANCE.salvagingUpgradeConfig.filterSlots::get));
    public static final RegistryObject<Item> ADVANCED_SALVAGING_UPGRADE = ITEMS.register("advanced_salvaging_upgrade", () -> new SalvagingUpgrade(
            SUAConfig.INSTANCE.advancedSalvagingUpgradeConfig.enable::get,
            SUAConfig.INSTANCE.advancedSalvagingUpgradeConfig.filterSlots::get));

    public static final RegistryObject<Item> RS_MAGNET_UPGRADE = ITEMS.register("rs_magnet_upgrade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RS_PICKUP_UPGRADE = ITEMS.register("rs_pickup_upgrade", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RS_DEPOSIT_UPGRADE = ITEMS.register("rs_deposit_upgrade", () -> new Item(new Item.Properties()));


    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("main", () ->
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

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(SUAItems::registerContainers);
        EnderChestUpgrade.init();
    }

    private static final UpgradeContainerType<NetworkMagnetUpgradeWrapper, NetworkMagnetUpgradeContainer> BASIC_NETWORK_MAGNET_TYPE = new UpgradeContainerType<>(NetworkMagnetUpgradeContainer::new);
    private static final UpgradeContainerType<NetworkPickupUpgradeWrapper, ContentsFilteredUpgradeContainer<NetworkPickupUpgradeWrapper>> BASIC_NETWORK_PICKUP_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
    private static final UpgradeContainerType<DepositUpgradeWrapper, DepositUpgradeContainer> BASIC_NETWORK_DEPOSIT_TYPE = new UpgradeContainerType<>(DepositUpgradeContainer::new);
    private static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> SUPER_VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
    private static final UpgradeContainerType<DrinkUpgradeWrapper, DrinkUpgradeContainer> BASIC_DRINK_TYPE = new UpgradeContainerType<>(DrinkUpgradeContainer::new);
    private static final UpgradeContainerType<DrinkUpgradeWrapper, DrinkUpgradeContainer> ADVANCED_DRINK_TYPE = new UpgradeContainerType<>(DrinkUpgradeContainer::new);
    private static final UpgradeContainerType<PotionCharmUpgradeWrapper, PotionCharmUpgradeContainer> BASIC_POTION_CHARM_TYPE = new UpgradeContainerType<>(PotionCharmUpgradeContainer::new);
    private static final UpgradeContainerType<PotionCharmUpgradeWrapper, PotionCharmUpgradeContainer> ADVANCED_POTION_CHARM_TYPE = new UpgradeContainerType<>(PotionCharmUpgradeContainer::new);
    private static final UpgradeContainerType<SalvagingUpgradeWrapper, SalvagingUpgradeContainer> BASIC_SALVAGING_UPGRADE_TYPE = new UpgradeContainerType<>(SalvagingUpgradeContainer::new);
    private static final UpgradeContainerType<SalvagingUpgradeWrapper, SalvagingUpgradeContainer> ADVANCED_SALVAGING_UPGRADE_TYPE = new UpgradeContainerType<>(SalvagingUpgradeContainer::new);


    @SuppressWarnings("DataFlowIssue")
    private static void registerContainers(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.MENU_TYPES)) return;
        UpgradeContainerRegistry.register(NETWORK_MAGNET_UPGRADE.getId(), BASIC_NETWORK_MAGNET_TYPE);
        UpgradeContainerRegistry.register(NETWORK_PICKUP_UPGRADE.getId(), BASIC_NETWORK_PICKUP_TYPE);
        UpgradeContainerRegistry.register(NETWORK_DEPOSIT_UPGRADE.getId(), BASIC_NETWORK_DEPOSIT_TYPE);
        UpgradeContainerRegistry.register(SUPER_VOID_UPGRADE.getId(), SUPER_VOID_TYPE);
        UpgradeContainerRegistry.register(DRINK_UPGRADE.getId(), BASIC_DRINK_TYPE);
        UpgradeContainerRegistry.register(ADVANCED_DRINK_UPGRADE.getId(), ADVANCED_DRINK_TYPE);
        UpgradeContainerRegistry.register(POTION_CHARM_UPGRADE.getId(), BASIC_POTION_CHARM_TYPE);
        UpgradeContainerRegistry.register(ADVANCED_POTION_CHARM_UPGRADE.getId(), ADVANCED_POTION_CHARM_TYPE);
        UpgradeContainerRegistry.register(SALVAGING_UPGRADE.getId(), BASIC_SALVAGING_UPGRADE_TYPE);
        UpgradeContainerRegistry.register(ADVANCED_SALVAGING_UPGRADE.getId(), ADVANCED_SALVAGING_UPGRADE_TYPE);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            UpgradeGuiManager.registerTab(BASIC_NETWORK_MAGNET_TYPE, (NetworkMagnetUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new NetworkMagnetUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.networkMagnetUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
            UpgradeGuiManager.registerTab(BASIC_NETWORK_PICKUP_TYPE, (ContentsFilteredUpgradeContainer<NetworkPickupUpgradeWrapper> container, Position position, StorageScreenBase<?> base) -> new NetworkPickupUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.networkPickupUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
            UpgradeGuiManager.registerTab(BASIC_NETWORK_DEPOSIT_TYPE, NetworkDepositUpgradeTab.Basic::new);
            UpgradeGuiManager.registerTab(SUPER_VOID_TYPE, (VoidUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new SuperVoidUpgradeTab(container, position, base, SUAConfig.INSTANCE.superVoidUpgrade.slotsInRow.get()));
            UpgradeGuiManager.registerTab(BASIC_DRINK_TYPE, (DrinkUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new DrinkUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.drinkUpgrade.slotsInRow.get()));
            UpgradeGuiManager.registerTab(ADVANCED_DRINK_TYPE, (DrinkUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new DrinkUpgradeTab.Advanced(container, position, base, SUAConfig.INSTANCE.advancedDrinkUpgrade.slotsInRow.get()));
            UpgradeGuiManager.registerTab(BASIC_POTION_CHARM_TYPE, (PotionCharmUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new PotionCharmUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.potionCharmUpgradeConfig.slotsInRow.get()));
            UpgradeGuiManager.registerTab(ADVANCED_POTION_CHARM_TYPE, (PotionCharmUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new PotionCharmUpgradeTab.Advanced(container, position, base, SUAConfig.INSTANCE.advancedPotionCharmUpgradeConfig.slotsInRow.get()));
            UpgradeGuiManager.registerTab(BASIC_SALVAGING_UPGRADE_TYPE, (SalvagingUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new SalvagingUpgradeTab.Basic(container, position, base, SUAConfig.INSTANCE.salvagingUpgradeConfig.slotsInRow.get()));
            UpgradeGuiManager.registerTab(ADVANCED_SALVAGING_UPGRADE_TYPE, (SalvagingUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new SalvagingUpgradeTab.Advanced(container, position, base, SUAConfig.INSTANCE.advancedSalvagingUpgradeConfig.slotsInRow.get()));

        });
    }

}
