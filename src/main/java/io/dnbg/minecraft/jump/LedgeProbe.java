package io.dnbg.minecraft.jump;

import java.util.OptionalDouble;

/**
 * Finds how far the player can still travel in one direction before the ground runs out (D8),
 * counted only along a path the player can walk (D7), against the ground they would walk over
 * rather than the height they stand at now (D11).
 */
public final class LedgeProbe {
	public static final double SCAN_STEP = 0.05;
	/** Much finer than the scan step, so a jitter swing of a few hundredths still moves the jump point. */
	public static final double PRECISION = 0.001;
	/** How far to look: past the largest threshold the config can produce. */
	public static final double LOOKAHEAD =
			EdgeJumpConfig.MAX_EDGE_DISTANCE * (1 + EdgeJumpConfig.MAX_JITTER_PERCENT / 100.0) + SCAN_STEP;

	/**
	 * A test of the player's bounding box shifted by {@code (dx, dy, dz)}: {@link #remainingTravel}
	 * passes {@code dy ≤ 0}, the drop to the ground it has followed so far.
	 */
	@FunctionalInterface
	public interface BoxTest {
		boolean test(double dx, double dy, double dz);
	}

	private LedgeProbe() {}

	/**
	 * The remaining travel along the unit direction {@code (dirX, dirZ)}, or empty when there is no
	 * ledge within {@link #LOOKAHEAD}, or an obstruction comes before it.
	 *
	 * <p>The result never overstates the travel: it is the last offset found supported.
	 *
	 * @param canFallAt whether nothing lies within {@code stepHeight} below the shifted box
	 * @param collidesAt whether the shifted box collides with anything
	 */
	public static OptionalDouble remainingTravel(
			double dirX, double dirZ, BoxTest canFallAt, BoxTest collidesAt, double stepHeight) {
		double supported = 0;
		double dy = 0;
		for (int i = 0; ; i++) {
			double s = Math.min(i * SCAN_STEP, LOOKAHEAD);
			double dx = dirX * s;
			double dz = dirZ * s;
			// Blocked wins over a fall at the same sample, so an obstruction never reads as a ledge.
			// Colliding raised by the step height too is what makes it more than a step-up bump.
			if (collidesAt.test(dx, dy, dz) && collidesAt.test(dx, dy + stepHeight, dz)) {
				return OptionalDouble.empty();
			}
			if (canFallAt.test(dx, dy, dz)) {
				if (i == 0) {
					return OptionalDouble.of(0);
				}
				double unsupported = s;
				while (unsupported - supported > PRECISION) {
					double mid = (supported + unsupported) / 2;
					if (canFallAt.test(dirX * mid, dy, dirZ * mid)) {
						unsupported = mid;
					} else {
						supported = mid;
					}
				}
				return OptionalDouble.of(supported);
			}
			dy -= floorDrop(dx, dy, dz, collidesAt, stepHeight);
			supported = s;
			if (s >= LOOKAHEAD) {
				return OptionalDouble.empty();
			}
		}
	}

	/**
	 * How far below {@code dy} the floor under a supported sample lies, to within {@link #PRECISION}:
	 * the largest drop, at most {@code stepHeight}, at which the box still does not collide. Zero
	 * when the box is resting on, or pushed up into, something at {@code dy}.
	 */
	private static double floorDrop(double dx, double dy, double dz, BoxTest collidesAt, double stepHeight) {
		if (collidesAt.test(dx, dy - PRECISION, dz)) {
			return 0;
		}
		double clear = PRECISION;
		double blocked = stepHeight;
		while (blocked - clear > PRECISION) {
			double mid = (clear + blocked) / 2;
			if (collidesAt.test(dx, dy - mid, dz)) {
				blocked = mid;
			} else {
				clear = mid;
			}
		}
		return clear;
	}
}
