package top.morenrx.sua.upgrades.network_deposit;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class NetworkDepositUpgrade extends DepositUpgradeItem implements ISUAItemConfig {
    public static final UpgradeType<DepositUpgradeWrapper> TYPE = new UpgradeType<>(DepositUpgradeWrapper::new);
    private final BooleanSupplier enable;

    public NetworkDepositUpgrade(BooleanSupplier enable, IntSupplier filterSlots) {
        super(filterSlots);
        this.enable = enable;
    }

    @Override
    public boolean isEnable() {
        return enable.getAsBoolean() && NetworkStorageProvider.get().hasExternalStorages();
    }

    @Override
    public @NotNull UpgradeType<DepositUpgradeWrapper> getType() {
        return TYPE;
    }

    @Override
    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (!isEnable()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.tooltip.disable").withStyle(ChatFormatting.RED));
            return;
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
