package io.dnbg.minecraft.jump;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

class LedgeProbeTest {
	private static final double STEP_HEIGHT = 0.6;
	/** Far enough below that nothing within a step of the player is ground. */
	private static final double VOID = -10;

	/**
	 * The height of the highest surface under the player's footprint shifted by {@code (dx, dz)},
	 * relative to where the player stands now. Everything below it is solid.
	 */
	@FunctionalInterface
	private interface Terrain {
		double topAt(double dx, double dz);
	}

	private static OptionalDouble probe(double dirX, double dirZ, Terrain terrain) {
		return probe(Optional.of(new ProbeDirection(dirX, dirZ)), terrain);
	}

	private static OptionalDouble probe(Optional<ProbeDirection> direction, Terrain terrain) {
		return LedgeProbe.remainingTravel(
				direction,
				(dx, dy, dz) -> terrain.topAt(dx, dz) < dy - STEP_HEIGHT,
				(dx, dy, dz) -> dy < terrain.topAt(dx, dz),
				STEP_HEIGHT);
	}

	private static OptionalDouble probeAlongX(Terrain terrain) {
		return probe(1, 0, terrain);
	}

	/** Level ground straight ahead along +x, falling away once the offset passes {@code edge}. */
	private static Terrain fallsBeyond(double edge) {
		return (dx, dz) -> dx > edge ? VOID : 0;
	}

	@Test
	void findsTheLedgeToWithinThePrecision() {
		OptionalDouble remaining = probeAlongX(fallsBeyond(0.37));
		assertTrue(remaining.isPresent());
		assertEquals(0.37, remaining.getAsDouble(), LedgeProbe.PRECISION);
		assertTrue(remaining.getAsDouble() <= 0.37, "the result never overstates the travel left");
	}

	@Test
	void followsTheDirectionGiven() {
		double diagonal = Math.sqrt(0.5);
		OptionalDouble remaining =
				probe(diagonal, -diagonal, (dx, dz) -> Math.hypot(dx, dz) > 0.6 && dz < 0 ? VOID : 0);
		assertEquals(0.6, remaining.getAsDouble(), LedgeProbe.PRECISION);
	}

	@Test
	void reportsNoLedgeWhenNoneIsWithinTheLookahead() {
		assertFalse(probeAlongX(fallsBeyond(LedgeProbe.LOOKAHEAD + 0.01)).isPresent());
	}

	@Test
	void reportsZeroWhenAlreadyAtTheEdge() {
		assertEquals(OptionalDouble.of(0), probeAlongX((dx, dz) -> VOID));
	}

	@Test
	void reportsNoLedgeWhenAnObstructionComesFirst() {
		Terrain wallFrom020 = (dx, dz) -> dx >= 0.20 ? 10 : fallsBeyond(0.50).topAt(dx, dz);
		assertFalse(probeAlongX(wallFrom020).isPresent());
	}

	@Test
	void aBumpNoHigherThanAStepDoesNotStopTheScan() {
		Terrain slabBump = (dx, dz) -> dx >= 0.20 && dx <= 0.30 ? 0.5 : fallsBeyond(0.50).topAt(dx, dz);
		assertEquals(0.50, probeAlongX(slabBump).getAsDouble(), LedgeProbe.PRECISION);
	}

	@Test
	void anObstructionTallerThanAStepBlocks() {
		Terrain railing = (dx, dz) -> dx >= 0.20 ? 1.0 : 0;
		assertFalse(probeAlongX(railing).isPresent());
	}

	@Test
	void aDescendingStaircaseIsNotALedge() {
		Terrain stairs = (dx, dz) -> -0.5 * Math.floor(dx / 0.5);
		assertFalse(probeAlongX(stairs).isPresent());
	}

	@Test
	void aDropBeyondAStepDownIsMeasuredFromTheLowerStep() {
		Terrain stepThenDrop = (dx, dz) -> dx > 1.3 ? VOID : dx > 0.3 ? -0.5 : 0;
		assertEquals(1.3, probeAlongX(stepThenDrop).getAsDouble(), LedgeProbe.PRECISION);
	}

	@Test
	void aDropOfMoreThanAStepWithNoStepBeforeItIsALedge() {
		Terrain drop = (dx, dz) -> dx > 0.3 ? -0.7 : 0;
		assertEquals(0.3, probeAlongX(drop).getAsDouble(), LedgeProbe.PRECISION);
	}

	@Test
	void withNoDirectionOnlyThePlayersOwnPositionCounts() {
		assertEquals(OptionalDouble.of(0), probe(Optional.empty(), (dx, dz) -> VOID));
		assertFalse(probe(Optional.empty(), fallsBeyond(0.01)).isPresent());
	}
}
