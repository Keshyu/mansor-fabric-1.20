package com.icloud.keshyu.mixin;

import com.icloud.keshyu.MansorChunk;
import com.icloud.keshyu.MansorMod;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
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
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk", at = @At("HEAD"))
    public void tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        final var mansorChunk = (MansorChunk) chunk;

        if (mansorChunk.mansor$getIsFirstTick() && mansorChunk.mansor$getLastUnloadTime() != 0) {
            mansorChunk.mansor$setIsFirstTick(false);

            final var unloadDuration = getTime() - mansorChunk.mansor$getLastUnloadTime();

            if (randomTickSpeed > 0) {
                final var ticksPerBlock = unloadDuration * randomTickSpeed / (16 * 16 * 16);
                var sections = chunk.getSectionArray();

                for (var s = 0; s < sections.length; s++) {
                    var chunkSection = sections[s];
                    if (!chunkSection.hasRandomTicks()) continue;
                    final var sectionBottom = ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(s));

                    for (var i = 0; i < 16; i++) for (var j = 0; j < 16; j++) for (var k = 0; k < 16; k++) {
                        var blockState = chunkSection.getBlockState(i, j, k);

                        if (blockState.hasRandomTicks()) {
                            var block = blockState.getBlock();

                            if (block instanceof CropBlock crop) {
                                final int age = crop.getAge(blockState);
                                final var x = chunk.getPos().getOffsetX(i);
                                final var z = chunk.getPos().getOffsetZ(k);
                                final var y = sectionBottom + j;
                                final var blockPos = new BlockPos(x, y, z);
                                // FIXME: Take into account night-time
                                final var growthPerTick =
                                        1 / (25 / CropBlock.getAvailableMoisture(block, this, blockPos) + 1);
                                final var growth = (int) Math.floor(ticksPerBlock * growthPerTick);
                                MansorMod.LOGGER.info("Growth[" + blockPos + "]: " + growth);
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
    }
}
