package top.morenrx.sua.init;

import net.minecraftforge.fml.loading.FMLLoader;

import java.util.function.BooleanSupplier;

public class SUACompat {
    public static final BooleanSupplier SOPHISTICATED_BACKPACKS = isModLoaded("sophisticatedbackpacks");
    public static final BooleanSupplier BACKPACK_SIDE_GUI = isModLoaded("backpack_side_gui");
    public static final BooleanSupplier APOTHEOSIS = isModLoaded("apotheosis");
    public static final BooleanSupplier REFINED_STORAGE = isModLoaded("refinedstorage");
    public static final BooleanSupplier APPLIED_ENERGISTICS = isModLoaded("ae2");
    public static final BooleanSupplier BEYOND_DIMENSIONS = isModLoaded("beyonddimensions");
    public static final BooleanSupplier TOMS_STORAGE = isModLoaded("toms_storage");
    public static final BooleanSupplier THIRST = isModLoaded("thirst");


    private static BooleanSupplier isModLoaded(String modId) {
        return () -> FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }
}
