package top.nebula.tinker.client.gui.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ScreenElement {
	@OnlyIn(Dist.CLIENT)
	void render(GuiGraphics graphics, int var0, int var1);
}