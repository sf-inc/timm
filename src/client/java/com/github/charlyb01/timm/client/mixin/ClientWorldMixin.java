package com.github.charlyb01.timm.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZJ)V", at = @At("TAIL"))
    private void setCurrentSound(double x, double y, double z, SoundEvent event, SoundCategory category, float volume,
                                 float pitch, boolean useDistance, long seed, CallbackInfo ci,
                                 @Local PositionedSoundInstance soundInstance) {
        if (category.equals(SoundCategory.MUSIC) && !useDistance) {
            ((MusicTrackerAccessor) this.client.getMusicTracker()).setCurrent(soundInstance);
        }
    }
}
