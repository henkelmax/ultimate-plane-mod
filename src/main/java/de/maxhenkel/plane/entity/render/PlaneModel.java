package de.maxhenkel.plane.entity.render;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneBase;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class PlaneModel extends OBJModelRenderer {

    List<OBJModelInstance> models;

    public PlaneModel(EntityRendererManager renderManager) {
        super(renderManager);

        models = Arrays.asList(new OBJModelInstance(
                new OBJModel(new ResourceLocation(Main.MODID, "models/entity/wood_body.obj"),
                        new ResourceLocation(Main.MODID, "textures/entity/car_wood_oak.png"))
        ));
    }

    @Override
    public List<OBJModelInstance> getModels(EntityPlaneBase entity) {
        return models; //entity.getModels();
    }
}