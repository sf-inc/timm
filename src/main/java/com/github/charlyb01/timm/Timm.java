package com.github.charlyb01.timm;

import com.github.charlyb01.timm.config.ModConfig;
import com.github.charlyb01.timm.music.StructurePlaylist;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timm implements ModInitializer {
    public static final String MOD_ID = "timm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));
        StructurePlaylist.init();
    }

    public static Identifier id(final String path) {
        return new Identifier(MOD_ID, path);
    }

    public static void debugLog(String debugString) {
        if (ModConfig.get().general.debugLog) {
            LOGGER.info(debugString);
        }
    }
}
