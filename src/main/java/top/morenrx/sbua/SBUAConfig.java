package top.morenrx.sbua;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
@Mod.EventBusSubscriber(modid = SBUAInit.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.BooleanValue RS_MAGNET_UPGRADE_ENABLE = BUILDER.comment("(RS)网络磁铁升级 启用").define("RSMagnetUpgradeEnable", true);
    private static final ForgeConfigSpec.IntValue RS_MAGNET_UPGRADE_RANGE = BUILDER.comment("(RS)网络磁铁升级 吸取范围").defineInRange("RSMagnetUpgradeRange", 5, 1, 20);
    private static final ForgeConfigSpec.IntValue RS_MAGNET_UPGRADE_FILTER_SLOTS = BUILDER.comment("(RS)网络磁铁升级 过滤槽位数量").defineInRange("RSMagnetUpgradeFilterSlots", 16, 1, 20);
    private static final ForgeConfigSpec.BooleanValue POTION_CHARM_UPGRADE_ENABLE = BUILDER.comment("(神化)是否启用 药水护符升级").define("PotionCharmUpgradeEnable", true);
    private static final ForgeConfigSpec.BooleanValue DRINK_UPGRADE_ENABLE = BUILDER.comment("(口渴)是否启用 饮水升级").define("DrinkUpgradeEnable", true);
    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableRSMagnetUpgrade;
    public static boolean enablePotionCharmUpgrade;
    public static boolean enableDrinkUpgrade;


    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        enableRSMagnetUpgrade = RS_MAGNET_UPGRADE_ENABLE.get();
        enablePotionCharmUpgrade = POTION_CHARM_UPGRADE_ENABLE.get();
        enableDrinkUpgrade = DRINK_UPGRADE_ENABLE.get();
    }
}
