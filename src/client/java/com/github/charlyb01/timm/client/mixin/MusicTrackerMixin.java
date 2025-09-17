package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.command.NowPlayingCmd;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public class MusicTrackerMixin {
    @Shadow private @Nullable SoundInstance current;

    @Definition(id = "current", field = "Lnet/minecraft/client/sound/MusicTracker;current:Lnet/minecraft/client/sound/SoundInstance;")
    @Expression("this.current = null")
    @Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void resetMusicIdentifierOnNull(CallbackInfo ci) {
        if (this.current == null || this.current.getSound() == null) return;

        if (this.current.getSound().getIdentifier().equals(NowPlayingCmd.SONG_ID)) {
            NowPlayingCmd.SONG_ID = null;
        }
    }
}
