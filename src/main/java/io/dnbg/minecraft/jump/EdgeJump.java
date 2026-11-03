package io.dnbg.minecraft.jump;

import io.dnbg.minecraft.jump.mixin.PlayerInvoker;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.random.RandomGenerator;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/** Applies edge jump to the local player once per tick, between reading input and moving. */
public final class EdgeJump {
	/** The inset {@code Player.canFallAtLeast} shrinks its test box by. */
	private static final double FALL_TEST_INSET = 1.0E-7;

	private final JitteredThreshold threshold = new JitteredThreshold(RandomGenerator.getDefault());

	public void afterInputTick(LocalPlayer player, ClientInput input, EdgeJumpConfig config) {
		double t = threshold.threshold(config.edgeDistance, config.jitter, config.jitterPercent, player.onGround());
		if (!config.enabled) {
			return;
		}
		boolean eligible = player.onGround()
				&& !player.getAbilities().flying
				&& !player.isPassenger()
				&& !player.isInWater()
				&& !player.isInLava()
				&& !player.onClimbable()
				&& !player.isFallFlying()
				&& !input.keyPresses.jump();
		if (!eligible) {
			return;
		}

		double stepHeight = player.maxUpStep();
		AABB box = player.getBoundingBox();
		LedgeProbe.BoxTest canFallAt = (dx, dy, dz) -> canFallAt(player, box, dx, dy, dz, stepHeight);
		Vec3 velocity = player.getDeltaMovement();
		Vec2 move = input.getMoveVector();
		Optional<ProbeDirection> direction = ProbeDirection.of(velocity.x, velocity.z, move.x, move.y, player.getYRot());
		// With no direction there is nothing to look ahead along, but the fall test at the player's
		// own position needs none, so the hang tick of a slide that has all but stopped still jumps.
		OptionalDouble remaining = direction.isPresent()
				? LedgeProbe.remainingTravel(
						direction.get().x(),
						direction.get().z(),
						canFallAt,
						(dx, dy, dz) -> !player.level().noCollision(player, box.move(dx, dy, dz)),
						stepHeight)
				: canFallAt.test(0, 0, 0) ? OptionalDouble.of(0) : OptionalDouble.empty();

		boolean sneaking = input.keyPresses.shift();
		if (EdgeJumpDecision.shouldJump(true, sneaking, config.jumpWhileSneaking, remaining, t)) {
			input.makeJump();
		}
	}

	/**
	 * Vanilla's own sneak-ledge test at the player's height, so the hang tick (D8) is decided by
	 * vanilla's code. Below it, where the probe has followed the ground down (D11), the same test
	 * rebuilt from {@code Player.canFallAtLeast} with its box lowered by {@code dy}.
	 */
	private static boolean canFallAt(
			LocalPlayer player, AABB box, double dx, double dy, double dz, double stepHeight) {
		if (dy == 0) {
			return ((PlayerInvoker) player).mcmlxxxivJump$canFallAtLeast(dx, dz, stepHeight);
		}
		double floor = box.minY + dy;
		AABB below = new AABB(
				box.minX + FALL_TEST_INSET + dx,
				floor - stepHeight - FALL_TEST_INSET,
				box.minZ + FALL_TEST_INSET + dz,
				box.maxX - FALL_TEST_INSET + dx,
				floor,
				box.maxZ - FALL_TEST_INSET + dz);
		return player.level().noCollision(player, below);
	}
}
