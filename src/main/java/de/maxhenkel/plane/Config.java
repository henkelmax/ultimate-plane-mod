package de.maxhenkel.plane;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config {

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> VALID_FUELS;
    public static List<Fluid> VALID_FUEL_LIST = new ArrayList<>();

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

        }
    }

}
