package de.maxhenkel.plane;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageSourcePlane {

    public static final ResourceKey<DamageType> DAMAGE_PLANE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Main.MODID, "hit_plane"));

}
