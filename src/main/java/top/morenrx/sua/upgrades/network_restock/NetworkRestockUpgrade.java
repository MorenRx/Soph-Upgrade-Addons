package top.morenrx.sua.upgrades.network_restock;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class NetworkRestockUpgrade extends RestockUpgradeItem implements ISUAItemConfig {
    public static final UpgradeType<RestockUpgradeWrapper> TYPE = new UpgradeType<>(RestockUpgradeWrapper::new);
    private final BooleanSupplier enable;
    public NetworkRestockUpgrade(BooleanSupplier enable, IntSupplier filterSlots) {
        super(filterSlots);
        this.enable = enable;
    }

    @Override
    public boolean isEnable() {
        return enable.getAsBoolean() && !NetworkStorageProvider.get().getNetworkStorageHandlers().isEmpty();
    }

    @Override
    public @NotNull UpgradeType<RestockUpgradeWrapper> getType() {
        return TYPE;
    }

    @Override
    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (!isEnable()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.tooltip.disable").withStyle(ChatFormatting.RED));
            return;
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.RARE;
    }
}
