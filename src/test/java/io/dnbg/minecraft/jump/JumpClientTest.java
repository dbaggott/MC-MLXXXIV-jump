package io.dnbg.minecraft.jump;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JumpClientTest {
	// Proves the JUnit wiring runs under `./gradlew build`; the feature brings the real tests.
	@Test
	void modIdMatchesFabricModJson() {
		assertEquals("mcmlxxxiv_jump", JumpClient.MOD_ID);
	}
}
