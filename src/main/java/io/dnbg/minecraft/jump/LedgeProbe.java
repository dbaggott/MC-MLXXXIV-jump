package io.dnbg.minecraft.jump;

import java.util.OptionalDouble;

/**
 * Finds how far the player can still travel in one direction before the ground runs out: the
 * remaining travel of D2, counted only along a path the player can walk (D7).
 */
public final class LedgeProbe {
	public static final double SCAN_STEP = 0.05;
	/** Much finer than the scan step, so a jitter swing of a few hundredths still moves the jump point. */
	public static final double PRECISION = 0.001;

	/** A test of the player's bounding box shifted horizontally by {@code (dx, dz)}. */
	@FunctionalInterface
	public interface OffsetTest {
		boolean test(double dx, double dz);
	}

	/** Whether the player's bounding box, shifted by {@code (dx, dy, dz)}, collides with anything. */
	@FunctionalInterface
	public interface Collision {
		boolean collidesAt(double dx, double dy, double dz);
	}

	private LedgeProbe() {}

	/**
	 * A sample is blocked when the box collides both where it is and raised by the step height:
	 * the raised case is vanilla's step-up, so a bump no higher than a step does not block.
	 */
	public static OffsetTest blockedAt(Collision collision, double stepHeight) {
		return (dx, dz) -> collision.collidesAt(dx, 0, dz) && collision.collidesAt(dx, stepHeight, dz);
	}

	/**
	 * How far to look: past the largest threshold the config can produce, and past what this tick's
	 * movement can cover, so the D4 carry-past case always sees the ledge it would carry over.
	 */
	public static double lookahead(double tickTravel) {
		return EdgeJumpConfig.MAX_EDGE_DISTANCE * (1 + EdgeJumpConfig.MAX_JITTER_PERCENT / 100.0)
				+ tickTravel + SCAN_STEP;
	}

	/**
	 * The remaining travel along the unit direction {@code (dirX, dirZ)}, or empty when there is no
	 * ledge within {@code lookahead}, or an obstruction comes before it.
	 *
	 * <p>The result never overstates the travel: it is the last offset found supported.
	 */
	public static OptionalDouble remainingTravel(
			double dirX, double dirZ, double lookahead, OffsetTest canFallAt, OffsetTest blockedAt) {
		double supported = 0;
		for (int i = 0; ; i++) {
			double s = Math.min(i * SCAN_STEP, lookahead);
			// Blocked wins over a fall at the same sample, so an obstruction never reads as a ledge.
			if (blockedAt.test(dirX * s, dirZ * s)) {
				return OptionalDouble.empty();
			}
			if (canFallAt.test(dirX * s, dirZ * s)) {
				if (i == 0) {
					return OptionalDouble.of(0);
				}
				double unsupported = s;
				while (unsupported - supported > PRECISION) {
					double mid = (supported + unsupported) / 2;
					if (canFallAt.test(dirX * mid, dirZ * mid)) {
						unsupported = mid;
					} else {
						supported = mid;
					}
				}
				return OptionalDouble.of(supported);
			}
			supported = s;
			if (s >= lookahead) {
				return OptionalDouble.empty();
			}
		}
	}
}
