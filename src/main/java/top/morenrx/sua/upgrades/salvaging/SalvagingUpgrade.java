package top.morenrx.sua.upgrades.salvaging;

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

public class SalvagingUpgrade extends UpgradeItemBase<SalvagingUpgradeWrapper> implements ISUAItemConfig {

    public static class Data {
        public static final String KEY_WORK_IN_GUI = "workInGUI";
        public static final String KEY_EQUIPMENT_RARITY_MASK = "equipmentRarityMask";
        public static final String KEY_GEM_RARITY_MASK = "gemRarityMask";
        public static final String KEY_SALVAGING_EQUIPMENT = "salvagingEquipment";
        public static final String KEY_SALVAGING_GEM = "salvagingGem";
        public static final String KEY_SALVAGING_OTHER = "salvagingOther";
    }

    public static final UpgradeType<SalvagingUpgradeWrapper> TYPE = new UpgradeType<>(SalvagingUpgradeWrapper::new);
    private final BooleanSupplier enable;
    private final IntSupplier filterSlots;

    public SalvagingUpgrade(BooleanSupplier enable, IntSupplier filterSlots) {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.enable = enable;
        this.filterSlots = filterSlots;
    }

    public int getFilterSlotCount() {
        return filterSlots.getAsInt();
    }

    @Override
    public @NotNull UpgradeType<SalvagingUpgradeWrapper> getType() {
        return TYPE;
    }

    @Override
    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    @Override
    public boolean isEnable() {
         return SUACompat.APOTHEOSIS.getAsBoolean() && enable.getAsBoolean();
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
