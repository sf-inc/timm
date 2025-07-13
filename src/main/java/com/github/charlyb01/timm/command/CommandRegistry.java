package com.github.charlyb01.timm.command;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.config.Config;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;

public class CommandRegistry {
    public static void init(RegisterClientCommandsEvent event) {
        if (Config.DEBUG_LOG.get()) {
            Timm.LOGGER.info("Registering client-side commands");
        }

        HelpCmd.register(event.getDispatcher());
        NowPlayingCmd.register(event.getDispatcher());
        OpenConfigCmd.register(event.getDispatcher());
        SkipCmd.register(event.getDispatcher());
        StopCmd.register(event.getDispatcher());
    }
}
