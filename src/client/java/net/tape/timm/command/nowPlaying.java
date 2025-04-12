package net.tape.timm.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.tape.timm.songControls;
import net.tape.timm.util.SpotifyLinks;

public class nowPlaying {

    // Credit to Earthcomputer for writing most of the command handling code
    // i couldn't have done command interface shit if it weren't for clientcommands being open source
    // earthcomputer a real one for that
    // https://github.com/Earthcomputer/clientcommands

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("nowplaying")
                .requires(fabricClientCommandSource -> true)
                .executes(ctx -> printNowPlaying(ctx.getSource())));
        dispatcher.register(literal("np")
                .requires(fabricClientCommandSource -> true)
                .executes(ctx -> printNowPlaying(ctx.getSource())));
    }

    private static int printNowPlaying(FabricClientCommandSource source) {
        if (songControls.nowPlaying() != null) {

            String song = songControls.nowPlaying();
            String songURL = SpotifyLinks.get(song);


            if (songURL != null) {
                Text songLink = Text.literal(song)
                        .setStyle(Style.EMPTY.withColor(0x1ABA53)
                                .withUnderline(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, songURL)));

                source.sendFeedback(Text.literal("Now playing: ").append(songLink));
            } else {

                source.sendFeedback(Text.literal("Now playing: " + song));
            }
        } else {

            source.sendFeedback(Text.translatable("timm.commands.nowPlaying.null"));
        }

        return Command.SINGLE_SUCCESS;
    }
}
