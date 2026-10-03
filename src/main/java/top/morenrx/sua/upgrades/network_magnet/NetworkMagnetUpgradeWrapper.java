package top.morenrx.sua.upgrades.network_magnet;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;
import net.p3pp3rf1y.sophisticatedcore.init.ModFluids;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.*;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.IMagnetPreventionChecker;
import net.p3pp3rf1y.sophisticatedcore.util.XpHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.data.NetworkLocation;
import top.morenrx.sua.helper.SalvagingHelper;
import top.morenrx.sua.init.SUADataComponents;
import top.morenrx.sua.mixin.common.sophisticatedcore.MagnetUpgradeWrapperAccessor;
import top.morenrx.sua.upgrades.compat.network.INetworkStorage;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeWrapper;
import top.morenrx.sua.util.SUAUtils;

import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(modid = SophUpgradeAddons.MODID)
public class NetworkMagnetUpgradeWrapper extends UpgradeWrapperBase<NetworkMagnetUpgradeWrapper, NetworkMagnetUpgrade>
        implements IContentsFilteredUpgrade, ITickableUpgrade, IPickupResponseUpgrade {
    private static final int COOLDOWN_TICKS = 10;
    private static long nextTickTime = Long.MIN_VALUE;

    @SubscribeEvent
    public static void globalPostTick(LevelTickEvent.Pre event) {
        if (event.getLevel().isClientSide())
            return;

        long gameTime = event.getLevel().getGameTime();
        if (gameTime > nextTickTime)
            nextTickTime = gameTime + COOLDOWN_TICKS;
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload evt) {
        nextTickTime = Long.MIN_VALUE;
    }

    private static final int FULL_COOLDOWN_TICKS = 40;
    private final ContentsFilterLogic filterLogic;
    private Player playerCache = null;
    private NetworkLocation locationCache = null;

    public NetworkMagnetUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        filterLogic = new ContentsFilterLogic(upgrade, stack -> save(), upgradeItem.getFilterSlotCount(), storageWrapper::getInventoryHandler,
                storageWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class), ModCoreDataComponents.FILTER_ATTRIBUTES);
    }

    private boolean isInCooldown(Level level, @Nullable Entity entity) {
        if (!(entity instanceof Player))
            return super.isInCooldown(level);

        return nextTickTime > level.getGameTime();
    }

    @Override
    public @NotNull ContentsFilterLogic getFilterLogic() {
        return filterLogic;
    }

    public NetworkLocation getNetworkLocation() {
        if (locationCache == null)
            locationCache = NetworkLocation.fromUpgrade(upgrade);
        return locationCache;
    }

    @Override
    public @NotNull ItemStack pickup(@NotNull Level world, @NotNull ItemStack stack, boolean simulate) {
        if (!shouldPickupItems() || !filterLogic.matchesFilter(stack) || !(world instanceof ServerLevel level))
            return stack;

        if (playerCache == null)
            playerCache = SUAUtils.Backpack.getBackpackOwner(level, storageWrapper.getContentsUuid().orElse(null));

        NetworkLocation location = getNetworkLocation();
        INetworkStorage storage = location.storage();
        if (storage == null || !location.hasBinding())
            return stack;

        SalvagingUpgradeWrapper wrapper;
        if (!simulate && (wrapper = SalvagingHelper.shouldSalvaging(storageWrapper, stack)) != null) {
            int consumeCount = wrapper.trySalvagingAndInsertItem(stack, (tempStack, tempSimulate) ->
                    storage.insert(storageWrapper, upgrade, level, playerCache, tempStack, tempSimulate, location));
            if (consumeCount <= 0)
                return stack;
            if (consumeCount == stack.getCount())
                return ItemStack.EMPTY;

            ItemStack copy = stack.copy();
            copy.setCount(copy.getCount() - consumeCount);
            return copy;
        }

        if (shouldEnableVoid() && SUAUtils.Backpack.shouldDestroy(storageWrapper, stack))
            return ItemStack.EMPTY;

        return storage.insert(storageWrapper, upgrade, level, playerCache, stack, simulate, location);
    }

    @Override
    public void tick(@Nullable Entity entity, @NotNull Level world, @NotNull BlockPos pos) {
        if (isInCooldown(world, entity)) return;

        if (world instanceof ServerLevel level) {
            if (this.playerCache != entity && entity instanceof Player player && !(player instanceof FakePlayer)) {
                this.playerCache = player;
            } else if (this.playerCache == null || this.playerCache.isRemoved()) {
                this.playerCache = SUAUtils.Backpack.getBackpackOwner(level, storageWrapper.getContentsUuid().orElse(null));
                if (this.playerCache instanceof FakePlayer) {
                    this.playerCache = null;
                }
            }
        }

        int cooldown = shouldPickupItems() ? pickupItems(entity, world, pos) : FULL_COOLDOWN_TICKS;

        if (shouldPickupXp() && canFillStorageWithXp())
            cooldown = Math.min(cooldown, pickupXpOrbs(entity, world, pos));

        setCooldown(world, cooldown);
    }

    private boolean canFillStorageWithXp() {
        return storageWrapper.getFluidHandler().map(fluidHandler -> fluidHandler.fill(ModFluids.EXPERIENCE_TAG, 1, ModFluids.XP_STILL.get(), IFluidHandler.FluidAction.SIMULATE) > 0).orElse(false);
    }

    private int pickupXpOrbs(@Nullable Entity entity, Level world, BlockPos pos) {
        List<ExperienceOrb> xpEntities = world.getEntitiesOfClass(ExperienceOrb.class, new AABB(pos).inflate(upgradeItem.getRadius()), e -> true);
        if (xpEntities.isEmpty())
            return COOLDOWN_TICKS;

        int cooldown = COOLDOWN_TICKS;
        for (ExperienceOrb xpOrb : xpEntities) {
            if (xpOrb.isAlive() && !canNotPickup(xpOrb, entity) && !tryToFillTank(xpOrb, entity, world)) {
                cooldown = FULL_COOLDOWN_TICKS;
                break;
            }
        }
        return cooldown;
    }

    private boolean tryToFillTank(ExperienceOrb xpOrb, @Nullable Entity entity, Level world) {
        int amountToTransfer = XpHelper.experienceToLiquid(xpOrb.getValue());

        return storageWrapper.getFluidHandler().map(fluidHandler -> {
            int amountAdded = fluidHandler.fill(ModFluids.EXPERIENCE_TAG, amountToTransfer, ModFluids.XP_STILL.get(), IFluidHandler.FluidAction.EXECUTE);

            if (amountAdded > 0) {
                Vec3 pos = xpOrb.position();
                xpOrb.value = 0;
                xpOrb.discard();

                if (entity instanceof Player player)
                    playXpPickupSound(world, player);

                if (amountToTransfer > amountAdded)
                    world.addFreshEntity(new ExperienceOrb(world, pos.x(), pos.y(), pos.z(), (int) XpHelper.liquidToExperience(amountToTransfer - amountAdded)));
                return true;
            }
            return false;
        }).orElse(false);
    }

    private int pickupItems(@Nullable Entity entity, Level world, BlockPos pos) {
        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(upgradeItem.getRadius()), e -> true);
        if (itemEntities.isEmpty())
            return COOLDOWN_TICKS;

        Player player = entity instanceof Player ? (Player) entity : null;

        int cooldown = FULL_COOLDOWN_TICKS;
        for (ItemEntity itemEntity : itemEntities) {
            if (!itemEntity.isAlive() || itemEntity.pickupDelay == ItemEntity.INFINITE_PICKUP_DELAY || !filterLogic.matchesFilter(itemEntity.getItem()) || canNotPickup(itemEntity, entity))
                continue;
            if (tryToInsertItem(itemEntity)) {
                if (player != null)
                    playItemPickupSound(world, player);
                cooldown = COOLDOWN_TICKS;
            }
        }
        return cooldown;
    }

    private static void playItemPickupSound(Level world, @NotNull Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (world.random.nextFloat() - world.random.nextFloat()) * 1.4F + 2.0F);
    }

    private static void playXpPickupSound(Level world, @NotNull Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, (world.random.nextFloat() - world.random.nextFloat()) * 0.35F + 0.9F);
    }

    private boolean isBlockedBySomething(Entity entity) {
        for (IMagnetPreventionChecker checker : MagnetUpgradeWrapperAccessor.sua$getMagnetCheckers()) {
            if (checker.isBlocked(entity)) {
                return true;
            }
        }
        return false;
    }

    private boolean canNotPickup(Entity pickedUpEntity, @Nullable Entity entity) {
        if (isBlockedBySomething(pickedUpEntity)) {
            return true;
        }

        CompoundTag data = pickedUpEntity.getPersistentData();
        return entity instanceof Player ? data.contains(NetworkMagnetUpgrade.Data.KEY_PREVENT_REMOTE_MOVEMENT) : data.contains(NetworkMagnetUpgrade.Data.KEY_PREVENT_REMOTE_MOVEMENT) && !data.contains(NetworkMagnetUpgrade.Data.KEY_ALLOW_MACHINE_MOVEMENT);
    }

    private boolean tryToInsertItem(ItemEntity itemEntity) {
        if (!(itemEntity.level() instanceof ServerLevel level))
            return false;

        NetworkLocation location = getNetworkLocation();
        INetworkStorage storage = location.storage();
        if (storage == null || !location.hasBinding())
            return false;

        ItemStack stack = itemEntity.getItem();
        int originalCount = stack.getCount();

        SalvagingUpgradeWrapper wrapper;
        if ((wrapper = SalvagingHelper.shouldSalvaging(storageWrapper, stack)) != null) {
            int consumeCount = wrapper.trySalvagingAndInsertItem(stack, (tempStack, tempSimulate) ->
                    storage.insert(storageWrapper, upgrade, level, playerCache, tempStack, tempSimulate, location));
            if (consumeCount <= 0)
                return false;
            if (consumeCount == stack.getCount()) {
                itemEntity.setItem(ItemStack.EMPTY);
            } else {
                ItemStack copy = stack.copy();
                copy.setCount(copy.getCount() - consumeCount);
                itemEntity.setItem(copy);
            }
            if (playerCache != null && !(playerCache instanceof FakePlayer)) {
                playerCache.awardStat(Stats.ITEM_PICKED_UP.get(stack.getItem()), consumeCount);
            }
            return true;
        }

        if (shouldEnableVoid() && SUAUtils.Backpack.shouldDestroy(storageWrapper, stack)) {
            itemEntity.setItem(ItemStack.EMPTY);
            if (playerCache != null && !(playerCache instanceof FakePlayer)) {
                playerCache.awardStat(Stats.ITEM_PICKED_UP.get(stack.getItem()), originalCount);
            }
            return true;
        }

        ItemStack remainingStack = storage.insert(storageWrapper, upgrade, level, playerCache, stack, true, location);
        if (remainingStack.getCount() >= originalCount)
            return false;
        remainingStack = storage.insert(storageWrapper, upgrade, level, playerCache, stack, false, location);

        itemEntity.setItem(remainingStack);
        if (playerCache != null && !(playerCache instanceof FakePlayer)) {
            playerCache.awardStat(Stats.ITEM_PICKED_UP.get(stack.getItem()), originalCount - remainingStack.getCount());
        }
        return true;
    }

    public void setPickupItems(boolean pickupItems) {
        upgrade.set(ModCoreDataComponents.PICKUP_ITEMS, pickupItems);
        save();
    }

    public boolean shouldPickupItems() {
        return upgrade.getOrDefault(ModCoreDataComponents.PICKUP_ITEMS, true);
    }

    public void setPickupXp(boolean pickupXp) {
        upgrade.set(ModCoreDataComponents.PICKUP_XP, pickupXp);
        save();
    }

    public boolean shouldPickupXp() {
        return upgrade.getOrDefault(ModCoreDataComponents.PICKUP_XP, true);
    }

    public void setEnableVoid(boolean enableVoid) {
        upgrade.set(SUADataComponents.ENABLE_VOID, enableVoid);
        save();
    }

    public boolean shouldEnableVoid() {
        return upgrade.getOrDefault(SUADataComponents.ENABLE_VOID, true);
    }

    public void setNetworkType(String networkType) {
        upgrade.set(SUADataComponents.NETWORK_TYPE, networkType);
        locationCache = null;
        save();
    }

    public String shouldNetworkType() {
        return getNetworkLocation().storageType();
    }
}