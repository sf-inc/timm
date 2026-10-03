package com.github.charlyb01.timm.network;

import com.github.charlyb01.timm.Timm;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PlayPayload(Identifier soundId) implements CustomPacketPayload {
    public static final Identifier PLAY_PAYLOAD_ID = Timm.id("play_packet");
    public static final CustomPacketPayload.Type<PlayPayload> ID = new CustomPacketPayload.Type<>(PLAY_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayPayload> CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, PlayPayload::soundId, PlayPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
