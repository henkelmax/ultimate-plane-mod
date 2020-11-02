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

    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(EntityPlaneDamageBase.class, DataSerializers.FLOAT);

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

        if (!((EntityPlaneSoundBase) this).isStarted()) {
            return;
        }

        Vector3d lookVec = getLookVec().normalize().scale(1.5D);
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
                getPosX() + offX + (rand.nextDouble() * random - random / 2D),
                getPosY() + offY + (rand.nextDouble() * random - random / 2D),
                getPosZ() + offZ + (rand.nextDouble() * random - random / 2D),
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

        if (isPassenger(player)) {
            return false;
        }

        if (player.abilities.isCreativeMode) {
            if (player.isSneaking()) {
                destroyPlane(source, player);
                return true;
            }
        }

        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.getItem().equals(ModItems.WRENCH) && (heldItem.getMaxDamage() - heldItem.getDamage()) >= 512) {
            heldItem.damageItem(512, player, playerEntity -> {
            });
            destroyPlane(source, player);
        }

        return false;
    }

    public void destroyPlane(DamageSource source, PlayerEntity player) {
        IInventory inventory = ((EntityPlaneInventoryBase) this).getInventory();
        InventoryHelper.dropInventoryItems(world, getPosition(), inventory);
        inventory.clear();

        LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(getLootTable());

        LootContext.Builder context = new LootContext.Builder((ServerWorld) world)
                .withParameter(LootParameters.field_237457_g_, getPositionVec())
                .withParameter(LootParameters.THIS_ENTITY, this)
                .withParameter(LootParameters.DAMAGE_SOURCE, source)
                .withParameter(LootParameters.KILLER_ENTITY, player)
                .withParameter(LootParameters.DIRECT_KILLER_ENTITY, player);
        loottable.generate(context.build(LootParameterSets.ENTITY), this::entityDropItem);

        remove();
    }

    public abstract ResourceLocation getLootTable();

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
    public boolean canCollide(Entity entity) {
        if (entity instanceof LivingEntity && !getPassengers().contains(entity)) {
            if (entity.getBoundingBox().intersects(getBoundingBox())) {
                double speed = getMotion().length();
                if (speed > 0.35F) {
                    float damage = Math.min((float) (speed * 10D), 15F);
                    entity.attackEntityFrom(DamageSourcePlane.DAMAGE_PLANE, damage);
                }

            }
        }
        return super.canCollide(entity);
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
