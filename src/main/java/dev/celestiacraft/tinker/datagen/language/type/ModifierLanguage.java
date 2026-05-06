package dev.celestiacraft.tinker.datagen.language.type;

import dev.celestiacraft.tinker.datagen.language.LanguageGenerate;

public class ModifierLanguage extends LanguageGenerate {
	public static void addLang() {
		names();
		flavor();
		description();
		extra();
	}

	private static void names() {
		addModifierLang("petramor", "Petramor", "爱石");
		addModifierLang("aquadynamic", "Aquadynamic", "喜水");
		addModifierLang("crude", "Crude", "粗暴");
		addModifierLang("killing_rhythm", "Killing Rhythm", "杀戮节奏");
		addModifierLang("capture_king", "Capture King", "擒王");
		addModifierLang("death_echo", "Death Echo", "生命回响");
		addModifierLang("acupoint", "Deadly Acupoint", "死穴");
		addModifierLang("frenzy", "Frenzy", "狂乱");
		addModifierLang("clockwork", "Clockwork", "发条");
		addModifierLang("causal_truncation", "Causal Truncation", "因果裁断");
		addModifierLang("abuser", "Abuser", "虐待者");
		addModifierLang("converge", "Converge", "收束");
		addModifierLang("shadow", "Shadow", "暗影");
		addModifierLang("divinization", "Divinization", "神化");
		addModifierLang("demonization", "Demonization", "魔化");
		addModifierLang("divine_demonic_harmony", "Divine-Demonic Harmony", "归一");
	}

	private static void flavor() {
		addFlavorLang("petramor", "Stone is Right", "赤石大王");
		addFlavorLang("aquadynamic", "Water! Cool!", "如鱼得水");
		addFlavorLang("crude", "Bullying the unarmed", "欺负手无寸铁");
		addFlavorLang("killing_rhythm", "Fast Fast Fast!", "快快快！");
		addFlavorLang("capture_king", "King and King's Fight", "王与王的战斗!");
		addFlavorLang("death_echo", "Fight to the end!", "战斗到底!");
		addFlavorLang("acupoint", "You're in the kill zone!", "你已进入斩杀线！");
		addFlavorLang("frenzy", "Speed is life", "速度即是生命");
		addFlavorLang("clockwork", "Winding up", "蓄势待发");
		addFlavorLang("causal_truncation", "Sever the chains of causality", "斩断因果，得证大道");
		addFlavorLang("abuser", "Kick them when they're down", "趁你病，要你命");
		addFlavorLang("converge", "Arrows like rain", "箭如雨下");
		addFlavorLang("shadow", "A hit from the VOID", "来自虚无的一刀");
		addFlavorLang("divinization", "Infused with divine power", "灌注神圣之力");
		addFlavorLang("demonization", "Corrupted by demonic power", "被恶魔之力腐蚀");
		addFlavorLang("divine_demonic_harmony", "Balance between light and darkness...", "光与暗的平衡...");
	}

	private static void description() {
		addDescriptionLang(
				"shadow",
				"Melee and ranged attacks deal bonus void damage based on Shadow level.",
				"进行近战或远程攻击时，除了原本的攻击伤害，还会根据暗影强化等级额外施加固定量的虚空伤害，伤害量随等级提升"
		);
		addDescriptionLang(
				"petramor",
				"Mines stone blocks have a chance to restore durability; mining ores restores double durability.",
				"挖掘石头时有一定概率回复耐久，如果挖掘矿石则会回复两倍的耐久"
		);
		addDescriptionLang(
				"aquadynamic",
				"Mining speed increases in water (higher when submerged). Negates underwater penalties. Rain bonus stacks.",
				"水中提升挖掘速度（完全浸没时更高），免疫水下惩罚；雨天同样提升，且可叠加"
		);
		addDescriptionLang(
				"crude",
				"Deals bonus damage when target defense ≤ 5 (up to level 3).",
				"当目标防御小于或等于5时攻击会造成额外伤害，最高三级"
		);
		addDescriptionLang(
				"killing_rhythm",
				"Kill 2 enemies quickly to gain 25% crit chance for 6s. Each extra kill: +5% crit, +1s duration.",
				"迅速杀死2名敌人后获得25%暴击率，持续6秒。每多杀1个: +5%暴击率, +1秒持续时间"
		);
		addDescriptionLang(
				"capture_king",
				"Chance to critically strike bosses directly. Higher level increases chance.",
				"攻击时概率对BOSS直接造成暴击，等级越高暴击率越高"
		);
		addDescriptionLang(
				"death_echo",
				"All attacks critically strike when HP is below 20%.",
				"当血量低于20%时攻击会全部变为暴击"
		);
		addDescriptionLang(
				"acupoint",
				"Chance to instantly kill enemies below 50% HP (25% for bosses).",
				"怪物血量低于50%时有概率直接斩杀 (BOSS为25%)"
		);
		addDescriptionLang(
				"frenzy",
				"Heal on hit while having Speed effect.",
				"拥有速度效果时，攻击会回复生命值"
		);
		addDescriptionLang(
				"clockwork",
				"§7Always crit while having Speed. Higher Speed level increases crit damage.",
				"§7拥有速度效果时，攻击必定暴击（速度等级越高，暴击伤害越高）"
		);
		addDescriptionLang(
				"causal_truncation",
				"Chance to deal 33% of target's current health as damage.",
				"攻击时有概率造成目标33%当前血量的伤害"
		);
		addDescriptionLang(
				"abuser",
				"Deals 50% more damage to poisoned, withered or bleeding targets.",
				"如果目标身上有中毒、凋零或流血效果，造成更高伤害"
		);
		addDescriptionLang(
				"converge",
				"Reduces arrow spread for bows with Multishot.",
				"使带有“多重射击”的弓箭矢散布更加集中"
		);
		addDescriptionLang(
				"divinization",
				"Grants 3 random divine attributes when applied.\n§6Total 9 levels, each level enhances effects.",
				"每次应用时随机赋予3种神圣属性"
		);
		addDescriptionLang(
				"demonization",
				"Grants 3 powerful attributes with 1 negative effect.\n§cPeriodically deals damage.",
				"赋予3种强力属性，但附带1个负面效果"
		);
		addDescriptionLang(
				"divine_demonic_harmony",
				"§7Allows divine and demonic powers to coexist.\n§cDeals periodic damage based on imbalance.",
				"§7让神圣与恶魔之力和谐共存"
		);
	}

	private static void extra() {
		addModifierLang(
				"divinization.tooltip.title",
				"§6§lDivinization Attributes: ",
				"§6§l神化属性: "
		);
		addModifierLang(
				"demonization.tooltip.title",
				"§4§lDemonization Attributes: ",
				"§4§l魔化属性: "
		);
		addModifierLang(
				"demonization.positive",
				"§aPositive Effects: ",
				"§a正面效果: "
		);
		addModifierLang(
				"demonization.negative",
				"§cNegative Effects: ",
				"§c负面效果: "
		);
		addModifierLang(
				"divine_demonic_harmony.tooltip.title",
				"§5§lDivine-Demonic Harmony: ",
				"§5§l归一: "
		);
	}

	private static void addModifierLang(String key, String english, String chinese) {
		addLanguage("modifier", key, english, chinese);
	}

	private static void addFlavorLang(String key, String english, String chinese) {
		addLanguage("modifier", key + ".flavor", english, chinese);
	}

	private static void addDescriptionLang(String key, String english, String chinese) {
		addLanguage("modifier", key + ".description", english, chinese);
	}
}