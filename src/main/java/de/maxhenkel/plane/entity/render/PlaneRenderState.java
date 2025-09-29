package de.maxhenkel.plane.entity.render;

import de.maxhenkel.plane.PlaneType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class PlaneRenderState extends EntityRenderState {

    public PlaneType type;
    public float xRot;
    public float yRot;
    public float wheelRotation;
    public float propellerRotation;
    public Vec3 bodyRotationCenter;
    @Nullable
    public FormattedCharSequence customName;
}
