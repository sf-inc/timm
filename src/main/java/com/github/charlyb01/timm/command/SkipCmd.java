package com.github.charlyb01.timm.command;

import com.github.charlyb01.timm.config.Config;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;

public class SkipCmd {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(net.minecraft.commands.Commands.literal("skip")
                .executes(SkipCmd::skip));
        dispatcher.register(net.minecraft.commands.Commands.literal("next")
                .executes(SkipCmd::skip));
    }

    private static int skip(CommandContext<CommandSourceStack> context) {
        Minecraft mc = Minecraft.getInstance();
        mc.getMusicManager().stopPlaying();
        mc.getMusicManager().startPlaying(mc.getSituationalMusic());

        if (Config.PRINT_ON_SKIP.get()) {
            NowPlayingCmd.nowPlaying(context);
        }

        return Command.SINGLE_SUCCESS;
    }
}