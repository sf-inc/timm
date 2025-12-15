package com.github.charlyb01.timm.client.music;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.client.registry.SoundEventRegistry;
import com.github.charlyb01.timm.config.ModConfig;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class BiomePlaylist {
    public static final Identifier UNDEFINED_BIOME = Timm.id("undefined_biome");
    public static Identifier CURRENT_BIOME_EVENT = UNDEFINED_BIOME;
    public static final HashMap<Identifier, ArrayList<Identifier>> EVENTS_BY_BIOME = new HashMap<>();
    private static final Identifier CREATIVE_ID = new Identifier("creative");
    private static final Identifier END_ID = new Identifier("end");
    private static final Identifier MENU_ID = new Identifier("menu");

    public static MusicSound getMusicSound(Identifier biomeId, Random random) {
        ArrayList<Identifier> musics = EVENTS_BY_BIOME.get(biomeId);
        if (musics == null || musics.isEmpty()) {
            CURRENT_BIOME_EVENT = UNDEFINED_BIOME;
            return null;
        }

        Identifier soundEventId = musics.get(random.nextInt(musics.size()));
        RegistryEntry<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(soundEventId);
        if (soundEvent == null) {
            CURRENT_BIOME_EVENT = UNDEFINED_BIOME;
            return null;
        }

        CURRENT_BIOME_EVENT = soundEventId;
        return new MusicSound(
                soundEvent,
                ModConfig.get().general.minDelay * 20,
                ModConfig.get().general.maxDelay * 20,
                false);
    }

    public static MusicSound getCreativeMusic(Random random) {
        ArrayList<Identifier> musics = EVENTS_BY_BIOME.get(CREATIVE_ID);
        if (musics == null || musics.isEmpty()) return null;

        Identifier soundEventId = musics.get(random.nextInt(musics.size()));
        RegistryEntry<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(soundEventId);
        if (soundEvent == null) return null;

        return new MusicSound(
                soundEvent,
                ModConfig.get().general.minDelay * 20,
                ModConfig.get().general.maxDelay * 20,
                false);
    }

    public static MusicSound getEndMusic(Random random) {
        ArrayList<Identifier> musics = EVENTS_BY_BIOME.get(END_ID);
        if (musics == null || musics.isEmpty()) return null;

        Identifier soundEventId = musics.get(random.nextInt(musics.size()));
        RegistryEntry<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(soundEventId);
        if (soundEvent == null) return null;

        return new MusicSound(
                soundEvent,
                ModConfig.get().general.minDelay * 20,
                ModConfig.get().general.maxDelay * 20,
                false);
    }

    public static MusicSound getMenuMusic() {
        ArrayList<Identifier> musics = EVENTS_BY_BIOME.get(MENU_ID);
        if (musics == null || musics.isEmpty()) return null;

        Identifier soundEventId = musics.get(0);
        RegistryEntry<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(soundEventId);
        if (soundEvent == null) return null;

        return new MusicSound(soundEvent, 20, 60, false);
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
                    Identifier biomeId = new Identifier(biomeName);
                    ArrayList<Identifier> musics = new ArrayList<>();

                    if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            String musicId = jsonReader.nextString();
                            musics.add(new Identifier(musicId));
                        }
                        jsonReader.endArray();
                    }

                    EVENTS_BY_BIOME.put(biomeId, musics);
                }
            }
            Timm.LOGGER.info("Biome playlists successfully initialized");
        } catch (IOException e) {
            Timm.LOGGER.error("Error reading biome playlist file: {}", e.getMessage());
        }
    }

    private static Path getPath() {
        FabricLoader loader = FabricLoader.getInstance();
        Path filePath = loader.getConfigDir()
                .resolve(Timm.MOD_ID)
                .resolve("biome_playlists.json");

        if (Files.exists(filePath)) {
            return filePath;
        }

        Timm.debugLog("Player biome_playlist.json not found, using default one");

        if (loader.getModContainer(Timm.MOD_ID).isEmpty()) {
            Timm.LOGGER.error("Mod not correctly loaded");
            return null;
        }

        ModContainer mod = loader.getModContainer(Timm.MOD_ID).get();
        Optional<Path> path = mod.findPath("assets/timm/custom/biome_playlists.json");
        if (path.isEmpty()) {
            Timm.LOGGER.error("Could not locate default biome_playlist.json");
            return null;
        }

        filePath = path.get();
        if (!Files.exists(filePath)) {
            Timm.LOGGER.error("Default biome_playlist.json does not exist");
            return null;
        }

        return filePath;
    }
}
