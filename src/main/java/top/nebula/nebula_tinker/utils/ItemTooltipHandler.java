package top.nebula.nebula_tinker.utils;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.entity.AttributeApplicator;

import java.util.List;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID)
public class ItemTooltipHandler {
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		Player player = event.getEntity();

		if (player == null || stack.isEmpty()) {
			return;
		}

		boolean hasDemonization = SimpleTConUtils.hasModifier(
				stack,
				NebulaTinker.loadResource("demonization").toString()
		);
		boolean hasDivinization = SimpleTConUtils.hasModifier(
				stack,
				NebulaTinker.loadResource("divinization").toString()
		);

		if (!hasDemonization && !hasDivinization) {
			return;
		}

		List<Component> tooltips = event.getToolTip();
		int insertIndex = findInsertIndex(tooltips);

		// 是否按住 Alt
		boolean altDown = Screen.hasAltDown();
		if (!altDown) {
			MutableComponent hint = Component.literal("按住")
					.append(Component.literal("Alt")
							.withStyle(ChatFormatting.RED)
							.withStyle(ChatFormatting.ITALIC))
					.append(Component.literal("查看"))
					.append(Component.literal("神魔化")
							.withStyle(ChatFormatting.GOLD)
							.withStyle(ChatFormatting.BOLD))
					.append(Component.literal("属性"));

			tooltips.add(insertIndex, hint);
			return;
		}

		// 按住 Alt → 显示真实属性
		List<Component> attributeTooltips = AttributeApplicator.getAttributeTooltips(stack, player);
		if (!attributeTooltips.isEmpty()) {
			for (int i = attributeTooltips.size() - 1; i >= 0; i--) {
				tooltips.add(insertIndex, attributeTooltips.get(i));
			}
		}
	}

	/**
	 * 查找插入位置
	 */
	private static int findInsertIndex(List<Component> tooltips) {
		// 首先尝试在修饰符之后插入
		boolean foundModifiers = false;
		for (int i = 0; i < tooltips.size(); i++) {
			String text = tooltips.get(i).getString();
			if (text.contains("修饰符") || text.contains("Modifier")) {
				foundModifiers = true;
				// 找到修饰符段落后的空行
				for (int j = i + 1; j < tooltips.size(); j++) {
					if (tooltips.get(j).getString().isEmpty()) {
						return j;
					}
				}
				return i + 1;
			}
		}

		// 如果没有找到修饰符，在"按住Shift查看详情"或"按住Ctrl查看详细信息"之后插入
		for (int i = 0; i < tooltips.size(); i++) {
			String text = tooltips.get(i).getString().toLowerCase();
			if (text.contains("shift") || text.contains("ctrl") || text.contains("详细信息")) {
				return i + 1;
			}
		}

		// 如果还没有找到，在倒数第二行插入（最后一行通常是Mod名称）
		return Math.max(0, tooltips.size() - 1);
	}
}