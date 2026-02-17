package dev.celestiacraft.tinker.tags.builder;

import dev.celestiacraft.tinker.tags.builder.type.BlockTag;
import dev.celestiacraft.tinker.tags.builder.type.EntityTypeTag;
import dev.celestiacraft.tinker.tags.builder.type.FluidTag;
import dev.celestiacraft.tinker.tags.builder.type.ItemTag;
import top.nebula.tinker.tags.builder.type.*;

public class TagsBuilder {
	public static BlockTag block(String name) {
		return new BlockTag(name);
	}

	public static ItemTag item(String name) {
		return new ItemTag(name);
	}

	public static FluidTag fluid(String name) {
		return new FluidTag(name);
	}

	public static EntityTypeTag entity(String name) {
		return new EntityTypeTag(name);
	}
}