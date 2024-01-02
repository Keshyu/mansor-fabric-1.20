package com.icloud.keshyu.mixin;

import com.icloud.keshyu.MansorChunk;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements MansorChunk {
    @Unique
    private long mansor$lastUnloadTime = 0;
    @Unique
    private boolean mansor$isFirstTick = true;

    @Override
    public void mansor$setLastUnloadTime(long lastUnloadTime) {
        mansor$lastUnloadTime = lastUnloadTime;
    }

    @Override
    public long mansor$getLastUnloadTime() {
        return mansor$lastUnloadTime;
    }

    @Override
    public void mansor$setIsFirstTick(boolean isFirstTick) {
        mansor$isFirstTick = isFirstTick;
    }

    @Override
    public boolean mansor$getIsFirstTick() {
        return mansor$isFirstTick;
    }
}
