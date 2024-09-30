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

    private final ModConfigSpec.ConfigValue<List<? extends String>> validFuelsSpec;
    public List<Tag<Fluid>> validFuels = new ArrayList<>();

    public ServerConfig(ModConfigSpec.Builder builder) {
        super(builder);
        validFuelsSpec = builder.defineList("valid_fuels", List.of("#car:gas_station"), Objects::nonNull);
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
