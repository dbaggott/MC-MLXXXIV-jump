package io.dnbg.minecraft.jump;

import java.util.Optional;
import net.minecraft.util.Mth;

/** The unit horizontal direction the ledge probe looks along: momentum first, then movement input (D10). */
public record ProbeDirection(double x, double z) {
	/**
	 * Vanilla's cutoff below which {@code LivingEntity.aiStep} zeroes a velocity component, applied
	 * to x and z separately rather than to the length.
	 */
	public static final double VELOCITY_CUTOFF = 0.003;

	/**
	 * @param velocityX the player's velocity along x
	 * @param velocityZ the player's velocity along z
	 * @param moveX the movement input's strafe component, as {@code ClientInput.getMoveVector()} gives it
	 * @param moveY the movement input's forward component
	 * @param yRot the player's yaw in degrees
	 * @return empty when the player is neither moving above the cutoff nor pressing a movement key
	 */
	public static Optional<ProbeDirection> of(
			double velocityX, double velocityZ, float moveX, float moveY, float yRot) {
		if (Math.abs(velocityX) >= VELOCITY_CUTOFF || Math.abs(velocityZ) >= VELOCITY_CUTOFF) {
			return Optional.of(normalized(velocityX, velocityZ));
		}
		if (moveX == 0 && moveY == 0) {
			return Optional.empty();
		}
		// The same rotation vanilla applies to movement input (Entity.getInputVector).
		float sin = Mth.sin(yRot * Mth.DEG_TO_RAD);
		float cos = Mth.cos(yRot * Mth.DEG_TO_RAD);
		return Optional.of(normalized(moveX * cos - moveY * sin, moveY * cos + moveX * sin));
	}

	private static ProbeDirection normalized(double x, double z) {
		double length = Math.sqrt(x * x + z * z);
		return new ProbeDirection(x / length, z / length);
	}
}
