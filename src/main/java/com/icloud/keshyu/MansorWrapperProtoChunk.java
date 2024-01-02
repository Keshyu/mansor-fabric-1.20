package com.icloud.keshyu;

import net.minecraft.world.chunk.WorldChunk;

public interface MansorWrapperProtoChunk {
    WorldChunk getWrapped();
    boolean getpPropagateToWrapped();
}
