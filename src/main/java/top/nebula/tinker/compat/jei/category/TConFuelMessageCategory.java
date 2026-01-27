package top.nebula.tinker.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import top.nebula.tinker.NebulaTinker;
import top.nebula.tinker.common.recipe.TConFuelMessageRecipe;

import java.util.List;

public class TConFuelMessageCategory implements IRecipeCategory<TConFuelMessageRecipe> {
	private final IDrawable icon;
	private static final int WIDTH = 192;
	private static final int HEIGHT = 24;

	private static final Lazy<Item> ICON_ITEM = Lazy.of(() -> {
		return ForgeRegistries.ITEMS.getValue(ResourceLocation.parse("tconstruct:blazing_blood_bucket"));
	});

	public static final RecipeType<TConFuelMessageRecipe> RECIPE_TYPE = RecipeType.create(
			NebulaTinker.MODID,
			"tcon_fuel_message",
			TConFuelMessageRecipe.class
	);

	public TConFuelMessageCategory(IGuiHelper helper) {
		this.icon = helper.createDrawableItemStack(ICON_ITEM.get().getDefaultInstance());
	}

	@Override
	public @NotNull RecipeType<TConFuelMessageRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("jei.category.nebula_tinker.tcon_fuel_message");
	}

	@Override
	public @NotNull IDrawable getIcon() {
		return icon;
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public void setRecipe(
			@NotNull IRecipeLayoutBuilder builder,
			@NotNull TConFuelMessageRecipe recipe,
			@NotNull IFocusGroup group
	) {
		FluidStack fuel = recipe.fuel();

		IRecipeSlotBuilder slot = builder.addInputSlot();
		slot.setPosition(0, 0, WIDTH, HEIGHT, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
		slot.addFluidStack(fuel.getFluid(), 1000, fuel.getTag());
	}

	@Override
	public void draw(
			@NotNull TConFuelMessageRecipe recipe,
			@NotNull IRecipeSlotsView recipeSlotsView,
			@NotNull GuiGraphics graphics,
			double mouseX,
			double mouseY
	) {
		Font font = Minecraft.getInstance().font;

		TextLayout layout = TextLayout.defaultLayout();

		List<TextPair> leftTexts = List.of(
				TextPair.of("temp", recipe.temperature(), layout.colorTemp),
				TextPair.of("rate", (double) recipe.rate() / 10, layout.colorRate)
		);

		List<TextPair> rightTexts = List.of(
				TextPair.of("amount", recipe.fuel().getAmount(), layout.colorAmount),
				TextPair.of("ruration", recipe.ruration(), layout.colorDuration)
		);

		int xCenter = WIDTH / 2;
		drawLeftGroup(graphics, font, leftTexts, xCenter + layout.spacing, layout);
		drawRightGroup(graphics, font, rightTexts, xCenter - layout.spacing, layout);
	}

	private static void drawLeftGroup(GuiGraphics graphics, Font font, List<TextPair> pairs, int x, TextLayout layout) {
		for (int i = 0; i < pairs.size(); i++) {
			int y = layout.baseY + i * layout.lineHeight;
			drawFromLeft(graphics, font, pairs.get(i), x, y, layout.colorTitle);
		}
	}

	private static void drawRightGroup(GuiGraphics graphics, Font font, List<TextPair> pairs, int x, TextLayout layout) {
		for (int i = 0; i < pairs.size(); i++) {
			int y = layout.baseY + i * layout.lineHeight;
			drawFromRight(graphics, font, pairs.get(i), x, y, layout.colorTitle);
		}
	}

	private static void drawFromLeft(GuiGraphics graphics, Font font, TextPair pair, int x, int y, int titleColor) {
		int valueX = x + font.width(pair.title.getVisualOrderText());
		graphics.drawString(font, pair.title, x, y, titleColor);
		graphics.drawString(font, pair.value, valueX, y, pair.valueColor);
	}

	private static void drawFromRight(GuiGraphics graphics, Font font, TextPair pair, int x, int y, int titleColor) {
		int valueX = x - font.width(pair.value.getVisualOrderText());
		int titleX = valueX - font.width(pair.title.getVisualOrderText());
		graphics.drawString(font, pair.title, titleX, y, titleColor);
		graphics.drawString(font, pair.value, valueX, y, pair.valueColor);
	}

	private record TextPair(Component title, Component value, int valueColor) {
		static TextPair of(String key, Object value, int color) {
			return new TextPair(
					setTranKey(String.format("%s.title", key)),
					setTranKey(key, value),
					color
			);
		}
	}

	private static class TextLayout {
		final int colorTitle = 0xffffffff;
		final int colorTemp = 0xffff1100;
		final int colorRate = 0xffee33ff;
		final int colorAmount = 0xffff7700;
		final int colorDuration = 0xff00ffaa;

		final int spacing = 14;
		final int baseY = 1;
		final int lineHeight = 13;

		static TextLayout defaultLayout() {
			return new TextLayout();
		}
	}

	private static Component setTranKey(String key, Object... args) {
		String tranKey = String.format("jei.category.%s.%s", NebulaTinker.MODID, key);
		return Component.translatable(tranKey, args);
	}
}