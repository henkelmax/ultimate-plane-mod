package de.maxhenkel.plane;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerConfig extends ConfigBase {

    private final ModConfigSpec.ConfigValue<List<? extends String>> validFuelsSpec;
    public static List<Fluid> validFuels = new ArrayList<>();

    public ServerConfig(ModConfigSpec.Builder builder) {
        super(builder);
        validFuelsSpec = builder.defineList("valid_fuels", Arrays.asList("car:bio_diesel"), Objects::nonNull);
    }

    @Override
    public void onReload(ModConfigEvent event) {
        super.onReload(event);
        validFuels = validFuelsSpec.get().stream().map(ResourceLocation::new).map(BuiltInRegistries.FLUID::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
