package top.morenrx.sua.crafting;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;

public class ItemEnabledCondition implements ICondition {
    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(SophUpgradeAddons.MODID, "item_enabled");
    private final ResourceLocation itemRegistryName;

    public ItemEnabledCondition(ResourceLocation itemRegistryName) {
        this.itemRegistryName = itemRegistryName;
    }

    public ResourceLocation getID() {
        return NAME;
    }

    public boolean test(ICondition.IContext context) {
        Item item = ForgeRegistries.ITEMS.getValue(itemRegistryName);
        return !(item instanceof ISUAItemConfig config) || config.isEnable();
    }

    public static class Serializer implements IConditionSerializer<ItemEnabledCondition> {
        public static final ItemEnabledCondition.Serializer INSTANCE = new ItemEnabledCondition.Serializer();

        public Serializer() {
        }

        public void write(JsonObject json, ItemEnabledCondition value) {
            json.addProperty("itemRegistryName", value.itemRegistryName.toString());
        }

        public ItemEnabledCondition read(JsonObject json) {
            return new ItemEnabledCondition(ResourceLocation.parse(GsonHelper.getAsString(json, "itemRegistryName")));
        }

        public ResourceLocation getID() {
            return ItemEnabledCondition.NAME;
        }
    }
}
