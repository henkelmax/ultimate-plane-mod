package de.maxhenkel.plane.entity.render;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaneModel extends OBJRenderer<EntityPlane> {

    private static final List<OBJModelInstance> MODELS = Arrays.asList(
            new OBJModelInstance(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/wheel.obj")
                    ),
                    new OBJModelOptions(
                            new ResourceLocation(Main.MODID, "textures/entity/wheel.png"),
                            new Vec3d(-10D / 16D, 2D / 16D, -17.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.rotate(Vector3f.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/wheel.obj")
                    ),
                    new OBJModelOptions(
                            new ResourceLocation(Main.MODID, "textures/entity/wheel.png"),
                            new Vec3d(10D / 16D, 2D / 16D, -17.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.rotate(Vector3f.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/propeller.obj")
                    ),
                    new OBJModelOptions(
                            new ResourceLocation(Main.MODID, "textures/entity/propeller.png"),
                            new Vec3d(0D / 16D, 16D / 16D, -29.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-plane.getPropellerRotation(partialTicks)));
                            }
                    )
            )
    );

    private static List<OBJModelInstance> OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/oak_planks.png"));
    private static List<OBJModelInstance> DARK_OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/dark_oak_planks.png"));
    private static List<OBJModelInstance> BIRCH_MODEL = getPlaneModel(new ResourceLocation("textures/block/birch_planks.png"));
    private static List<OBJModelInstance> JUNGLE_MODEL = getPlaneModel(new ResourceLocation("textures/block/jungle_planks.png"));
    private static List<OBJModelInstance> ACACIA_MODEL = getPlaneModel(new ResourceLocation("textures/block/acacia_planks.png"));
    private static List<OBJModelInstance> SPRUCE_MODEL = getPlaneModel(new ResourceLocation("textures/block/spruce_planks.png"));

    public PlaneModel(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public List<OBJModelInstance> getModels(EntityPlane entity) {
        return getModelFromType(entity);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityPlane entity) {
        return null;
    }

    private static List<OBJModelInstance> getModelFromType(EntityPlane plane) {
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

    private static List<OBJModelInstance> getPlaneModel(ResourceLocation texture) {
        List<OBJModelInstance> models = new ArrayList<>(MODELS);
        models.add(new OBJModelInstance(
                new OBJModel(
                        new ResourceLocation(Main.MODID, "models/entity/plane.obj")
                ),
                new OBJModelOptions(
                        texture,
                        new Vec3d(0D, 8D / 16D, 0D),
                        new Rotation(180F, Vector3f.YP),
                        (plane, matrixStack, partialTicks) -> matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F)
                )
        ));
        return models;
    }
}

