package top.nebula.nebula_tinker.datagen.language.type;

import top.nebula.nebula_tinker.datagen.language.LanguageGenerate;

public class TooltipLanguage extends LanguageGenerate {
	public static void register() {
		addLanguage(
				"tooltip",
				"demonization.title",
				"§6§lDemonization Attributes: ",
				"§6§l魔化属性: "
		);
		addLanguage(
				"tooltip",
				"divinization.title",
				"§b§lDivinization Attributes: ",
				"§b§l神化属性: "
		);
		addLanguage(
				"tooltip",
				"negative.title",
				"§c§lNegative Effects: ",
				"§c§l负面效果: "
		);
		addLanguage(
				"tooltip",
				"hold",
				"Hold ",
				"按住"
		);
		addLanguage(
				"tooltip",
				"view",
				" to view ",
				"查看"
		);
		addLanguage(
				"tooltip",
				"divine_demonic",
				"Divine/Demonic",
				"神魔化"
		);
		addLanguage(
				"tooltip",
				"attributes",
				" attributes",
				"属性"
		);
		addLanguage(
				"tooltip",
				"divine_demonic_harmony.damage_interval",
				"§7Damage Interval: §f%s seconds",
				"§7伤害间隔: §f%s秒"
		);
		addLanguage(
				"tooltip",
				"divine_demonic_harmony.damage_factor",
				"§7Damage Factor: §f%s%% of max health",
				"§7伤害系数: §f%s%%最大生命值"
		);
		addLanguage(
				"tooltip",
				"divine_demonic_harmony.level_diff",
				"§7Level Difference: §f%s",
				"§7等级差: §f%s"
		);
	}
}