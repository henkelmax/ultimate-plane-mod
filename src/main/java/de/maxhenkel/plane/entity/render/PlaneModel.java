package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.platform.GlStateManager;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

public class PlaneModel extends OBJModelRenderer {

    private List<OBJModelInstance> models;

    public PlaneModel(EntityRendererManager renderManager) {
        super(renderManager);

        models = Arrays.asList(
                new OBJModelInstance(
                        new OBJModel(
                                new ResourceLocation(Main.MODID, "models/entity/plane.obj"),
                                new ResourceLocation(Main.MODID, "textures/entity/plane.png")
                        ),
                        new OBJModelOptions(
                                new Vec3d(0D, 8D / 16D, 0D),
                                new Rotation(180F, 0F, 1F, 0F),
                                (plane, partialTicks) -> GlStateManager.scalef(1F / 16F, 1F / 16F, 1F / 16F)
                        )
                ),
                new OBJModelInstance(
                        new OBJModel(
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
                        new OBJModel(
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
                )

        );
    }

    @Override
    public List<OBJModelInstance> getModels(EntityPlane entity) {
        return models;
    }
}