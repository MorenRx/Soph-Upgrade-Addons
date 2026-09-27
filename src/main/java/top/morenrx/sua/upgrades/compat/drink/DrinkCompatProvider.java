package top.morenrx.sua.upgrades.compat.drink;

import net.minecraft.world.item.ItemStack;
import top.morenrx.sua.init.SUACompat;

import java.util.LinkedHashMap;
import java.util.Map;

public class DrinkCompatProvider {
    public static class Data {
        public static final String THIRST_WAS_RECLAIMED = "thirst_was_reclaimed";
        public static final String THIRST_WAS_TAKEN = "thirst_was_taken";
        public static final String LEGENDARY_SURVIVAL_OVERHAUL = "legendary_survival_overhaul";
        public static final String TOUGH_AS_NAILS = "tough_as_nails";
    }

    private static final DrinkCompatProvider INSTANCE = new DrinkCompatProvider();

    private final Map<String, IThirstCompat> compats = new LinkedHashMap<>();

    public static DrinkCompatProvider get() {
        return INSTANCE;
    }

    private DrinkCompatProvider() {
        if (SUACompat.THIRST.getAsBoolean()) {
            if (isThirstWasReclaimed()) {
                this.compats.put(Data.THIRST_WAS_RECLAIMED, new MlusThirstCompat());
            } else {
                this.compats.put(Data.THIRST_WAS_TAKEN, new GhenThirstCompat());
            }
        }

        if (SUACompat.LEGENDARY_SURVIVAL.getAsBoolean()) {
            this.compats.put(Data.LEGENDARY_SURVIVAL_OVERHAUL, new LSOThirstCompat());
        }

        if (SUACompat.TOUGH_AS_NAILS.getAsBoolean()) {
            this.compats.put(Data.TOUGH_AS_NAILS, new TANThirstCompat());
        }
    }

    private static boolean isThirstWasReclaimed() {
        try {
            Class.forName("cn.mlus.thirst.api.ThirstHelper", false, DrinkCompatProvider.class.getClassLoader());
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public boolean hasAnyThirstMod() {
        return !this.compats.isEmpty();
    }

    public Map<String, IThirstCompat> getCompats() {
        return this.compats;
    }

    public boolean itemRestoresThirst(ItemStack itemStack) {
        for (IThirstCompat compat : this.compats.values()) {
            if (compat.itemRestoresThirst(itemStack)) {
                return true;
            }
        }
        return false;
    }
}
