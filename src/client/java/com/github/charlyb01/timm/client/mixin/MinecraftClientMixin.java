package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public ClientPlayerEntity player;

    @ModifyExpressionValue(method = "getMusicInstance", at = @At(value = "FIELD", target = "Lnet/minecraft/sound/MusicType;MENU:Lnet/minecraft/sound/MusicSound;"))
    private MusicSound updateMenuMusic(MusicSound original) {
        MusicSound musicSound = BiomePlaylist.getMenuMusic();
        return musicSound != null ? musicSound : original;
    }

    @ModifyExpressionValue(method = "getMusicInstance", at = @At(value = "FIELD", target = "Lnet/minecraft/sound/MusicType;END:Lnet/minecraft/sound/MusicSound;"))
    private MusicSound updateEndMusic(MusicSound original) {
        if (this.player == null) return original;

        RegistryEntry<Biome> biome = this.player.getEntityWorld().getBiome(this.player.getBlockPos());
        Optional<RegistryKey<Biome>> biomeKey = biome.getKey();
        if (biomeKey.isEmpty()) return original;
        
        MusicSound musicSound = BiomePlaylist.getMusicSound(biomeKey.get().getValue(), this.player.getRandom());
        return musicSound != null ? musicSound : original;
    }

    @ModifyExpressionValue(method = "getMusicInstance", at = @At(value = "FIELD", target = "Lnet/minecraft/sound/MusicType;CREATIVE:Lnet/minecraft/sound/MusicSound;"))
    private MusicSound updateCreativeMusic(MusicSound original) {
        if (this.player == null) return original;

        MusicSound musicSound = BiomePlaylist.getCreativeMusic(this.player.getRandom());
        return musicSound != null ? musicSound : original;
    }

    @Definition(id = "getMusic", method = "Lnet/minecraft/world/biome/Biome;getMusic()Ljava/util/Optional;")
    @Expression("?.getMusic()")
    @ModifyExpressionValue(method = "getMusicInstance", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Optional<Pool<MusicSound>> updateBiomeMusic(Optional<Pool<MusicSound>> original) {
        if (this.player == null) return original;

        RegistryEntry<Biome> biome = this.player.getEntityWorld().getBiome(this.player.getBlockPos());
        Optional<RegistryKey<Biome>> biomeKey = biome.getKey();
        if (biomeKey.isEmpty()) return original;

        MusicSound musicSound = BiomePlaylist.getMusicSound(biomeKey.get().getValue(), this.player.getRandom());
        Pool<MusicSound> pool = Pool.of(musicSound);
        return musicSound != null ? Optional.of(pool) : original;
    }
}
