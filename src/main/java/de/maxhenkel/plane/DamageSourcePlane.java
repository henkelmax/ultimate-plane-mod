package de.maxhenkel.plane;

import net.minecraft.util.DamageSource;

public class DamageSourcePlane extends DamageSource {

    public static final DamageSourcePlane DAMAGE_PLANE = new DamageSourcePlane();

    public DamageSourcePlane() {
        super("hit_plane");
    }

    @Override
    public boolean canHarmInCreative() {
        return false;
    }

    @Override
    public boolean isDamageAbsolute() {
        return false;
    }

    @Override
    public boolean isDifficultyScaled() {
        return false;
    }

    @Override
    public boolean isUnblockable() {
        return true;
    }

    @Override
    public boolean isExplosion() {
        return false;
    }

    @Override
    public boolean isFireDamage() {
        return false;
    }

    @Override
    public boolean isMagicDamage() {
        return false;
    }

    @Override
    public boolean isProjectile() {
        return false;
    }

}
