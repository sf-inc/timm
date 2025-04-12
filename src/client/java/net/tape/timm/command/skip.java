package net.tape.timm.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundEvent;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import net.tape.timm.songControls;
import net.tape.timm.util.SpotifyLinks;

public class skip {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("skip")
                .requires(fabricClientCommandSource -> true)
                .executes(ctx -> {
                    final FabricClientCommandSource src = ctx.getSource();
                    return skipNoSong(src);
                })
                .then(argument("song", StringArgumentType.string())
                        .executes(ctx -> {
                            final FabricClientCommandSource src = ctx.getSource();
                            final String songName = StringArgumentType.getString(ctx, "song");
                            return skipWithSong(src, songName);
                        })
                ));
    }

    private static int skipNoSong(FabricClientCommandSource source) {
        songControls.skip();

        String song = songControls.nowPlaying();
        String songURL = SpotifyLinks.get(song);

        if (song != null && songURL != null) {
            Text songLink = Text.literal(song)
                    .setStyle(Style.EMPTY.withColor(0x1ABA53)
                            .withUnderline(true)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, songURL)));

            source.sendFeedback(Text.literal("Now playing: ").append(songLink));
        } else if (song != null) {
            source.sendFeedback(Text.literal("Now playing: " + song));
        } else {
            source.sendFeedback(Text.literal("Nothing is currently playing."));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int skipWithSong(FabricClientCommandSource source, String songName) {
        Identifier id = Identifier.tryParse(songName);

        if (id != null) {
            SoundEvent sound = SoundEvent.of(id);
            songControls.skip(sound);

            String song = songControls.nowPlaying();
            String songURL = SpotifyLinks.get(song);

            if (song != null && songURL != null) {
                Text songLink = Text.literal(song)
                        .setStyle(Style.EMPTY.withColor(0x1ABA53)
                                .withUnderline(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, songURL)));

                source.sendFeedback(Text.literal("Now playing: ").append(songLink));
            } else if (song != null) {
                source.sendFeedback(Text.literal("Now playing: " + song));
            } else {
                source.sendFeedback(Text.literal("Nothing is currently playing."));
            }

            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFeedback(Text.translatable("timm.commands.skip.badId", songName));
            return -1;
        }
    }
}

