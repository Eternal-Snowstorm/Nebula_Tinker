package dev.celestiacraft.tinker.datagen.recipes;

import dev.celestiacraft.tinker.NebulaTinker;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.IRecipeHelper;

public abstract class NTRecipeProvider extends RecipeProvider implements IConditionBuilder, IRecipeHelper {
	public NTRecipeProvider(PackOutput output) {
		super(output);
	}

	/**
	 * 生成配方文件的目录
	 *
	 * @return
	 */
	public abstract String getPath();

	@Override
	public @NotNull ResourceLocation location(@NotNull String id) {
		return NebulaTinker.loadResource("%s/%s".formatted(getPath(), id));
	}

	@Override
	public @NotNull String getModId() {
		return NebulaTinker.MODID;
	}
}