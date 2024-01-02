package com.icloud.keshyu;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MansorMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mansor");

	@Override
	public void onInitialize() {
		AttackBlockCallback.EVENT.register((player, world, hand, pos, dir) -> {
			final var chunk = (MansorChunk) world.getWorldChunk(pos);
			LOGGER.info("World time: " + world.getTime());
			LOGGER.info("Current chunk time: " + chunk.mansor$getLastUnloadTime());

			return ActionResult.PASS;
		});
	}
}