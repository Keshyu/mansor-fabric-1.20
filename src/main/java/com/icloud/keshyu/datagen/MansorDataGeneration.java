package com.icloud.keshyu.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

public class MansorDataGeneration implements DataGeneratorEntrypoint {
    private static class MansorBlockLootTableGenerator extends FabricBlockLootTableProvider {
        public MansorBlockLootTableGenerator(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            addDrop(Blocks.GRASS_BLOCK, drops(Items.OBSIDIAN));
        }
    }
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(MansorBlockLootTableGenerator::new);
    }
}
