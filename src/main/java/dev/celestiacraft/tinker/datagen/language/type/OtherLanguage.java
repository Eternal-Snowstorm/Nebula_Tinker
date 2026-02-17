package dev.celestiacraft.tinker.datagen.language.type;

import dev.celestiacraft.tinker.datagen.language.LanguageGenerate;

public class OtherLanguage extends LanguageGenerate {
	public static void register() {
		addLanguage(
				"itemGroup",
				"tab",
				"Nebula Tinker",
				"星云工匠"
		);
		addLanguage(
				"format",
				"multiplier",
				"x",
				"倍"
		);
		addLanguage(
				"format",
				"blocks",
				" blocks",
				"格"
		);
		addLanguage(
				"format",
				"seconds",
				"s",
				"秒"
		);
	}
}