package com.github.charlyb01.timm.client.mixin;

import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MusicTracker.class)
public interface MusicTrackerAccessor {
    @Accessor("current")
    void setCurrent(SoundInstance soundInstance);
}
