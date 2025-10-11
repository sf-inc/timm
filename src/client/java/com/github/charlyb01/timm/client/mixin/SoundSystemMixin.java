package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.imixin.VolumeSettingIMixin;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(SoundSystem.class)
public class SoundSystemMixin implements VolumeSettingIMixin {
    @Shadow private boolean started;
    @Shadow @Final private GameOptions settings;
    @Shadow @Final private Map<SoundInstance, Channel.SourceManager> sources;

    @Override
    public void timm$setVolume(SoundInstance sound, float volume) {
        if (this.started) {
            Channel.SourceManager sourceManager = this.sources.get(sound);
            if (sourceManager != null) {
                sourceManager.run(source -> source.setVolume(volume * this.getAdjustedVolume(sound)));
            }
        }
    }

    @Unique
    private float getAdjustedVolume(SoundInstance sound) {
        return this.getAdjustedVolume(sound.getVolume(), sound.getCategory());
    }

    @Unique
    private float getAdjustedVolume(float volume, SoundCategory category) {
        return MathHelper.clamp(volume * this.method_72233(category), 0.0F, 1.0F);
    }

    @Unique
    private float method_72233(@Nullable SoundCategory soundCategory) {
        return soundCategory != null && soundCategory != SoundCategory.MASTER
                ? this.settings.getSoundVolume(soundCategory) : 1.0F;
    }
}
