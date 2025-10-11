package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.command.NowPlayingCmd;
import com.github.charlyb01.timm.client.imixin.VolumeSettingIMixin;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin implements VolumeSettingIMixin {
    @Shadow @Final private SoundSystem soundSystem;

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("TAIL"))
    private void saveMusicIdentifier(SoundInstance sound, CallbackInfo ci) {
        if (!sound.getCategory().equals(SoundCategory.MUSIC) || sound.getSound() == null) return;
        NowPlayingCmd.SONG_ID = sound.getSound().getIdentifier();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void resetMusicIdentifierOnStop(CallbackInfo ci) {
        NowPlayingCmd.SONG_ID = null;
    }

    @Override
    public void timm$setVolume(SoundInstance sound, float volume) {
        ((VolumeSettingIMixin) this.soundSystem).timm$setVolume(sound, volume);
    }
}
