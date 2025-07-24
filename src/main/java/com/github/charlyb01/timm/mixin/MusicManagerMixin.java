package com.github.charlyb01.timm.mixin;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.command.NowPlayingCmd;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(MusicManager.class)
public class MusicManagerMixin {
    @Shadow @Nullable private SoundInstance currentMusic;

    @Inject(method = "startPlaying", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", shift = At.Shift.AFTER))
    private void saveMusicIdentifier(Music music, CallbackInfo ci) {
        Timm.LOGGER.info("[MusicMixin] Called startPlaying");

        if (this.currentMusic == null || this.currentMusic.getSound() == null) {
            Timm.LOGGER.info("[TIMMMOD]: currentMusic is NULL");
            return;
        }

        Timm.LOGGER.info("[MusicMixin] currentMusic: {}, sound: {}", currentMusic, currentMusic.getSound());
        NowPlayingCmd.SONG_ID = this.currentMusic.getSound().getLocation();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void resetMusicIdentifierOnNull(CallbackInfo ci) {
        if (this.currentMusic == null) {
            NowPlayingCmd.SONG_ID = null;
        }
    }

    @Inject(method = "stopPlaying()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;stop(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"))
    private void resetMusicIdentifierOnStop(CallbackInfo ci) {
        NowPlayingCmd.SONG_ID = null;
    }
}