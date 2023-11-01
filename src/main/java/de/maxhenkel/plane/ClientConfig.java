package de.maxhenkel.plane;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.function.Function;

public class ClientConfig extends ConfigBase {

    public final ModConfigSpec.BooleanValue showPlaneInfo;
    public final ModConfigSpec.DoubleValue planeInfoScale;
    public final ModConfigSpec.EnumValue<SpeedType> planeInfoSpeedType;
    public final ModConfigSpec.DoubleValue planeZoom;

    public ClientConfig(ModConfigSpec.Builder builder) {
        super(builder);
        showPlaneInfo = builder.define("plane_info.enabled", true);
        planeInfoScale = builder.defineInRange("plane_info.scale", 0.75D, 0.1D, 2D);
        planeInfoSpeedType = builder.defineEnum("plane_info.speed_type", SpeedType.KILOMETERS_PER_HOUR);
        planeZoom = builder.defineInRange("plane.third_person_zoom", 6D, 1D, 20D);
    }

    public static enum SpeedType {
        KILOMETERS_PER_HOUR("plane.speedtype.kmh", bpt -> (bpt * 20D * 60D * 60D) / 1000D), BLOCKS_PER_SECOND("plane.speedtype.bps", bpt -> bpt * 20D);

        private String translationKey;
        private Function<Double, Double> converterFunction;

        SpeedType(String translationKey, Function<Double, Double> converterFunction) {
            this.translationKey = translationKey;
            this.converterFunction = converterFunction;
        }

        public Component getTextComponent(double bpt) {
            return Component.translatable(translationKey, Math.round(converterFunction.apply(bpt)));
        }
    }

}
