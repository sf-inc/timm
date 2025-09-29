package com.github.charlyb01.timm.client.network;

import com.github.charlyb01.timm.config.ModConfig;
import com.github.charlyb01.timm.network.Constants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class NetworkingRegistry {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(
                Constants.PLAY_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    if (!ModConfig.get().general.enableStructureMusic) return;
                    if (client.world == null || client.player == null) return;

                    client.getMusicTracker().stop();

                    Identifier soundId = buf.readIdentifier();
                    client.world.playSound(
                            client.player.getX(),
                            client.player.getY(),
                            client.player.getZ(),
                            SoundEvent.of(soundId), SoundCategory.MUSIC,
                            1.0F,
                            1.0F,
                            false);
                });
    }
}
