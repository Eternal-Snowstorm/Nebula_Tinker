package dev.celestiacraft.tinker.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.common.recipe.TConFuelMessageRecipe;
import dev.celestiacraft.tinker.compat.jei.category.TConFuelMessageCategory;
import dev.celestiacraft.tinker.api.ICheckModLoaded;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class NebulaTinkerJeiPlugin implements IModPlugin {
	@Override
	public @NotNull ResourceLocation getPluginUid() {
		return NebulaTinker.loadResource("jei_plugin");
	}

	@Override
	public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
		IJeiHelpers helpers = registration.getJeiHelpers();
		IGuiHelper helper = helpers.getGuiHelper();

		TConFuelMessageCategory fuelCategory = new TConFuelMessageCategory(helper);
		registration.addRecipeCategories(fuelCategory);
	}

	@Override
	public void registerRecipes(@NotNull IRecipeRegistration registration) {
		Level level = Minecraft.getInstance().level;
		RecipeManager recipeManager = Objects.requireNonNull(level).getRecipeManager();
		List<TConFuelMessageRecipe> tconFuelRecipe = RecipeHelper.getRecipes(recipeManager, TinkerRecipeTypes.FUEL.get(), MeltingFuel.class)
				.stream()
				.flatMap((fuel) -> {
					return fuel.getInputs()
							.stream()
							.map((fluid) -> {
								return new TConFuelMessageRecipe(
										fluid,
										fuel.getTemperature(),
										fuel.getRate(),
										fuel.getDuration()
								);
							});
				})
				.sorted(Comparator.comparingInt(TConFuelMessageRecipe::temperature))
				.toList();

		if (!ICheckModLoaded.hasMod("justenoughfuels")) {
			registration.addRecipes(TConFuelMessageCategory.RECIPE_TYPE, tconFuelRecipe);
		}
	}
}