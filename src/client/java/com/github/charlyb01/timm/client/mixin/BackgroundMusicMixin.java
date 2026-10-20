package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.attribute.BackgroundMusic;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

// Only the default and creative musics are replaced: underwater music stays vanilla
@Mixin(BackgroundMusic.class)
public class BackgroundMusicMixin {
    // Second read only: the first one is the presence check, so creative music is used only where vanilla has one
    @ModifyExpressionValue(method = "getCurrent", at = @At(value = "FIELD", target = "Lnet/minecraft/world/attribute/BackgroundMusic;creativeMusic:Ljava/util/Optional;", opcode = Opcodes.GETFIELD, ordinal = 1))
    private Optional<MusicSound> updateCreativeMusic(Optional<MusicSound> original) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return original;

        return Optional.ofNullable(BiomePlaylist.getCreativeMusic(original.orElse(null), player.getRandom()));
    }

    // Empty when vanilla plays nothing (e.g. pale garden)
    @ModifyExpressionValue(method = "getCurrent", at = @At(value = "FIELD", target = "Lnet/minecraft/world/attribute/BackgroundMusic;defaultMusic:Ljava/util/Optional;", opcode = Opcodes.GETFIELD))
    private Optional<MusicSound> updateDefaultMusic(Optional<MusicSound> original) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return original;

        World world = player.getEntityWorld();
        if (world.getRegistryKey() == World.END) {
            return Optional.ofNullable(BiomePlaylist.getEndMusic(original.orElse(null), player.getRandom()));
        }

        Identifier biomeId = world.getBiome(player.getBlockPos()).getKey().map(RegistryKey::getValue).orElse(null);
        return Optional.ofNullable(BiomePlaylist.getMusicSound(biomeId, original.orElse(null), player.getRandom()));
    }
}
