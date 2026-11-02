package io.dnbg.minecraft.jump;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.SplittableRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JitteredThresholdTest {
	@ParameterizedTest
	@ValueSource(ints = {0, 10, 50})
	void staysWithinTheJitterBounds(int percent) {
		double distance = 0.20;
		JitteredThreshold threshold = new JitteredThreshold(new SplittableRandom(1));
		double low = distance * (1 - percent / 100.0);
		double high = distance * (1 + percent / 100.0);
		for (int i = 0; i < 10_000; i++) {
			threshold.threshold(distance, true, percent, false);
			double t = threshold.threshold(distance, true, percent, true);
			assertTrue(t >= low - 1e-12 && t <= high + 1e-12, () -> t + " outside [" + low + ", " + high + "]");
			assertTrue(t >= 0);
		}
	}

	@Test
	void neverGoesBelowZero() {
		JitteredThreshold threshold = new JitteredThreshold(new SplittableRandom(2));
		for (int i = 0; i < 10_000; i++) {
			threshold.threshold(0, true, 50, false);
			assertTrue(threshold.threshold(0, true, 50, true) >= 0);
		}
	}

	@Test
	void keepsOneValuePerGroundContact() {
		JitteredThreshold threshold = new JitteredThreshold(new SplittableRandom(3));
		double first = threshold.threshold(0.5, true, 50, true);
		for (int i = 0; i < 100; i++) {
			assertEquals(first, threshold.threshold(0.5, true, 50, true));
		}
		assertEquals(first, threshold.threshold(0.5, true, 50, false), "leaving the ground draws nothing");
		double second = threshold.threshold(0.5, true, 50, true);
		assertNotEquals(first, second, "landing again draws a new value");
		assertEquals(second, threshold.threshold(0.5, true, 50, true));
	}

	@Test
	void drawsAgainWhenTheSettingsChange() {
		JitteredThreshold threshold = new JitteredThreshold(new SplittableRandom(4));
		threshold.threshold(0.5, false, 50, true);
		assertEquals(0.5, threshold.threshold(0.5, false, 50, true));
		double jittered = threshold.threshold(0.5, true, 50, true);
		assertNotEquals(0.5, jittered, "turning jitter on mid-contact takes effect at once");
	}
}
