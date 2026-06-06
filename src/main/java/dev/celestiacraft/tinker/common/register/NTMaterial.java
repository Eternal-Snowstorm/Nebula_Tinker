package dev.celestiacraft.tinker.common.register;

import dev.celestiacraft.tinker.NebulaTinker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

public class NTMaterial {
	public static final MaterialId
			RUBBER_WOOD;

	static {
		RUBBER_WOOD = addMaterial("rubber_wood");
	}

	private static MaterialId addMaterial(String name) {
		return new MaterialId(NebulaTinker.MODID, name);
	}
}