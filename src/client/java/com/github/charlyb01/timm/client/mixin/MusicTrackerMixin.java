package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.client.imixin.VolumeSettingIMixin;
import com.github.charlyb01.timm.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public class MusicTrackerMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow private @Nullable SoundInstance current;
    @Shadow private int timeUntilNextSong;

    @Unique private RegistryEntry<Biome> biome;
    @Unique private float volume = 1.0F;

    @Inject(method = "tick", at = @At("HEAD"))
    private void fadeOutMusic(CallbackInfo ci) {
        if (this.client.world == null || this.client.player == null || this.biome == null) return;
        if (this.biome.getKey().isEmpty()) {
            Timm.debugLog("Empty registry key for biome! Likely a bug");
            return;
        }

        float delta = 1.f / (ModConfig.get().general.fadeDuration * 20);
        boolean fadeIn = this.client.world.getBiome(this.client.player.getBlockPos()).matchesKey(this.biome.getKey().get());

        if (fadeIn) {
            if (this.volume < 1.f) {
                this.volume = Math.min(1.f, this.volume + delta);
                ((VolumeSettingIMixin) this.client.getSoundManager()).timm$setVolume(this.current, this.volume);
            }
        } else {
            this.volume = Math.max(0.f, this.volume - delta);
            ((VolumeSettingIMixin) this.client.getSoundManager()).timm$setVolume(this.current, this.volume);

            if (this.volume == 0.f) {
                this.client.getSoundManager().stop(this.current);
                this.volume = 1.f;
                this.timeUntilNextSong = 10;
                this.current = null;
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/MusicTracker;play(Lnet/minecraft/sound/MusicSound;)V"))
    private void saveCurrentBiome(CallbackInfo ci) {
        if (this.client.world == null || this.client.player == null) return;

        this.biome = this.client.world.getBiome(this.client.player.getBlockPos());
    }
}
