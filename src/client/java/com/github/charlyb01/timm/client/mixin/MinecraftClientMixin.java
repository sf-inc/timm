package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.MusicType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public ClientPlayerEntity player;

    @Unique private static final Random MENU_RANDOM = Random.create();

    @Inject(method = "getMusicType", at = @At("HEAD"))
    private void resetBiomeEvent(CallbackInfoReturnable<MusicSound> cir) {
        // Only the biome branch sets it back: any other music type must not fade out on biome switch
        BiomePlaylist.CURRENT_BIOME_EVENT = BiomePlaylist.UNDEFINED_BIOME;
    }

    // Vanilla only picks the menu music when there is no player, so it can't provide the random source
    @ModifyExpressionValue(method = "getMusicType", at = @At(value = "FIELD", target = "Lnet/minecraft/sound/MusicType;MENU:Lnet/minecraft/sound/MusicSound;", opcode = Opcodes.GETSTATIC))
    private MusicSound updateMenuMusic(MusicSound original) {
        return BiomePlaylist.getMenuMusic(original, MENU_RANDOM);
    }

    @ModifyExpressionValue(method = "getMusicType", at = @At(value = "FIELD", target = "Lnet/minecraft/sound/MusicType;END:Lnet/minecraft/sound/MusicSound;", opcode = Opcodes.GETSTATIC))
    private MusicSound updateEndMusic(MusicSound original) {
        if (this.player == null) return original;

        return BiomePlaylist.getEndMusic(original, this.player.getRandom());
    }

    @ModifyExpressionValue(method = "getMusicType", at = @At(value = "FIELD", target = "Lnet/minecraft/sound/MusicType;CREATIVE:Lnet/minecraft/sound/MusicSound;", opcode = Opcodes.GETSTATIC))
    private MusicSound updateCreativeMusic(MusicSound original) {
        if (this.player == null) return original;

        return BiomePlaylist.getCreativeMusic(original, this.player.getRandom());
    }

    @Definition(id = "getMusic", method = "Lnet/minecraft/world/biome/Biome;getMusic()Ljava/util/Optional;")
    @Expression("?.getMusic()")
    @ModifyExpressionValue(method = "getMusicType", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Optional<MusicSound> updateBiomeMusic(Optional<MusicSound> original) {
        if (this.player == null) return original;

        RegistryEntry<Biome> biome = this.player.getWorld().getBiome(this.player.getBlockPos());
        Identifier biomeId = biome.getKey().map(RegistryKey::getValue).orElse(null);

        // Vanilla falls back to the game music when the biome has none
        return Optional.of(BiomePlaylist.getMusicSound(biomeId, original.orElse(MusicType.GAME), this.player.getRandom()));
    }
}
