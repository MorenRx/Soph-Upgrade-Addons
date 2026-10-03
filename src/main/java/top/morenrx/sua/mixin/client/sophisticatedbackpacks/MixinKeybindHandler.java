package top.morenrx.sua.mixin.client.sophisticatedbackpacks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler;
import net.p3pp3rf1y.sophisticatedcore.util.CapabilityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.morenrx.sua.helper.client.NetworkKeybindHelper;

import java.util.function.Function;

@Mixin(value = KeybindHandler.class, remap = false)
public class MixinKeybindHandler {

    @Redirect(
            method = "sendInteractWithInventoryMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedcore/util/CapabilityHelper;getFromItemHandler(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/function/Function;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private static Object redirectGetFromItemHandler(Level level, BlockPos pos, Function<IItemHandler, Object> get, Object defaultValue) {
        if (Boolean.TRUE.equals(CapabilityHelper.getFromItemHandler(level, pos, get, defaultValue)))
            return true;

        BlockEntity te = level.getBlockEntity(pos);
        return NetworkKeybindHelper.isNetworkStorageBlock(te);
    }
}
