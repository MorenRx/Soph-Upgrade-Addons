package top.morenrx.sua.upgrades.compat.drink;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sfiomn.legendarysurvivaloverhaul.api.data.json.JsonMobEffect;
import sfiomn.legendarysurvivaloverhaul.api.data.json.JsonThirstConsumable;
import sfiomn.legendarysurvivaloverhaul.api.data.manager.ThirstDataManager;
import sfiomn.legendarysurvivaloverhaul.api.thirst.HydrationEnum;
import sfiomn.legendarysurvivaloverhaul.api.thirst.ThirstUtil;
import sfiomn.legendarysurvivaloverhaul.common.capabilities.thirst.ThirstCapability;
import sfiomn.legendarysurvivaloverhaul.common.items.drink.CanteenItem;
import sfiomn.legendarysurvivaloverhaul.config.Config;
import sfiomn.legendarysurvivaloverhaul.util.CapabilityUtil;

public class LSOThirstCompat implements IThirstCompat {
    @Override
    public String getName() {
        return DrinkCompatProvider.Data.LEGENDARY_SURVIVAL_OVERHAUL;
    }

    @Override
    public boolean isThirstActive(Player player) {
        return Config.Baked.thirstEnabled && ThirstUtil.isThirstActive(player);
    }

    @Override
    public boolean itemRestoresThirst(ItemStack itemStack) {
        if (itemStack.getItem() instanceof CanteenItem && CanteenItem.canDrink(itemStack)) {
            return true;
        }
        JsonThirstConsumable consumable = ThirstDataManager.getConsumable(itemStack);
        return consumable != null && consumable.hydration > 0;
    }

    @Override
    public boolean isDrink(ItemStack itemStack) {
        if (itemStack.getItem() instanceof CanteenItem) {
            return true;
        }
        return ThirstDataManager.getConsumable(itemStack) != null;
    }

    @Override
    public int getPurity(ItemStack itemStack) {
        HydrationEnum hydrationEnum = ThirstUtil.getHydrationEnumTag(itemStack);
        if (hydrationEnum != null) {
            return switch (hydrationEnum) {
                case NORMAL -> 0;
                case RAIN -> 1;
                case POTION -> 2;
                case PURIFIED -> 3;
            };
        }
        JsonThirstConsumable json = ThirstDataManager.getConsumable(itemStack);
        if (json != null) {
            if (json.effects != null) {
                for (JsonMobEffect effect : json.effects) {
                    if (effect.name != null && effect.name.contains("thirst")) {
                        return 0;
                    }
                }
            }
            return 3;
        }
        return 3;
    }

    @Override
    public int getThirst(ItemStack itemStack) {
        JsonThirstConsumable consumable = ThirstDataManager.getConsumable(itemStack);
        if (consumable != null) {
            return consumable.hydration;
        }
        if (itemStack.getItem() instanceof CanteenItem) {
            HydrationEnum hydrationEnum = ThirstUtil.getHydrationEnumTag(itemStack);
            return hydrationEnum == HydrationEnum.PURIFIED ? 6 : 3;
        }
        return 0;
    }

    @Override
    public int getPlayerThirst(Player player, int defaultValue) {
        ThirstCapability thirstCapability = CapabilityUtil.getThirstCapability(player);
        return thirstCapability != null ? thirstCapability.getHydrationLevel() : defaultValue;
    }
}
