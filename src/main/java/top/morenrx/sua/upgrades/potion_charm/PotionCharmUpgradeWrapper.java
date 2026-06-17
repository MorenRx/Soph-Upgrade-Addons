package top.morenrx.sua.upgrades.potion_charm;

import dev.shadowsoffire.apotheosis.potion.PotionCharmItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PotionCharmUpgradeWrapper extends UpgradeWrapperBase<PotionCharmUpgradeWrapper, PotionCharmUpgrade> implements ITickableUpgrade {
    private final ItemStackHandler inventory;
    private static final int COOLDOWN_TICKS = 5;

    public PotionCharmUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        this.inventory = new ItemStackHandler(upgradeItem.getCharmSlotCount()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                ItemStack stack = getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    CompoundTag tag = stack.getOrCreateTag();
                    if (!tag.getBoolean("charm_enabled")) {
                        tag.putBoolean("charm_enabled", true);
                    }
                }
                upgrade.addTagElement("potionCharmInventory", serializeNBT());
                save();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.isEmpty() || PotionCharmItem.hasEffect(stack);
            }

            @Override
            public void setSize(int size) {
                if (size < upgradeItem.getCharmSlotCount()) {
                    size = upgradeItem.getCharmSlotCount();
                }
                super.setSize(size);
            }
        };
        NBTHelper.getCompound(upgrade, "potionCharmInventory").ifPresent(inventory::deserializeNBT);
    }

    public ItemStackHandler getPotionCharmInventory() {
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
