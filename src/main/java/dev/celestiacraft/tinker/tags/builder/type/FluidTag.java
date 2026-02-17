package dev.celestiacraft.tinker.tags.builder.type;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import dev.celestiacraft.tinker.tags.builder.AbstractTagBuilder;

public class FluidTag extends AbstractTagBuilder<Fluid> {
	public FluidTag(String name) {
		super(name);
	}

	@Override
	public TagKey<Fluid> build() {
		return FluidTags.create(id());
	}
}