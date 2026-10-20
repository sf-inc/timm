package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import net.minecraft.util.RandomSource;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Unique private static final RandomSource MENU_RANDOM = RandomSource.create();

    @Inject(method = "getSituationalMusic", at = @At("HEAD"))
    private void resetBiomeEvent(CallbackInfoReturnable<Music> cir) {
        // Only the biome branch sets it back: any other music type must not fade out on biome switch
        BiomePlaylist.CURRENT_BIOME_EVENT = BiomePlaylist.UNDEFINED_BIOME;
    }

    // Vanilla only picks the menu music when there is no player, so it can't provide the random source
    @ModifyExpressionValue(method = "getSituationalMusic", at = @At(value = "FIELD", target = "Lnet/minecraft/sounds/Musics;MENU:Lnet/minecraft/sounds/Music;", opcode = Opcodes.GETSTATIC))
    private Music updateMenuMusic(Music original) {
        return BiomePlaylist.getMenuMusic(original, MENU_RANDOM);
    }
}
