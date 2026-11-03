package io.dnbg.minecraft.jump;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProbeDirectionTest {
	private static final double TOLERANCE = 1.0E-3;

	private static void assertDirection(double x, double z, Optional<ProbeDirection> direction) {
		assertTrue(direction.isPresent());
		assertEquals(x, direction.get().x(), TOLERANCE);
		assertEquals(z, direction.get().z(), TOLERANCE);
	}

	@Test
	void velocityWinsOverInput() {
		// Sliding along +x while pressing forward facing +z (yaw 0).
		assertDirection(1, 0, ProbeDirection.of(0.2, 0, 0, 1, 0));
	}

	@Test
	void eitherVelocityComponentAtTheCutoffCounts() {
		// x alone is below the cutoff, but the velocity is still used whole.
		double c = ProbeDirection.VELOCITY_CUTOFF;
		assertDirection(1 / Math.sqrt(10), -3 / Math.sqrt(10), ProbeDirection.of(c / 3, -c, 0, 1, 0));
	}

	@Test
	void belowTheCutoffInputRotatedByYawIsUsed() {
		double below = ProbeDirection.VELOCITY_CUTOFF / 2;
		assertDirection(0, 1, ProbeDirection.of(below, below, 0, 1, 0));
		// Facing -x (yaw 90), forward is -x.
		assertDirection(-1, 0, ProbeDirection.of(0, 0, 0, 1, 90));
	}

	@Test
	void neitherGivesNoDirection() {
		assertEquals(Optional.empty(), ProbeDirection.of(0.001, 0.001, 0, 0, 0));
	}
}
