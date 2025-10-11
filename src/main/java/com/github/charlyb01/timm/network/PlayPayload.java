package com.github.charlyb01.timm.network;

import com.github.charlyb01.timm.Timm;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayPayload(Identifier soundId) implements CustomPayload {
    public static final Identifier PLAY_PAYLOAD_ID = Timm.id("play_packet");
    public static final CustomPayload.Id<PlayPayload> ID = new CustomPayload.Id<>(PLAY_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, PlayPayload> CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, PlayPayload::soundId, PlayPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
