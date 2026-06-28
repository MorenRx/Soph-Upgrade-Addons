package top.morenrx.sua.upgrades.drink.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ThirstCompat {
    boolean itemRestoresThirst(ItemStack itemStack);
    boolean isDrink(ItemStack itemStack);
    int getPurity(ItemStack itemStack);
    int getThirst(ItemStack itemStack);
    int getPlayerThirst(Player player, int defaultValue);
}
