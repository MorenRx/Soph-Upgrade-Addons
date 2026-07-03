package top.morenrx.sua.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.morenrx.sua.upgrades.drink.DrinkUpgradeConfig;
import top.morenrx.sua.upgrades.ender_chest.EnderChestUpgradeConfig;
import top.morenrx.sua.upgrades.network_deposit.NetworkDepositUpgradeConfig;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgradeConfig;
import top.morenrx.sua.upgrades.network_pickup.NetworkPickupUpgradeConfig;
import top.morenrx.sua.upgrades.potion_charm.PotionCharmUpgradeConfig;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeConfig;
import top.morenrx.sua.upgrades.voiding.SuperVoidUpgradeConfig;

public class SUAConfig {
    public static final SUAConfig INSTANCE = new SUAConfig();
    public final ForgeConfigSpec SPEC;

    public final NetworkMagnetUpgradeConfig networkMagnetUpgrade;
    public final NetworkPickupUpgradeConfig networkPickupUpgrade;
    public final NetworkDepositUpgradeConfig networkDepositUpgrade;
    public final SuperVoidUpgradeConfig superVoidUpgrade;
    public final EnderChestUpgradeConfig endChestUpgrade;
    public final DrinkUpgradeConfig drinkUpgrade;
    public final DrinkUpgradeConfig advancedDrinkUpgrade;
    public final PotionCharmUpgradeConfig potionCharmUpgradeConfig;
    public final PotionCharmUpgradeConfig advancedPotionCharmUpgradeConfig;
    public final SalvagingUpgradeConfig salvagingUpgradeConfig;
    public final SalvagingUpgradeConfig advancedSalvagingUpgradeConfig;


    public SUAConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        networkMagnetUpgrade = new NetworkMagnetUpgradeConfig(builder, "网络磁铁升级", "NetworkMagnetUpgrade", 24, 4, 5);
        networkPickupUpgrade = new NetworkPickupUpgradeConfig(builder, "网络拾取升级", "NetworkPickupUpgrade", 24, 4);
        networkDepositUpgrade = new NetworkDepositUpgradeConfig(builder, "网络卸货升级", "NetworkDepositUpgrade", 24, 4);

        endChestUpgrade = new EnderChestUpgradeConfig(builder, "末影升级", "EndChestUpgrade");
        superVoidUpgrade = new SuperVoidUpgradeConfig(builder, "超级虚空升级", "SuperVoidUpgrade", 48, 6);
        drinkUpgrade = new DrinkUpgradeConfig(builder, "饮水升级", "DrinkUpgrade", 9, 3);
        advancedDrinkUpgrade = new DrinkUpgradeConfig(builder, "高级饮水升级", "AdvancedDrinkUpgrade", 16, 4);
        potionCharmUpgradeConfig = new PotionCharmUpgradeConfig(builder, "药水护符升级", "PotionCharmUpgrade", 4, 2);
        advancedPotionCharmUpgradeConfig = new PotionCharmUpgradeConfig(builder, "高级药水护符升级", "AdvancedPotionCharmUpgrade", 9, 3);
        salvagingUpgradeConfig = new SalvagingUpgradeConfig(builder, "神化回收升级", "SalvagingUpgrade", 9, 3);
        advancedSalvagingUpgradeConfig = new SalvagingUpgradeConfig(builder, "高级神化回收升级", "AdvancedSalvagingUpgrade", 16, 4);

        SPEC = builder.build();
    }

    public static void init(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, SUAConfig.INSTANCE.SPEC);
    }
}
