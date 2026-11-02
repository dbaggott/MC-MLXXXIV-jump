package io.dnbg.minecraft.jump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The edge-jump options, persisted as JSON. */
public final class EdgeJumpConfig {
	public static final double MAX_EDGE_DISTANCE = 1.0;
	public static final int MAX_JITTER_PERCENT = 50;

	private static final Logger LOGGER = LoggerFactory.getLogger(JumpClient.MOD_ID);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static final boolean DEFAULT_ENABLED = false;
	public static final double DEFAULT_EDGE_DISTANCE = 0.10;
	public static final boolean DEFAULT_JITTER = false;
	public static final int DEFAULT_JITTER_PERCENT = 10;
	public static final boolean DEFAULT_JUMP_WHILE_SNEAKING = false;

	public boolean enabled = DEFAULT_ENABLED;
	/** Blocks of travel left before the ledge at which to jump, in steps of 0.01. */
	public double edgeDistance = DEFAULT_EDGE_DISTANCE;
	public boolean jitter = DEFAULT_JITTER;
	public int jitterPercent = DEFAULT_JITTER_PERCENT;
	public boolean jumpWhileSneaking = DEFAULT_JUMP_WHILE_SNEAKING;

	/**
	 * Reads the file, or returns the defaults when it is missing or unreadable. Out-of-range values
	 * are pulled into range, since the file can be hand-edited.
	 */
	public static EdgeJumpConfig load(Path file) {
		if (!Files.exists(file)) {
			return new EdgeJumpConfig();
		}
		try {
			EdgeJumpConfig config = GSON.fromJson(Files.readString(file), EdgeJumpConfig.class);
			if (config == null) {
				return new EdgeJumpConfig();
			}
			config.clamp();
			return config;
		} catch (IOException | JsonParseException e) {
			LOGGER.warn("Could not read {}, using the default edge-jump settings until it is fixed or saved over", file, e);
			return new EdgeJumpConfig();
		}
	}

	public void save(Path file) {
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, GSON.toJson(this));
		} catch (IOException e) {
			LOGGER.warn("Could not save the edge-jump settings to {}", file, e);
		}
	}

	private void clamp() {
		if (Double.isNaN(edgeDistance)) {
			edgeDistance = DEFAULT_EDGE_DISTANCE;
		}
		edgeDistance = Math.round(Math.clamp(edgeDistance, 0, MAX_EDGE_DISTANCE) * 100) / 100.0;
		jitterPercent = Math.clamp(jitterPercent, 0, MAX_JITTER_PERCENT);
	}
}
