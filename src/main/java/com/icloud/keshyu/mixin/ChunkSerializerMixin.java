package com.icloud.keshyu.mixin;

import com.icloud.keshyu.MansorChunk;
import com.icloud.keshyu.MansorWrapperProtoChunk;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    private final static String LAST_UNLOAD_TIME_KEY = "Mansor$LastUnloadTime";

    @Inject(
            method = "serialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/nbt/NbtCompound;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void serialize(ServerWorld world, Chunk chunk, CallbackInfoReturnable<NbtCompound> ci) {
        var nbtCompound = ci.getReturnValue();
        final var chunkM = (MansorChunk) chunk;

        nbtCompound.putLong(LAST_UNLOAD_TIME_KEY, chunkM.mansor$getLastUnloadTime());

        ci.setReturnValue(nbtCompound);
    }
    
    @Inject(
            method = "deserialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/poi/PointOfInterestStorage;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/world/chunk/ProtoChunk;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void deserialize(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> ci) {
        var ret = ci.getReturnValue();
        final var lastUnloadTime = nbt.getLong(LAST_UNLOAD_TIME_KEY);

        if (ret instanceof WrapperProtoChunk) {
            var wrapperChunk = (MansorWrapperProtoChunk) ci.getReturnValue();
            var mansorChunk = (MansorChunk) wrapperChunk.getWrapped();

            mansorChunk.mansor$setLastUnloadTime(lastUnloadTime);
            ci.setReturnValue(new WrapperProtoChunk((WorldChunk) mansorChunk, wrapperChunk.getpPropagateToWrapped()));
        } else {
            var mansorChunk = (MansorChunk) ci.getReturnValue();

            mansorChunk.mansor$setLastUnloadTime(lastUnloadTime);
            ci.setReturnValue((ProtoChunk) mansorChunk);
        }
    }
}
