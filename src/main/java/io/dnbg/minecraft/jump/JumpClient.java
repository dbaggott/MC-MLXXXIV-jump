package io.dnbg.minecraft.jump;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JumpClient implements ClientModInitializer {
	public static final String MOD_ID = "mcmlxxxiv_jump";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("MCMLXXXIV Jump initialized");
	}
}
