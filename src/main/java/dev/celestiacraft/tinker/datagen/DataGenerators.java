package dev.celestiacraft.tinker.datagen;

import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.datagen.language.LanguageGenerate;
import dev.celestiacraft.tinker.datagen.language.locale.Chinese;
import dev.celestiacraft.tinker.datagen.language.locale.English;
import dev.celestiacraft.tinker.datagen.loot.NTLootModifierProvider;
import dev.celestiacraft.tinker.datagen.models.item.NTItemModelProvider;
import dev.celestiacraft.tinker.datagen.recipes.tconstruct.ModifierRecipe;
import dev.celestiacraft.tinker.datagen.tags.ModBlockTagsProvider;
import dev.celestiacraft.tinker.datagen.tags.ModEntityTagsProvider;
import dev.celestiacraft.tinker.datagen.tags.ModFluidTagsProvider;
import dev.celestiacraft.tinker.datagen.tags.ModItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	@SubscribeEvent
	public static void datagen(GatherDataEvent event) {
		addClientProviders(event);
		addServerProviders(event);
	}

	private static void addClientProviders(GatherDataEvent event) {
		Consumer<Function<PackOutput, ? extends DataProvider>> client = addProvider(
				event,
				event.includeClient()
		);

		ExistingFileHelper helper = event.getExistingFileHelper();

		client.accept(English::new);
		client.accept(Chinese::new);
		client.accept((output) -> {
			return new NTItemModelProvider(output, helper);
		});

		LanguageGenerate.register();
	}

	private static void addServerProviders(GatherDataEvent event) {
		Consumer<Function<PackOutput, ? extends DataProvider>> server = addProvider(
				event,
				event.includeServer()
		);

		PackOutput output = event.getGenerator().getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

		ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, provider, helper);
		ModItemTagsProvider itemTags = new ModItemTagsProvider(output, provider, blockTags, helper);

		server.accept((packOutput) -> {
			return blockTags;
		});
		server.accept((packOutput) -> {
			return itemTags;
		});

		server.accept((packOutput) -> {
			return new ModFluidTagsProvider(packOutput, provider, helper);
		});

		server.accept((packOutput) -> {
			return new ModEntityTagsProvider(packOutput, provider, helper);
		});

		server.accept(NTLootModifierProvider::new);

		addTConRecipes(server);
	}

	private static void addTConRecipes(Consumer<Function<PackOutput, ? extends DataProvider>> consumer) {
		consumer.accept(ModifierRecipe::new);
	}

	private static Consumer<Function<PackOutput, ? extends DataProvider>> addProvider(
			GatherDataEvent event,
			boolean include
	) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();

		return (function) -> {
			generator.addProvider(include, function.apply(output));
		};
	}
}