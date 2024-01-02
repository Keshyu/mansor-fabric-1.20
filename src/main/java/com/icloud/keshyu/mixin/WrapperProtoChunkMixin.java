package com.icloud.keshyu.mixin;

import com.icloud.keshyu.MansorWrapperProtoChunk;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.minecraft.world.gen.chunk.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WrapperProtoChunk.class)
public abstract class WrapperProtoChunkMixin extends ProtoChunk implements MansorWrapperProtoChunk {
    @Shadow
    private WorldChunk wrapped;
    @Shadow
    private boolean propagateToWrapped;

    public WrapperProtoChunkMixin(ChunkPos pos, UpgradeData upgradeData, HeightLimitView world, Registry<Biome> biomeRegistry, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, world, biomeRegistry, blendingData);
    }

    @Override
    public WorldChunk getWrapped() {
        return wrapped;
    }

    @Override
    public boolean getpPropagateToWrapped() {
        return propagateToWrapped;
    }
}
