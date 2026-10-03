package top.morenrx.sua.mixin.common.sophisticatedcore;

import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.IMagnetPreventionChecker;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = MagnetUpgradeWrapper.class, remap = false)
public interface MagnetUpgradeWrapperAccessor {
    @Accessor("magnetCheckers")
    static Set<IMagnetPreventionChecker> sua$getMagnetCheckers() {
        throw new AssertionError();
    }
}
