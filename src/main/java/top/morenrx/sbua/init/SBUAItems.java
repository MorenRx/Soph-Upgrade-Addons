package top.morenrx.sbua.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.config.ModConfigEvent;
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
import top.morenrx.sbua.SBUAConfig;
import top.morenrx.sbua.SophUpgradeAddons;
import top.morenrx.sbua.upgrades.drink.DrinkUpgrade;
import top.morenrx.sbua.upgrades.potion_charm.PotionCharmUpgrade;
import top.morenrx.sbua.upgrades.ender_chest.EnderChestUpgrade;
import top.morenrx.sbua.upgrades.rs_deposit.RSDepositUpgrade;
import top.morenrx.sbua.upgrades.rs_deposit.RSDepositUpgradeTab;
import top.morenrx.sbua.upgrades.rs_magnet.RSMagnetUpgrade;
import top.morenrx.sbua.upgrades.rs_magnet.RSMagnetUpgradeContainer;
import top.morenrx.sbua.upgrades.rs_magnet.RSMagnetUpgradeTab;
import top.morenrx.sbua.upgrades.rs_magnet.RSMagnetUpgradeWrapper;
import top.morenrx.sbua.upgrades.rs_pickup.RSPickupUpgrade;
import top.morenrx.sbua.upgrades.rs_pickup.RSPickupUpgradeTab;
import top.morenrx.sbua.upgrades.rs_pickup.RSPickupUpgradeWrapper;
import top.morenrx.sbua.upgrades.voiding.SuperVoidUpgrade;
import top.morenrx.sbua.upgrades.voiding.SuperVoidUpgradeTab;


public class SBUAItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SophUpgradeAddons.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SophUpgradeAddons.MODID);


    public static final RegistryObject<Item> MOD_ICON = ITEMS.register("mod_icon", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RS_MAGNET_UPGRADE = ITEMS.register("rs_magnet_upgrade", RSMagnetUpgrade::new);
    public static final RegistryObject<Item> RS_PICKUP_UPGRADE = ITEMS.register("rs_pickup_upgrade", RSPickupUpgrade::new);
    public static final RegistryObject<Item> RS_DEPOSIT_UPGRADE = ITEMS.register("rs_deposit_upgrade", RSDepositUpgrade::new);
    public static final RegistryObject<Item> SUPER_VOID_UPGRADE = ITEMS.register("super_void_upgrade", SuperVoidUpgrade::new);
    public static final RegistryObject<Item> ENDER_CHEST_UPGRADE = ITEMS.register("ender_chest_upgrade", EnderChestUpgrade::new);
    public static final RegistryObject<Item> POTION_CHARM_UPGRADE = ITEMS.register("potion_charm_upgrade", () -> new PotionCharmUpgrade(SBUAConfig.INSTANCE.potionCharmUpgradeConfig));
    public static final RegistryObject<Item> ADVANCED_POTION_CHARM_UPGRADE = ITEMS.register("advanced_potion_charm_upgrade", () -> new PotionCharmUpgrade(SBUAConfig.INSTANCE.advancedPotionCharmUpgradeConfig));
    public static final RegistryObject<Item> DRINK_UPGRADE = ITEMS.register("drink_upgrade", () -> new DrinkUpgrade(SBUAConfig.INSTANCE.drinkUpgrade));
    public static final RegistryObject<Item> ADVANCED_DRINK_UPGRADE = ITEMS.register("advanced_drink_upgrade", () -> new DrinkUpgrade(SBUAConfig.INSTANCE.drinkUpgrade));


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
        modEventBus.addListener(SBUAItems::registerContainers);
        modEventBus.addListener(SBUAItems::setup);


    }

    public static void setup(ModConfigEvent.Loading event) {
        EnderChestUpgrade.init();
    }


    private static final UpgradeContainerType<RSMagnetUpgradeWrapper, RSMagnetUpgradeContainer> RS_MAGNET_BASIC_TYPE = new UpgradeContainerType<>(RSMagnetUpgradeContainer::new);
    private static final UpgradeContainerType<RSPickupUpgradeWrapper, ContentsFilteredUpgradeContainer<RSPickupUpgradeWrapper>> RS_PICKUP_BASIC_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
    private static final UpgradeContainerType<DepositUpgradeWrapper, DepositUpgradeContainer> RS_DEPOSIT_BASIC_TYPE = new UpgradeContainerType<>(DepositUpgradeContainer::new);
    private static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> VOID_SUPER_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);


    @SuppressWarnings("DataFlowIssue")
    private static void registerContainers(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.Keys.MENU_TYPES)) return;
        UpgradeContainerRegistry.register(RS_MAGNET_UPGRADE.getId(), RS_MAGNET_BASIC_TYPE);
        UpgradeContainerRegistry.register(RS_PICKUP_UPGRADE.getId(), RS_PICKUP_BASIC_TYPE);
        UpgradeContainerRegistry.register(RS_DEPOSIT_UPGRADE.getId(), RS_DEPOSIT_BASIC_TYPE);
        UpgradeContainerRegistry.register(SUPER_VOID_UPGRADE.getId(), VOID_SUPER_TYPE);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            UpgradeGuiManager.registerTab(RS_MAGNET_BASIC_TYPE, (RSMagnetUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new RSMagnetUpgradeTab.Basic(container, position, base, SBUAConfig.INSTANCE.rsMagnetUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
            UpgradeGuiManager.registerTab(RS_PICKUP_BASIC_TYPE, (ContentsFilteredUpgradeContainer<RSPickupUpgradeWrapper> container, Position position, StorageScreenBase<?> base) -> new RSPickupUpgradeTab.Basic(container, position, base, SBUAConfig.INSTANCE.rsPickupUpgrade.slotsInRow.get(), SBPButtonDefinitions.BACKPACK_CONTENTS_FILTER_TYPE));
            UpgradeGuiManager.registerTab(RS_DEPOSIT_BASIC_TYPE, RSDepositUpgradeTab.Basic::new);
            UpgradeGuiManager.registerTab(VOID_SUPER_TYPE, (VoidUpgradeContainer container, Position position, StorageScreenBase<?> base) -> new SuperVoidUpgradeTab(container, position, base, SBUAConfig.INSTANCE.superVoidUpgrade.slotsInRow.get()));

        });
    }

}
