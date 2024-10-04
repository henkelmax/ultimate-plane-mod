package de.maxhenkel.plane;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageSourcePlane {

    public static final ResourceKey<DamageType> DAMAGE_HIT_PLANE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Main.MODID, "hit_plane"));
    public static final ResourceKey<DamageType> DAMAGE_PLANE_CRASH = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Main.MODID, "plane_crash"));

}
