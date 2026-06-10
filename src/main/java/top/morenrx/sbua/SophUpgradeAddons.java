package top.morenrx.sbua;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.morenrx.sbua.init.SBUAItems;
import top.morenrx.sbua.init.SBUANetwork;

@Mod(SBUAInit.MODID)
public class SBUAInit {
    public static final String MODID = "soph_upgrade_addons";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SBUAInit(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.SERVER, SBUAConfig.INSTANCE.SPEC);

        SBUAItems.init(modEventBus);
        SBUANetwork.NETWORK_HANDLER.register();
    }

}
