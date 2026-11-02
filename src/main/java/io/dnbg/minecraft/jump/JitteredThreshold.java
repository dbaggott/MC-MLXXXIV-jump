package io.dnbg.minecraft.jump;

import java.util.random.RandomGenerator;

/**
 * The effective jump distance of D3: the configured distance scaled by a random factor that is drawn
 * once per ground contact, and again whenever the settings change.
 */
public final class JitteredThreshold {
	private record Settings(double distance, boolean jitter, int jitterPercent) {}

	private final RandomGenerator random;
	private Settings settings;
	private boolean wasOnGround;
	private double threshold;

	public JitteredThreshold(RandomGenerator random) {
		this.random = random;
	}

	/** Call once per tick, grounded or not, so a new ground contact is seen on the tick it starts. */
	public double threshold(double distance, boolean jitter, int jitterPercent, boolean onGround) {
		Settings current = new Settings(distance, jitter, jitterPercent);
		boolean newContact = onGround && !wasOnGround;
		wasOnGround = onGround;
		if (newContact || !current.equals(settings)) {
			settings = current;
			threshold = Math.max(0, distance * (1 + draw(jitter, jitterPercent)));
		}
		return threshold;
	}

	private double draw(boolean jitter, int jitterPercent) {
		if (!jitter || jitterPercent == 0) {
			return 0;
		}
		double spread = jitterPercent / 100.0;
		return random.nextDouble(-spread, Math.nextUp(spread));
	}
}
