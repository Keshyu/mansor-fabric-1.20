package com.icloud.keshyu.mixin;

import com.icloud.keshyu.MansorChunk;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin extends VersionedChunkStorage {
    @Shadow
    @Final
    ServerWorld world;
//    @Shadow
//    @Final
//    private MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> mainExecutor;

    public ThreadedAnvilChunkStorageMixin(Path directory, DataFixer dataFixer, boolean dsync) {
        super(directory, dataFixer, dsync);
    }

//    @Inject(
//            method = "convertToFullChunk(Lnet/minecraft/server/world/ChunkHolder;)Ljava/util/concurrent/CompletableFuture;",
//            at = @At("RETURN"),
//            cancellable = true
//    )
//    private void convertToFullChunk(ChunkHolder chunkHolder, CallbackInfoReturnable<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> ci) {
//        var completableFuture = ci.getReturnValue();
//        completableFuture.thenApplyAsync(either -> either.mapLeft(worldChunk -> {
//            var mansorChunk = (MansorChunk) worldChunk;
//
//            return worldChunk;
//        }), task -> mainExecutor.send(ChunkTaskPrioritySystem.createMessage(task, chunkHolder.getPos().toLong(), chunkHolder::getLevel)));
//        ci.setReturnValue(completableFuture);
//    }

    @Inject(method = "save(Lnet/minecraft/world/chunk/Chunk;)Z", at = @At("HEAD"))
    private void save(Chunk chunk, CallbackInfoReturnable<Boolean> ci) {
        var mansorChunk = (MansorChunk) chunk;
        mansorChunk.mansor$setLastUnloadTime(world.getTime());
    }
}
