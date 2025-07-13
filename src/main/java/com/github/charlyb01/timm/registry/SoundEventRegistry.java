package com.github.charlyb01.timm.registry;

import com.github.charlyb01.timm.Timm;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

public class SoundEventRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Timm.MOD_ID);
    public static final HashMap<ResourceLocation, Holder<SoundEvent>> SOUNDEVENT_BY_ID = new HashMap<>();
    private static final HashMap<ResourceLocation, RegistryObject<SoundEvent>> REGISTRY_OBJECTS = new HashMap<>();

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
        RegistryObject<SoundEvent> reg = SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
        REGISTRY_OBJECTS.put(id, reg);
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
        eventBus.addListener(SoundEventRegistry::setup);
    }

    private static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
                REGISTRY_OBJECTS.forEach((id, reg) ->
                        ForgeRegistries.SOUND_EVENTS.getHolder(reg.get()).ifPresent(holder ->
                                SOUNDEVENT_BY_ID.put(id, holder)
                        )
                )
        );
    }

}
