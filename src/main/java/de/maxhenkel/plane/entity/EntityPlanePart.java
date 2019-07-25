package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.net.DataSerializerEntitySize;
import de.maxhenkel.plane.net.DataSerializerVec3d;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class EntityPlanePart extends Entity {

    private static final DataParameter<Vec3d> OFFSET = EntityDataManager.createKey(EntityPlanePart.class, DataSerializerVec3d.VEC3D);
    private static final DataParameter<EntitySize> SIZE = EntityDataManager.createKey(EntityPlanePart.class, DataSerializerEntitySize.ENTITY_SIZE);
    private static final DataParameter<Optional<UUID>> UUID = EntityDataManager.createKey(EntityPlanePart.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    private EntityPlane plane;
    private double lastPlanePosX, lastPlanePosY, lastPlanePosZ;

    public EntityPlanePart(EntityType type, World world) {
        super(type, world);
    }

    public EntityPlanePart(World world) {
        this(Main.PLANE_PART_ENTITY_TYPE, world);
    }

    public EntityPlanePart(EntityPlane plane) {
        this(plane.world);
        this.plane = plane;
    }

    @Override
    public void tick() {
        super.tick();

        if (plane == null) {
            Optional<EntityPlane> planeOptional = world.getEntitiesWithinAABB(EntityPlane.class, getBoundingBox().grow(16D), p -> p.getUniqueID().equals(getPlaneUUID().orElse(new UUID(0L, 0L)))).stream().findFirst();
            if (planeOptional.isPresent()) {
                this.plane = planeOptional.get();
                recalculateSize();
            } else {
                remove();
                return;
            }
        }

        if (!plane.isAlive()) {
            remove();
        }

        if (plane.posX == lastPlanePosX && plane.posY == lastPlanePosY && plane.posZ == lastPlanePosZ) {
            return;
        }

        Vec3d offset = getOffset();
        Vec3d partOffset = offset.rotatePitch((float) -Math.toRadians(plane.rotationPitch)).rotateYaw((float) -Math.toRadians(plane.rotationYaw)); //look.scale(offset.x).add(0D, offset.y, offset.rotateYaw(plane.rotationYaw).z);

        Vec3d newPos = new Vec3d(plane.posX + partOffset.x, plane.posY + partOffset.y, plane.posZ + partOffset.z);

        /*Vec3d theoreticalMoement = newPos.subtract(getPositionVec());
        move(MoverType.SELF, theoreticalMoement);

        if (collided) {
            Vec3d diff = getPositionVec().subtract(newPos);
            collidedX = diff.x != 0D;
            collidedY = diff.y != 0D;
            collidedZ = diff.z != 0D;
            System.out.println(collidedX + " " + collidedY + " " + collidedZ);
            // Vec3d planeMotion = plane.getMotion();
            // plane.setMotion(diff.x == 0D ? planeMotion.x : 0D, diff.y == 0D ? planeMotion.y : 0D, diff.z == 0D ? planeMotion.z : 0D);
            // System.out.println(diff);
        } else if (!isInBlock()) {
            collidedX = false;
            collidedY = false;
            collidedZ = false;
        } else {
            System.out.println("INBLOCK");
        }*/

        setPosition(newPos.x, newPos.y, newPos.z);
        prevPosX = plane.prevPosX + partOffset.x;
        prevPosY = plane.prevPosY + partOffset.y;
        prevPosZ = plane.prevPosZ + partOffset.z;
        rotationYaw = plane.rotationYaw;
        rotationPitch = plane.rotationPitch;
        prevRotationYaw = plane.prevRotationYaw;
        prevRotationPitch = plane.prevRotationPitch;
        lastPlanePosX = plane.posX;
        lastPlanePosY = plane.posY;
        lastPlanePosZ = plane.posZ;

    }

    /*private boolean collidedX, collidedY, collidedZ;

    public Vec3d applyCollisionMotion(Vec3d motion) {
        return new Vec3d(collidedX || collidedZ ? 0D : motion.x, collidedY ? 0D : motion.y, collidedZ || collidedX ? 0D : motion.z);
    }*/

    public boolean isInBlock() {
        AxisAlignedBB bb = getBoundingBox();
        Stream<VoxelShape> stream = world.getCollisionShapes(this, bb);
        return stream.map(shape -> {
            boolean coll = false;
            for (AxisAlignedBB aabb : shape.toBoundingBoxList()) {
                if (bb.intersects(aabb)) {
                    coll = true;
                    break;
                }
            }
            return coll;
        }).anyMatch(coll -> coll);
    }

    public EntityPlane getPlane() {
        return plane;
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        if (plane != null) {
            plane.processInitialInteract(player, hand);
        }
        return false;
    }

    protected void readAdditional(CompoundNBT compound) {
    }

    protected void writeAdditional(CompoundNBT compound) {
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (plane != null) {
            return plane.attackEntityFrom(source, damage);
        } else {
            return false;
        }
    }

    public boolean isEntityEqual(Entity entity) {
        return this == entity || plane == entity;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerData() {
        dataManager.register(OFFSET, Vec3d.ZERO);
        dataManager.register(SIZE, EntitySize.flexible(0F, 0F));
        dataManager.register(UUID, Optional.empty());
    }

    public Vec3d getOffset() {
        return dataManager.get(OFFSET);
    }

    public void setOffset(Vec3d offset) {
        dataManager.set(OFFSET, offset);
    }

    @Override
    public EntitySize getSize(Pose pose) {
        return dataManager.get(SIZE);
    }

    public void setSize(EntitySize size) {
        dataManager.set(SIZE, size);
    }

    public Optional<UUID> getPlaneUUID() {
        return dataManager.get(UUID);
    }

    public void setPlaneUUID(UUID uuid) {
        if (uuid == null) {
            dataManager.set(UUID, Optional.empty());
        } else {
            dataManager.set(UUID, Optional.of(uuid));
        }
    }
}
