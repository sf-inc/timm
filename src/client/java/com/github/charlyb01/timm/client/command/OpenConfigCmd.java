package com.github.charlyb01.timm.client.command;

import com.github.charlyb01.timm.config.ModConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class OpenConfigCmd {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("cfg")
                .requires(fabricClientCommandSource -> true)
                .executes(OpenConfigCmd::configScreen));
    }

    private static int configScreen(CommandContext<FabricClientCommandSource> context ) {
        context.getSource().getClient().send(() -> context.getSource().getClient().setScreen(
                AutoConfigClient.getConfigScreen(ModConfig.class, context.getSource().getClient().currentScreen).get()));
        return Command.SINGLE_SUCCESS;
    }
}
