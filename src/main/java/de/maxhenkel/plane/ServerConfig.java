package de.maxhenkel.plane;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerConfig extends ConfigBase {

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> validFuelsSpec;
    public static List<Fluid> validFuels = new ArrayList<>();

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
        validFuelsSpec = builder.defineList("valid_fuels", Arrays.asList("car:bio_diesel"), Objects::nonNull);
    }

    @Override
    public void onReload(ModConfig.ModConfigEvent event) {
        super.onReload(event);
        validFuels = validFuelsSpec.get().stream().map(ResourceLocation::new).map(ForgeRegistries.FLUIDS::getValue).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
