package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.client.obj.OBJModel;
import de.maxhenkel.corelib.client.obj.OBJModelInstance;
import de.maxhenkel.corelib.client.obj.OBJModelOptions;
import de.maxhenkel.corelib.math.Rotation;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityBushPlane;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BushPlaneModel extends AbstractPlaneModel<EntityBushPlane> {

    private static final List<OBJModelInstance<EntityBushPlane>> MODELS = Arrays.asList(
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/wheel.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/wheel.png"),
                            new Vector3d(-10D / 16D, 2D / 16D, -17.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.rotate(Vector3f.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
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
                                matrixStack.rotate(Vector3f.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/propeller.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/propeller.png"),
                            new Vector3d(0D / 16D, 16D / 16D, -29.5D / 16D),
                            (plane, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-plane.getPropellerRotation(partialTicks)));
                            }
                    )
            )
    );

    private static final List<OBJModelInstance<EntityBushPlane>> OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/oak_planks.png"));
    private static final List<OBJModelInstance<EntityBushPlane>> DARK_OAK_MODEL = getPlaneModel(new ResourceLocation("textures/block/dark_oak_planks.png"));
    private static final List<OBJModelInstance<EntityBushPlane>> BIRCH_MODEL = getPlaneModel(new ResourceLocation("textures/block/birch_planks.png"));
    private static final List<OBJModelInstance<EntityBushPlane>> JUNGLE_MODEL = getPlaneModel(new ResourceLocation("textures/block/jungle_planks.png"));
    private static final List<OBJModelInstance<EntityBushPlane>> ACACIA_MODEL = getPlaneModel(new ResourceLocation("textures/block/acacia_planks.png"));
    private static final List<OBJModelInstance<EntityBushPlane>> SPRUCE_MODEL = getPlaneModel(new ResourceLocation("textures/block/spruce_planks.png"));

    public BushPlaneModel(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void translateName(EntityBushPlane plane, MatrixStack matrixStack, boolean left) {
        if (left) {
            matrixStack.translate(8.01D / 16D, -20D / 16D, 0D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(90F));
        } else {
            matrixStack.translate(-8.01D / 16D, -20D / 16D, 0D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-90F));
        }
    }

    @Override
    public List<OBJModelInstance<EntityBushPlane>> getModels(EntityBushPlane entity) {
        return getModelFromType(entity);
    }

    private static List<OBJModelInstance<EntityBushPlane>> getModelFromType(EntityBushPlane plane) {
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

    private static List<OBJModelInstance<EntityBushPlane>> getPlaneModel(ResourceLocation texture) {
        List<OBJModelInstance<EntityBushPlane>> models = new ArrayList<>(MODELS);
        models.add(new OBJModelInstance<>(
                new OBJModel(
                        new ResourceLocation(Main.MODID, "models/entity/bush_plane.obj")
                ),
                new OBJModelOptions<>(
                        texture,
                        new Vector3d(0D, 8D / 16D, 0D),
                        new Rotation(180F, Vector3f.YP),
                        (plane, matrixStack, partialTicks) -> {
                        }
                )
        ));
        return models;
    }

}
