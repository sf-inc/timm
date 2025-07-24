package com.github.charlyb01.timm.registry;

import com.github.charlyb01.timm.Timm;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SoundEventRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Timm.MOD_ID);
    public static final HashMap<ResourceLocation, Holder<SoundEvent>> SOUNDEVENT_BY_ID = new HashMap<>();
    private static final HashMap<ResourceLocation, Supplier<SoundEvent>> REGISTRY_OBJECTS = new HashMap<>();

    static {
        register("menu");

        register("badlands");
        register("bamboo_jungle");
        register("beach");
        register("birch_forest");
        register("cherry_grove");
        register("cold_ocean");
        register("dark_forest");
        register("deep_dark");
        register("desert");
        register("dripstone_caves");
        register("flower_forest");
        register("forest");
        register("ice_spikes");
        register("jungle");
        register("lush_caves");
        register("meadow");
        register("mountains");
        register("mushroom_fields");
        register("ocean");
        register("plains");
        register("river");
        register("savanna");
        register("snow_plains");
        register("swamp");
        register("taiga");
        register("warm_ocean");
        register("windy_hills");

        register("basalt_deltas");
        register("crimson_forest");
        register("nether_wastes");
        register("soul_sand_valley");

        register("end");
    }

    private static void register(final String name) {
        ResourceLocation id = Timm.id(name);
        Supplier<SoundEvent> reg = SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
        REGISTRY_OBJECTS.put(id, reg);
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
        eventBus.addListener(SoundEventRegistry::setup);
    }

    private static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (Map.Entry<ResourceLocation, Supplier<SoundEvent>> entry : REGISTRY_OBJECTS.entrySet()) {
                SoundEvent sound = entry.getValue().get();
                ResourceLocation id = entry.getKey();

                Holder.Reference<SoundEvent> holder = BuiltInRegistries.SOUND_EVENT.getHolder(BuiltInRegistries.SOUND_EVENT.getResourceKey(sound).orElseThrow()).orElseThrow();

                SOUNDEVENT_BY_ID.put(id, holder);
            }
        });
    }
}
