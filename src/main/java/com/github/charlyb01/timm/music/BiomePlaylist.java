package com.github.charlyb01.timm.music;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.config.Config;
import com.github.charlyb01.timm.registry.SoundEventRegistry;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class BiomePlaylist {
    public static final HashMap<ResourceLocation, ArrayList<ResourceLocation>> EVENTS_BY_BIOME = new HashMap<>();
    private static final ResourceLocation CREATIVE_ID = new ResourceLocation("creative");
    private static final ResourceLocation MENU_ID = new ResourceLocation("menu");

    public static Music getMusicSound(ResourceLocation biomeId, RandomSource random) {
        ArrayList<ResourceLocation> musics = EVENTS_BY_BIOME.get(biomeId);
        if (musics == null || musics.isEmpty()) return null;

        ResourceLocation soundEventId = musics.get(random.nextInt(musics.size()));
        Holder<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(soundEventId);
        if (soundEvent == null) return null;

        return new Music(
                soundEvent,
                Config.MIN_DELAY.get() * 20,
                Config.MAX_DELAY.get() * 20,
                false
        );
    }

    public static Music getCreativeMusic(RandomSource random) {
        ArrayList<ResourceLocation> musics = EVENTS_BY_BIOME.get(CREATIVE_ID);
        if (musics == null || musics.isEmpty()) return null;

        ResourceLocation soundEventId = musics.get(random.nextInt(musics.size()));
        Holder<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(soundEventId);
        if (soundEvent == null) return null;

        return new Music(
                soundEvent,
                Config.MIN_DELAY.get() * 20,
                Config.MAX_DELAY.get() * 20,
                false
        );
    }

    public static Music getMenuMusic() {
        ArrayList<ResourceLocation> musics = EVENTS_BY_BIOME.get(MENU_ID);
        if (musics == null || musics.isEmpty()) return null;

        ResourceLocation soundEventId = musics.get(0);
        Holder<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(soundEventId);
        if (soundEvent == null) return null;

        return new Music(soundEvent, 20, 60, false);
    }

    public static void init() {
        Timm.LOGGER.info("Initializing biome playlists");

        Path path = getPath();
        if (path == null) return;

        try {
            JsonReader jsonReader = new JsonReader(new InputStreamReader(Files.newInputStream(path)));
            while (jsonReader.hasNext()) {
                JsonToken jsonToken = jsonReader.peek();
                if (jsonToken == JsonToken.BEGIN_OBJECT) {
                    jsonReader.beginObject();
                } else if (jsonToken == JsonToken.END_OBJECT) {
                    jsonReader.endObject();
                } else {
                    String biomeName = jsonReader.nextName();
                    ResourceLocation biomeId = new ResourceLocation(biomeName);
                    ArrayList<ResourceLocation> musics = new ArrayList<>();

                    if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            String musicId = jsonReader.nextString();
                            musics.add(new ResourceLocation(musicId));
                        }
                        jsonReader.endArray();
                    }

                    EVENTS_BY_BIOME.put(biomeId, musics);
                }
            }
            Timm.LOGGER.info("Biome playlists successfully initialized");
        } catch (IOException why) {
            Timm.LOGGER.error("Error reading biome playlist file: {}", why.getMessage());
        }
    }

    private static Path getPath() {
        Path loader = FMLPaths.CONFIGDIR.get();
        Path filePath = loader
                .resolve(Timm.MOD_ID)
                .resolve("biome_playlists.json");

        if (Files.exists(filePath)) {
            return filePath;
        }

        if (Config.DEBUG_LOG.get()) {
            Timm.LOGGER.info("Player biome_playlist.json not found, using default one");
        }

        Optional<? extends ModContainer> container = ModList.get().getModContainerById(Timm.MOD_ID);
        if (container.isEmpty()) {
            Timm.LOGGER.error("Mod not correctly loaded");
            return null;
        }

        ModContainer mod = container.get();
        Optional<Path> path = Optional.of(mod
                .getModInfo()
                .getOwningFile()
                .getFile()
                .findResource("assets/timm/custom/biome_playlists.json")
        );

        filePath = path.get();
        if (!Files.exists(filePath)) {
            Timm.LOGGER.error("Default biome_playlist.json does not exist");
            return null;
        }

        return filePath;
    }
}
