package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.corelib.client.obj.OBJModel;
import de.maxhenkel.corelib.client.obj.OBJModelInstance;
import de.maxhenkel.corelib.client.obj.OBJModelOptions;
import de.maxhenkel.corelib.math.Rotation;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaneModel extends AbstractPlaneModel<EntityPlane> {

    private static final List<OBJModelInstance<EntityPlane>> MODELS = Arrays.asList(
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/wheel.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/wheel.png"),
                            new Vector3d(-10D / 16D, 2D / 16D, -17.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Axis.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/wheel.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/wheel.png"),
                            new Vector3d(10D / 16D, 2D / 16D, -17.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Axis.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/propeller.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation("textures/block/spruce_planks.png"),
                            new Vector3d(0D / 16D, 16D / 16D, -29.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Axis.ZP.rotationDegrees(-plane.getPropellerRotation(partialTicks)));
                            }
                    )
            )
    );

    private static final List<OBJModelInstance<EntityPlane>> OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/oak_planks.png"));
    private static final List<OBJModelInstance<EntityPlane>> DARK_OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/dark_oak_planks.png"));
    private static final List<OBJModelInstance<EntityPlane>> BIRCH_MODEL = getPlaneModel(new ResourceLocation("textures/block/birch_planks.png"));
    private static final List<OBJModelInstance<EntityPlane>> JUNGLE_MODEL = getPlaneModel(new ResourceLocation("textures/block/jungle_planks.png"));
    private static final List<OBJModelInstance<EntityPlane>> ACACIA_MODEL = getPlaneModel(new ResourceLocation("textures/block/acacia_planks.png"));
    private static final List<OBJModelInstance<EntityPlane>> SPRUCE_MODEL = getPlaneModel(new ResourceLocation("textures/block/spruce_planks.png"));
    private static final List<OBJModelInstance<EntityPlane>> WARPED_MODEL = getPlaneModel(new ResourceLocation("textures/block/warped_planks.png"));
    private static final List<OBJModelInstance<EntityPlane>> CRIMSON_MODEL = getPlaneModel(new ResourceLocation("textures/block/crimson_planks.png"));

    public PlaneModel(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    protected void translateName(EntityPlane plane, PoseStack matrixStack, boolean left) {
        if (left) {
            matrixStack.translate(8.01D / 16D, -20D / 16D, -1D);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90F));
        } else {
            matrixStack.translate(-8.01D / 16D, -20D / 16D, -1D);
            matrixStack.mulPose(Axis.YP.rotationDegrees(-90F));
        }
    }

    @Override
    public List<OBJModelInstance<EntityPlane>> getModels(EntityPlane entity) {
        return getModelFromType(entity);
    }


    private static List<OBJModelInstance<EntityPlane>> getModelFromType(EntityPlane plane) {
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
        }
    }

    private static List<OBJModelInstance<EntityPlane>> getPlaneModel(ResourceLocation texture) {
        List<OBJModelInstance<EntityPlane>> models = new ArrayList<>(MODELS);
        models.add(new OBJModelInstance<>(
                new OBJModel(
                        new ResourceLocation(Main.MODID, "models/entity/plane.obj")
                ),
                new OBJModelOptions<>(
                        texture,
                        new Vector3d(0D, 8D / 16D, 0D),
                        new Rotation(180F, Axis.YP),
                        (plane, matrixStack, partialTicks) -> matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F)
                )
        ));
        return models;
    }

}

