package top.morenrx.sua.init;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.crafting.ItemEnabledCondition;

import java.util.function.Supplier;

public class SUARecipes {
    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, SophUpgradeAddons.MODID);

    public static final Supplier<MapCodec<ItemEnabledCondition>> ITEM_ENABLED_CONDITION =
            CONDITION_CODECS.register("item_enabled", () -> ItemEnabledCondition.CODEC);

    public static void init(IEventBus modEventBus) {
        CONDITION_CODECS.register(modEventBus);
    }
}
