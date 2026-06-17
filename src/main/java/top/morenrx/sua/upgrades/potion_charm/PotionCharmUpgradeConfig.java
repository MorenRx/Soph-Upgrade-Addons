package top.morenrx.sua.upgrades.potion_charm;

import net.minecraftforge.common.ForgeConfigSpec;

public class PotionCharmUpgradeConfig {
    public final ForgeConfigSpec.BooleanValue enable;
    public final ForgeConfigSpec.IntValue slots;
    public final ForgeConfigSpec.IntValue slotsInRow;

    public PotionCharmUpgradeConfig(ForgeConfigSpec.Builder builder, String name, String path, int defaultSlots, int defaultSlotsInRow) {
        builder.comment(name + "设置").push(path);
        enable = builder.comment("是否启用").define("enable", true);
        slots = builder.comment("槽位数量").defineInRange("slots", defaultSlots, 1, 100);
        slotsInRow = builder.comment("每行显示的槽位数量").defineInRange("slotsInRow", defaultSlotsInRow, 1, 6);
        builder.pop();
    }
}
