package top.morenrx.sua.upgrades.potion_charm;

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

public class PotionCharmUpgrade extends UpgradeItemBase<PotionCharmUpgradeWrapper> implements ISUAItemConfig {
    private static final UpgradeType<PotionCharmUpgradeWrapper> TYPE = new UpgradeType<>(PotionCharmUpgradeWrapper::new);
    private final BooleanSupplier enable;
    private final IntSupplier slots;

    public PotionCharmUpgrade(BooleanSupplier enable, IntSupplier slots) {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.enable = enable;
        this.slots = slots;
    }


    public @NotNull UpgradeType<PotionCharmUpgradeWrapper> getType() {
        return TYPE;
    }

    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    public int getCharmSlotCount() {
        return slots.getAsInt();
    }

    @Override
    public boolean isEnable() {
        return SUACompat.APOTHEOSIS && enable.getAsBoolean();
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

