package de.maxhenkel.plane.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPlaneDamageBase extends EntityPlaneBase {

    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityPlaneDamageBase.class,
            DataSerializers.FLOAT);

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
        if (!world.isRemote) {
            return;
        }

        if (!((EntityPlane) this).isStarted()) {
            return;
        }

        Vec3d lookVec = getLookVec().normalize();
        double offX = lookVec.x;
        double offY = lookVec.y;
        double offZ = lookVec.z;

        float damage = getPlaneDamage();

        float chance = Math.max(damage - 25F, 0) / 100F;

        if (rand.nextFloat() < chance) {
            spawnParticle(ParticleTypes.LARGE_SMOKE, offX, offY, offZ);
        }
    }

    private void spawnParticle(IParticleData particleTypes, double offX, double offY, double offZ, double random) {
        world.addParticle(particleTypes,
                posX + offX + (rand.nextDouble() * random - random / 2D),
                posY + offY + (rand.nextDouble() * random - random / 2D),
                posZ + offZ + (rand.nextDouble() * random - random / 2D),
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
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isInvulnerable()) {
            return false;
        }

        if (world.isRemote || !isAlive()) {
            return false;
        }

        if (!(source.getImmediateSource() instanceof PlayerEntity)) {
            return false;
        }
        PlayerEntity player = (PlayerEntity) source.getImmediateSource();

        if (player == null) {
            return false;
        }

        if (player.equals(getDriver())) {
            return false;
        }

        if (player.abilities.isCreativeMode) {
            if (player.isSneaking()) {
                destroyPlane(player, false);
                return true;
            }
            return false;
        }

        if (getPlaneDamage() >= 90F && amount > 0F) {
            destroyPlane(player, true);
        }

        return false;
    }

    public void destroyPlane(PlayerEntity player, boolean dropParts) {
        remove();
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(DAMAGE, 0F);
    }

    public float getPlaneDamage() {
        return dataManager.get(DAMAGE);
    }

    public void setPlaneDamage(float damage) {
        dataManager.set(DAMAGE, damage);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setPlaneDamage(compound.getFloat("Damage"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putFloat("Damage", getPlaneDamage());
    }
}
