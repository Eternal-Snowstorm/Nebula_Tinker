package dev.celestiacraft.tinker.datagen.recipes.tconstruct;

import dev.celestiacraft.tinker.common.register.NTModifier;
import dev.celestiacraft.tinker.datagen.recipes.NTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;

public class ModifierRecipe extends NTRecipeProvider {
	public ModifierRecipe(PackOutput output) {
		super(output);
	}

	@Override
	public String getPath() {
		return "tinker/modifier";
	}

	@Override
	protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
		addUpgradesRecipes(consumer);
	}

	private void addUpgradesRecipes(Consumer<FinishedRecipe> consumer) {
		String folder = "upgrades/";

		ModifierRecipeBuilder.modifier(NTModifier.CRUDE)
				.addInput(Items.FLINT, 5)
				.setMaxLevel(3)
				.setSlots(SlotType.UPGRADE, 1)
				.setTools(TinkerTags.Items.MELEE_PRIMARY)
				.save(consumer, location(folder + "crude"));
	}
}