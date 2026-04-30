package dev.celestiacraft.tinker.tags.builder.type;

import dev.celestiacraft.tinker.tags.builder.AbstractTagBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTag extends AbstractTagBuilder<EntityType<?>> {
	public EntityTypeTag(String name) {
		super(name);
	}

	@Override
	protected TagKey<EntityType<?>> create(ResourceLocation id) {
		return TagKey.create(Registries.ENTITY_TYPE, id);
	}
}