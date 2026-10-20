package com.github.charlyb01.timm.client.music;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.client.imixin.MusicTrackerIMixin;
import com.github.charlyb01.timm.client.registry.SoundEventRegistry;
import com.github.charlyb01.timm.config.ModConfig;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

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
    private static final Identifier CREATIVE_ID = Identifier.parse("creative");
    private static final Identifier END_ID = Identifier.parse("end");
    private static final Identifier MENU_ID = Identifier.parse("menu");

    @Nullable
    public static Music getMusicSound(@Nullable Identifier biomeId, @Nullable Music original, RandomSource random) {
        Identifier soundEventId = pick(biomeId, random);
        Holder<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(soundEventId);
        CURRENT_BIOME_EVENT = soundEvent != null ? soundEventId : UNDEFINED_BIOME;
        return music(soundEvent, original);
    }

    @Nullable
    public static Music getCreativeMusic(@Nullable Music original, RandomSource random) {
        Holder<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(pick(CREATIVE_ID, random));
        return music(soundEvent, original);
    }

    @Nullable
    public static Music getEndMusic(@Nullable Music original, RandomSource random) {
        Holder<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(pick(END_ID, random));
        return music(soundEvent, original);
    }

    @Nullable
    public static Music getMenuMusic(@Nullable Music original, RandomSource random) {
        Holder<SoundEvent> soundEvent = SoundEventRegistry.SOUNDEVENT_BY_ID.get(pick(MENU_ID, random));
        return music(soundEvent, original, 20, 60);
    }

    @Nullable
    private static Identifier pick(@Nullable Identifier playlistId, RandomSource random) {
        ArrayList<Identifier> musics = playlistId == null ? null : EVENTS_BY_BIOME.get(playlistId);
        if (musics == null || musics.isEmpty()) return null;

        return musics.get(random.nextInt(musics.size()));
    }

    // Without a Timm song ("default", any other id, or no playlist), play the original music instead (none if silent).
    // The pick is redone every tick: the original music must not replace a playing Timm song, or vanilla music
    // (menu, End, biomes with replace_current_music) would cut it.
    // Every song uses the config delays
    @Nullable
    private static Music music(@Nullable Holder<SoundEvent> soundEvent, @Nullable Music original) {
        return music(soundEvent, original, ModConfig.get().general.minDelay * 20, ModConfig.get().general.maxDelay * 20);
    }

    @Nullable
    private static Music music(@Nullable Holder<SoundEvent> soundEvent, @Nullable Music original, int minDelay, int maxDelay) {
        if (soundEvent != null) return new Music(soundEvent, minDelay, maxDelay, false);
        if (original == null) return null;

        boolean timmPlaying = ((MusicTrackerIMixin) Minecraft.getInstance().getMusicManager()).timm$isPlayingTimmSong();
        return new Music(original.sound(), minDelay, maxDelay, original.replaceCurrentMusic() && !timmPlaying);
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
                    Identifier biomeId = Identifier.parse(biomeName);
                    ArrayList<Identifier> musics = new ArrayList<>();

                    if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            String musicId = jsonReader.nextString();
                            musics.add(Identifier.parse(musicId));
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
