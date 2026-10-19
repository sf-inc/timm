package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
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

    // End, creative and biome music all come from the background music environment attribute (empty: silence)
    @ModifyExpressionValue(method = "getMusicInstance", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/BackgroundMusic;getCurrent(ZZ)Ljava/util/Optional;"))
    private Optional<MusicSound> updateBiomeMusic(Optional<MusicSound> original) {
        if (this.player == null) return original;

        World world = this.player.getEntityWorld();
        MusicSound originalMusic = original.orElse(null);
        if (world.getRegistryKey() == World.END) {
            return Optional.ofNullable(BiomePlaylist.getEndMusic(originalMusic, this.player.getRandom()));
        }

        if (this.player.getAbilities().creativeMode && this.player.getAbilities().allowFlying) {
            return Optional.ofNullable(BiomePlaylist.getCreativeMusic(originalMusic, this.player.getRandom()));
        }

        RegistryEntry<Biome> biome = world.getBiome(this.player.getBlockPos());
        Identifier biomeId = biome.getKey().map(RegistryKey::getValue).orElse(null);
        return Optional.ofNullable(BiomePlaylist.getMusicSound(biomeId, originalMusic, this.player.getRandom()));
    }
}
