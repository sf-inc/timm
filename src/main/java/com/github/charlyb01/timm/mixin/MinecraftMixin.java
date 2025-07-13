package com.github.charlyb01.timm.mixin;

import com.github.charlyb01.timm.music.BiomePlaylist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    public LocalPlayer player;

    @Inject(method = "getSituationalMusic", at = @At(value = "FIELD", target = "Lnet/minecraft/sounds/Musics;MENU:Lnet/minecraft/sounds/Music;"), cancellable = true)
    private void updateMenuMusic(CallbackInfoReturnable<Music> cir) {
        Music music = BiomePlaylist.getMenuMusic();
        if (music != null) {
            cir.setReturnValue(music);
        }
    }

    @Inject(method = "getSituationalMusic", at = @At(value = "FIELD", target = "Lnet/minecraft/sounds/Musics;END:Lnet/minecraft/sounds/Music;"), cancellable = true)
    private void updateEndMusic(CallbackInfoReturnable<Music> cir) {
        if (this.player == null) return;

        Holder<Biome> biome = this.player.level().getBiome(this.player.blockPosition());
        Optional<ResourceKey<Biome>> biomeKey = biome.unwrapKey();
        if (biomeKey.isEmpty()) return;

        Music musicSound = BiomePlaylist.getMusicSound(biomeKey.get().location(), this.player.getRandom());
        if (musicSound != null) {
            cir.setReturnValue(musicSound);
        }
    }

    @Inject(method = "getSituationalMusic", at = @At(value = "FIELD", target = "Lnet/minecraft/sounds/Musics;CREATIVE:Lnet/minecraft/sounds/Music;"), cancellable = true)
    private void updateCreativeMusic(CallbackInfoReturnable<Music> cir) {
        if (this.player == null) return;

        Music musicSound = BiomePlaylist.getCreativeMusic(this.player.getRandom());
        if (musicSound != null) {
            cir.setReturnValue(musicSound);
        }
    }

    @Inject(method = "getSituationalMusic", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getBackgroundMusic()Ljava/util/Optional;", shift = At.Shift.AFTER), cancellable = true)
    private void updateBiomeMusic(CallbackInfoReturnable<Music> cir) {
        if (this.player == null) return;

        Holder<Biome> biome = this.player.level().getBiome(this.player.blockPosition());
        Optional<ResourceKey<Biome>> biomeKey = biome.unwrapKey();
        if (biomeKey.isEmpty()) return;

        Music musicSound = BiomePlaylist.getMusicSound(biomeKey.get().location(), this.player.getRandom());
        if (musicSound != null) {
            cir.setReturnValue(musicSound);
        }
    }
}