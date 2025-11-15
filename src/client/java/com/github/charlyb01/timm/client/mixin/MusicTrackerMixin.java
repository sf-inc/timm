package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.client.imixin.MusicTrackerIMixin;
import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.github.charlyb01.timm.config.ModConfig;
import com.github.charlyb01.timm.config.StructureFadeOut;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicInstance;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public abstract class MusicTrackerMixin implements MusicTrackerIMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private Random random;
    @Shadow private @Nullable SoundInstance current;
    @Shadow private int timeUntilNextSong;

    @Shadow public abstract void play(MusicInstance music);

    @Unique private Identifier lastBiomeEvent;
    @Unique private Identifier structureEvent;
    @Unique private Identifier structureEventPlaying;
    @Unique private float volume = 1.0F;
    @Unique private int switchDelay = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.client.world == null || this.client.player == null) return;

        if (this.current == null) {
            if (this.structureEvent != null) this.playStructureMusic();
            return;
        }

        // current is not null: fading management
        float delta = 1.f / (ModConfig.get().general.fadeDuration * 20);

        if (this.shouldFadeOut()) {
            this.volume = Math.max(0.f, this.volume - delta);
            this.client.getSoundManager().setVolume(this.current, this.volume);

            if (this.volume > 0.f) return;
            this.client.getSoundManager().stop(this.current);
            this.volume = 1.f;
            this.timeUntilNextSong = ModConfig.get().general.resetDelayOnBiomeSwitch
                ? this.random.nextBetween(ModConfig.get().general.minDelay, ModConfig.get().general.maxDelay)
                : 10;
            this.current = null;

            if (this.structureEvent == null) return;
            this.playStructureMusic();
        } else if (this.volume < 1.f) {
            this.volume = Math.min(1.f, this.volume + delta);
            this.client.getSoundManager().setVolume(this.current, this.volume);
        }
    }

    @Inject(method = "play", at = @At("HEAD"))
    private void saveCurrentBiome(CallbackInfo ci) {
        this.lastBiomeEvent = BiomePlaylist.CURRENT_BIOME_EVENT;
    }

    @Inject(method = "play", at = @At("HEAD"))
    private void resetStructure(MusicInstance music, CallbackInfo ci) {
        this.structureEventPlaying = null;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/MusicTracker;canFadeTowardsVolume(F)Z"))
    private boolean useOnlyOneFadeMethod(MusicTracker instance, float volume, Operation<Boolean> original) {
        return false;
    }

    @Unique
    private boolean biomeSwitch() {
        if (BiomePlaylist.UNDEFINED_BIOME.equals(this.lastBiomeEvent)) {
            // This happens if we're opening a world in creative
            return false;
        }

        var currentBiome = this.client.world.getBiome(this.client.player.getBlockPos()).getKey();
        if (currentBiome.isEmpty()) {
            Timm.debugLog("Biome was not registered: likely a bug!");
            return true;
        }

        var eventsForCurrentBiome = BiomePlaylist.EVENTS_BY_BIOME.get(currentBiome.get().getValue());
        if (eventsForCurrentBiome == null) {
            Timm.debugLog("Current biome was not registered in playlist: fade out to default");
            return true;
        }

        return !eventsForCurrentBiome.contains(this.lastBiomeEvent);
    }

    @Unique
    private boolean shouldFadeOut() {
        if (this.structureEvent != null && !this.structureEvent.equals(this.structureEventPlaying)) return true;
        if (this.structureEventPlaying != null && ModConfig.get().general.structureFadeOut.equals(StructureFadeOut.NEVER))
            return false;

        if (this.biomeSwitch()) {
            return ++this.switchDelay >= ModConfig.get().general.fadeDelay * 20;
        } else {
            this.switchDelay = 0;
            return false;
        }
    }

    @Unique
    private void playStructureMusic() {
        SoundEvent soundEvent = SoundEvent.of(this.structureEvent);
        MusicSound musicSound = new MusicSound(Registries.SOUND_EVENT.getEntry(soundEvent),
                ModConfig.get().general.minDelay,
                ModConfig.get().general.maxDelay,
                false);
        this.play(new MusicInstance(musicSound));
        this.structureEventPlaying = this.structureEvent;
        this.structureEvent = null;
    }

    @Override
    public void timm$setStructureEventId(Identifier soundId) {
        this.structureEvent = soundId;
    }
}
