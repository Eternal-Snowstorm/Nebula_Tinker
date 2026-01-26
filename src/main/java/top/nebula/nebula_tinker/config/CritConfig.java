package top.nebula.nebula_tinker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CritConfig {
	public static final ForgeConfigSpec.Builder BUILDER;
	public static final ForgeConfigSpec SPEC;
	
	// 基础暴击配置
	public static final ForgeConfigSpec.DoubleValue BASE_CRIT_CHANCE;
	public static final ForgeConfigSpec.DoubleValue BASE_CRIT_MULTIPLIER;
	
	// 跳劈暴击配置
	public static final ForgeConfigSpec.DoubleValue JUMP_CRIT_CHANCE;
	public static final ForgeConfigSpec.DoubleValue JUMP_CRIT_MULTIPLIER;
	
	// 全局暴击属性配置
	public static final ForgeConfigSpec.DoubleValue MAX_CRIT_CHANCE;
	public static final ForgeConfigSpec.DoubleValue MAX_CRIT_MULTIPLIER;
	public static final ForgeConfigSpec.DoubleValue MAX_CRIT_RESISTANCE;
	public static final ForgeConfigSpec.DoubleValue MAX_CRIT_DAMAGE_REDUCTION;
	
	// 随机数种子配置
	public static final ForgeConfigSpec.IntValue RANDOM_SEED_MODIFIER;
	
	static {
		BUILDER = new ForgeConfigSpec.Builder();
		
		BUILDER.comment("Critical Hit System Configuration")
				.push("crit_system");
		
		BASE_CRIT_CHANCE = BUILDER
				                   .comment("Base critical chance for normal attacks (0.0-1.0)")
				                   .defineInRange("base_crit_chance", 0.05, 0.0, 1.0);
		
		BASE_CRIT_MULTIPLIER = BUILDER
				                       .comment("Base critical damage multiplier for normal attacks")
				                       .defineInRange("base_crit_multiplier", 1.5, 1.0, 10.0);
		
		JUMP_CRIT_CHANCE = BUILDER
				                   .comment("Critical chance for jump attacks (0.0-1.0)")
				                   .defineInRange("jump_crit_chance", 1.0, 0.0, 1.0);
		
		JUMP_CRIT_MULTIPLIER = BUILDER
				                       .comment("Critical damage multiplier for jump attacks")
				                       .defineInRange("jump_crit_multiplier", 2.0, 1.0, 20.0);
		
		MAX_CRIT_CHANCE = BUILDER
				                  .comment("Maximum critical chance cap (0.0-1.0)")
				                  .defineInRange("max_crit_chance", 1.0, 0.0, 1.0);
		
		MAX_CRIT_MULTIPLIER = BUILDER
				                      .comment("Maximum critical multiplier cap")
				                      .defineInRange("max_crit_multiplier", 10.0, 1.0, 50.0);
		
		MAX_CRIT_RESISTANCE = BUILDER
				                      .comment("Maximum critical resistance cap (0.0-1.0)")
				                      .defineInRange("max_crit_resistance", 0.8, 0.0, 1.0);
		
		MAX_CRIT_DAMAGE_REDUCTION = BUILDER
				                            .comment("Maximum critical damage reduction cap (0.0-1.0)")
				                            .defineInRange("max_crit_damage_reduction", 0.8, 0.0, 1.0);
		
		RANDOM_SEED_MODIFIER = BUILDER
				                       .comment("Modifier for random seed calculation (adjust to change crit RNG)")
				                       .defineInRange("random_seed_modifier", 12345, 0, Integer.MAX_VALUE);
		
		BUILDER.pop();
		
		SPEC = BUILDER.build();
	}
}