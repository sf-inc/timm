package com.github.charlyb01.timm.client.mixin;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.client.command.NowPlayingCmd;
import com.github.charlyb01.timm.client.music.Songs;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.toast.NowPlayingToast;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NowPlayingToast.class)
public class NowPlayingToastMixin {
    @ModifyReturnValue(method = "getMusicText", at = @At("RETURN"))
    private static Text updateMusicTextWithTimm(Text original)
    {
        if (NowPlayingCmd.SONG_ID != null && NowPlayingCmd.SONG_ID.getNamespace().equals(Timm.MOD_ID)) {
            return Songs.getSongText(NowPlayingCmd.SONG_ID).copyContentOnly();
        }
        return original;
    }
}
