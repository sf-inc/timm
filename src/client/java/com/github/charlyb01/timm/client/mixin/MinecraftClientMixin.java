package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.random.Random;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Unique private static final Random MENU_RANDOM = Random.create();

    @Inject(method = "getMusicInstance", at = @At("HEAD"))
    private void resetBiomeEvent(CallbackInfoReturnable<MusicSound> cir) {
        // Only the biome branch sets it back: any other music type must not fade out on biome switch
        BiomePlaylist.CURRENT_BIOME_EVENT = BiomePlaylist.UNDEFINED_BIOME;
    }

    // Vanilla only picks the menu music when there is no player, so it can't provide the random source
    @ModifyExpressionValue(method = "getMusicInstance", at = @At(value = "FIELD", target = "Lnet/minecraft/sound/MusicType;MENU:Lnet/minecraft/sound/MusicSound;", opcode = Opcodes.GETSTATIC))
    private MusicSound updateMenuMusic(MusicSound original) {
        return BiomePlaylist.getMenuMusic(original, MENU_RANDOM);
    }
}
