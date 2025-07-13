package com.github.charlyb01.timm.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class HelpCmd {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("timmhelp")
                .executes(HelpCmd::help));
    }

    private static int help(CommandContext<CommandSourceStack> context) {
        Component cfg = Component.literal("\n/cfg ")
                .append(Component.translatable("cmd.help.cfg"));
        Component np = Component.literal("\n/nowplaying /np ")
                .append(Component.translatable("cmd.help.nowPlaying"));
        Component skip = Component.literal("\n/skip /next ")
                .append(Component.translatable("cmd.help.skip"));
        Component stop = Component.literal("\n/timmstop /stp ")
                .append(Component.translatable("cmd.help.stop"));
        Component help = Component.translatable("cmd.help")
                .append(cfg).append(np).append(skip).append(stop);

        context.getSource().sendSystemMessage(help);
        return Command.SINGLE_SUCCESS;
    }
}
