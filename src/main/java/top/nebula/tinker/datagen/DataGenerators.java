package top.nebula.tinker.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.tinker.NebulaTinker;
import top.nebula.tinker.datagen.language.LanguageGenerate;
import top.nebula.tinker.datagen.language.locale.Chinese;
import top.nebula.tinker.datagen.language.locale.English;
import top.nebula.tinker.datagen.tags.ModBlockTagsProvider;
import top.nebula.tinker.datagen.tags.ModFluidTagsProvider;
import top.nebula.tinker.datagen.tags.ModItemTagsProvider;

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