package com.github.charlyb01.timm;

import com.github.charlyb01.timm.command.CommandRegistry;
import com.github.charlyb01.timm.config.Config;
import com.github.charlyb01.timm.config.ModConfigScreen;
import com.github.charlyb01.timm.music.BiomePlaylist;
import com.github.charlyb01.timm.music.Songs;
import com.github.charlyb01.timm.registry.SoundEventRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Timm.MOD_ID)
public class Timm
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "timm";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(final String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public Timm(IEventBus modEventBus)
    {
        NeoForge.EVENT_BUS.addListener(ClientModEvents::onClientCommands);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        SoundEventRegistry.register(modEventBus);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(
                            (minecraft, screen) -> ModConfigScreen.create(screen)
                    )
            );

            event.enqueueWork(() -> {
                BiomePlaylist.init();
                Songs.init();
            });
        }

        public static void onClientCommands(RegisterClientCommandsEvent event) {
            CommandRegistry.init(event);
        }
    }
}