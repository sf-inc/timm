package com.github.charlyb01.timm.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class StopCmd {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(net.minecraft.commands.Commands.literal("timmstop")
                .executes(StopCmd::stop));
        dispatcher.register(net.minecraft.commands.Commands.literal("stp")
                .executes(StopCmd::stop));
    }

    private static int stop(CommandContext<CommandSourceStack> context) {
        if (NowPlayingCmd.SONG_ID == null) {
            context.getSource().sendSystemMessage(Component.translatable("cmd.stop.none"));
        } else {
            context.getSource().sendSystemMessage(Component.translatable("cmd.stop"));
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }
        return Command.SINGLE_SUCCESS;
    }
}
