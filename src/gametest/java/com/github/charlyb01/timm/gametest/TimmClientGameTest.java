package com.github.charlyb01.timm.gametest;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.github.charlyb01.timm.client.registry.SoundEventRegistry;
import com.github.charlyb01.timm.music.StructurePlaylist;
import com.github.charlyb01.timm.network.PlayPayload;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.List;

/**
 * Starts the game with the mod and checks that every mixin applies, that the music is picked from TIMM's playlists
 * in the main menu, in survival and in creative, and that the structure music sent by the server is played.
 */
public class TimmClientGameTest implements FabricClientGameTest {
    // The playlist is rolled on every call, so roll it enough times to get both TIMM's songs and the vanilla ones
    private static final int ROLLS = 100;

    @Override
    public void runTest(ClientGameTestContext context) {
        context.runOnClient(client -> {
            check(!BiomePlaylist.EVENTS_BY_BIOME.isEmpty(), "The biome playlists were not loaded");
            check(!StructurePlaylist.EVENT_ID_FROM_STRUCTURE.isEmpty(), "The structure playlists were not loaded");
            checkMusic(client, Identifier.parse("menu"), false);
        });

        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getConnection().waitForChunksRender();
            // Apply the mixins of the classes that are not loaded yet, so that a mixin not matching the game fails
            MixinEnvironment.getCurrentEnvironment().audit();
            context.takeScreenshot("in_game");

            // Survival: the songs come from the playlist of the biome
            context.runOnClient(client -> {
                Identifier biome = client.level.getBiome(client.player.blockPosition()).unwrapKey().orElseThrow().identifier();
                checkMusic(client, biome, true);
            });

            // Creative: the songs are not tied to a biome, so that they do not fade out on biome switch
            singleplayer.getServer().runCommand("gamemode creative @a");
            context.waitFor(client -> client.player.getAbilities().instabuild);
            context.runOnClient(client -> checkMusic(client, Identifier.parse("creative"), false));

            // Structure music: the song sent by the server starts right away when no music is playing
            Music villageMusic = new Music(SoundEventRegistry.SOUNDEVENT_BY_ID.get(Timm.id("village")), 0, 0, false);
            context.runOnClient(client -> client.getMusicManager().stopPlaying());
            singleplayer.getServer().runOnServer(server ->
                    ServerPlayNetworking.send(singleplayer.getConnection().getServerPlayer(), new PlayPayload(Timm.id("village"))));
            context.waitFor(client -> client.getMusicManager().isPlayingMusic(villageMusic), 100);

            // Let the server check the structures around the player for a few seconds
            singleplayer.getServer().runCommand("gamemode survival @a");
            context.waitTicks(100);
        }
    }

    private static void checkMusic(Minecraft client, Identifier playlistId, boolean tiedToBiome) {
        List<Identifier> playlist = BiomePlaylist.EVENTS_BY_BIOME.get(playlistId);
        check(playlist != null && !playlist.isEmpty(), "No playlist for " + playlistId);

        boolean timmSongPicked = false;
        for (int i = 0; i < ROLLS; ++i) {
            Music music = client.getSituationalMusic();
            check(music != null, "No music for " + playlistId);
            Identifier sound = music.sound().value().location();
            check(playlist.contains(sound), "The music " + sound + " is not in the playlist of " + playlistId);
            check(!music.replaceCurrentMusic(), "The music " + sound + " would cut the current song");

            boolean timmSong = SoundEventRegistry.SOUNDEVENT_BY_ID.containsKey(sound);
            timmSongPicked |= timmSong;
            // Only TIMM's biome songs are tied to the biome, so that they fade out when leaving it
            Identifier biomeEvent = tiedToBiome && timmSong ? sound : BiomePlaylist.UNDEFINED_BIOME;
            check(biomeEvent.equals(BiomePlaylist.CURRENT_BIOME_EVENT),
                    "The music " + sound + " is tied to " + BiomePlaylist.CURRENT_BIOME_EVENT + " instead of " + biomeEvent);
        }
        check(timmSongPicked, "No TIMM song was picked for " + playlistId);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
