package top.nebula.nebula_tinker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
public static final ForgeConfigSpec.Builder BUILDER;
public static final ForgeConfigSpec SPEC;

// 配置定义
public static final ForgeConfigSpec.DoubleValue BASE_CRIT_CHANCE;
public static final ForgeConfigSpec.DoubleValue BASE_CRIT_DAMAGE;
public static final ForgeConfigSpec.DoubleValue BASE_CRIT_RESISTANCE;
public static final ForgeConfigSpec.DoubleValue BASE_CRIT_DAMAGE_REDUCTION;
public static final ForgeConfigSpec.DoubleValue MAX_CRIT_RESISTANCE;
public static final ForgeConfigSpec.DoubleValue MAX_CRIT_DAMAGE_REDUCTION;

static {
	BUILDER = new ForgeConfigSpec.Builder();
	
	BUILDER.comment("Here is the common config file for Nebula Tinker")
			.push("general");
	
	BUILDER.pop();
	
	// 全局暴击配置
	BUILDER.comment("Global Critical System Settings")
			.push("global_crit");
	
	BASE_CRIT_CHANCE = BUILDER
			                   .comment("Base global critical chance for all entities (0.0-1.0)")
			                   .defineInRange("base_crit_chance", 0.05, 0.0, 1.0);
	
	BASE_CRIT_DAMAGE = BUILDER
			                   .comment("Base global critical damage multiplier for all entities")
			                   .defineInRange("base_crit_damage", 1.5, 1.0, 10.0);
	
	BASE_CRIT_RESISTANCE = BUILDER
			                       .comment("Base critical resistance for all entities (0.0-1.0)")
			                       .defineInRange("base_crit_resistance", 0.0, 0.0, 1.0);
	
	BASE_CRIT_DAMAGE_REDUCTION = BUILDER
			                             .comment("Base critical damage reduction for all entities (0.0-1.0)")
			                             .defineInRange("base_crit_damage_reduction", 0.0, 0.0, 1.0);
	
	MAX_CRIT_RESISTANCE = BUILDER
			                      .comment("Maximum critical resistance cap (0.0-1.0)")
			                      .defineInRange("max_crit_resistance", 0.8, 0.0, 1.0);
	
	MAX_CRIT_DAMAGE_REDUCTION = BUILDER
			                            .comment("Maximum critical damage reduction cap (0.0-1.0)")
			                            .defineInRange("max_crit_damage_reduction", 0.8, 0.0, 1.0);
	
	BUILDER.pop();
	
	SPEC = BUILDER.build();
}
}