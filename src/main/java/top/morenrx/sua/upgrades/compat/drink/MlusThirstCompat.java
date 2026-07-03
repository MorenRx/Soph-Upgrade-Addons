package top.morenrx.sua.upgrades.compat.drink;

import cn.mlus.thirst.api.ThirstHelper;
import cn.mlus.thirst.foundation.common.capability.IThirst;
import cn.mlus.thirst.foundation.common.capability.ModCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MlusThirstCompat implements IThirstCompat {
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
        return player.getCapability(ModCapabilities.PLAYER_THIRST).map(IThirst::getThirst).orElse(defaultValue);
    }
}
