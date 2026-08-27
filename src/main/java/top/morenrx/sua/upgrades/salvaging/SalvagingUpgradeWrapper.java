package top.morenrx.sua.upgrades.salvaging;

import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemItem;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedcore.api.ISlotChangeResponseUpgrade;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.*;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.util.SUAUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SalvagingUpgradeWrapper extends UpgradeWrapperBase<SalvagingUpgradeWrapper, SalvagingUpgrade> implements IInsertResponseUpgrade, IFilteredUpgrade, ISlotChangeResponseUpgrade, ITickableUpgrade {
    private final FilterLogic filterLogic;
    private final Set<Integer> slotsToSalvaging = new HashSet<>();
    private BlockPos pos;
    private Level level;

    public SalvagingUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        this.filterLogic = new FilterLogic(upgrade, upgradeSaveHandler, this.upgradeItem.getFilterSlotCount());
    }

    @Override
    public void onSlotChange(@NotNull IItemHandler handler, int slot) {
        if (shouldWorkInGUI()) {
            slotsToSalvaging.add(slot);
        }
    }

    @Override
    public @NotNull ItemStack onBeforeInsert(InventoryHandler inventoryHandler, int slot, @NotNull ItemStack stack, boolean simulate) {
        if (inventoryHandler.getStackInSlot(slot).isEmpty()) return stack;
        return onBeforeInsert(inventoryHandler, stack, simulate);
    }

    @Override
    public @NotNull ItemStack onBeforeInsert(@NotNull InventoryHandler inventoryHandler, @NotNull ItemStack stack, boolean simulate) {
        if (simulate) return stack;
        if (!filterLogic.matchesFilter(stack) || !canSalvaging(stack)) return stack;
        int consumeCount = trySalvagingAndInsertItem(stack, (tempStack, tempSimulate) ->
                storageWrapper.getInventoryForUpgradeProcessing().insertItem(tempStack, tempSimulate));
        if (consumeCount <= 0) return stack;
        if (consumeCount == stack.getCount()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack copy = stack.copy();
            copy.setCount(copy.getCount() - consumeCount);
            return copy;
        }
    }

    public boolean canSalvaging(ItemStack stack) {
        if (!SUAUtils.Recipe.findMatchSalvaging(stack)) return false;

        DynamicHolder<LootRarity> rarityDynamicHolder = AffixHelper.getRarity(stack);
        if (!rarityDynamicHolder.isBound()) {
            return shouldSalvagingOther() && stack.getMaxStackSize() == 1;
        }

        LootRarity lootRarity = rarityDynamicHolder.get();
        if (stack.getItem() instanceof GemItem) {
            return shouldSalvagingGem() && (shouldGemRarityMask() & (1 << lootRarity.ordinal())) != 0;
        } else {
            return shouldSalvagingEquipment() && (shouldEquipmentRarityMask() & (1 << lootRarity.ordinal())) != 0;
        }
    }

    public int trySalvagingAndInsertItem(ItemStack stack, BiFunction<ItemStack, Boolean, ItemStack> insertHandler) {
        int consumeCount = 0;
        int count = stack.getCount();
        for (int i = 0; i < count; i++, consumeCount++) {
            List<ItemStack> salvagingResult = SUAUtils.Recipe.getSalvagingResult(stack);
            if (salvagingResult.isEmpty()) return consumeCount;

            for (int j = 0; j < salvagingResult.size(); j++) {
                ItemStack result = salvagingResult.get(j).copy();
                ItemStack remainingStack = insertHandler.apply(result, true);
                if (j == 0 && remainingStack.getCount() != 0) return consumeCount;
                if (remainingStack.getCount() < result.getCount()) {
                    remainingStack = insertHandler.apply(result, false);
                }
                dropStack(remainingStack);
            }
        }

        return consumeCount;
    }

    private void dropStack(ItemStack stack) {
        if (stack.isEmpty()) return;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.5;
        double z = pos.getZ() + 0.5;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }


    @Override
    public void tick(@Nullable Entity entity, @NotNull Level world, @NotNull BlockPos pos) {
        if (this.pos == null || this.level == null){
            this.pos = pos;
            this.level = world;
        }
        if (slotsToSalvaging.isEmpty()) {
            return;
        }

        InventoryHandler storageInventory = storageWrapper.getInventoryHandler();
        for (int slot : slotsToSalvaging) {
            ItemStack stack = storageInventory.getStackInSlot(slot);
            if (!filterLogic.matchesFilter(stack) || !canSalvaging(stack)) continue;
            int consumeCount = trySalvagingAndInsertItem(stack, (tempStack, tempSimulate) ->
                    storageWrapper.getInventoryForUpgradeProcessing().insertItem(tempStack, tempSimulate));
            if (consumeCount == 0) continue;
            storageInventory.extractItem(slot, consumeCount, false);
        }

        slotsToSalvaging.clear();
    }

    @Override
    public @NotNull FilterLogic getFilterLogic() {
        return filterLogic;
    }

    public void setWorkInGUI(boolean workInGUI) {
        NBTHelper.setBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_WORK_IN_GUI, workInGUI);
        this.save();
    }

    public boolean shouldWorkInGUI() {
        return NBTHelper.getBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_WORK_IN_GUI).orElse(false);
    }

    public void setEquipmentRarityMask(int equipmentRarityMask) {
        NBTHelper.setInteger(this.upgrade, SalvagingUpgrade.Data.KEY_EQUIPMENT_RARITY_MASK, equipmentRarityMask);
        this.save();
    }

    public int shouldEquipmentRarityMask() {
        return NBTHelper.getInt(this.upgrade, SalvagingUpgrade.Data.KEY_EQUIPMENT_RARITY_MASK).orElse(Integer.MAX_VALUE);
    }

    public void setGemRarityMask(int gemRarityMask) {
        NBTHelper.setInteger(this.upgrade, SalvagingUpgrade.Data.KEY_GEM_RARITY_MASK, gemRarityMask);
        this.save();
    }

    public int shouldGemRarityMask() {
        return NBTHelper.getInt(this.upgrade, SalvagingUpgrade.Data.KEY_GEM_RARITY_MASK).orElse(Integer.MAX_VALUE);
    }

    public void setSalvagingEquipment(boolean salvagingEquipment) {
        NBTHelper.setBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_SALVAGING_EQUIPMENT, salvagingEquipment);
        this.save();
    }

    public boolean shouldSalvagingEquipment() {
        return NBTHelper.getBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_SALVAGING_EQUIPMENT).orElse(true);
    }

    public void setSalvagingGem(boolean salvagingGem) {
        NBTHelper.setBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_SALVAGING_GEM, salvagingGem);
        this.save();
    }

    public boolean shouldSalvagingGem() {
        return NBTHelper.getBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_SALVAGING_GEM).orElse(true);
    }

    public void setSalvagingOther(boolean salvagingOther) {
        NBTHelper.setBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_SALVAGING_OTHER, salvagingOther);
        this.save();
    }

    public boolean shouldSalvagingOther() {
        return NBTHelper.getBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_SALVAGING_OTHER).orElse(false);
    }
}
