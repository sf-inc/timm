package com.github.charlyb01.timm.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class StopCmd {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("timmstop")
                .requires(fabricClientCommandSource -> true)
                .executes(StopCmd::stop));

        dispatcher.register(ClientCommands.literal("stp")
                .requires(fabricClientCommandSource -> true)
                .executes(StopCmd::stop));
    }

    private static int stop(CommandContext<FabricClientCommandSource> context ) {
        if (NowPlayingCmd.SONG_ID == null) {
            context.getSource().sendFeedback(Component.translatable("cmd.stop.none"));
        } else {
            context.getSource().sendFeedback(Component.translatable("cmd.stop"));
            context.getSource().getClient().getMusicManager().stopPlaying();
        }
        return Command.SINGLE_SUCCESS;
    }
}
