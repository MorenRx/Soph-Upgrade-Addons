package top.morenrx.sua.upgrades.drink;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
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
import top.morenrx.sua.upgrades.compat.drink.DrinkCompatProvider;
import top.morenrx.sua.upgrades.compat.drink.IThirstCompat;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DrinkUpgradeWrapper extends UpgradeWrapperBase<DrinkUpgradeWrapper, DrinkUpgrade> implements ITickableUpgrade, IFilteredUpgrade {

    private static final int COOLDOWN = 100;
    private static final int STILL_THIRST_COOLDOWN = 10;
    private static final int RANGE = 3;
    private final FilterLogic filterLogic;

    public DrinkUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        filterLogic = new FilterLogic(upgrade, upgradeSaveHandler, upgradeItem.getFilterSlotCount(), stack -> DrinkCompatProvider.get().itemRestoresThirst(stack));
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
        for (IThirstCompat compat : DrinkCompatProvider.get().getCompats().values()) {
            if (!compat.isThirstActive(player)) {
                continue;
            }
            int thirstLevel = 20 - compat.getPlayerThirst(player, 20);
            if (thirstLevel <= 0) {
                continue;
            }
            if (tryDrinkingFromStorage(level, thirstLevel, player, compat)) {
                return compat.getPlayerThirst(player, 20) < 20;
            }
        }
        return false;
    }

    private boolean tryDrinkingFromStorage(Level level, int thirstLevel, Player player, IThirstCompat compat) {
        ITrackedContentsItemHandler inventory = storageWrapper.getInventoryForUpgradeProcessing();
        return InventoryHelper.iterate(inventory, (slot, stack) -> tryDrinkingStack(level, thirstLevel, player, slot, stack, inventory, compat), () -> false, ret -> ret);
    }

    private boolean tryDrinkingStack(Level level, int thirstLevel, Player player, Integer slot, ItemStack stack, ITrackedContentsItemHandler inventory, IThirstCompat compat) {
        boolean isHurt = player.getHealth() < player.getMaxHealth() - 0.1F;
        if (!isDrink(stack, compat) || !meetsPurity(stack, compat)) return false;
        if (!filterLogic.matchesFilter(stack)) return false;
        if (!(isThirstEnoughForDrink(thirstLevel, stack, compat) || shouldDrinkForHurt() && thirstLevel > 0 && isHurt)) return false;

        ItemStack singleItemCopy = stack.copy();
        singleItemCopy.setCount(1);
        ItemStack drinkItem = singleItemCopy.copy();
        int thirstBefore = compat.getPlayerThirst(player, 20);

        stack.shrink(1);
        inventory.setStackInSlot(slot, stack);

        ItemStack resultItem = ForgeEventFactory.onItemUseFinish(player, singleItemCopy.copy(), 0, singleItemCopy.getItem().finishUsingItem(singleItemCopy, level, player));
        compat.onDrink(player, drinkItem, thirstBefore);
        if (!resultItem.isEmpty()) {
            ItemStack insertResult = inventory.insertItem(resultItem, false);
            if (!insertResult.isEmpty()) {
                player.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(playerInventory ->
                        InventoryHelper.insertOrDropItem(player, insertResult, playerInventory));
            }
        }
        return true;
    }

    private boolean isDrink(ItemStack stack, IThirstCompat compat) {
        if (!compat.itemRestoresThirst(stack)) return false;
        UseAnim useAnimation = stack.getUseAnimation();
        return useAnimation == UseAnim.DRINK || useAnimation == UseAnim.EAT;
    }

    private boolean meetsPurity(ItemStack stack, IThirstCompat compat) {
        if (!compat.isDrink(stack)) return true;
        return compat.getPurity(stack) >= shouldPurity();
    }

    private boolean isThirstEnoughForDrink(int thirstLevel, ItemStack stack, IThirstCompat compat) {
        int drinkAtThirstLevel = getDrinkAtThirstLevel();
        if (drinkAtThirstLevel == DrinkUpgrade.Data.THIRST_LEVEL_ANY) {
            return true;
        }

        int thirst = compat.getThirst(stack);
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
