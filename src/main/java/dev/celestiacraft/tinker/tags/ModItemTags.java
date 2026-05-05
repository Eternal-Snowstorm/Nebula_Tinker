package dev.celestiacraft.tinker.tags;

import dev.celestiacraft.tinker.tags.builder.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
	public static final TagKey<Item> FLINTS;

	static {
		FLINTS = TagsBuilder.item("flints").forge();
	}
}