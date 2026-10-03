package top.morenrx.sua;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import top.morenrx.sua.helper.SalvagingHelper;
import top.morenrx.sua.init.*;

@Mod(SophUpgradeAddons.MODID)
public class SophUpgradeAddons {
    public static final String MODID = "soph_upgrade_addons";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SophUpgradeAddons(IEventBus modEventBus, ModContainer modContainer) {
        SUAConfig.init(modContainer);
        SUADataComponents.register(modEventBus);
        SUAItems.init(modEventBus);
        SUARecipes.init(modEventBus);
        SUANetwork.init(modEventBus);

        SalvagingHelper.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            SUAClient.init(modEventBus);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static ResourceLocation id(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation parse(String location) {
        return ResourceLocation.parse(location);
    }
}
