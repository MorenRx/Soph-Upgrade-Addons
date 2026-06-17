package top.morenrx.sua;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.morenrx.sua.init.SUAConfig;
import top.morenrx.sua.init.SUAItems;
import top.morenrx.sua.init.SUANetwork;
import top.morenrx.sua.init.SUARecipes;

@Mod(SophUpgradeAddons.MODID)
public class SophUpgradeAddons {
    public static final String MODID = "soph_upgrade_addons";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SophUpgradeAddons(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        SUAConfig.init(context);
        SUAItems.init(modEventBus);
        SUARecipes.init(modEventBus);
        SUANetwork.init();
    }

}
