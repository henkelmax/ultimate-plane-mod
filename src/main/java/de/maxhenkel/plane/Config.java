package de.maxhenkel.plane;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> VALID_FUELS;
    public static List<Fluid> VALID_FUEL_LIST = new ArrayList<>();

    public static ForgeConfigSpec.BooleanValue SHOW_PLANE_INFO;
    public static ForgeConfigSpec.DoubleValue PLANE_INFO_SCALE;
    public static ForgeConfigSpec.EnumValue<SpeedType> PLANE_INFO_SPEED_TYPE;

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<ServerConfig, ForgeConfigSpec> specPairServer = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPairServer.getRight();
        SERVER = specPairServer.getLeft();

        Pair<ClientConfig, ForgeConfigSpec> specPairClient = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPairClient.getRight();
        CLIENT = specPairClient.getLeft();
    }

    public static void loadServer() {
        VALID_FUEL_LIST = VALID_FUELS.get().stream().map(ResourceLocation::new).map(ForgeRegistries.FLUIDS::getValue).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static class ServerConfig {

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            VALID_FUELS = builder.defineList("valid_fuels", Arrays.asList("car:bio_diesel"), Objects::nonNull);
        }
    }

    public static class ClientConfig {
        public ClientConfig(ForgeConfigSpec.Builder builder) {
            SHOW_PLANE_INFO = builder.define("plane_info.enabled", true);
            PLANE_INFO_SCALE = builder.defineInRange("plane_info.scale", 0.75D, 0.1D, 2D);
            PLANE_INFO_SPEED_TYPE = builder.defineEnum("plane_info.speed_type", SpeedType.KILOMETERS_PER_HOUR);
        }
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
