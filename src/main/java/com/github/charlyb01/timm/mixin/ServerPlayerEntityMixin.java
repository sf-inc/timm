package com.github.charlyb01.timm.mixin;

import com.github.charlyb01.timm.Timm;
import com.github.charlyb01.timm.music.StructurePlaylist;
import com.github.charlyb01.timm.network.PlayPayload;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;
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

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public abstract ServerWorld getServerWorld();

    @Unique private Identifier currentSoundId;
    @Unique private final int tickCheck;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
        this.tickCheck = this.uuid.hashCode() % 20;
    }

    @Inject(method = "playerTick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (this.isCreative()) return;
        if (this.age % 20 != this.tickCheck) return; // Check once per second, tick depends on player to avoid overload

        StructureAccessor structureAccessor = this.getServerWorld().getStructureAccessor();
        BlockPos playerPos = this.getBlockPos();
        HashMap<ChunkSectionPos, Set<Structure>> structuresByPos = getStructuresAroundPlayer(playerPos, structureAccessor);
        for (Map.Entry<ChunkSectionPos, Set<Structure>> entry : structuresByPos.entrySet()){
            for (Structure struct : entry.getValue()) {
                var tagKey = struct.getValidBiomes().getTagKey();
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
                ServerPlayNetworking.send((ServerPlayerEntity)(Object) this, new PlayPayload(soundId));
                break;
            }
        }
    }

    @Unique
    private static @NotNull HashMap<ChunkSectionPos, Set<Structure>> getStructuresAroundPlayer(
            BlockPos playerPos, StructureAccessor structureAccessor) {
        HashMap<ChunkSectionPos, Set<Structure>> structures = new HashMap<>();
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                BlockPos pos = playerPos.add(16 * i, 0, 16 * j);
                structures.put(ChunkSectionPos.from(pos), structureAccessor.getStructureReferences(pos).keySet());
            }
        }
        return structures;
    }

    @Unique
    private static boolean structureContains(ChunkSectionPos chunkPos, BlockPos playerPos, Structure structure,
                                             int expansion, StructureAccessor structureAccessor) {
        for (StructureStart structureStart : structureAccessor.getStructureStarts(chunkPos, structure)) {
            for (StructurePiece structurePiece : structureStart.getChildren()) {
                if (structurePiece.getBoundingBox().expand(expansion).contains(playerPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Unique
    private static String getStructureName(TagKey<Biome> biomeTagKey) {
        var id = biomeTagKey.id().getPath().split("/");
        return id[id.length - 1];
    }
}
