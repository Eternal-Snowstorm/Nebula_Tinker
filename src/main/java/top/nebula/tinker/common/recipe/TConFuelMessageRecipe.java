package top.nebula.tinker.common.recipe;

import net.minecraftforge.fluids.FluidStack;

public record TConFuelMessageRecipe(FluidStack fuel, int temperature, int rate, int ruration) {
}