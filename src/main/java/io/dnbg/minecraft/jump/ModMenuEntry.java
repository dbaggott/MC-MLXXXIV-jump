package io.dnbg.minecraft.jump;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Puts the config screen behind this mod's entry in Mod Menu; loaded only when Mod Menu is. */
public final class ModMenuEntry implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return EdgeJumpConfigScreen::create;
	}
}
