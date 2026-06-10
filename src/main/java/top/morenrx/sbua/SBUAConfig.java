package top.morenrx.sbua;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.morenrx.sbua.upgrades.drink.DrinkUpgradeConfig;
import top.morenrx.sbua.upgrades.ender_chest.EnderChestUpgradeConfig;
import top.morenrx.sbua.upgrades.potion_charm.PotionCharmUpgradeConfig;
import top.morenrx.sbua.upgrades.rs_deposit.RSDepositUpgradeConfig;
import top.morenrx.sbua.upgrades.rs_magnet.RSMagnetUpgradeConfig;
import top.morenrx.sbua.upgrades.rs_pickup.RSPickupUpgradeConfig;
import top.morenrx.sbua.upgrades.voiding.SuperVoidUpgradeConfig;

public class SBUAConfig {
    public static final SBUAConfig INSTANCE = new SBUAConfig();
    public final ForgeConfigSpec SPEC;

    public final RSMagnetUpgradeConfig rsMagnetUpgrade;
    public final RSPickupUpgradeConfig rsPickupUpgrade;
    public final RSDepositUpgradeConfig rsDepositUpgrade;
    public final SuperVoidUpgradeConfig superVoidUpgrade;
    public final EnderChestUpgradeConfig endChestUpgrade;
    public final DrinkUpgradeConfig drinkUpgrade;
    public final DrinkUpgradeConfig advancedDrinkUpgrade;
    public final PotionCharmUpgradeConfig potionCharmUpgradeConfig;
    public final PotionCharmUpgradeConfig advancedPotionCharmUpgradeConfig;


    public SBUAConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        rsMagnetUpgrade = new RSMagnetUpgradeConfig(builder, "RS网络磁铁升级", "RSMagnetUpgrade", 24, 4, 5);
        rsPickupUpgrade = new RSPickupUpgradeConfig(builder, "RS网络拾取升级", "RSPickupUpgrade", 24, 4);
        rsDepositUpgrade = new RSDepositUpgradeConfig(builder, "RS卸货升级", "RSDepositUpgrade", 24, 4);

        endChestUpgrade = new EnderChestUpgradeConfig(builder, "末影升级", "EndChestUpgrade");
        superVoidUpgrade = new SuperVoidUpgradeConfig(builder, "超级虚空升级", "SuperVoidUpgrade", 48, 6);
        drinkUpgrade = new DrinkUpgradeConfig(builder, "饮水升级", "DrinkUpgrade", 9, 3);
        advancedDrinkUpgrade = new DrinkUpgradeConfig(builder, "高级饮水升级", "AdvancedDrinkUpgrade", 16, 4);
        potionCharmUpgradeConfig = new PotionCharmUpgradeConfig(builder, "药水护符升级", "PotionCharmUpgrade", 4, 2);
        advancedPotionCharmUpgradeConfig = new PotionCharmUpgradeConfig(builder, "高级药水护符升级", "PotionCharmUpgrade", 9, 3);

        SPEC = builder.build();
    }

    public static void init(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, SBUAConfig.INSTANCE.SPEC);
    }
}
