package com.icloud.keshyu.mixin;

import com.icloud.keshyu.MansorChunk;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties,
                               RegistryKey<World> registryRef,
                               DynamicRegistryManager registryManager,
                               RegistryEntry<DimensionType> dimensionEntry,
                               Supplier<Profiler> profiler,
                               boolean isClient,
                               boolean debugWorld,
                               long biomeAccess,
                               int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk", at = @At("HEAD"))
    public void tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        final var mansorChunk = (MansorChunk) chunk;

        if (mansorChunk.mansor$getLastUnloadTime() != 0 && randomTickSpeed > 0) {
            final long unloadDuration = getTime() - mansorChunk.mansor$getLastUnloadTime();
            final double ticksPerBlock = unloadDuration * (long) randomTickSpeed / (16d * 16d * 16d);
            final var sections = chunk.getSectionArray();

            for (var s = 0; s < sections.length; s++) {
                final var chunkSection = sections[s];
                if (!chunkSection.hasRandomTicks()) continue;
                final var sectionBottom = ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(s));

                for (var i = 0; i < 16; i++) for (var j = 0; j < 16; j++) for (var k = 0; k < 16; k++) {
                    final var blockState = chunkSection.getBlockState(i, j, k);
                    if (!blockState.hasRandomTicks()) { continue; }
                    final var block = blockState.getBlock();

                    if (block instanceof CropBlock crop) {
                        final int age = crop.getAge(blockState);
                        final var blockPos = new BlockPos(
                                chunk.getPos().getOffsetX(i),
                                sectionBottom + j,
                                chunk.getPos().getOffsetZ(k));
                        // FIXME: Shouldn't grow in insufficient light
                        final double growthPerTick = (double) CropBlock.getAvailableMoisture(block, this, blockPos) / 25d;
                        final var growth = (int) Math.floor(ticksPerBlock * growthPerTick);
//                        MansorMod.LOGGER.info("Growth[{}]: {}", blockPos, growth);
//                        MansorMod.LOGGER.info("Growth[{}]: {} * {}", blockPos, ticksPerBlock, growthPerTick);
                        setBlockState(
                                blockPos,
                                crop.withAge(Math.min(age + growth, crop.getMaxAge())),
                                Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }
    }
}
