package top.morenrx.sua.upgrades.drink;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.init.SUACompat;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class DrinkUpgrade extends UpgradeItemBase<DrinkUpgradeWrapper> implements ISUAItemConfig {

    public static class Data {
        public static final String KEY_THIRST_LEVEL = "thirstLevel";
        public static final String KEY_DRINK_FOR_HURT = "drinkForHurt";
        public static final String KEY_PURITY = "purity";

        public static final int PURITY_DIRTY = 0;
        public static final int PURITY_SLIGHTLY_DIRTY = 1;
        public static final int PURITY_ACCEPTABLE = 2;
        public static final int PURITY_PURIFIED = 3;

        public static final int THIRST_LEVEL_ANY = 0;
        public static final int THIRST_LEVEL_HALF = 1;
        public static final int THIRST_LEVEL_FULL = 2;
    }

    public static final UpgradeType<DrinkUpgradeWrapper> TYPE = new UpgradeType<>(DrinkUpgradeWrapper::new);
    private final BooleanSupplier enable;
    private final IntSupplier filterSlots;

    public DrinkUpgrade(BooleanSupplier enable, IntSupplier filterSlots) {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.enable = enable;
        this.filterSlots = filterSlots;
    }

    public int getFilterSlotCount() {
        return filterSlots.getAsInt();
    }

    public @NotNull UpgradeType<DrinkUpgradeWrapper> getType() {
        return TYPE;
    }

    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    @Override
    public boolean isEnable() {
        return SUACompat.THIRST.getAsBoolean() && enable.getAsBoolean();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (!isEnable()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.tooltip.disable").withStyle(ChatFormatting.RED));
            return;
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
