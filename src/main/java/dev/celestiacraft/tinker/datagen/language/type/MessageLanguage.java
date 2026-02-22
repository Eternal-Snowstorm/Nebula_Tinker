package dev.celestiacraft.tinker.datagen.language.type;

import dev.celestiacraft.tinker.datagen.language.LanguageGenerate;

public class MessageLanguage extends LanguageGenerate {
	public static void register() {
		addLanguage(
				"message",
				"modifier.acupoint",
				"The skill Deadly Acupoint has activated",
				"技能<死穴>的效果已发动"
		);
		addLanguage(
				"message",
				"modifier.capture_king",
				"The skill Capture King has activated",
				"技能<擒王>的效果已发动"
		);
		addLanguage(
				"message",
				"modifier.killing_rhythm",
				"§6The skill Killing Rhythm has activated\nCurrent crit chance %s%%",
				"§6技能<杀戮节奏>的效果已发动\n当前暴击率为%s%%"
		);
		addLanguage(
				"message",
				"modifier.killing_rhythm.stack",
				"§eThe skill Killing Rhythm has strengthened\nCurrent crit: %s%% | Duration: %s sec.",
				"§e技能<杀戮节奏>的效果已强化\n当前暴击率: %s%% | 持续: %s秒"
		);
		addLanguage(
				"message",
				"divinization.generate",
				"Divine power surges through your weapon!",
				"神圣之力涌入你的武器！"
		);
		addLanguage(
				"message",
				"demonization.generate",
				"Demonic energy corrupts your weapon!",
				"恶魔能量腐蚀了你的武器！"
		);
		addLanguage(
				"message",
				"divine_demonic_harmony.damage",
				"§dDivine-Demonic Harmony deals §c%s damage§d to you!",
				"§d归一对你造成§c%s伤害§d！"
		);
		addLanguage(
				"message",
				"divine_demonic_harmony.health_reduction",
				"§4Your maximum health has been reduced by %s!",
				"§4你的最大生命值已被降低%s点！"
		);
		addLanguage(
				"message",
				"divine_demonic_harmony.health_restored",
				"§aYour maximum health has been restored!",
				"§a你的最大生命值已恢复！"
		);
	}
}