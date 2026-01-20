package top.nebula.nebula_tinker.tags.builder;

import top.nebula.nebula_tinker.tags.builder.type.*;

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