package top.morenrx.sua.upgrades.compat.drink;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IThirstCompat {
    String getName();
    default boolean isThirstActive(Player player) { return true; }
    boolean itemRestoresThirst(ItemStack itemStack);
    boolean isDrink(ItemStack itemStack);
    int getPurity(ItemStack itemStack);
    int getThirst(ItemStack itemStack);
    int getPlayerThirst(Player player, int defaultValue);
    default void onDrink(Player player, ItemStack drinkItem, int thirstBefore) {}
}
