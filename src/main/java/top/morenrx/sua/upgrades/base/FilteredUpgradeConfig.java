package top.morenrx.sua.upgrades.base;

import net.neoforged.neoforge.common.ModConfigSpec;

public class FilteredUpgradeConfig {
    public final ModConfigSpec.IntValue filterSlots;
    public final ModConfigSpec.IntValue slotsInRow;

    protected FilteredUpgradeConfig(ModConfigSpec.Builder builder, String name, String path, int defaultFilterSlots, int defaultSlotsInRow) {
        builder.comment(name + "设置").push(path);
        this.filterSlots = builder.comment("过滤槽数量").defineInRange("filterSlots", defaultFilterSlots, 1, 100);
        this.slotsInRow = builder.comment("每行显示的槽位数量").defineInRange("slotsInRow", defaultSlotsInRow, 1, 6);
    }
}
