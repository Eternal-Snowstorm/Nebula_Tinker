package top.nebula.tinker.datagen.language.type;

import top.nebula.tinker.datagen.language.LanguageGenerate;

public class AttributeLanguage extends LanguageGenerate {
	public static void register() {
		addLanguage(
				"attribute",
				"modifier.attack_damage",
				"Attack Damage +%s",
				"攻击伤害 +%s"
		);
		addLanguage(
				"attribute",
				"modifier.attack_speed",
				"Attack Speed +%s",
				"攻击速度 +%s"
		);
		addLanguage(
				"attribute",
				"modifier.critical_chance",
				"Critical Chance +%s",
				"暴击几率 +%s"
		);
		addLanguage(
				"attribute",
				"modifier.mining_speed_reduction",
				"Mining Speed -%s",
				"挖掘速度 -%s"
		);
		addLanguage(
				"attribute",
				"modifier.durability_reduction",
				"Durability -%s",
				"耐久度 -%s"
		);
		addLanguage(
				"attribute",
				"modifier.harvest_level_reduction",
				"Harvest Level -%s",
				"采集等级 -%s"
		);
		addLanguage(
				"attribute",
				"modifier.health_reduction",
				"health_reduction -%s",
				"扣除最大生命值 -%s"
		);
		addLanguage(
				"modifier.attribute",
				"movement_speed",
				"movement speed",
				"移动速度 +%s"
		);
	}
}