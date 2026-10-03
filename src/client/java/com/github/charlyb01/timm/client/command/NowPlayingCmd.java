package com.github.charlyb01.timm.client.command;

import com.github.charlyb01.timm.client.music.Songs;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class NowPlayingCmd {
    public static Identifier SONG_ID;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("nowplaying")
                .requires(fabricClientCommandSource -> true)
                .executes(NowPlayingCmd::nowPlaying));

        dispatcher.register(ClientCommands.literal("np")
                .requires(fabricClientCommandSource -> true)
                .executes(NowPlayingCmd::nowPlaying));
    }

    public static int nowPlaying(CommandContext<FabricClientCommandSource> context ) {
        Component song = Songs.getSongText(NowPlayingCmd.SONG_ID);
        Component text = song == null
                ? Component.translatable("cmd.nowPlaying.none")
                : Component.translatable("record.nowPlaying", song);
        context.getSource().sendFeedback(text);
        return Command.SINGLE_SUCCESS;
    }
}
