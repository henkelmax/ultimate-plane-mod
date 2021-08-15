package de.maxhenkel.plane;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSourcePlane extends DamageSource {

    public static final DamageSourcePlane DAMAGE_PLANE = new DamageSourcePlane();

    public DamageSourcePlane() {
        super("hit_plane");
    }

    @Override
    public boolean isBypassInvul() {
        return false;
    }

    @Override
    public boolean isBypassMagic() {
        return false;
    }

    @Override
    public boolean scalesWithDifficulty() {
        return false;
    }

    @Override
    public boolean isBypassArmor() {
        return true;
    }

    @Override
    public boolean isExplosion() {
        return false;
    }

    @Override
    public boolean isFire() {
        return false;
    }

    @Override
    public boolean isMagic() {
        return false;
    }

    @Override
    public boolean isProjectile() {
        return false;
    }

}
