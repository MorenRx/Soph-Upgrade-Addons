package top.morenrx.sua.upgrades.drink;

import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.ForgeEventFactory;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.ITrackedContentsItemHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IFilteredUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.InventoryHelper;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DrinkUpgradeWrapper extends UpgradeWrapperBase<DrinkUpgradeWrapper, DrinkUpgrade> implements ITickableUpgrade, IFilteredUpgrade {

    private static final int COOLDOWN = 100;
    private static final int STILL_THIRST_COOLDOWN = 10;
    private static final int RANGE = 3;
    private final FilterLogic filterLogic;

    public DrinkUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        filterLogic = new FilterLogic(upgrade, upgradeSaveHandler, upgradeItem.getFilterSlotCount(), ThirstHelper::itemRestoresThirst);
    }

    @Override
    public void tick(@Nullable Entity entity, @NotNull Level level, @NotNull BlockPos pos) {
        if (isInCooldown(level) || (entity != null && !(entity instanceof Player))) {
            return;
        }

        boolean thirstPlayer = false;
        if (entity == null) {
            AtomicBoolean stillThirstPlayer = new AtomicBoolean(false);
            level.getEntities(EntityType.PLAYER, new AABB(pos).inflate(RANGE), p -> true).forEach(p -> stillThirstPlayer.set(stillThirstPlayer.get() || drinkPlayerAndGetThirst(p, level)));
            thirstPlayer = stillThirstPlayer.get();
        } else {
            if (drinkPlayerAndGetThirst((Player) entity, level)) {
                thirstPlayer = true;
            }
        }
        if (thirstPlayer) {
            setCooldown(level, STILL_THIRST_COOLDOWN);
            return;
        }

        setCooldown(level, COOLDOWN);
    }

    private boolean drinkPlayerAndGetThirst(Player player, Level level) {
        int thirstLevel = 20 - player.getCapability(ModCapabilities.PLAYER_THIRST).map(IThirst::getThirst).orElse(20);
        if (thirstLevel == 0) {
            return false;
        }
        return tryDrinkingFromStorage(level, thirstLevel, player) && player.getCapability(ModCapabilities.PLAYER_THIRST).map(IThirst::getThirst).orElse(20) < 20;
    }

    private boolean tryDrinkingFromStorage(Level level, int thirstLevel, Player player) {
        ITrackedContentsItemHandler inventory = storageWrapper.getInventoryForUpgradeProcessing();
        return InventoryHelper.iterate(inventory, (slot, stack) -> tryDrinkingStack(level, thirstLevel, player, slot, stack, inventory), () -> false, ret -> ret);
    }

    private boolean tryDrinkingStack(Level level, int thirstLevel, Player player, Integer slot, ItemStack stack, ITrackedContentsItemHandler inventory) {
        boolean isHurt = player.getHealth() < player.getMaxHealth() - 0.1F;
        if (isDrink(stack) && filterLogic.matchesFilter(stack) && (isThirstEnoughForDrink(thirstLevel, stack) || shouldDrinkForHurt() && thirstLevel > 0 && isHurt)) {
            ItemStack mainHandItem = player.getMainHandItem();
            player.getInventory().items.set(player.getInventory().selected, stack);

            ItemStack singleItemCopy = stack.copy();
            singleItemCopy.setCount(1);
            if (singleItemCopy.use(level, player, InteractionHand.MAIN_HAND).getResult() == InteractionResult.CONSUME) {
                stack.shrink(1);
                inventory.setStackInSlot(slot, stack);

                ItemStack resultItem = ForgeEventFactory.onItemUseFinish(player, singleItemCopy.copy(), 0, singleItemCopy.getItem().finishUsingItem(singleItemCopy, level, player));
                if (!resultItem.isEmpty()) {
                    ItemStack insertResult = inventory.insertItem(resultItem, false);
                    if (!insertResult.isEmpty()) {
                        player.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(playerInventory ->
                                InventoryHelper.insertOrDropItem(player, insertResult, playerInventory));
                    }
                }

                player.getInventory().items.set(player.getInventory().selected, mainHandItem);
                return true;
            }
            player.getInventory().items.set(player.getInventory().selected, mainHandItem);
        }
        return false;
    }

    private boolean isDrink(ItemStack stack) {
        if (!ThirstHelper.itemRestoresThirst(stack)) return false;
        if (!ThirstHelper.isDrink(stack)) return true;
        return ThirstHelper.getPurity(stack) >= shouldPurity();
    }

    private boolean isThirstEnoughForDrink(int thirstLevel, ItemStack stack) {
        int drinkAtThirstLevel = getDrinkAtThirstLevel();
        if (drinkAtThirstLevel == DrinkUpgrade.Data.THIRST_LEVEL_ANY) {
            return true;
        }

        int thirst = ThirstHelper.getThirst(stack);
        return (drinkAtThirstLevel == DrinkUpgrade.Data.THIRST_LEVEL_HALF ? (thirst / 2) : thirst) <= thirstLevel;
    }

    @Override
    public @NotNull FilterLogic getFilterLogic() {
        return filterLogic;
    }

    public int getDrinkAtThirstLevel() {
        return NBTHelper.getInt(upgrade, DrinkUpgrade.Data.KEY_THIRST_LEVEL).orElse(DrinkUpgrade.Data.THIRST_LEVEL_HALF);
    }

    public void setDrinkAtThirstLevel(int thirstLevel) {
        NBTHelper.setInteger(upgrade, DrinkUpgrade.Data.KEY_THIRST_LEVEL, thirstLevel);
        save();
    }

    public boolean shouldDrinkForHurt() {
        return NBTHelper.getBoolean(upgrade, DrinkUpgrade.Data.KEY_DRINK_FOR_HURT).orElse(false);
    }

    public void setDrinkForHurt(boolean drinkForHurt) {
        NBTHelper.setBoolean(upgrade, DrinkUpgrade.Data.KEY_DRINK_FOR_HURT, drinkForHurt);
        save();
    }

    public int shouldPurity() {
        return NBTHelper.getInt(upgrade, DrinkUpgrade.Data.KEY_PURITY).orElse(DrinkUpgrade.Data.PURITY_DIRTY);
    }

    public void setPurity(int purity) {
        NBTHelper.setInteger(upgrade, DrinkUpgrade.Data.KEY_PURITY, purity);
        save();
    }
}
