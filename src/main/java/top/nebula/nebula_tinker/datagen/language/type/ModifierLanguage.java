package top.nebula.nebula_tinker.datagen.language.type;

import top.nebula.nebula_tinker.datagen.language.LanguageGenerate;

public class ModifierLanguage extends LanguageGenerate {
	public static void register() {
		ModifierFlavor.register();
		ModifierDescription.register();

		addLanguage(
				"modifier",
				"killing_rhythm",
				"Killing Rhythm",
				"杀戮节奏"
		);
		addLanguage(
				"modifier",
				"capture_king",
				"Capture King",
				"擒王"
		);
		addLanguage(
				"modifier",
				"death_echo",
				"Death Echo",
				"生命回响"
		);
		addLanguage(
				"modifier",
				"acupoint",
				"Deadly Acupoint",
				"死穴"
		);
		addLanguage(
				"modifier",
				"frenzy",
				"Frenzy",
				"狂乱"
		);
		addLanguage(
				"modifier",
				"swift_blade",
				"Swift Blade",
				"迅捷之刃"
		);
		addLanguage(
				"modifier",
				"causal_truncation",
				"Causal Truncation",
				"因果裁断"
		);
		addLanguage(
				"modifier",
				"abuser",
				"Abuser",
				"虐待者"
		);
		addLanguage(
				"modifier",
				"converge",
				"Converge",
				"收束"
		);
		addLanguage(
				"modifier",
				"divinization",
				"Divinization",
				"神化"
		);
		addLanguage(
				"modifier",
				"demonization",
				"Demonization",
				"魔化"
		);
		addLanguage(
				"modifier",
				"divine_demonic_harmony",
				"Divine-Demonic Harmony",
				"归一"
		);
		addLanguage(
				"modifier",
				"divinization.tooltip.title",
				"§6§lDivinization Attributes: ",
				"§6§l神化属性: "
		);
		addLanguage(
				"modifier",
				"demonization.tooltip.title",
				"§4§lDemonization Attributes: ",
				"§4§l魔化属性: "
		);
		addLanguage(
				"modifier",
				"demonization.positive",
				"§aPositive Effects: ",
				"§a正面效果: "
		);
		addLanguage(
				"modifier",
				"demonization.negative",
				"§cNegative Effects: ",
				"§c负面效果: "
		);
		addLanguage(
				"modifier",
				"divine_demonic_harmony.tooltip.title",
				"§5§lDivine-Demonic Harmony: ",
				"§5§l归一: "
		);
	}

	public static class ModifierFlavor extends LanguageGenerate {
		public static void register() {
			addLanguage(
					"modifier",
					"killing_rhythm.flavor",
					"Fast Fast Fast！",
					"Fast Fast Fast！"
			);
			addLanguage(
					"modifier",
					"capture_king.flavor",
					"King and King's Fight",
					"王与王的战斗! "
			);
			addLanguage(
					"modifier",
					"death_echo.flavor",
					"Fight to the end!",
					"战斗到底!"
			);
			addLanguage(
					"modifier",
					"acupoint.flavor",
					"You're in the kill zone!",
					"你已进入斩杀线！"
			);
			addLanguage(
					"modifier",
					"frenzy.flavor",
					"Speed is life",
					"速度即是生命"
			);
			addLanguage(
					"modifier",
					"swift_blade.flavor",
					"I AM THE STORM THAT IS APPROOOOOOOOOOOOOOOOOOOOOOOACHING",
					"我就是即将到来的风风风风风风风风风风风风风风风暴"
			);
			addLanguage(
					"modifier",
					"causal_truncation.flavor",
					"Sever the chains of causality",
					"斩断因果，得证大道"
			);
			addLanguage(
					"modifier",
					"abuser.flavor",
					"Kick them when they're down",
					"趁你病，要你命"
			);
			addLanguage(
					"modifier",
					"converge.flavor",
					"Arrows like rain",
					"箭如雨下"
			);
			addLanguage(
					"modifier",
					"divinization.flavor",
					"Infused with divine power",
					"灌注神圣之力"
			);
			addLanguage(
					"modifier",
					"demonization.flavor",
					"Corrupted by demonic power",
					"被恶魔之力腐蚀"
			);
			addLanguage(
					"modifier",
					"divine_demonic_harmony.flavor",
					"The balance between light and darkness...",
					"光与暗的平衡..."
			);
		}
	}

