package io.dnbg.minecraft.jump;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

class EdgeJumpDecisionTest {
	private static boolean walking(OptionalDouble remaining, double tickTravel, double threshold) {
		return EdgeJumpDecision.shouldJump(true, false, false, remaining, tickTravel, threshold);
	}

	@Test
	void jumpsOnceTheRemainingTravelIsWithinTheThreshold() {
		assertFalse(walking(OptionalDouble.of(0.31), 0.1, 0.30));
		assertTrue(walking(OptionalDouble.of(0.30), 0.1, 0.30));
		assertTrue(walking(OptionalDouble.of(0.10), 0.1, 0.30));
	}

	@Test
	void jumpsWhenThisTickWouldCarryPastTheEdge() {
		assertTrue(walking(OptionalDouble.of(0.25), 0.28, 0.0));
		assertFalse(walking(OptionalDouble.of(0.29), 0.28, 0.0));
	}

	@Test
	void neverJumpsWithoutAReachableLedge() {
		assertFalse(walking(OptionalDouble.empty(), 0.28, 1.0));
	}

	@Test
	void neverJumpsWhenIneligible() {
		assertFalse(EdgeJumpDecision.shouldJump(false, false, false, OptionalDouble.of(0), 0.28, 1.0));
	}

	@Test
	void sneakingSuppressesItUnlessJumpWhileSneaking() {
		assertFalse(EdgeJumpDecision.shouldJump(true, true, false, OptionalDouble.of(0), 0.0, 0.5));
		assertTrue(EdgeJumpDecision.shouldJump(true, true, true, OptionalDouble.of(0.4), 0.0, 0.5));
	}

	@Test
	void aSneakingPlayerStoppedShortOfTheEdgeStillJumps() {
		assertTrue(EdgeJumpDecision.shouldJump(true, true, true, OptionalDouble.of(0.04), 0.0, 0.0));
		assertFalse(EdgeJumpDecision.shouldJump(true, true, false, OptionalDouble.of(0.04), 0.0, 0.0));
		assertFalse(walking(OptionalDouble.of(0.04), 0.0, 0.0), "the sneak floor is for sneaking only");
	}
}
