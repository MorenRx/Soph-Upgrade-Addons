package top.morenrx.sbua.init;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import top.morenrx.sbua.crafting.ItemEnabledCondition;

public class SBUARecipes {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(SBUARecipes::registerRecipeCondition);
    }

    private static void registerRecipeCondition(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            CraftingHelper.register(ItemEnabledCondition.Serializer.INSTANCE);
        }
    }
}
