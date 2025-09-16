package com.github.charlyb01.timm.music;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.config.ModConfig;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class StructurePlaylist {
    public static HashMap<String, Integer> DISTANCE_FROM_STRUCTURE = new HashMap<>();
    public static HashMap<String, Identifier> EVENT_ID_FROM_STRUCTURE = new HashMap<>();

    public static void init() {
        Timm.LOGGER.info("Initializing structure playlists");

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
                    String structure = jsonReader.nextName();
                    Identifier structureId = Timm.id(structure);
                    int distance = 0;
                    ArrayList<String> structures = new ArrayList<>();

                    if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String name = jsonReader.nextName();
                            if (name.equals("distance")) {
                                distance = jsonReader.nextInt();
                            } else if (name.equals("structures") && jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                                jsonReader.beginArray();
                                while (jsonReader.hasNext()) {
                                    String musicId = jsonReader.nextString();
                                    structures.add(musicId);
                                }
                                jsonReader.endArray();
                            }
                        }
                        jsonReader.endObject();
                    }

                    for (String structureName : structures) {
                        DISTANCE_FROM_STRUCTURE.put(structureName, distance);
                        EVENT_ID_FROM_STRUCTURE.put(structureName, structureId);
                    }
                }
            }
            Timm.LOGGER.info("Structure playlists successfully initialized");
        } catch (IOException e) {
            Timm.LOGGER.error("Error reading structure playlist file: {}", e.getMessage());
        }
    }

    private static Path getPath() {
        FabricLoader loader = FabricLoader.getInstance();
        Path filePath = loader.getConfigDir()
                .resolve(Timm.MOD_ID)
                .resolve("structure_playlists.json");

        if (Files.exists(filePath)) {
            return filePath;
        }

        if (ModConfig.get().general.debugLog) {
            Timm.LOGGER.info("Player structure_playlists.json not found, using default one");
        }

        if (loader.getModContainer(Timm.MOD_ID).isEmpty()) {
            Timm.LOGGER.error("Mod not correctly loaded");
            return null;
        }

        ModContainer mod = loader.getModContainer(Timm.MOD_ID).get();
        Optional<Path> path = mod.findPath("assets/timm/custom/structure_playlists.json");
        if (path.isEmpty()) {
            Timm.LOGGER.error("Could not locate default structure_playlists.json");
            return null;
        }

        filePath = path.get();
        if (!Files.exists(filePath)) {
            Timm.LOGGER.error("Default structure_playlists.json does not exist");
            return null;
        }

        return filePath;
    }
}
