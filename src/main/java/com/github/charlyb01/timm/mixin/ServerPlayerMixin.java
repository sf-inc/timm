package com.github.charlyb01.timm.mixin;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.music.StructurePlaylist;
import com.github.charlyb01.timm.network.PlayPayload;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Shadow public abstract ServerLevel level();

    @Unique private Identifier currentSoundId;

    public ServerPlayerMixin(Level level, GameProfile profile) {
        super(level, profile);
    }

    @Inject(method = "doTick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.isCreative()) return;
        // Check once per second, on a tick that depends on the player to avoid overload
        if ((this.tickCount + this.getId()) % 20 != 0) return;
        // Players without the mod cannot play the structure music
        if (!ServerPlayNetworking.canSend((ServerPlayer)(Object) this, PlayPayload.ID)) return;

        StructureManager structureManager = this.level().structureManager();
        BlockPos playerPos = this.blockPosition();
        Identifier foundSoundId = null;
        for (Map.Entry<Structure, LongSet> entry : getStructuresAroundPlayer(playerPos, structureManager).entrySet()) {
            Structure struct = entry.getKey();
            var tagKey = struct.biomes().unwrapKey();
            if (tagKey.isEmpty()) continue;

            String structureName = getStructureName(tagKey.get());
            Integer distance  = StructurePlaylist.DISTANCE_FROM_STRUCTURE.get(structureName);
            if (distance == null) {
                Timm.debugLog("Structure distance was not registered for: " + structureName);
                continue;
            }

            Identifier soundId = StructurePlaylist.EVENT_ID_FROM_STRUCTURE.get(structureName);
            if (soundId == null) {
                Timm.debugLog("Structure ids were not registered for: " + structureName);
                continue;
            }
            if (!structureContains(struct, entry.getValue(), playerPos, distance, structureManager)) continue;

            // Keep the structure that was sent last while the player is still near it, otherwise two
            // structures in range would be sent alternately and keep restarting each other's music
            if (soundId.equals(this.currentSoundId)) return;
            if (foundSoundId == null) foundSoundId = soundId;
        }

        // Forget the structure once the player left it, so its music plays again when coming back
        this.currentSoundId = foundSoundId;
        if (foundSoundId == null) return;
        ServerPlayNetworking.send((ServerPlayer)(Object) this, new PlayPayload(foundSoundId));
    }

    @Unique
    private static Map<Structure, LongSet> getStructuresAroundPlayer(BlockPos playerPos, StructureManager structureManager) {
        // Merge the references of the chunks around the player, so that a structure spanning several chunks is
        // only checked once
        Map<Structure, LongSet> structures = new HashMap<>();
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                BlockPos pos = playerPos.offset(16 * i, 0, 16 * j);
                structureManager.getAllStructuresAt(pos).forEach((structure, references) ->
                        structures.computeIfAbsent(structure, s -> new LongOpenHashSet()).addAll(references));
            }
        }
        return structures;
    }

    @Unique
    private static boolean structureContains(Structure structure, LongSet references, BlockPos playerPos,
                                             int expansion, StructureManager structureManager) {
        List<StructureStart> structureStarts = new ArrayList<>();
        structureManager.fillStartsForStructure(structure, references, structureStarts::add);
        for (StructureStart structureStart : structureStarts) {
            for (StructurePiece structurePiece : structureStart.getPieces()) {
                if (structurePiece.getBoundingBox().inflatedBy(expansion).isInside(playerPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Unique
    private static String getStructureName(TagKey<Biome> biomeTagKey) {
        var id = biomeTagKey.location().getPath().split("/");
        return id[id.length - 1];
    }
}
