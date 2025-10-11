package com.github.charlyb01.timm.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NetworkingRegistry {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(PlayPayload.ID, PlayPayload.CODEC);
    }
}
