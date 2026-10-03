package top.morenrx.sua.init;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;

import java.util.function.BooleanSupplier;

public class SUACompat {
    public static final BooleanSupplier SOPHISTICATED_BACKPACKS = isModLoaded("sophisticatedbackpacks");
    public static final BooleanSupplier APOTHEOSIS = isModLoaded("apotheosis");
    public static final BooleanSupplier REFINED_STORAGE = isModLoaded("refinedstorage");
    public static final BooleanSupplier APPLIED_ENERGISTICS = isModLoaded("ae2");
    public static final BooleanSupplier BEYOND_DIMENSIONS = isModLoaded("beyonddimensions");
    public static final BooleanSupplier TOMS_STORAGE = isModLoaded("toms_storage");
    public static final BooleanSupplier THIRST = isModLoaded("thirst");
    public static final BooleanSupplier THIRST_WAS_TAKEN = isModLoaded("thirst_was_taken");
    public static final BooleanSupplier TOUGH_AS_NAILS = isModLoaded("toughasnails");
    public static final BooleanSupplier LEGENDARY_SURVIVAL = isModLoaded("legendarysurvivaloverhaul");

    private static BooleanSupplier isModLoaded(String modId) {
        return () -> ModList.get() != null ? ModList.get().isLoaded(modId) : LoadingModList.get().getModFileById(modId) != null;
    }
}
