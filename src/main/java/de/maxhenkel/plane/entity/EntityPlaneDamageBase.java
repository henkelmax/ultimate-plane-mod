package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.DamageSourcePlane;
import de.maxhenkel.plane.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class EntityPlaneDamageBase extends EntityPlaneBase {

    private static final DataParameter<Float> DAMAGE = EntityDataManager.defineId(EntityPlaneDamageBase.class, DataSerializers.FLOAT);

    public EntityPlaneDamageBase(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        if (isInLava()) {
            setPlaneDamage(getPlaneDamage() + 1F);
        }

        handleParticles();
    }

    protected void handleParticles() {
        if (!level.isClientSide) {
            return;
        }

        if (!((EntityPlaneSoundBase) this).isStarted()) {
            return;
        }

        float damage = getPlaneDamage();

        float chance = Math.max(damage - 25F, 0) / 100F;

        if (random.nextFloat() < chance) {
            Vector3d lookVec = getLookAngle().normalize().scale(1.5D);
            spawnParticle(ParticleTypes.LARGE_SMOKE, lookVec.x, lookVec.y, lookVec.z);
        }
    }

    private void spawnParticle(IParticleData particleTypes, double offX, double offY, double offZ, double rand) {
        level.addParticle(particleTypes,
                getX() + offX + (random.nextDouble() * rand - rand / 2D),
                getY() + getBbHeight() / 2D + offY + (random.nextDouble() * rand - rand / 2D),
                getZ() + offZ + (random.nextDouble() * rand - rand / 2D),
                0D, 0D, 0D);
    }

    private void spawnParticle(IParticleData particleTypes, double offX, double offY, double offZ) {
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

        if (level.isClientSide || !isAlive()) {
            return false;
        }

        if (!(source.getDirectEntity() instanceof PlayerEntity)) {
            return false;
        }
        PlayerEntity player = (PlayerEntity) source.getDirectEntity();

        if (player == null) {
            return false;
        }

        if (hasPassenger(player)) {
            return false;
        }

        if (player.abilities.instabuild) {
            if (player.isShiftKeyDown()) {
                destroyPlane(source, player);
                return true;
            }
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem().equals(ModItems.WRENCH) && (heldItem.getMaxDamage() - heldItem.getDamageValue()) >= 512) {
            heldItem.hurtAndBreak(512, player, playerEntity -> {
            });
            destroyPlane(source, player);
        }

        return false;
    }

    public void destroyPlane(DamageSource source, PlayerEntity player) {
        IInventory inventory = ((EntityPlaneInventoryBase) this).getInventory();
        InventoryHelper.dropContents(level, blockPosition(), inventory);
        inventory.clearContent();

        LootTable loottable = this.level.getServer().getLootTables().get(getLootTable());

        LootContext.Builder context = new LootContext.Builder((ServerWorld) level)
                .withParameter(LootParameters.ORIGIN, position())
                .withParameter(LootParameters.THIS_ENTITY, this)
                .withParameter(LootParameters.DAMAGE_SOURCE, source)
                .withParameter(LootParameters.KILLER_ENTITY, player)
                .withParameter(LootParameters.DIRECT_KILLER_ENTITY, player);
        loottable.getRandomItems(context.create(LootParameterSets.ENTITY)).forEach(this::spawnAtLocation);

        remove();
    }

    public abstract ResourceLocation getLootTable();

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DAMAGE, 0F);
    }

    public float getPlaneDamage() {
        return entityData.get(DAMAGE);
    }

    public void setPlaneDamage(float damage) {
        entityData.set(DAMAGE, damage);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (entity instanceof LivingEntity && !getPassengers().contains(entity)) {
            if (entity.getBoundingBox().intersects(getBoundingBox())) {
                double speed = getDeltaMovement().length();
                if (speed > 0.35F) {
                    float damage = Math.min((float) (speed * 10D), 15F);
                    entity.hurt(DamageSourcePlane.DAMAGE_PLANE, damage);
                }

            }
        }
        return super.canCollideWith(entity);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        setPlaneDamage(compound.getFloat("Damage"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Damage", getPlaneDamage());
    }

}
