package com.github.charlyb01.timm.mixin;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.music.StructurePlaylist;
import com.github.charlyb01.timm.network.PlayPayload;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
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
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {
    @Shadow public abstract ServerLevel level();

    @Unique private Identifier currentSoundId;
    // Must be a field initializer: Mixin merges those into the target constructor, but not a mixin constructor body.
    @Unique private final int tickCheck = Math.floorMod(this.uuid.hashCode(), 20);

    public ServerPlayerEntityMixin(Level world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "doTick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.isCreative()) return;
        if (this.tickCount % 20 != this.tickCheck) return; // Check once per second, tick depends on player to avoid overload

        StructureManager structureAccessor = this.level().structureManager();
        BlockPos playerPos = this.blockPosition();
        HashMap<SectionPos, Set<Structure>> structuresByPos = getStructuresAroundPlayer(playerPos, structureAccessor);
        for (Map.Entry<SectionPos, Set<Structure>> entry : structuresByPos.entrySet()){
            for (Structure struct : entry.getValue()) {
                var tagKey = struct.biomes().unwrapKey();
                if (tagKey.isEmpty()) continue;

                String structureName = getStructureName(tagKey.get());
                Integer distance  = StructurePlaylist.DISTANCE_FROM_STRUCTURE.get(structureName);
                if (distance == null) {
                    Timm.debugLog("Structure distance was not registered for: " + structureName);
                    continue;
                }
                if (!structureContains(entry.getKey(), playerPos, struct, distance, structureAccessor)) continue;

                Identifier soundId = StructurePlaylist.EVENT_ID_FROM_STRUCTURE.get(structureName);
                if (soundId == null) {
                    Timm.debugLog("Structure ids were not registered for: " + structureName);
                    continue;
                }
                if (soundId.equals(this.currentSoundId)) break;

                this.currentSoundId = soundId;
                ServerPlayNetworking.send((ServerPlayer)(Object) this, new PlayPayload(soundId));
                break;
            }
        }
    }

    @Unique
    private static @NotNull HashMap<SectionPos, Set<Structure>> getStructuresAroundPlayer(
            BlockPos playerPos, StructureManager structureAccessor) {
        HashMap<SectionPos, Set<Structure>> structures = new HashMap<>();
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                BlockPos pos = playerPos.offset(16 * i, 0, 16 * j);
                structures.put(SectionPos.of(pos), structureAccessor.getAllStructuresAt(pos).keySet());
            }
        }
        return structures;
    }

    @Unique
    private static boolean structureContains(SectionPos chunkPos, BlockPos playerPos, Structure structure,
                                             int expansion, StructureManager structureAccessor) {
        for (StructureStart structureStart : structureAccessor.startsForStructure(chunkPos, structure)) {
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
