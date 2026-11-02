package io.dnbg.minecraft.jump;

import io.dnbg.minecraft.jump.mixin.PlayerInvoker;
import java.util.OptionalDouble;
import java.util.random.RandomGenerator;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/** Applies edge jump to the local player once per tick, between reading input and moving. */
public final class EdgeJump {
	/**
	 * Covers the tick sprinting starts: the sprint speed modifier lands after this hook reads
	 * {@code getSpeed()}, so the acceleration it reads can be the walking one.
	 */
	private static final double SPRINT_SPEED_MARGIN = 1.3;

	private final JitteredThreshold threshold = new JitteredThreshold(RandomGenerator.getDefault());

	public void afterInputTick(LocalPlayer player, ClientInput input, EdgeJumpConfig config) {
		double t = threshold.threshold(config.jumpDistance, config.jitter, config.jitterPercent, player.onGround());
		if (!config.enabled) {
			return;
		}
		Vec2 move = input.getMoveVector();
		boolean eligible = player.onGround()
				&& !player.getAbilities().flying
				&& !player.isPassenger()
				&& !player.isInWater()
				&& !player.isInLava()
				&& !player.onClimbable()
				&& !player.isFallFlying()
				&& move.lengthSquared() > 0
				&& !input.keyPresses.jump();
		if (!eligible) {
			return;
		}

		// The same rotation vanilla applies to movement input (Entity.getInputVector).
		float sin = Mth.sin(player.getYRot() * Mth.DEG_TO_RAD);
		float cos = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD);
		double worldX = move.x * cos - move.y * sin;
		double worldZ = move.y * cos + move.x * sin;
		double length = Math.sqrt(worldX * worldX + worldZ * worldZ);

		double tickTravel = tickTravel(player, Math.min(1, length));
		double stepHeight = player.maxUpStep();
		PlayerInvoker ledges = (PlayerInvoker) player;
		AABB box = player.getBoundingBox();
		OptionalDouble remaining = LedgeProbe.remainingTravel(
				worldX / length,
				worldZ / length,
				LedgeProbe.lookahead(tickTravel),
				(dx, dz) -> ledges.mcmlxxxivJump$canFallAtLeast(dx, dz, stepHeight),
				LedgeProbe.blockedAt(
						(dx, dy, dz) -> !player.level().noCollision(player, box.move(dx, dy, dz)), stepHeight));

		boolean sneaking = input.keyPresses.shift();
		if (EdgeJumpDecision.shouldJump(true, sneaking, config.activeWhileSneaking, remaining, tickTravel, t)) {
			input.makeJump();
		}
	}

	/**
	 * An upper bound on this tick's horizontal movement: the current velocity plus the input
	 * acceleration, which is at most the movement speed scaled by the input's strength on any block
	 * at least as slippery as ordinary ground.
	 */
	private static double tickTravel(LocalPlayer player, double inputStrength) {
		Vec3 velocity = player.getDeltaMovement();
		return velocity.horizontalDistance() + player.getSpeed() * inputStrength * SPRINT_SPEED_MARGIN;
	}
}
