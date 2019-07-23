package de.maxhenkel.plane.entity;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPlaneHitboxBase extends EntityPlaneInventoryBase {

    private EntityPlanePart[] parts;
    private EntityPlanePart front;
    private EntityPlanePart leftInner;
    private EntityPlanePart rightInner;
    private EntityPlanePart leftMiddle;
    private EntityPlanePart rightMiddle;
    private EntityPlanePart back;
    private EntityPlanePart top;

    private boolean initialized;

    public EntityPlaneHitboxBase(EntityType type, World world) {
        super(type, world);
    }

    private void initParts() {
        front = createPart(0D, 0.5D, 1D, 1F, 0.5F);
        back = createPart(0D, 0.5D, -1D, 1F, 0.5F);
        leftInner = createPart(-1D, 1.4D, 0D, 1F, 0.1F);
        rightInner = createPart(1D, 1.4D, 0D, 1F, 0.1F);
        leftMiddle = createPart(-2D, 1.4D, 0D, 1F, 0.1F);
        rightMiddle = createPart(2D, 1.4D, 0D, 1F, 0.1F);
        top = createPart(0D, 1D, 0D, 1F, 0.5F);

        parts = new EntityPlanePart[]{
                front,
                back,
                leftInner,
                rightInner,
                leftMiddle,
                rightMiddle,
                top
        };
    }

    private EntityPlanePart createPart(double offsetX, double offsetY, double offsetZ, float width, float height) {
        EntityPlanePart part = new EntityPlanePart((EntityPlane) this);
        part.setOffset(new Vec3d(offsetX, offsetY, offsetZ));
        part.setSize(EntitySize.flexible(width, height));
        part.setPlaneUUID(getUniqueID());
        return part;
    }

    @Override
    public void tick() {
        super.tick();

        if (world.isRemote) {
            return;
        }

        if (!initialized) {
            initParts();
            initialized = true;
        }

        for (EntityPlanePart part : parts) {
            if (world.getEntityByID(part.getEntityId()) == null) {
                part.removed = false;
                part.setPosition(posX, posY, posZ);
                world.addEntity(part);
            }
        }
    }

    @Override
    public void move(MoverType typeIn, Vec3d moveToVec) {
        /*if (parts == null) {
            super.move(typeIn, moveToVec);
            return;
        }
        Vec3d motion = moveToVec;
        for (EntityPlanePart part : parts) {
            motion = part.applyCollisionMotion(motion);
        }
        super.move(typeIn, motion);*/

       /* if (parts == null) {
            super.move(typeIn, moveToVec);
            return;
        }
        boolean collided = false;
        for (EntityPlanePart part : parts) {
            if (part.isInBlock()) {
                collided = true;
                moveToVec = moveToVec.mul(0D, 1D, 1D);
                break;
            }
        }*/

        super.move(typeIn, moveToVec);

        collidedHorizontally = collided;
    }
}
