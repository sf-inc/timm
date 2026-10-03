package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public @Nullable LocalPlayer player;

    @ModifyExpressionValue(method = "getSituationalMusic", at = @At(value = "FIELD", target = "Lnet/minecraft/sounds/Musics;MENU:Lnet/minecraft/sounds/Music;", opcode = Opcodes.GETSTATIC))
    private Music updateMenuMusic(Music original) {
        // The menu music is only picked when there is no player, so the player's random cannot be used
        Music music = BiomePlaylist.getMenuMusic(BiomePlaylist.MENU_RANDOM);
        if (music != null) return music;
        // The vanilla menu music replaces the current song: as the playlist is rolled every tick,
        // it would cut TIMM's menu songs right after they start
        return new Music(original.sound(), original.minDelay(), original.maxDelay(), false);
    }

    @ModifyExpressionValue(method = "getSituationalMusic", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/BackgroundMusic;select(ZZ)Ljava/util/Optional;"))
    private Optional<Music> updateBiomeMusic(Optional<Music> original) {
        if (this.player == null) return original;

        Level level = this.player.level();
        if (level.dimension() == Level.END) {
            Music music = BiomePlaylist.getEndMusic(this.player.getRandom());
            if (music != null) return Optional.of(music);
            // The vanilla End music replaces the current song: as the playlist is rolled every tick,
            // it would cut TIMM's End songs right after they start
            return original.map(vanilla -> new Music(vanilla.sound(), vanilla.minDelay(), vanilla.maxDelay(), false));
        }

        if (this.player.getAbilities().instabuild && this.player.getAbilities().mayfly) {
            Music music = BiomePlaylist.getCreativeMusic(this.player.getRandom());
            return music != null ? Optional.of(music) : original;
        }

        Holder<Biome> biome = level.getBiome(this.player.blockPosition());
        Optional<ResourceKey<Biome>> biomeKey = biome.unwrapKey();
        if (biomeKey.isEmpty()) return original;

        Music music = BiomePlaylist.getMusicSound(biomeKey.get().identifier(), this.player.getRandom());
        return music != null ? Optional.of(music) : original;
    }
}
