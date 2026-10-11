package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.command.NowPlayingCmd;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("TAIL"))
    private void saveMusicIdentifier(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        if (!sound.getSource().equals(SoundSource.MUSIC) || sound.getSound() == null) return;
        NowPlayingCmd.SONG_ID = sound.getSound().getLocation();
    }

    @Inject(method = "stop(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At("HEAD"))
    private void resetMusicIdentifierOnStop(CallbackInfo ci) {
        NowPlayingCmd.SONG_ID = null;
    }
}
