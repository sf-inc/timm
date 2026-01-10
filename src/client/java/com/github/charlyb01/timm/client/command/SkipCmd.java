package com.github.charlyb01.timm.client.command;

import com.github.charlyb01.timm.config.ModConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.sound.MusicSound;

public class SkipCmd {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("skip")
                .requires(fabricClientCommandSource -> true)
                .executes(SkipCmd::skip));

        dispatcher.register(ClientCommandManager.literal("next")
                .requires(fabricClientCommandSource -> true)
                .executes(SkipCmd::skip));
    }

    private static int skip(CommandContext<FabricClientCommandSource> context ) {
        context.getSource().getClient().getMusicTracker().stop();
        MusicSound musicInstance = context.getSource().getClient().getMusicInstance();
        if (musicInstance != null) {
            context.getSource().getClient().getMusicTracker().play(musicInstance);

            if (ModConfig.get().general.printOnSkip) {
                NowPlayingCmd.nowPlaying(context);
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
