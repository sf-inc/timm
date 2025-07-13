package com.github.charlyb01.timm;

import com.github.charlyb01.timm.command.CommandRegistry;
import com.github.charlyb01.timm.config.Config;
import com.github.charlyb01.timm.config.ModConfigScreen;
import com.github.charlyb01.timm.music.BiomePlaylist;
import com.github.charlyb01.timm.music.Songs;
import com.github.charlyb01.timm.registry.SoundEventRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

    public Timm()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(ClientModEvents::onClientCommands);

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
