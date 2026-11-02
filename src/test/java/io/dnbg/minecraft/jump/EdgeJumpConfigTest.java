package io.dnbg.minecraft.jump;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EdgeJumpConfigTest {
	@TempDir
	Path dir;

	@Test
	void aMissingFileGivesTheDefaults() {
		EdgeJumpConfig config = EdgeJumpConfig.load(dir.resolve("mcmlxxxiv_jump.json"));
		assertFalse(config.enabled);
		assertEquals(0.10, config.edgeDistance);
		assertFalse(config.jitter);
		assertEquals(10, config.jitterPercent);
		assertFalse(config.jumpWhileSneaking);
	}

	@Test
	void savedSettingsLoadBack() {
		Path file = dir.resolve("config/mcmlxxxiv_jump.json");
		EdgeJumpConfig saved = new EdgeJumpConfig();
		saved.enabled = true;
		saved.edgeDistance = 0.37;
		saved.jitter = true;
		saved.jitterPercent = 25;
		saved.jumpWhileSneaking = true;
		saved.save(file);

		EdgeJumpConfig loaded = EdgeJumpConfig.load(file);
		assertTrue(loaded.enabled);
		assertEquals(0.37, loaded.edgeDistance);
		assertTrue(loaded.jitter);
		assertEquals(25, loaded.jitterPercent);
		assertTrue(loaded.jumpWhileSneaking);
	}

	@Test
	void handEditedValuesArePulledIntoRange() throws IOException {
		Path file = dir.resolve("mcmlxxxiv_jump.json");
		Files.writeString(file, "{\"edgeDistance\": 3.0, \"jitterPercent\": -5}");
		EdgeJumpConfig config = EdgeJumpConfig.load(file);
		assertEquals(1.0, config.edgeDistance);
		assertEquals(0, config.jitterPercent);
	}

	@Test
	void anUnreadableFileGivesTheDefaults() throws IOException {
		Path file = dir.resolve("mcmlxxxiv_jump.json");
		Files.writeString(file, "{not json");
		assertEquals(0.10, EdgeJumpConfig.load(file).edgeDistance);
	}
}