	public static class ModifierDescription extends LanguageGenerate {
		public static void register() {
			addLanguage(
					"modifier",
					"killing_rhythm.description",
					"Quickly kill 2 enemies to gain 25%% crit chance for 6s. Each extra kill: +5%% crit, +1s duration",
					"迅速杀死2名敌人后获得25%暴击率，持续6秒。每多杀1个: +5%暴击率, +1秒持续时间"
			);
			addLanguage(
					"modifier",
					"capture_king.description",
					"When attacking, the probability will directly cause a critical strike to the BOSS. The higher the level, the higher the critical strike rate.",
					"攻击时概率对BOSS直接造成暴击, 等级越高暴击率越高"
			);
			addLanguage(
					"modifier",
					"death_echo.description",
					"When HP is less than 35%, attacks have a 15% chance to be critical directly.",
					"当血量低于35%时攻击有15%的概率直接暴击"
			);
			addLanguage(
					"modifier",
					"acupoint.description",
					"Chance to instantly kill enemies below 25% health (12.5% for BOSS)",
					"怪物血量低于25%时有概率直接斩杀 (BOSS为12.5%)"
			);
			addLanguage(
					"modifier",
					"frenzy.description",
					"Heal when attacking with speed effect",
					"拥有速度效果时，攻击会回复生命值"
			);
			addLanguage(
					"modifier",
					"swift_blade.description",
					"§7Always critical hit while having speed effect (Higher speed level increases critical damage)",
					"§7拥有速度效果时，攻击必定暴击 (速度等级越高，暴击伤害越高)"
			);
			addLanguage(
					"modifier",
					"causal_truncation.description",
					"Chance to deal 33% of target's current health as damage",
					"攻击时有概率造成目标33%当前血量的伤害"
			);
			addLanguage(
					"modifier",
					"abuser.description",
					"Deal 50% more damage to poisoned, withered or bleeding targets",
					"如果目标身上有中毒、凋零或流血的效果，攻击时必定造成暴击"
			);
			addLanguage(
					"modifier",
					"converge.description",
					"Converging arrow trajectory of bows with Multishot",
					"使带有\"多重射击\"强化的弓箭矢散布更加密集"
			);
			addLanguage(
					"modifier",
					"divinization.description",
					"Grants 3 random divine attributes when applied\n§7• Weapons: Attack, speed, elemental damage\n• Tools: Efficiency, durability, tier\n• Armor: Defense, health, resistance\n§6Total 9 levels, each level enhances effects",
					"每次应用时随机赋予3种神圣属性"
			);
			addLanguage(
					"modifier",
					"demonization.description",
					"Grants 3 powerful attributes with 1 negative effect\n§7• Weapons: High attack boost, but weaker survival\n• Tools: Extreme efficiency, but lower durability\n• Armor: Maximum defense, but weaker offense\n§cSide effect: Periodically takes damage",
					"赋予3种强力属性，但附带1个负面效果"
			);
			addLanguage(
					"modifier",
					"divine_demonic_harmony.description",
					"§7Allows Divine and Demonic powers to coexist in harmony\n§7• Both Divinization and Demonization must be present\n§7• At least one must be level 9 or higher\n§7• Damage scales inversely with level difference\n§7• At level difference 0: Reduces max health by 50%, deals 60% max health damage\n§cWarning: Periodically deals damage based on level difference",
					"§7让神圣与恶魔之力和谐共存"
			);
		}
	}
}