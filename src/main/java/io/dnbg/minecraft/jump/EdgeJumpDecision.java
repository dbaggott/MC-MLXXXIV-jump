package io.dnbg.minecraft.jump;

import java.util.OptionalDouble;

/** Whether to jump this tick, given where the ledge is (D4) and whether the player is sneaking (D5). */
public final class EdgeJumpDecision {
	/** The step vanilla's sneak back-off moves in, so a sneaking player can stop this short of a ledge. */
	public static final double SNEAK_BACK_OFF_STEP = 0.05;

	private EdgeJumpDecision() {}

	/**
	 * @param remaining the travel left before falling, or empty when there is no reachable ledge
	 * @param tickTravel an upper bound on how far this tick's movement carries the player
	 * @param threshold the effective edge distance
	 */
	public static boolean shouldJump(
			boolean eligible,
			boolean sneaking,
			boolean jumpWhileSneaking,
			OptionalDouble remaining,
			double tickTravel,
			double threshold) {
		if (!eligible || remaining.isEmpty() || (sneaking && !jumpWhileSneaking)) {
			return false;
		}
		double effective = sneaking ? Math.max(threshold, SNEAK_BACK_OFF_STEP) : threshold;
		double left = remaining.getAsDouble();
		return left <= effective || left <= tickTravel;
	}
}
