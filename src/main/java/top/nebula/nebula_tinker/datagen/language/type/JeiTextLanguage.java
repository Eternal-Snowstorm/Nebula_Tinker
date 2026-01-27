package top.nebula.nebula_tinker.datagen.language.type;

import top.nebula.nebula_tinker.datagen.language.LanguageGenerate;

public class JeiTextLanguage extends LanguageGenerate {
	private static final String JEI_CATAGORY = "jei.category";

	public static void register() {
		tconFuelMessage();
	}

	private static void tconFuelMessage() {
		setJeiCatagory(
				"tcon_fuel_message",
				"Fuel Message",
				"冶炼炉燃料信息"
		);

		setJeiCatagory(
				"amount.title",
				"Consumption: ",
				"消耗: "
		);
		setJeiCatagory(
				"amount",
				"%s mB",
				"%s mB"
		);

		setJeiCatagory(
				"temp.title",
				"Temperature: ",
				"温度: "
		);
		setJeiCatagory(
				"temp",
				"%s °C",
				"%s °C"
		);

		setJeiCatagory(
				"rate.title",
				"Rate: ",
				"速率: "
		);
		setJeiCatagory(
				"rate",
				"%s Multiplier",
				"%s 倍"
		);

		setJeiCatagory(
				"ruration.title",
				"Duration: ",
				"持续: "
		);
		setJeiCatagory(
				"ruration",
				"%s Tick",
				"%s Tick"
		);
	}

	private static void setJeiCatagory(String key, String english, String chinese) {
		addLanguage("jei.category", key, english, chinese);
	}
}