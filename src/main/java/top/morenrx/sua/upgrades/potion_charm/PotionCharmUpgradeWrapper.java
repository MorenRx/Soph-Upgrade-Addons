package top.morenrx.sua.upgrades.potion_charm;

import dev.shadowsoffire.apotheosis.Apoth.Components;
import dev.shadowsoffire.apotheosis.item.PotionCharmItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;
import net.p3pp3rf1y.sophisticatedcore.inventory.StatefulComponentItemHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PotionCharmUpgradeWrapper extends UpgradeWrapperBase<PotionCharmUpgradeWrapper, PotionCharmUpgrade> implements ITickableUpgrade {
    private final StatefulComponentItemHandler inventory;
    private static final int COOLDOWN_TICKS = 5;

    public PotionCharmUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        if (upgrade.has(DataComponents.CONTAINER)) {
            upgrade.set(ModCoreDataComponents.LENIENT_CONTAINER, upgrade.get(DataComponents.CONTAINER));
            upgrade.remove(DataComponents.CONTAINER);
        }

        this.inventory = new StatefulComponentItemHandler(upgrade, ModCoreDataComponents.LENIENT_CONTAINER.get(), upgradeItem.getCharmSlotCount()) {
            @Override
            protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {
                super.onContentsChanged(slot, oldStack, newStack);
                ItemStack stack = getStackInSlot(slot);
                if (!stack.isEmpty() && !Boolean.TRUE.equals(stack.get(Components.CHARM_ENABLED))) {
                    stack.set(Components.CHARM_ENABLED, true);
                    parent.set(component, ItemContainerContents.fromItems(stacks));
                }
                save();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.isEmpty() || PotionCharmItem.hasEffect(stack);
            }
        };
    }

    public StatefulComponentItemHandler getPotionCharmInventory() {
        return this.inventory;
    }


    @Override
    public void tick(@Nullable Entity entity, @NotNull Level level, @NotNull BlockPos pos) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) return;
        if (player.tickCount % COOLDOWN_TICKS != 0) return;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            stack.inventoryTick(level, player, -1, false);
        }
    }

}
