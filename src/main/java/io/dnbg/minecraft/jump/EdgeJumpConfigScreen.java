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
 *
 * <p>Options are explained by a line of text under their control rather than by a hover tooltip,
 * which Cloth draws over the rows beneath the cursor.
 */
public final class EdgeJumpConfigScreen {
	private static final String KEY = "config." + JumpClient.MOD_ID + ".";
	private static final int DESCRIPTION_COLOR = 0xFFAAAAAA;

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
				.setSaveConsumer(value -> config.enabled = value)
				.build());

		category.addEntry(entries.startIntSlider(
						Component.translatable(KEY + "edge_distance"),
						(int) Math.round(config.edgeDistance * 100),
						0,
						(int) Math.round(EdgeJumpConfig.MAX_EDGE_DISTANCE * 100))
				.setDefaultValue((int) Math.round(EdgeJumpConfig.DEFAULT_EDGE_DISTANCE * 100))
				.setTextGetter(hundredths -> Component.translatable(
						KEY + "edge_distance.value", String.format(Locale.ROOT, "%.2f", hundredths / 100.0)))
				.setSaveConsumer(hundredths -> config.edgeDistance = hundredths / 100.0)
				.build());
		addDescription(category, entries, "edge_distance");

		category.addEntry(entries.startBooleanToggle(Component.translatable(KEY + "jitter"), config.jitter)
				.setDefaultValue(EdgeJumpConfig.DEFAULT_JITTER)
				.setSaveConsumer(value -> config.jitter = value)
				.build());
		addDescription(category, entries, "jitter");

		category.addEntry(entries.startIntSlider(
						Component.translatable(KEY + "jitter_percent"),
						config.jitterPercent,
						0,
						EdgeJumpConfig.MAX_JITTER_PERCENT)
				.setDefaultValue(EdgeJumpConfig.DEFAULT_JITTER_PERCENT)
				.setTextGetter(percent -> Component.translatable(KEY + "jitter_percent.value", percent))
				.setSaveConsumer(value -> config.jitterPercent = value)
				.build());
		addDescription(category, entries, "jitter_percent");

		category.addEntry(entries.startBooleanToggle(
						Component.translatable(KEY + "jump_while_sneaking"), config.jumpWhileSneaking)
				.setDefaultValue(EdgeJumpConfig.DEFAULT_JUMP_WHILE_SNEAKING)
				.setSaveConsumer(value -> config.jumpWhileSneaking = value)
				.build());
		return builder.build();
	}

	private static void addDescription(ConfigCategory category, ConfigEntryBuilder entries, String option) {
		category.addEntry(entries.startTextDescription(Component.translatable(KEY + option + ".description"))
				.setColor(DESCRIPTION_COLOR)
				.build());
	}
}
