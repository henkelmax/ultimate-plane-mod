package de.maxhenkel.plane;

import de.maxhenkel.corelib.config.ConfigBase;
import de.maxhenkel.corelib.tag.Tag;
import de.maxhenkel.corelib.tag.TagUtils;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerConfig extends ConfigBase {

    public final ModConfigSpec.DoubleValue maxPlaneDamage;
    public final ModConfigSpec.BooleanValue planeToPlaneCollision;
    public final ModConfigSpec.IntValue bushPlaneFuelCapacity;
    public final ModConfigSpec.IntValue planeFuelCapacity;
    public final ModConfigSpec.IntValue cargoPlaneFuelCapacity;
    public final ModConfigSpec.IntValue transporterPlaneFuelCapacity;


    private final ModConfigSpec.ConfigValue<List<? extends String>> validFuelsSpec;
    public List<Tag<Fluid>> validFuels = new ArrayList<>();

    public ServerConfig(ModConfigSpec.Builder builder) {
        super(builder);
        maxPlaneDamage = builder.worldRestart().defineInRange("plane.max_damage", 200D, 100D, Double.MAX_VALUE);
        planeToPlaneCollision = builder.worldRestart().define("plane.plane_to_plane_collision", true);

        bushPlaneFuelCapacity = builder.worldRestart().defineInRange("plane.bush_plane.fuel_capacity", 8_000, 1000, Integer.MAX_VALUE);
        planeFuelCapacity = builder.worldRestart().defineInRange("plane.plane.fuel_capacity", 10_000, 1000, Integer.MAX_VALUE);
        cargoPlaneFuelCapacity = builder.worldRestart().defineInRange("plane.cargo.fuel_capacity", 16_000, 1000, Integer.MAX_VALUE);
        transporterPlaneFuelCapacity = builder.worldRestart().defineInRange("plane.transporter.fuel", 16_000, 1000, Integer.MAX_VALUE);

        validFuelsSpec = builder.worldRestart().defineList("plane.valid_fuels", () -> List.of("#car:gas_station"), () -> "", Objects::nonNull);
    }

    @Override
    public void onReload(ModConfigEvent.Reloading event) {
        super.onReload(event);
        onConfigChanged();
    }

    @Override
    public void onLoad(ModConfigEvent.Loading evt) {
        super.onLoad(evt);
        onConfigChanged();
    }

    private void onConfigChanged() {
        validFuels = validFuelsSpec.get().stream().map(TagUtils::getFluid).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
