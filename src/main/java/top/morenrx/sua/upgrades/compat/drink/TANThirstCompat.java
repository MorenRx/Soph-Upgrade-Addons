package top.morenrx.sua.upgrades.compat.drink;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.morenrx.sua.SophUpgradeAddons;
import toughasnails.api.thirst.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.init.ModTags;

public class TANThirstCompat implements IThirstCompat {
    @Override
    public String getName() {
        return DrinkCompatProvider.Data.TOUGH_AS_NAILS;
    }

    @Override
    public boolean isThirstActive(Player player) {
        return ThirstHelper.isThirstEnabled();
    }

    @Override
    public boolean itemRestoresThirst(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.DRINKS);
    }

    @Override
    public boolean isDrink(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.DRINKS);
    }

    @Override
    public int getPurity(ItemStack itemStack) {
        if (itemStack.is(ModTags.Items.SEVENTY_FIVE_POISON_CHANCE_DRINKS) || itemStack.is(ModTags.Items.ONE_HUNDRED_POISON_CHANCE_DRINKS)) {
            return 0;
        }
        if (itemStack.is(ModTags.Items.TWENTY_FIVE_POISON_CHANCE_DRINKS) || itemStack.is(ModTags.Items.FIFTY_POISON_CHANCE_DRINKS)) {
            return 2;
        }
        return 3;
    }

    @Override
    public int getThirst(ItemStack itemStack) {
        return ModTags.Items.getThirstRestored(itemStack);
    }

    @Override
    public int getPlayerThirst(Player player, int defaultValue) {
        IThirst thirst = ThirstHelper.getThirst(player);
        return thirst != null ? thirst.getThirst() : defaultValue;
    }

    @Override
    public void onDrink(Player player, ItemStack drinkItem, int thirstBefore) {
        if (!ThirstHelper.isThirstEnabled() || player.level().isClientSide()) {
            return;
        }

        IThirst thirst = ThirstHelper.getThirst(player);
        if (thirst == null) {
            return;
        }
        if (drinkItem.is(ModTags.Items.DRINKS)) {
            int thirstRestored = ModTags.Items.getThirstRestored(drinkItem);
            float hydration = getHydration(drinkItem);
            float poisonChance = getPoisonChance(drinkItem);

            thirst.addThirst(thirstRestored);
            thirst.addHydration(hydration);

            if (player.level().random.nextFloat() < poisonChance) {
                MobEffect thirstEffect = ForgeRegistries.MOB_EFFECTS.getValue(SophUpgradeAddons.id("toughasnails", "thirst"));
                if (thirstEffect != null) {
                    player.addEffect(new MobEffectInstance(thirstEffect, 600));
                }
            }
        }
    }

    private float getHydration(ItemStack itemStack) {
        if (itemStack.is(ModTags.Items.ONE_HUNDRED_HYDRATION_DRINKS)) return 1.0F;
        if (itemStack.is(ModTags.Items.NINETY_HYDRATION_DRINKS)) return 0.9F;
        if (itemStack.is(ModTags.Items.EIGHTY_HYDRATION_DRINKS)) return 0.8F;
        if (itemStack.is(ModTags.Items.SEVENTY_HYDRATION_DRINKS)) return 0.7F;
        if (itemStack.is(ModTags.Items.SIXTY_HYDRATION_DRINKS)) return 0.6F;
        if (itemStack.is(ModTags.Items.FIFTY_HYDRATION_DRINKS)) return 0.5F;
        if (itemStack.is(ModTags.Items.FOURTY_HYDRATION_DRINKS)) return 0.4F;
        if (itemStack.is(ModTags.Items.THIRTY_HYDRATION_DRINKS)) return 0.3F;
        if (itemStack.is(ModTags.Items.TWENTY_HYDRATION_DRINKS)) return 0.2F;
        if (itemStack.is(ModTags.Items.TEN_HYDRATION_DRINKS)) return 0.1F;
        return 0.0F;
    }

    private float getPoisonChance(ItemStack itemStack) {
        if (itemStack.is(ModTags.Items.ONE_HUNDRED_POISON_CHANCE_DRINKS)) return 1.0F;
        if (itemStack.is(ModTags.Items.SEVENTY_FIVE_POISON_CHANCE_DRINKS)) return 0.75F;
        if (itemStack.is(ModTags.Items.FIFTY_POISON_CHANCE_DRINKS)) return 0.5F;
        if (itemStack.is(ModTags.Items.TWENTY_FIVE_POISON_CHANCE_DRINKS)) return 0.25F;
        return 0.0F;
    }
}

