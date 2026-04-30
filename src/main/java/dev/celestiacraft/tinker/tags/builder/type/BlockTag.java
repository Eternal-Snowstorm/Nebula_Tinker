package dev.celestiacraft.tinker.tags.builder.type;

import dev.celestiacraft.tinker.tags.builder.AbstractTagBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTag extends AbstractTagBuilder<Block> {
	public BlockTag(String name) {
		super(name);
	}

	@Override
	protected TagKey<Block> create(ResourceLocation id) {
		return BlockTags.create(id);
	}
}