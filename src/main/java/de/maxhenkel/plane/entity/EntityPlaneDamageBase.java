package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.DamageSourcePlane;
import de.maxhenkel.plane.PlaneMod;
import de.maxhenkel.plane.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

        if (isEyeInFluidType(Fluids.WATER.getFluidType())) {
            if (this instanceof EntityPlaneControlBase plane) {
                if (plane.isStarted()) {
                    setPlaneDamage(getPlaneDamage() + 50F);
                    plane.setStarted(false);
                }
            }
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
    public void damagePlane(float damage, boolean horizontal) {
        super.damagePlane(damage, horizontal);
        setPlaneDamage(getPlaneDamage() + damage);
        damagePassengers(damage);
    }

    protected void damagePassengers(float planeDamage) {
        if (planeDamage < 20F) {
            return;
        }
        planeDamage = Math.min(planeDamage, 200F);

        float entityDamage = planeDamage / 10F;

        for (Entity entity : getPassengers()) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }
            damageEntity(livingEntity, entityDamage, DamageSourcePlane.DAMAGE_PLANE_CRASH);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
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
                destroyPlane(level, source, player);
                return true;
            }
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem().equals(ModItems.WRENCH.get()) && (heldItem.getMaxDamage() - heldItem.getDamageValue()) >= 512) {
            if (player instanceof ServerPlayer serverPlayer) {
                heldItem.hurtAndBreak(512, serverPlayer.level(), serverPlayer, (item) -> {
                });
            }
            destroyPlane(level, source, player);
        }

        return false;
    }

    public void destroyPlane(ServerLevel level, DamageSource source, Player player) {
        Container inventory = ((EntityPlaneInventoryBase) this).getInventory();
        Containers.dropContents(level(), blockPosition(), inventory);
        inventory.clearContent();

        ResourceKey<LootTable> lootTable = getPlaneLootTable();
        if (lootTable != null) {
            LootTable table = level().getServer().reloadableRegistries().getLootTable(lootTable);
            LootParams.Builder context = new LootParams.Builder((ServerLevel) level())
                    .withParameter(LootContextParams.ORIGIN, position())
                    .withParameter(LootContextParams.THIS_ENTITY, this)
                    .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                    .withParameter(LootContextParams.ATTACKING_ENTITY, player)
                    .withParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, player);
            table.getRandomItems(context.create(LootContextParamSets.ENTITY)).forEach(stack -> spawnAtLocation(level, stack));
        }

        kill(level);
    }

    @Nullable
    public abstract ResourceKey<LootTable> getPlaneLootTable();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DAMAGE, 0F);
    }

    public float getPlaneDamage() {
        return entityData.get(DAMAGE);
    }

    public void setPlaneDamage(float damage) {
        entityData.set(DAMAGE, Math.min(damage, PlaneMod.SERVER_CONFIG.maxPlaneDamage.get().floatValue()));
    }

    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    @Override
    public boolean canCollideWith(Entity entity) {
        damageEntityCollided(entity);
        return super.canCollideWith(entity);
    }

    protected void damageEntityCollided(Entity entity) {
        if (!(level() instanceof ServerLevel level)) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        if (getPassengers().contains(livingEntity)) {
            return;
        }
        if (!livingEntity.getBoundingBox().intersects(getBoundingBox())) {
            return;
        }
        if (livingEntity instanceof ServerPlayer && !livingEntity.onGround()) {
            //Don't damage players that are in the air, as this would damage players jumping out
            return;
        }
        double speed = getDeltaMovement().length();
        if (speed <= 0.35F) {
            return;
        }
        float damage = Math.min((float) (speed * 10D), 15F);
        damageEntity(entity, damage, DamageSourcePlane.DAMAGE_HIT_PLANE);
    }

    protected void damageEntity(Entity entity, float damage, ResourceKey<DamageType> damageType) {
        tasks.add(() -> {
            Optional<Holder.Reference<DamageType>> holder = level().registryAccess().get(damageType);
            holder.ifPresent(damageTypeReference -> entity.hurt(new DamageSource(damageTypeReference, this), damage));
        });
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        setPlaneDamage(valueInput.getFloatOr("Damage", 0F));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putFloat("Damage", getPlaneDamage());
    }

}
