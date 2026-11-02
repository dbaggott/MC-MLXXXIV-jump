package io.dnbg.minecraft.jump.mixin;

import io.dnbg.minecraft.jump.JumpClient;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Runs edge jump where vanilla's Auto-Jump presses jump: right after the tick's input is read. */
@Mixin(LocalPlayer.class)
abstract class LocalPlayerMixin {
	@Shadow
	public ClientInput input;

	@Inject(
			method = "aiStep",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/ClientInput;tick()V",
					shift = At.Shift.AFTER))
	private void mcmlxxxivJump$edgeJump(CallbackInfo ci) {
		JumpClient.edgeJump((LocalPlayer) (Object) this, input);
	}
}
