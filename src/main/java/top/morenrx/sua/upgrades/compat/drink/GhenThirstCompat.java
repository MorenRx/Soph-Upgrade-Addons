package top.morenrx.sua.upgrades.compat.drink;

import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GhenThirstCompat implements IThirstCompat {
    @Override
    public String getName() {
        return DrinkCompatProvider.Data.THIRST_WAS_TAKEN;
    }

    @Override
    public boolean isThirstActive(Player player) {
        return player.hasData(ModAttachment.PLAYER_THIRST);
    }

    @Override
    public boolean itemRestoresThirst(ItemStack itemStack) {
        return ThirstHelper.itemRestoresThirst(itemStack);
    }

    @Override
    public boolean isDrink(ItemStack itemStack) {
        return ThirstHelper.isDrink(itemStack);
    }

    @Override
    public int getPurity(ItemStack itemStack) {
        return ThirstHelper.getPurity(itemStack);
    }

    @Override
    public int getThirst(ItemStack itemStack) {
        return ThirstHelper.getThirst(itemStack);
    }

    @Override
    public int getPlayerThirst(Player player, int defaultValue) {
        if (player.hasData(ModAttachment.PLAYER_THIRST)) {
            var thirst = player.getData(ModAttachment.PLAYER_THIRST);
            return thirst != null ? thirst.getThirst() : defaultValue;
        }
        return defaultValue;
    }
}
