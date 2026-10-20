package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.client.music.BiomePlaylist;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.level.Level;

// Only the default and creative musics are replaced: underwater music stays vanilla
@Mixin(BackgroundMusic.class)
public class BackgroundMusicMixin {
    // Second read only: the first one is the presence check, so creative music is used only where vanilla has one
    @ModifyExpressionValue(method = "select", at = @At(value = "FIELD", target = "Lnet/minecraft/world/attribute/BackgroundMusic;creativeMusic:Ljava/util/Optional;", opcode = Opcodes.GETFIELD, ordinal = 1))
    private Optional<Music> updateCreativeMusic(Optional<Music> original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return original;

        return Optional.ofNullable(BiomePlaylist.getCreativeMusic(original.orElse(null), player.getRandom()));
    }

    // Empty when vanilla plays nothing (e.g. pale garden)
    @ModifyExpressionValue(method = "select", at = @At(value = "FIELD", target = "Lnet/minecraft/world/attribute/BackgroundMusic;defaultMusic:Ljava/util/Optional;", opcode = Opcodes.GETFIELD))
    private Optional<Music> updateDefaultMusic(Optional<Music> original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return original;

        Level world = player.level();
        if (world.dimension() == Level.END) {
            return Optional.ofNullable(BiomePlaylist.getEndMusic(original.orElse(null), player.getRandom()));
        }

        Identifier biomeId = world.getBiome(player.blockPosition()).unwrapKey().map(ResourceKey::identifier).orElse(null);
        return Optional.ofNullable(BiomePlaylist.getMusicSound(biomeId, original.orElse(null), player.getRandom()));
    }
}
