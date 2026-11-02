package io.dnbg.minecraft.jump;

import com.mojang.blaze3d.platform.InputConstants;
import java.nio.file.Path;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JumpClient implements ClientModInitializer {
	public static final String MOD_ID = "mcmlxxxiv_jump";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final EdgeJump EDGE_JUMP = new EdgeJump();
	private static Path configFile;
	private static EdgeJumpConfig config = new EdgeJumpConfig();

	@Override
	public void onInitializeClient() {
		configFile = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");
		config = EdgeJumpConfig.load(configFile);

		KeyMapping toggle = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key." + MOD_ID + ".toggle_edge_jump",
				InputConstants.Type.KEYBOARD,
				InputConstants.UNKNOWN.getValue(),
				KeyMapping.Category.MOVEMENT));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggle.consumeClick()) {
				toggleEnabled(client);
			}
		});
		LOGGER.info("MCMLXXXIV Jump initialized");
	}

	public static EdgeJumpConfig config() {
		return config;
	}

	public static void saveConfig() {
		config.save(configFile);
	}

	/** Called by the mixin on every local-player tick. */
	public static void edgeJump(LocalPlayer player, ClientInput input) {
		EDGE_JUMP.afterInputTick(player, input, config);
	}

	private static void toggleEnabled(Minecraft client) {
		config.enabled = !config.enabled;
		saveConfig();
		if (client.player != null) {
			client.player.sendOverlayMessage(
					Component.translatable(config.enabled ? "message." + MOD_ID + ".edge_jump_on" : "message." + MOD_ID + ".edge_jump_off"));
		}
	}
}
