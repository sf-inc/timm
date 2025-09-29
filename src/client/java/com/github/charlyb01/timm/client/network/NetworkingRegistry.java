package com.github.charlyb01.timm.client.network;

import com.github.charlyb01.timm.client.imixin.MusicTrackerIMixin;
import com.github.charlyb01.timm.config.ModConfig;
import com.github.charlyb01.timm.network.Constants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class NetworkingRegistry {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(
                Constants.PLAY_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    if (!ModConfig.get().general.enableStructureMusic) return;
                    if (client.world == null || client.player == null) return;

                    Identifier soundId = buf.readIdentifier();
                    ((MusicTrackerIMixin) client.getMusicTracker()).timm$setStructureEventId(soundId);
                });
    }
}
