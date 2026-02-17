package dev.celestiacraft.tinker.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.datagen.language.LanguageGenerate;
import dev.celestiacraft.tinker.datagen.language.locale.Chinese;
import dev.celestiacraft.tinker.datagen.language.locale.English;
import dev.celestiacraft.tinker.datagen.tags.ModBlockTagsProvider;
import dev.celestiacraft.tinker.datagen.tags.ModFluidTagsProvider;
import dev.celestiacraft.tinker.datagen.tags.ModItemTagsProvider;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	@SubscribeEvent
	public static void datagen(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

		LanguageGenerate.register();

		// Client
		generator.addProvider(event.includeClient(), new English(output));
		generator.addProvider(event.includeClient(), new Chinese(output));

		// Server
		ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, provider, helper);
		ModItemTagsProvider itemTags = new ModItemTagsProvider(output, provider, blockTags, helper);
		ModFluidTagsProvider fluidTags = new ModFluidTagsProvider(output, provider, helper);

		generator.addProvider(event.includeServer(), blockTags);
		generator.addProvider(event.includeServer(), itemTags);
		generator.addProvider(event.includeServer(), fluidTags);
	}
}