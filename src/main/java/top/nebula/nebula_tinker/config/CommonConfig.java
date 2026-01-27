package top.nebula.nebula_tinker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	public static final ForgeConfigSpec.Builder BUILDER;
	public static final ForgeConfigSpec SPEC;

	// 暴击系统配置
	public static class CritSystem {
		public static ForgeConfigSpec.DoubleValue BASE_CRIT_CHANCE;
		public static ForgeConfigSpec.DoubleValue BASE_CRIT_MULTIPLIER;
		public static ForgeConfigSpec.DoubleValue JUMP_CRIT_CHANCE;
		public static ForgeConfigSpec.DoubleValue JUMP_CRIT_MULTIPLIER;
		public static ForgeConfigSpec.DoubleValue MAX_CRIT_CHANCE;
		public static ForgeConfigSpec.DoubleValue MAX_CRIT_MULTIPLIER;
		public static ForgeConfigSpec.DoubleValue MAX_CRIT_RESISTANCE;
		public static ForgeConfigSpec.DoubleValue MAX_CRIT_DAMAGE_REDUCTION;
		public static ForgeConfigSpec.IntValue RANDOM_SEED_MODIFIER;
		public static ForgeConfigSpec.DoubleValue BASE_CRIT_RESISTANCE;
		public static ForgeConfigSpec.DoubleValue BASE_CRIT_DAMAGE_REDUCTION;

		// 全局暴击属性默认值
		public static ForgeConfigSpec.DoubleValue GLOBAL_CRIT_CHANCE_DEFAULT;
		public static ForgeConfigSpec.DoubleValue GLOBAL_CRIT_DAMAGE_DEFAULT;
		public static ForgeConfigSpec.DoubleValue CRIT_RESISTANCE_DEFAULT;
		public static ForgeConfigSpec.DoubleValue CRIT_DAMAGE_REDUCTION_DEFAULT;
	}

	static {
		BUILDER = new ForgeConfigSpec.Builder();

		BUILDER.comment("Common configuration for Nebula Tinker mod")
				.push("general");

		BUILDER.pop();

		// 暴击系统配置
		BUILDER.comment("Critical Hit System Settings")
				.push("crit_system");

		CritSystem.BASE_CRIT_CHANCE = BUILDER
				.comment("Base critical chance for normal attacks (0.0-1.0)")
				.defineInRange("base_crit_chance", 0.05, 0.0, 1.0);

		CritSystem.BASE_CRIT_MULTIPLIER = BUILDER
				.comment("Base critical damage multiplier for normal attacks")
				.defineInRange("base_crit_multiplier", 1.5, 1.0, 10.0);

		CritSystem.JUMP_CRIT_CHANCE = BUILDER
				.comment("Critical chance for jump attacks (0.0-1.0)")
				.defineInRange("jump_crit_chance", 1.0, 0.0, 1.0);

		CritSystem.JUMP_CRIT_MULTIPLIER = BUILDER
				.comment("Critical damage multiplier for jump attacks")
				.defineInRange("jump_crit_multiplier", 2.0, 1.0, 20.0);

		CritSystem.MAX_CRIT_CHANCE = BUILDER
				.comment("Maximum critical chance cap (0.0-1.0)")
				.defineInRange("max_crit_chance", 1.0, 0.0, 1.0);

		CritSystem.MAX_CRIT_MULTIPLIER = BUILDER
				.comment("Maximum critical multiplier cap")
				.defineInRange("max_crit_multiplier", 10.0, 1.0, 50.0);

		CritSystem.MAX_CRIT_RESISTANCE = BUILDER
				.comment("Maximum critical resistance cap (0.0-1.0)")
				.defineInRange("max_crit_resistance", 0.8, 0.0, 1.0);

		CritSystem.MAX_CRIT_DAMAGE_REDUCTION = BUILDER
				.comment("Maximum critical damage reduction cap (0.0-1.0)")
				.defineInRange("max_crit_damage_reduction", 0.8, 0.0, 1.0);

		CritSystem.RANDOM_SEED_MODIFIER = BUILDER
				.comment("Modifier for random seed calculation (adjust to change crit RNG)")
				.defineInRange("random_seed_modifier", 12345, 0, Integer.MAX_VALUE);

		CritSystem.BASE_CRIT_RESISTANCE = BUILDER
				.comment("Base critical resistance for all entities (0.0-1.0)")
				.defineInRange("base_crit_resistance", 0.0, 0.0, 1.0);

		CritSystem.BASE_CRIT_DAMAGE_REDUCTION = BUILDER
				.comment("Base critical damage reduction for all entities (0.0-1.0)")
				.defineInRange("base_crit_damage_reduction", 0.0, 0.0, 1.0);

		CritSystem.GLOBAL_CRIT_CHANCE_DEFAULT = BUILDER
				.comment("Default global critical chance attribute value")
				.defineInRange("global_crit_chance_default", 0.05, 0.0, 1.0);

		CritSystem.GLOBAL_CRIT_DAMAGE_DEFAULT = BUILDER
				.comment("Default global critical damage attribute value")
				.defineInRange("global_crit_damage_default", 1.5, 1.0, 10.0);

		CritSystem.CRIT_RESISTANCE_DEFAULT = BUILDER
				.comment("Default critical resistance attribute value")
				.defineInRange("crit_resistance_default", 0.0, 0.0, 1.0);

		CritSystem.CRIT_DAMAGE_REDUCTION_DEFAULT = BUILDER
				.comment("Default critical damage reduction attribute value")
				.defineInRange("crit_damage_reduction_default", 0.0, 0.0, 1.0);

		BUILDER.pop();

		SPEC = BUILDER.build();
	}
}