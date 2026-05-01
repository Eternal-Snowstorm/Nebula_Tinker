package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/**
 * 喜水
 * <p>
 * 基于环境(涉水/浸没/降雨)动态提升工具的挖掘速度
 *
 * <p><b>效果说明: </b>
 * <ul>
 *   <li>当玩家处于水中时(身体接触水), 挖掘速度提升至原来的 <b>2.5 倍</b></li>
 *   <li>当玩家完全浸没于水中时(视线位于水中), 挖掘速度提升至原来的 <b>5.0 倍</b></li>
 *   <li>当玩家处于降雨环境中时, 挖掘速度获得额外增幅: </li>
 * </ul>
 *
 * <p>
 * 雨天增幅计算公式:
 * <br>
 * 最终速度 = 原速度 × (1 + 降雨强度 / 1.6)
 *
 * <p>
 * 其中降雨强度范围为 0 ~ 1, 最大约提供 <b>62.5%</b> 的额外速度提升
 *
 * <p><b>叠加规则: </b>
 * <ul>
 *   <li>"水中"与"完全浸没"互斥, 仅取更高倍率</li>
 *   <li>雨天增幅可与水中效果叠加</li>
 * </ul>
 *
 * <p><b>示例: </b>
 * <ul>
 *   <li>仅下雨(最大强度): ~ 1.625 倍</li>
 *   <li>水中 + 下雨: ~ 2.5 × 1.625 ~ 4.06 倍</li>
 *   <li>完全浸没 + 下雨: ~ 5.0 × 1.625 ~ 8.125 倍</li>
 * </ul>
 */
public class Aquadynamic extends Modifier implements BreakSpeedModifierHook {
	@Override
	public void onBreakSpeed(@NotNull IToolStackView view, @NotNull ModifierEntry entry, PlayerEvent.@NotNull BreakSpeed speed, @NotNull Direction direction, boolean isEffective, float miningSpeedModifier) {
	}

	@Override
	public float modifyBreakSpeed(@NotNull IToolStackView view, @NotNull ModifierEntry entry, @NotNull BreakSpeedContext context, float speed) {
		Player player = context.player();
		Level level = player.level();

		if (!context.isEffective()) {
			return speed;
		}

		float env = context.miningSpeedMultiplier();

		// 删除全身入水惩罚
		float result = env > 0 ? speed / env : speed;
		float baseBonus = view.getMultiplier(ToolStats.MINING_SPEED);
		float bonus = 0;

		// 水中加成
		if (player.isEyeInFluidType(Fluids.WATER.getFluidType())) {
			bonus += baseBonus * 10.0f;
		} else if (player.isInWater()) {
			bonus += baseBonus * 20.5f;
		}

		// 雨天加成
		if (level.isRainingAt(player.blockPosition())) {
			float rain = level.getRainLevel(1.0F);
			bonus += baseBonus * (rain / 1.6f);
		}
		return result + bonus;
	}

	@Override
	protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
		builder.addHook(this, ModifierHooks.BREAK_SPEED);
	}
}