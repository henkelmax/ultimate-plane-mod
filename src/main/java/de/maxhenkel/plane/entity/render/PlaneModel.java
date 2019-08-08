package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.platform.GlStateManager;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

public class PlaneModel extends EntityRenderer<EntityPlane> {

    private static OBJModelInstance OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/oak_planks.png"));
    private static OBJModelInstance DARK_OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/dark_oak_planks.png"));
    private static OBJModelInstance BIRCH_MODEL = getPlaneModel(new ResourceLocation("textures/block/birch_planks.png"));
    private static OBJModelInstance JUNGLE_MODEL = getPlaneModel(new ResourceLocation("textures/block/jungle_planks.png"));
    private static OBJModelInstance ACACIA_MODEL = getPlaneModel(new ResourceLocation("textures/block/acacia_planks.png"));
    private static OBJModelInstance SPRUCE_MODEL = getPlaneModel(new ResourceLocation("textures/block/spruce_planks.png"));

    private static final List<OBJModelInstance> MODELS = Arrays.asList(
            new OBJModelInstance(
                    new de.maxhenkel.plane.entity.render.OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/wheel.obj"),
                            new ResourceLocation(Main.MODID, "textures/entity/wheel.png")
                    ),
                    new OBJModelOptions(
                            new Vec3d(-10D / 16D, 2D / 16D, -17.5D / 16D),
                            (plane, partialTicks) -> {
                                GlStateManager.scalef(1F / 16F, 1F / 16F, 1F / 16F);
                                GlStateManager.rotatef(-plane.getWheelRotation(partialTicks), 1F, 0F, 0F);
                            }
                    )
            ),
            new OBJModelInstance(
                    new de.maxhenkel.plane.entity.render.OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/wheel.obj"),
                            new ResourceLocation(Main.MODID, "textures/entity/wheel.png")
                    ),
                    new OBJModelOptions(
                            new Vec3d(10D / 16D, 2D / 16D, -17.5D / 16D),
                            (plane, partialTicks) -> {
                                GlStateManager.scalef(1F / 16F, 1F / 16F, 1F / 16F);
                                GlStateManager.rotatef(-plane.getWheelRotation(partialTicks), 1F, 0F, 0F);
                            }
                    )
            ),
            new OBJModelInstance(
                    new de.maxhenkel.plane.entity.render.OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/propeller.obj"),
                            new ResourceLocation(Main.MODID, "textures/entity/propeller.png")
                    ),
                    new OBJModelOptions(
                            new Vec3d(0D / 16D, 16D / 16D, -29.5D / 16D),
                            (plane, partialTicks) -> {
                                GlStateManager.scalef(1F / 16F, 1F / 16F, 1F / 16F);
                                GlStateManager.rotatef(-plane.getPropellerRotation(partialTicks), 0F, 0F, 1F);
                            }
                    )
            )
    );

    public PlaneModel(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPlane entity) {
        return null;
    }

    @Override
    public void doRender(EntityPlane plane, double x, double y, double z, float entityYaw, float partialTicks) {

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        setupRotation(plane, entityYaw, partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();

        renderModel(plane, partialTicks, getModelFromType(plane));

        for (OBJModelInstance instance : MODELS) {
            renderModel(plane, partialTicks, instance);
        }

        GlStateManager.disableLighting();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        super.doRender(plane, x, y, z, entityYaw, partialTicks);
    }

    private void renderModel(EntityPlane plane, float partialTicks, OBJModelInstance instance) {
        Minecraft.getInstance().getTextureManager().bindTexture(instance.getModel().getTexture());
        GlStateManager.pushMatrix();
        if (instance.getModel().hasCulling()) {
            GlStateManager.enableCull();
        } else {
            GlStateManager.disableCull();
        }

        GlStateManager.translated(instance.getOptions().getOffset().x, instance.getOptions().getOffset().y, instance.getOptions().getOffset().z);

        if (instance.getOptions().getRotation() != null) {
            instance.getOptions().getRotation().applyGLRotation();
        }

        if (instance.getOptions().getOnRender() != null) {
            instance.getOptions().getOnRender().onRender(plane, partialTicks);
        }

        OBJRenderer.renderObj(instance.getModel().getModel());
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    public void setupRotation(EntityPlane entity, float yaw, float partialTicks) {
        GlStateManager.rotatef(180.0F - yaw, 0F, 1F, 0F);
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        GlStateManager.rotatef(pitch, -1F, 0F, 0F);
    }

    @Override
    public boolean isMultipass() {
        return false;
    }

    private static OBJModelInstance getModelFromType(EntityPlane plane) {
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
        }
    }

    private static OBJModelInstance getPlaneModel(ResourceLocation texture) {
        return new OBJModelInstance(
                new de.maxhenkel.plane.entity.render.OBJModel(
                        new ResourceLocation(Main.MODID, "models/entity/plane.obj"),
                        texture
                ),
                new OBJModelOptions(
                        new Vec3d(0D, 8D / 16D, 0D),
                        new Rotation(180F, 0F, 1F, 0F),
                        (plane, partialTicks) -> GlStateManager.scalef(1F / 16F, 1F / 16F, 1F / 16F)
                )
        );
    }
}

