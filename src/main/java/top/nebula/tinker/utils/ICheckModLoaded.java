package top.nebula.tinker.utils;

import net.minecraftforge.fml.ModList;

public interface ICheckModLoaded {
	public static boolean hasMod(String modid) {
		return ModList.get().isLoaded(modid);
	}

	static boolean hasCreate() {
		return hasMod("create");
	}

	static boolean hasTCon() {
		return hasMod("tconstruct");
	}
}