package top.morenrx.sua.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.morenrx.sua.SophUpgradeAddons;

import java.util.function.Supplier;

public class SUADataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, SophUpgradeAddons.MODID);

    public static final Supplier<DataComponentType<Boolean>> ENABLE_VOID = DATA_COMPONENT_TYPES.register("enable_void",
            () -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final Supplier<DataComponentType<String>> NETWORK_TYPE = DATA_COMPONENT_TYPES.register("network_type",
            () -> new DataComponentType.Builder<String>().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final Supplier<DataComponentType<Integer>> THIRST_LEVEL = DATA_COMPONENT_TYPES.register("thirst_level",
            () -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<Boolean>> DRINK_FOR_HURT = DATA_COMPONENT_TYPES.register("drink_for_hurt",
            () -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final Supplier<DataComponentType<Integer>> PURITY = DATA_COMPONENT_TYPES.register("purity",
            () -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<Integer>> EQUIPMENT_RARITY_MASK = DATA_COMPONENT_TYPES.register("equipment_rarity_mask",
            () -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<Integer>> GEM_RARITY_MASK = DATA_COMPONENT_TYPES.register("gem_rarity_mask",
            () -> new DataComponentType.Builder<Integer>().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<Boolean>> SALVAGING_EQUIPMENT = DATA_COMPONENT_TYPES.register("salvaging_equipment",
            () -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final Supplier<DataComponentType<Boolean>> SALVAGING_GEM = DATA_COMPONENT_TYPES.register("salvaging_gem",
            () -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final Supplier<DataComponentType<Boolean>> SALVAGING_OTHER = DATA_COMPONENT_TYPES.register("salvaging_other",
            () -> new DataComponentType.Builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static void register(IEventBus modBus) {
        DATA_COMPONENT_TYPES.register(modBus);
    }
}
