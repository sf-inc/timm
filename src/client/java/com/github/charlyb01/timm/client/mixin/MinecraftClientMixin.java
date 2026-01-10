package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public ClientPlayerEntity player;

    @ModifyExpressionValue(method = "getMusicInstance", at = @At(value = "FIELD", target = "Lnet/minecraft/sound/MusicType;MENU:Lnet/minecraft/sound/MusicSound;", opcode = Opcodes.GETSTATIC))
    private MusicSound updateMenuMusic(MusicSound original) {
        if (this.player == null) return original;

        MusicSound musicSound = BiomePlaylist.getMenuMusic(this.player.getRandom());
        return musicSound != null ? musicSound : original;
    }

    @ModifyExpressionValue(method = "getMusicInstance", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/BackgroundMusic;getCurrent(ZZ)Ljava/util/Optional;"))
    private Optional<MusicSound> updateBiomeMusic(Optional<MusicSound> original) {
        if (this.player == null) return original;

        World world = this.player.getEntityWorld();
        if (world.getRegistryKey() == World.END) {
            MusicSound musicSound = BiomePlaylist.getEndMusic(this.player.getRandom());
            return musicSound != null ? Optional.of(musicSound) : original;
        }

        if (this.player.getAbilities().creativeMode && this.player.getAbilities().allowFlying) {
            MusicSound musicSound = BiomePlaylist.getCreativeMusic(this.player.getRandom());
            return musicSound != null ? Optional.of(musicSound) : original;
        }

        RegistryEntry<Biome> biome = world.getBiome(this.player.getBlockPos());
        Optional<RegistryKey<Biome>> biomeKey = biome.getKey();
        if (biomeKey.isEmpty()) return original;

        MusicSound musicSound = BiomePlaylist.getMusicSound(biomeKey.get().getValue(), this.player.getRandom());
        return musicSound != null ? Optional.of(musicSound) : original;
    }
}
