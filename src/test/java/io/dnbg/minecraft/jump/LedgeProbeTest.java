package io.dnbg.minecraft.jump;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

class LedgeProbeTest {
	private static final double LOOKAHEAD = LedgeProbe.lookahead(0.3);
	private static final LedgeProbe.OffsetTest NEVER = (dx, dz) -> false;

	/** A ledge straight ahead along +x, falling once the offset passes {@code edge}. */
	private static LedgeProbe.OffsetTest fallsBeyond(double edge) {
		return (dx, dz) -> dx > edge;
	}

	@Test
	void findsTheLedgeToWithinThePrecision() {
		OptionalDouble remaining = LedgeProbe.remainingTravel(1, 0, LOOKAHEAD, fallsBeyond(0.37), NEVER);
		assertTrue(remaining.isPresent());
		assertEquals(0.37, remaining.getAsDouble(), LedgeProbe.PRECISION);
		assertTrue(remaining.getAsDouble() <= 0.37, "the result never overstates the travel left");
	}

	@Test
	void followsTheDirectionGiven() {
		double diagonal = Math.sqrt(0.5);
		OptionalDouble remaining = LedgeProbe.remainingTravel(
				diagonal, -diagonal, LOOKAHEAD, (dx, dz) -> Math.hypot(dx, dz) > 0.6 && dz < 0, NEVER);
		assertEquals(0.6, remaining.getAsDouble(), LedgeProbe.PRECISION);
	}

	@Test
	void reportsNoLedgeWhenNoneIsWithinTheLookahead() {
		assertFalse(LedgeProbe.remainingTravel(1, 0, LOOKAHEAD, fallsBeyond(LOOKAHEAD + 0.01), NEVER).isPresent());
	}

	@Test
	void reportsZeroWhenAlreadyAtTheEdge() {
		assertEquals(OptionalDouble.of(0), LedgeProbe.remainingTravel(1, 0, LOOKAHEAD, (dx, dz) -> true, NEVER));
	}

	@Test
	void reportsNoLedgeWhenAnObstructionComesFirst() {
		LedgeProbe.OffsetTest blockedFrom020 = (dx, dz) -> dx >= 0.20;
		assertFalse(LedgeProbe.remainingTravel(1, 0, LOOKAHEAD, fallsBeyond(0.50), blockedFrom020).isPresent());
	}

	@Test
	void aBumpNoHigherThanAStepDoesNotStopTheScan() {
		double stepHeight = 0.6;
		// A slab-high bump from 0.20 to 0.30: colliding at the current height, clear when raised.
		LedgeProbe.Collision slabBump = (dx, dy, dz) -> dx >= 0.20 && dx <= 0.30 && dy < 0.5;
		OptionalDouble remaining = LedgeProbe.remainingTravel(
				1, 0, LOOKAHEAD, fallsBeyond(0.50), LedgeProbe.blockedAt(slabBump, stepHeight));
		assertEquals(0.50, remaining.getAsDouble(), LedgeProbe.PRECISION);
	}

	@Test
	void anObstructionTallerThanAStepBlocks() {
		LedgeProbe.Collision railing = (dx, dy, dz) -> dx >= 0.20 && dy < 1.0;
		assertFalse(LedgeProbe.remainingTravel(1, 0, LOOKAHEAD, fallsBeyond(0.50), LedgeProbe.blockedAt(railing, 0.6))
				.isPresent());
	}
}
