package top.nebula.tinker.datagen.language.type;

import top.nebula.tinker.datagen.language.LanguageGenerate;

public class CommandLanguage extends LanguageGenerate {
	public static void register() {
		addLanguage(
				"command",
				"player_only",
				"Only players can use this command",
				"只有玩家可以使用此命令"
		);
		addLanguage(
				"command",
				"no_modifier",
				"Main hand or off hand item has no Divinization or Demonization modifier",
				"主手或副手物品没有神化或魔化修饰器"
		);
		addLanguage(
				"command",
				"no_demonization",
				"Main hand or off hand item has no Demonization modifier",
				"主手或副手物品没有魔化修饰器"
		);
		addLanguage(
				"command",
				"no_divinization",
				"Main hand or off hand item has no Divinization modifier",
				"主手或副手物品没有神化修饰器"
		);

		addLanguage(
				"command",
				"debug.title",
				"=== Item Attribute Debug ===",
				"=== 物品属性调试 ==="
		);
		addLanguage(
				"command",
				"debug.main_hand",
				"Main hand item: %s",
				"主手物品: %s"
		);
		addLanguage(
				"command",
				"debug.off_hand",
				"Off hand item: %s",
				"副手物品: %s"
		);
		addLanguage(
				"command",
				"debug.main_has_demonization",
				"Main hand item has Demonization modifier",
				"主手物品有魔化修饰器"
		);
		addLanguage(
				"command",
				"debug.off_has_demonization",
				"Off hand item has Demonization modifier",
				"副手物品有魔化修饰器"
		);
		addLanguage(
				"command",
				"debug.main_has_divinization",
				"Main hand item has Divinization modifier",
				"主手物品有神化修饰器"
		);
		addLanguage(
				"command",
				"debug.off_has_divinization",
				"Off hand item has Divinization modifier",
				"副手物品有神化修饰器"
		);

		addLanguage(
				"command",
				"modifier.demonization",
				"Demonization",
				"魔化"
		);
		addLanguage(
				"command",
				"modifier.divinization",
				"Divinization",
				"神化"
		);
		addLanguage(
				"command",
				"modifier.divine_demonic_harmony",
				"Divine-Demonic Harmony",
				"归一"
		);
	}
}