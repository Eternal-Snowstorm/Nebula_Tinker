package top.nebula.nebula_tinker.datagen.language.type;

import top.nebula.nebula_tinker.datagen.language.LanguageGenerate;

public class ItemLanguage extends LanguageGenerate {
	public static void register() {
		addLanguage(
				"item",
				"demonization_stone",
				"Demonization Stone",
				"魔化石"
		);
		addLanguage(
				"item",
				"divinization_stone",
				"Divinization Stone",
				"神化石"
		);
	}
}