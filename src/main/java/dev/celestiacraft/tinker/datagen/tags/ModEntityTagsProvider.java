package dev.celestiacraft.tinker.datagen.tags;

import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.tags.ModEntityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagsProvider extends EntityTypeTagsProvider {

	public ModEntityTagsProvider(
			PackOutput output,
			CompletableFuture<HolderLookup.Provider> provider,
			ExistingFileHelper helper
	) {
		super(output, provider, NebulaTinker.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ModEntityTags.SPIDERS)
				.add(EntityType.SPIDER)
				.add(EntityType.CAVE_SPIDER);
	}
}