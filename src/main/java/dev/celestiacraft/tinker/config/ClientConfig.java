package dev.celestiacraft.tinker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
	private static final ForgeConfigSpec.Builder BUILDER;
	public static final ForgeConfigSpec SPEC;

	public static class CritSystem {
		public static ForgeConfigSpec.BooleanValue FUEL_JEI_DISPLAY;
	}

	static {
		BUILDER = new ForgeConfigSpec.Builder();

		BUILDER.comment("Here is the client config file for Nebula Tinker")
				.push("general");

		BUILDER.pop();

		CritSystem.FUEL_JEI_DISPLAY = BUILDER
				.comment("togging display of tconstruct fuel recipes")
				.define("fuel_jei_display", false);

		SPEC = BUILDER.build();
	}
}