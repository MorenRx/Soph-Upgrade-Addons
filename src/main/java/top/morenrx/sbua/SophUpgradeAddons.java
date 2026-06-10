package top.morenrx.sbua;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.morenrx.sbua.init.SBUAItems;
import top.morenrx.sbua.init.SBUANetwork;
import top.morenrx.sbua.init.SBUARecipes;

@Mod(SophUpgradeAddons.MODID)
public class SophUpgradeAddons {
    public static final String MODID = "soph_upgrade_addons";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SophUpgradeAddons(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        SBUAConfig.init(context);
        SBUAItems.init(modEventBus);
        SBUARecipes.init(modEventBus);
        SBUANetwork.init();
    }

}
