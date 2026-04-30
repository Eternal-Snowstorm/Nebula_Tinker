package dev.celestiacraft.tinker.tags.builder.type;

import dev.celestiacraft.tinker.tags.builder.AbstractTagBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class FluidTag extends AbstractTagBuilder<Fluid> {
	public FluidTag(String name) {
		super(name);
	}

	@Override
	protected TagKey<Fluid> create(ResourceLocation id) {
		return FluidTags.create(id);
	}
}