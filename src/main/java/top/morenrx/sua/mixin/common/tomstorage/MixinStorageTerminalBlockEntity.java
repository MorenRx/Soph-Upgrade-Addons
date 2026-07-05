package top.morenrx.sua.mixin.common.tomstorage;

import com.tom.storagemod.tile.StorageTerminalBlockEntity;
import com.tom.storagemod.util.StoredItemStack;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.morenrx.sua.access.tomstorage.IStorageTerminalBlockEntityAccess;

@Mixin(value = StorageTerminalBlockEntity.class, remap = false)
public class MixinStorageTerminalBlockEntity implements IStorageTerminalBlockEntityAccess {

    @Shadow
    private IItemHandler itemHandler;
    @Shadow
    private boolean updateItems;

    @Override
    public ItemStack sua$pushStack(ItemStack itemstack, boolean simulate) {
        if (itemHandler == null) {
            updateItems = true;
            ((StorageTerminalBlockEntity) (Object) this).updateServer();
        }
        StoredItemStack is = this.sua$pushStack(new StoredItemStack(itemstack), simulate);
        return is == null ? ItemStack.EMPTY : is.getActualStack();
    }

    @Unique
    private StoredItemStack sua$pushStack(StoredItemStack stack, boolean simulate) {
        if (stack != null && this.itemHandler != null) {
            ItemStack is = ItemHandlerHelper.insertItemStacked(this.itemHandler, stack.getActualStack(), simulate);
            return is.isEmpty() ? null : new StoredItemStack(is);
        } else {
            return stack;
        }
    }
}
