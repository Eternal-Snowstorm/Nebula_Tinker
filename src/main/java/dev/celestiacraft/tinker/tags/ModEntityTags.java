package dev.celestiacraft.tinker.tags;

import dev.celestiacraft.tinker.tags.builder.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModEntityTags {
	public static TagKey<EntityType<?>> SPIDERS;

	static {
		SPIDERS = TagsBuilder.entity("spiders").forge();
	}
}