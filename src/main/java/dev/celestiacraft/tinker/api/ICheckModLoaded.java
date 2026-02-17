package dev.celestiacraft.tinker.api;

import net.minecraftforge.fml.ModList;

public interface ICheckModLoaded {
	static boolean hasMod(String modid) {
		return ModList.get().isLoaded(modid);
	}

	static boolean hasCreate() {
		return hasMod("create");
	}

	static boolean hasTCon() {
		return hasMod("tconstruct");
	}
}