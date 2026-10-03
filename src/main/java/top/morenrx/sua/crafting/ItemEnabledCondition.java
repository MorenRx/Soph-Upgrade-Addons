package top.morenrx.sua.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ICondition;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;

public record ItemEnabledCondition(ResourceLocation itemRegistryName) implements ICondition {
    public static final MapCodec<ItemEnabledCondition> CODEC = RecordCodecBuilder
            .mapCodec(builder -> builder.group(ResourceLocation.CODEC.fieldOf("itemRegistryName").forGetter(ItemEnabledCondition::itemRegistryName))
                    .apply(builder, ItemEnabledCondition::new));

    public ItemEnabledCondition(Item item) {
        this(BuiltInRegistries.ITEM.getKey(item));
    }

    @Override
    public boolean test(IContext context) {
        Item item = BuiltInRegistries.ITEM.get(itemRegistryName);
        return !(item instanceof ISUAItemConfig config) || config.isEnable();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
