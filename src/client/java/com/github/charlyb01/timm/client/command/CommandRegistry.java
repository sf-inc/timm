package com.github.charlyb01.timm.client.command;

import com.github.charlyb01.timm.Timm;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class CommandRegistry {
    public static void init() {
        Timm.debugLog("Registering commands");

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            HelpCmd.register(dispatcher);
            NowPlayingCmd.register(dispatcher);
            OpenConfigCmd.register(dispatcher);
            SkipCmd.register(dispatcher);
            StopCmd.register(dispatcher);
        });
    }
}
