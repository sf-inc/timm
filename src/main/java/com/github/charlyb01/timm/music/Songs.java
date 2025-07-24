package com.github.charlyb01.timm.music;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.config.Config;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

public class Songs {
    private static final HashMap<ResourceLocation, MutableComponent> SONG_TEXT_BY_SONG_ID = new HashMap<>();

    public static MutableComponent getSongText(ResourceLocation songId) {
        if (songId == null) return null;
        return SONG_TEXT_BY_SONG_ID.getOrDefault(songId, Component.literal(songId.toString()));
    }

    public static void init() {
        Timm.LOGGER.info("Initializing songs");

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
                    String song = jsonReader.nextName();
                    ResourceLocation songId = new ResourceLocation(song);
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
        } catch (IOException why) {
            Timm.LOGGER.error("Error reading songs file: {}", why.getMessage());
        }
    }

    private static MutableComponent makeSongText(ResourceLocation identifier, String name, String url) {
        MutableComponent song = Component.literal(name == null
                ? identifier.toString()
                : name);
        if (url != null) {
            song.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        }
        return song;
    }

    private static Path getPath() {
        Path loader = FMLPaths.CONFIGDIR.get();
        Path filePath = loader
                .resolve(Timm.MOD_ID)
                .resolve("songs.json");

        if (Files.exists(filePath)) {
            return filePath;
        }

        if (Config.DEBUG_LOG.get()) {
            Timm.LOGGER.info("Player songs.json not found using default one");
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
                .findResource("assets/timm/custom/songs.json")
        );

        filePath = path.get();
        if (!Files.exists(filePath)) {
            Timm.LOGGER.error("Default songs.json does not exist");
            return null;
        }

        return filePath;
    }
}
