package io.dnbg.minecraft.jump;

import java.util.Locale;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The Cloth Config screen for {@link EdgeJumpConfig}. Built by hand rather than with AutoConfig,
 * whose sliders are integer-only: the distance is edited here in hundredths and stored in blocks.
 */
public final class EdgeJumpConfigScreen {
	private static final String KEY = "config." + JumpClient.MOD_ID + ".";

	private EdgeJumpConfigScreen() {}

	public static Screen create(Screen parent) {
		EdgeJumpConfig config = JumpClient.config();
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable(KEY + "title"))
				.setSavingRunnable(JumpClient::saveConfig);
		ConfigEntryBuilder entries = builder.entryBuilder();
		ConfigCategory category = builder.getOrCreateCategory(Component.translatable(KEY + "category"));

		category.addEntry(entries.startBooleanToggle(Component.translatable(KEY + "enabled"), config.enabled)
				.setDefaultValue(EdgeJumpConfig.DEFAULT_ENABLED)
				.setTooltip(Component.translatable(KEY + "enabled.tooltip"))
				.setSaveConsumer(value -> config.enabled = value)
				.build());
		int maxHundredths = (int) Math.round(EdgeJumpConfig.MAX_JUMP_DISTANCE * 100);
		category.addEntry(entries.startIntSlider(
						Component.translatable(KEY + "jump_distance"),
						(int) Math.round(config.jumpDistance * 100),
						0,
						maxHundredths)
				.setDefaultValue((int) Math.round(EdgeJumpConfig.DEFAULT_JUMP_DISTANCE * 100))
				.setTextGetter(hundredths -> Component.translatable(
						KEY + "jump_distance.value", String.format(Locale.ROOT, "%.2f", hundredths / 100.0)))
				.setTooltip(Component.translatable(KEY + "jump_distance.tooltip"))
				.setSaveConsumer(hundredths -> config.jumpDistance = hundredths / 100.0)
				.build());
		category.addEntry(entries.startBooleanToggle(Component.translatable(KEY + "jitter"), config.jitter)
				.setDefaultValue(EdgeJumpConfig.DEFAULT_JITTER)
				.setTooltip(Component.translatable(KEY + "jitter.tooltip"))
				.setSaveConsumer(value -> config.jitter = value)
				.build());
		category.addEntry(entries.startIntSlider(
						Component.translatable(KEY + "jitter_percent"),
						config.jitterPercent,
						0,
						EdgeJumpConfig.MAX_JITTER_PERCENT)
				.setDefaultValue(EdgeJumpConfig.DEFAULT_JITTER_PERCENT)
				.setTextGetter(percent -> Component.translatable(KEY + "jitter_percent.value", percent))
				.setTooltip(Component.translatable(KEY + "jitter_percent.tooltip"))
				.setSaveConsumer(value -> config.jitterPercent = value)
				.build());
		category.addEntry(entries.startBooleanToggle(
						Component.translatable(KEY + "active_while_sneaking"), config.activeWhileSneaking)
				.setDefaultValue(EdgeJumpConfig.DEFAULT_ACTIVE_WHILE_SNEAKING)
				.setTooltip(Component.translatable(KEY + "active_while_sneaking.tooltip"))
				.setSaveConsumer(value -> config.activeWhileSneaking = value)
				.build());
		return builder.build();
	}
}
