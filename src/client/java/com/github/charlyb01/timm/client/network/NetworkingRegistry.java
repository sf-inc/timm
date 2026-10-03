package com.github.charlyb01.timm.client.network;

import com.github.charlyb01.timm.client.imixin.MusicManagerIMixin;
import com.github.charlyb01.timm.config.ModConfig;
import com.github.charlyb01.timm.network.PlayPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.Identifier;

public class NetworkingRegistry {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(
                PlayPayload.ID,
                (payload, context) -> {
                    if (!ModConfig.get().general.enableStructureMusic) return;
                    if (context.client().level == null || context.player() == null) return;

                    Identifier soundId = payload.soundId();
                    ((MusicManagerIMixin) context.client().getMusicManager()).timm$setStructureEventId(soundId);
                });
    }
}
