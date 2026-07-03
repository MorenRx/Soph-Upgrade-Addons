package top.morenrx.sua.upgrades.compat.drink;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IThirstCompat {
    boolean itemRestoresThirst(ItemStack itemStack);
    boolean isDrink(ItemStack itemStack);
    int getPurity(ItemStack itemStack);
    int getThirst(ItemStack itemStack);
    int getPlayerThirst(Player player, int defaultValue);
}
