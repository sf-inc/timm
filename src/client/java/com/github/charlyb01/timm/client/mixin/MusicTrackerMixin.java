package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.client.imixin.MusicTrackerIMixin;
import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.github.charlyb01.timm.config.ModConfig;
import com.github.charlyb01.timm.config.StructureFadeOut;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public abstract class MusicTrackerMixin implements MusicTrackerIMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private RandomSource random;
    @Shadow private @Nullable SoundInstance currentMusic;
    @Shadow private int nextSongDelay;

    @Shadow public abstract void startPlaying(Music music);

    @Unique private Identifier lastBiomeEvent;
    @Unique private Identifier structureEvent;
    @Unique private Identifier structureEventPlaying;
    @Unique private float volume = 1.0F;
    @Unique private int switchDelay = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.minecraft.level == null || this.minecraft.player == null) return;

        if (this.currentMusic == null) {
            if (this.structureEvent != null) this.playStructureMusic();
            return;
        }

        // current is not null: fading management
        float delta = 1.f / (Math.max(1, ModConfig.get().general.fadeDuration) * 20);

        if (this.shouldFadeOut()) {
            this.volume = Math.max(0.f, this.volume - delta);
            this.minecraft.getSoundManager().updateCategoryVolume(SoundSource.MUSIC, this.volume);

            if (this.volume > 0.f) return;
            this.minecraft.getSoundManager().stop(this.currentMusic);
            this.volume = 1.f;
            this.minecraft.getSoundManager().updateCategoryVolume(SoundSource.MUSIC, this.volume);
            this.nextSongDelay = ModConfig.get().general.resetDelayOnBiomeSwitch
                ? this.random.nextIntBetweenInclusive(ModConfig.get().general.minDelay * 20, ModConfig.get().general.maxDelay * 20)
                : 10;
            this.currentMusic = null;

            if (this.structureEvent == null) return;
            this.playStructureMusic();
        } else if (this.volume < 1.f) {
            this.volume = Math.min(1.f, this.volume + delta);
            this.minecraft.getSoundManager().updateCategoryVolume(SoundSource.MUSIC, this.volume);
        }
    }

    @Inject(method = "startPlaying", at = @At("HEAD"))
    private void saveCurrentBiome(CallbackInfo ci) {
        this.lastBiomeEvent = BiomePlaylist.CURRENT_BIOME_EVENT;
    }

    @Inject(method = "startPlaying", at = @At("HEAD"))
    private void resetStructure(CallbackInfo ci) {
        this.structureEventPlaying = null;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/MusicManager;fadePlaying(F)Z"))
    private boolean useOnlyOneFadeMethod(MusicManager instance, float volume, Operation<Boolean> original) {
        return false;
    }

    @Unique
    private boolean biomeSwitch() {
        if (BiomePlaylist.UNDEFINED_BIOME.equals(this.lastBiomeEvent)) {
            // Not a biome song (creative, end, menu, underwater...): never fade out on biome switch
            return false;
        }

        var currentBiome = this.minecraft.level.getBiome(this.minecraft.player.blockPosition()).unwrapKey();
        if (currentBiome.isEmpty()) {
            Timm.debugLog("Biome was not registered: likely a bug!");
            return true;
        }

        var eventsForCurrentBiome = BiomePlaylist.EVENTS_BY_BIOME.get(currentBiome.get().identifier());
        if (eventsForCurrentBiome == null) {
            Timm.debugLog("Current biome was not registered in playlist: fade out to default");
            return true;
        }

        return !eventsForCurrentBiome.contains(this.lastBiomeEvent);
    }

    @Unique
    private boolean shouldFadeOut() {
        if (this.minecraft.getMusicVolume() <= 0.f) return true;

        if(!ModConfig.get().general.enableMusicFading)
            return false;

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
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(this.structureEvent);
        Music musicSound = new Music(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent),
                ModConfig.get().general.minDelay * 20,
                ModConfig.get().general.maxDelay * 20,
                false);
        this.startPlaying(musicSound);
        this.structureEventPlaying = this.structureEvent;
        this.structureEvent = null;
    }

    @Override
    public void timm$setStructureEventId(Identifier soundId) {
        // Already playing this structure song: drop any pending switch, otherwise it restarts as soon as it ends
        if (this.currentMusic != null && soundId.equals(this.structureEventPlaying)) {
            this.structureEvent = null;
            return;
        }
        this.structureEvent = soundId;
    }

    @Override
    public boolean timm$isPlayingTimmSong() {
        return this.currentMusic != null && Timm.MOD_ID.equals(this.currentMusic.getIdentifier().getNamespace());
    }
}
