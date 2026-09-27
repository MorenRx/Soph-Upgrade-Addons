package top.morenrx.sua.mixin.client.sophisticatedbackpacks;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.morenrx.sua.helper.client.NetworkKeybindHelper;

@Mixin(value = KeybindHandler.class, remap = false)
public class MixinKeybindHandler {

    @Inject(method = "lambda$sendInteractWithInventoryMessage$1", at = @At("HEAD"), cancellable = true)
    private static void onCheckItemHandlerCapability(BlockEntity te, CallbackInfoReturnable<Boolean> cir) {
        if (NetworkKeybindHelper.isNetworkStorageBlock(te))
            cir.setReturnValue(true);
    }
}
