package top.morenrx.sua.init;

import net.minecraftforge.fml.loading.FMLLoader;

public class SUACompat {
    public static final boolean SOPHISTICATED_BACKPACKS = isModLoaded("sophisticatedbackpacks");
    public static final boolean BACKPACK_SIDE_GUI = isModLoaded("backpack_side_gui");
    public static final boolean APOTHEOSIS = isModLoaded("apotheosis");
    public static final boolean REFINED_STORAGE = isModLoaded("refinedstorage");
    public static final boolean THIRST = isModLoaded("thirst");


    private static boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }
}
