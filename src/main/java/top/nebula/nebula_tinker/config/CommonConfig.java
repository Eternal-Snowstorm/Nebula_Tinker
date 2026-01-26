package top.nebula.nebula_tinker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	public static final ForgeConfigSpec.Builder BUILDER;
	public static final ForgeConfigSpec SPEC;
	
	
	static {
		BUILDER = new ForgeConfigSpec.Builder();
		
		BUILDER.comment("Common configuration for Nebula Tinker mod")
				.push("general");
	
		BUILDER.pop();
		
		SPEC = BUILDER.build();
	}
}