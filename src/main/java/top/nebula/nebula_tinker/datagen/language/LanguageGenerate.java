package top.nebula.nebula_tinker.datagen.language;

import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.datagen.language.type.*;

import java.util.ArrayList;
import java.util.List;

public class LanguageGenerate {
public static final List<List<String>> TRANSLATION_LIST = new ArrayList<>();

	public static void register() {
		ItemLanguage.register();
		OtherLanguage.register();
		EffectLanguage.register();
		MessageLanguage.register();
		ModifierLanguage.register();
		ModifierLanguage.ModifierFlavor.register();
		ModifierLanguage.ModifierDescription.register();
		TooltipLanguage.register();
		CommandLanguage.register();
		AttributeLanguage.register();
	}
	
	/**
	 * 添加翻译
	 *
	 * @param type 类型
	 * @param key key
	 * @param english 英文
	 * @param chinese 中文
	 */
	@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
	public static void addLanguage(String type, String key, String english, String chinese) {
		List<String> newList = new ArrayList<>();
		if (type == null) {
			newList.add(String.format("%s.%s", NebulaTinker.MODID, key));
		} else {
			newList.add(String.format("%s.%s.%s", type, NebulaTinker.MODID, key));
		}
		newList.add(english);
		newList.add(chinese);
		TRANSLATION_LIST.add(newList);
	}
}