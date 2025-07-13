package com.github.charlyb01.timm.command;

import com.github.charlyb01.timm.config.ModConfigScreen;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;

public class OpenConfigCmd {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(net.minecraft.commands.Commands.literal("cfg")
                .executes(OpenConfigCmd::configScreen));
    }

    private static int configScreen(CommandContext<CommandSourceStack> context) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> mc.setScreen(ModConfigScreen.create(mc.screen)));
        return Command.SINGLE_SUCCESS;
    }
}
