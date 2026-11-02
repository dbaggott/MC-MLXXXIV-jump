package io.dnbg.minecraft.jump.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Vanilla's own sneak-ledge test, so edge jump's idea of a ledge is exactly where sneaking stops. */
@Mixin(Player.class)
public interface PlayerInvoker {
	@Invoker("canFallAtLeast")
	boolean mcmlxxxivJump$canFallAtLeast(double dx, double dz, double height);
}
