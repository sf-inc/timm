package com.github.charlyb01.timm.client.network;

import com.github.charlyb01.timm.client.imixin.MusicTrackerIMixin;
import com.github.charlyb01.timm.config.ModConfig;
import com.github.charlyb01.timm.network.PlayPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class NetworkingRegistry {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(
                PlayPayload.ID,
                (payload, context) -> {
                    if (!ModConfig.get().general.enableStructureMusic) return;
                    if (context.client().world == null || context.player() == null) return;

                    Identifier soundId = payload.soundId();
                    ((MusicTrackerIMixin) context.client().getMusicTracker()).timm$setStructureEventId(soundId);
                });
    }
}
