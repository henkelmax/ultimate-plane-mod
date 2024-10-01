package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.DamageSourcePlane;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class EntityPlaneDamageBase extends EntityFlyableBase {

    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(EntityPlaneDamageBase.class, EntityDataSerializers.FLOAT);

    public EntityPlaneDamageBase(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        Runnable task;
        while ((task = tasks.poll()) != null) {
            task.run();
        }

        if (isInLava()) {
            setPlaneDamage(getPlaneDamage() + 1F);
        }

        handleParticles();
    }

    protected void handleParticles() {
        if (!level().isClientSide) {
            return;
        }

        if (!((EntityPlaneSoundBase) this).isStarted()) {
            return;
        }

        float damage = getPlaneDamage();

        float chance = Math.max(damage - 25F, 0) / 100F;

        if (random.nextFloat() < chance) {
            Vec3 lookVec = getLookAngle().normalize().scale(1.5D);
            spawnParticle(ParticleTypes.LARGE_SMOKE, lookVec.x, lookVec.y, lookVec.z);
        }
    }

    private void spawnParticle(ParticleOptions particleTypes, double offX, double offY, double offZ, double rand) {
        level().addParticle(particleTypes,
                getX() + offX + (random.nextDouble() * rand - rand / 2D),
                getY() + getBbHeight() / 2D + offY + (random.nextDouble() * rand - rand / 2D),
                getZ() + offZ + (random.nextDouble() * rand - rand / 2D),
                0D, 0D, 0D);
    }

    private void spawnParticle(ParticleOptions particleTypes, double offX, double offY, double offZ) {
        spawnParticle(particleTypes, offX, offY, offZ, 1D);
    }

    @Override
    public void damagePlane(double damage, boolean horizontal) {
        super.damagePlane(damage, horizontal);
        setPlaneDamage((float) (getPlaneDamage() + damage));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerable()) {
            return false;
        }

        if (level().isClientSide || !isAlive()) {
            return false;
        }

        if (!(source.getDirectEntity() instanceof Player)) {
            return false;
        }
        Player player = (Player) source.getDirectEntity();

        if (player == null) {
            return false;
        }

        if (hasPassenger(player)) {
            return false;
        }

        if (player.getAbilities().instabuild) {
            if (player.isShiftKeyDown()) {
                destroyPlane(source, player);
                return true;
            }
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem().equals(ModItems.WRENCH.get()) && (heldItem.getMaxDamage() - heldItem.getDamageValue()) >= 512) {
            if (player instanceof ServerPlayer serverPlayer) {
                heldItem.hurtAndBreak(512, serverPlayer.serverLevel(), serverPlayer, (item) -> {
                });
            }
            destroyPlane(source, player);
        }

        return false;
    }

    public void destroyPlane(DamageSource source, Player player) {
        Container inventory = ((EntityPlaneInventoryBase) this).getInventory();
        Containers.dropContents(level(), blockPosition(), inventory);
        inventory.clearContent();

        ResourceKey<LootTable> lootTable = getLootTable();
        if (lootTable != null) {
            LootTable table = level().getServer().reloadableRegistries().getLootTable(lootTable);
            LootParams.Builder context = new LootParams.Builder((ServerLevel) level())
                    .withParameter(LootContextParams.ORIGIN, position())
                    .withParameter(LootContextParams.THIS_ENTITY, this)
                    .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                    .withParameter(LootContextParams.ATTACKING_ENTITY, player)
                    .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, player);
            table.getRandomItems(context.create(LootContextParamSets.ENTITY)).forEach(this::spawnAtLocation);
        }

        kill();
    }

    @Nullable
    public abstract ResourceKey<LootTable> getLootTable();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DAMAGE, 0F);
    }

    public float getPlaneDamage() {
        return entityData.get(DAMAGE);
    }

    public void setPlaneDamage(float damage) {
        entityData.set(DAMAGE, Math.min(damage, Main.SERVER_CONFIG.maxPlaneDamage.get().floatValue()));
    }

    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    @Override
    public boolean canCollideWith(Entity entity) {
        if (!level().isClientSide && entity instanceof LivingEntity && !getPassengers().contains(entity)) {
            if (entity.getBoundingBox().intersects(getBoundingBox())) {
                double speed = getDeltaMovement().length();
                if (speed > 0.35F) {
                    float damage = Math.min((float) (speed * 10D), 15F);

                    tasks.add(() -> {
                        ServerLevel serverLevel = (ServerLevel) level();
                        Optional<Holder.Reference<DamageType>> holder = serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(DamageSourcePlane.DAMAGE_PLANE_TYPE);
                        holder.ifPresent(damageTypeReference -> entity.hurt(new DamageSource(damageTypeReference, this), damage));
                    });
                }
            }
        }
        return super.canCollideWith(entity);
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setPlaneDamage(compound.getFloat("Damage"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Damage", getPlaneDamage());
    }

}
