package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.corelib.client.obj.OBJModel;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class PlaneModel extends AbstractPlaneModel<EntityPlane> {

    private static final OBJModel PLANE_MODEL = new OBJModel(ResourceLocation.fromNamespaceAndPath(Main.MODID, "models/entity/plane.obj"));
    private static final Vector3f BODY_OFFSET = new Vector3f(0F, 8F / 16F, 0F);
    private static final Vector3f PROPELLER_OFFSET = new Vector3f(0F / 16F, 16F / 16F, -29.5F / 16F);
    private static final Vector3f LEFT_WHEEL_OFFSET = new Vector3f(-10F / 16F, 2F / 16F, -17.5F / 16F);
    private static final Vector3f RIGHT_WHEEL_OFFSET = new Vector3f(10F / 16F, 2F / 16F, -17.5F / 16F);

    public PlaneModel(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    protected void translateName(EntityPlane plane, PoseStack matrixStack, boolean left) {
        if (left) {
            matrixStack.translate(8.01D / 16D, -20D / 16D, 1D);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90F));
        } else {
            matrixStack.translate(-8.01D / 16D, -20D / 16D, 1D);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-90F));
        }
    }

    @Override
    protected Vector3f getLeftWheelOffset(EntityPlane plane) {
        return LEFT_WHEEL_OFFSET;
    }

    @Override
    protected Vector3f getRightWheelOffset(EntityPlane plane) {
        return RIGHT_WHEEL_OFFSET;
    }

    @Override
    protected Vector3f getPropellerOffset(EntityPlane plane) {
        return PROPELLER_OFFSET;
    }

    @Override
    protected Vector3f getBodyOffset(EntityPlane plane) {
        return BODY_OFFSET;
    }

    @Override
    protected OBJModel getBodyModel(EntityPlane plane) {
        return PLANE_MODEL;
    }

    @Override
    protected ResourceLocation getBodyTexture(EntityPlane plane) {
        switch (plane.getPlaneType()) {
            default:
            case OAK:
                return OAK_MODEL;
            case DARK_OAK:
                return DARK_OAK_MODEL;
            case SPRUCE:
                return SPRUCE_MODEL;
            case JUNGLE:
                return JUNGLE_MODEL;
            case BIRCH:
                return BIRCH_MODEL;
            case ACACIA:
                return ACACIA_MODEL;
            case WARPED:
                return WARPED_MODEL;
            case CRIMSON:
                return CRIMSON_MODEL;
            case BAMBOO:
                return BAMBOO_MODEL;
            case CHERRY:
                return CHERRY_MODEL;
            case MANGROVE:
                return MANGROVE_MODEL;
        }
    }

}

