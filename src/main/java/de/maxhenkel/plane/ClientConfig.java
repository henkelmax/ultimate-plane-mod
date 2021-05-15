package de.maxhenkel.plane;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

public class ClientConfig extends ConfigBase {

    public final ForgeConfigSpec.BooleanValue showPlaneInfo;
    public final ForgeConfigSpec.DoubleValue planeInfoScale;
    public final ForgeConfigSpec.EnumValue<SpeedType> planeInfoSpeedType;
    public final ForgeConfigSpec.DoubleValue planeZoom;
    public final ForgeConfigSpec.DoubleValue fuelConsumptionFactor;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
        showPlaneInfo = builder.define("plane_info.enabled", true);
        planeInfoScale = builder.defineInRange("plane_info.scale", 0.75D, 0.1D, 2D);
        planeInfoSpeedType = builder.defineEnum("plane_info.speed_type", SpeedType.KILOMETERS_PER_HOUR);
        planeZoom = builder.defineInRange("plane.third_person_zoom", 6D, 1D, 20D);
        fuelConsumptionFactor = builder.defineInRange("plane.fuel_consumption_factor", 1D, 0D, 100D);
    }

    public static enum SpeedType {
        KILOMETERS_PER_HOUR("plane.speedtype.kmh", bpt -> (bpt * 20D * 60D * 60D) / 1000D), BLOCKS_PER_SECOND("plane.speedtype.bps", bpt -> bpt * 20D);

        private String translationKey;
        private Function<Double, Double> converterFunction;

        SpeedType(String translationKey, Function<Double, Double> converterFunction) {
            this.translationKey = translationKey;
            this.converterFunction = converterFunction;
        }

        public ITextComponent getTextComponent(double bpt) {
            return new TranslationTextComponent(translationKey, Math.round(converterFunction.apply(bpt)));
        }
    }

}
