package com.github.charlyb01.timm.client.music;

import com.github.charlyb01.timm.Timm;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

public class Songs {
    private static final HashMap<Identifier, MutableComponent> SONG_TEXT_BY_SONG_ID = new HashMap<>();

    public static MutableComponent getSongText(Identifier songId) {
        if (songId == null) return null;
        return SONG_TEXT_BY_SONG_ID.getOrDefault(songId, Component.literal(songId.toString()));
    }

    public static void init() {
        Timm.LOGGER.info("Initializing songs");

        Path path = getPath();
        if (path == null) return;

        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(Files.newInputStream(path)))) {
            while (jsonReader.hasNext()) {
                JsonToken jsonToken = jsonReader.peek();
                if (jsonToken == JsonToken.BEGIN_OBJECT) {
                    jsonReader.beginObject();
                } else if (jsonToken == JsonToken.END_OBJECT) {
                    jsonReader.endObject();
                } else {
                    String song = jsonReader.nextName();
                    Identifier songId = Identifier.parse(song);
                    String songName = null;
                    String songUrl = null;

                    if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String name = jsonReader.nextName();
                            if (name.equals("name")) {
                                songName = jsonReader.nextString();
                            } else if (name.equals("link")) {
                                songUrl = jsonReader.nextString();
                            }
                        }
                        jsonReader.endObject();
                    }

                    SONG_TEXT_BY_SONG_ID.put(songId, makeSongText(songId, songName, songUrl));
                }
            }
            Timm.LOGGER.info("Songs successfully initialized");
        } catch (IOException e) {
            Timm.LOGGER.error("Error reading songs file: {}", e.getMessage());
        }
    }

    private static MutableComponent makeSongText(Identifier identifier, String name, String url) {
        MutableComponent song = Component.literal(name == null
                ? identifier.toString()
                : name);
        if (url != null) {
            song.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent.OpenUrl(URI.create(url))));
        }
        return song;
    }

    private static Path getPath() {
        FabricLoader loader = FabricLoader.getInstance();
        Path filePath = loader.getConfigDir()
                .resolve(Timm.MOD_ID)
                .resolve("songs.json");

        if (Files.exists(filePath)) {
            return filePath;
        }

        Timm.debugLog("Player songs.json not found, using default one");

        if (loader.getModContainer(Timm.MOD_ID).isEmpty()) {
            Timm.LOGGER.error("Mod not correctly loaded");
            return null;
        }

        ModContainer mod = loader.getModContainer(Timm.MOD_ID).get();
        Optional<Path> path = mod.findPath("assets/timm/custom/songs.json");
        if (path.isEmpty()) {
            Timm.LOGGER.error("Could not locate default songs.json");
            return null;
        }

        filePath = path.get();
        if (!Files.exists(filePath)) {
            Timm.LOGGER.error("Default songs.json does not exist");
            return null;
        }

        return filePath;
    }
}
