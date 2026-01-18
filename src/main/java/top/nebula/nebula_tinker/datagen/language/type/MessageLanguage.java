package top.nebula.nebula_tinker.datagen.language.type;

import top.nebula.nebula_tinker.datagen.language.LanguageGenerate;

public class MessageLanguage extends LanguageGenerate {
	public static void register() {
		addLanguage(
				"message",
				"modifier.acupoint",
				"Deadly Acupoint activated",
				"<死穴>效果发动"
		);
		addLanguage(
				"message",
				"modifier.capture_king",
				"Capture King activated",
				"<擒王>效果发动"
		);
		addLanguage(
				"message",
				"modifier.killing_rhythm",
				"§6Killing Rhythm activated! %s%% crit chance!",
				"§6<杀戮节奏>效果发动！%s%%暴击率！"
		);
		addLanguage(
				"message",
				"modifier.killing_rhythm.stack",
				"§e+Kill! Crit: %s%% | Duration: %ss",
				"§e+击杀！暴击率: %s%% | 持续: %s秒"
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