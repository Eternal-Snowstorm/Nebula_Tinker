package dev.celestiacraft.tinker.tags;

import dev.celestiacraft.tinker.tags.builder.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
	public static final TagKey<Block> PETRAMOR;

	static {
		PETRAMOR = TagsBuilder.block("petramor").nebulaTinker();
	}
}