package com.github.charlyb01.timm.client.imixin;

import net.minecraft.resources.Identifier;

public interface MusicTrackerIMixin {
    void timm$setStructureEventId(Identifier soundId);

    boolean timm$isPlayingTimmSong();
}
