package top.morenrx.sua.upgrades.compat.drink;

import cn.mlus.thirst.api.ThirstHelper;
import cn.mlus.thirst.foundation.common.capability.IThirst;
import cn.mlus.thirst.foundation.common.capability.ModAttachment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MlusThirstCompat implements IThirstCompat {
    @Override
    public String getName() {
        return DrinkCompatProvider.Data.THIRST_WAS_RECLAIMED;
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
            IThirst thirst = player.getData(ModAttachment.PLAYER_THIRST);
            return thirst != null ? thirst.getThirst() : defaultValue;
        }
        return defaultValue;
    }
}
