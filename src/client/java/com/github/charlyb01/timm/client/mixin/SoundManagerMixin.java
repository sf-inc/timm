package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.command.NowPlayingCmd;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("TAIL"))
    private void saveMusicIdentifier(SoundInstance sound, CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
        if (!sound.getCategory().equals(SoundCategory.MUSIC) || sound.getSound() == null) return;
        NowPlayingCmd.SONG_ID = sound.getSound().getIdentifier();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void resetMusicIdentifierOnStop(CallbackInfo ci) {
        NowPlayingCmd.SONG_ID = null;
    }
}
