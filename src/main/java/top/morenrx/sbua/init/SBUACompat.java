package top.morenrx.sbua.init;

import net.minecraftforge.fml.loading.FMLLoader;

public class SBUACompat {
    public static final boolean APOTHEOSIS = isModLoaded("apotheosis");
    public static final boolean REFINED_STORAGE = isModLoaded("refinedstorage");
    public static final boolean THIRST = isModLoaded("thirst");


    private static boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }
}
