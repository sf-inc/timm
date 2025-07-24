package com.github.charlyb01.timm.command;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.music.Songs;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NowPlayingCmd {
    public static ResourceLocation SONG_ID;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(net.minecraft.commands.Commands.literal("nowplaying")
                .executes(NowPlayingCmd::nowPlaying));
        dispatcher.register(net.minecraft.commands.Commands.literal("np")
                .executes(NowPlayingCmd::nowPlaying));
    }

    public static int nowPlaying(CommandContext<CommandSourceStack> context) {
        Timm.LOGGER.info("[TIMMOD] SONG_ID: {}", SONG_ID);
        Component song = Songs.getSongText(SONG_ID);
        Timm.LOGGER.info("[TIMMMOD] Song: {}", song);
        Component text = song == null
                ? Component.translatable("cmd.nowPlaying.none")
                : Component.translatable("record.nowPlaying", song);
        context.getSource().sendSystemMessage(text);
        return Command.SINGLE_SUCCESS;
    }
}