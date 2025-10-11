package com.github.charlyb01.timm.client.imixin;

import net.minecraft.client.sound.SoundInstance;

public interface VolumeSettingIMixin {
    void timm$setVolume(SoundInstance sound, float volume);
}
