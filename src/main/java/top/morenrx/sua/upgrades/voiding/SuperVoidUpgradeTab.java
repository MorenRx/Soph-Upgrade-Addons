package top.morenrx.sua.upgrades.voiding;

import net.minecraftforge.fml.loading.FMLLoader;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.WidgetBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeTab;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import java.lang.reflect.Field;

public class SuperVoidUpgradeTab extends VoidUpgradeTab {

    private static final ArtifactVersion FLUID_VOID_SUPPORT_VERSION = new DefaultArtifactVersion("1.4.85.2251");

    public SuperVoidUpgradeTab(VoidUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow) {
        super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("super_void"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("super_void"));
        ArtifactVersion currentVersion = new DefaultArtifactVersion(FMLLoader.getLoadingModList().getModFileById(SophisticatedCore.MOD_ID).versionString());
        try {
            WidgetBase control = currentVersion.compareTo(FLUID_VOID_SUPPORT_VERSION) >= 0
                    ? createFluidVoidControl(screen, slotsPerRow)
                    : createLegacyVoidControl(screen, slotsPerRow);

            Field field = VoidUpgradeTab.class.getDeclaredField("filterLogicControl");
            field.setAccessible(true);
            field.set(this, this.addHideableChild(control));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("[SUA] SuperVoidUpgradeTab 反射失败", e);
        }
    }

    private WidgetBase createFluidVoidControl(StorageScreenBase<?> screen, int slotsPerRow) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName("net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidFilterLogicControl$Advanced");
        Object fluidFilterContainer = VoidUpgradeContainer.class.getMethod("getFluidFilterContainer").invoke(this.getContainer());
        return (WidgetBase) clazz.getConstructors()[0].newInstance(screen, new Position(this.x + 3, this.y + 44),
                getContainer().getFilterLogicContainer(), fluidFilterContainer, slotsPerRow);
    }

    private WidgetBase createLegacyVoidControl(StorageScreenBase<?> screen, int slotsPerRow) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName("net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicControl$Advanced");
        return (WidgetBase) clazz.getConstructors()[0].newInstance(screen, new Position(x + 3, y + 44),
                getContainer().getFilterLogicContainer(), slotsPerRow);
    }
}
