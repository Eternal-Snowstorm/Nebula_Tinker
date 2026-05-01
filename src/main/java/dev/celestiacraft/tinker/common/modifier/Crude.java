package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * 粗暴
 * <p>
 * 当使用带有该词条的工具攻击目标时, 若目标护甲值较低, 则造成额外伤害
 *
 * <p><b>效果说明: </b>
 * <ul>
 *   <li>当目标护甲值 ≤ 5 时触发效果</li>
 *   <li>额外伤害基于工具的<b>基础伤害</b>计算</li>
 * </ul>
 *
 * <p>
 * 额外伤害计算公式:
 * <br>
 * 额外伤害 = 基础伤害 × (5% × 词条等级)
 *
 * <p><b>等级上限: </b> III(3级)
 *
 * <p><b>示例: </b>
 * <ul>
 *   <li>基础伤害 = 10, 词条等级 = III, 目标护甲 = 4</li>
 *   <li>额外伤害 = 10 × (0.05 × 3) = 1.5</li>
 *   <li>最终伤害 = 原伤害 + 1.5</li>
 * </ul>
 *
 * <p><b>注意: </b>
 * <ul>
 *   <li>该加成仅在目标护甲值 <= 5 时生效</li>
 *   <li>加成基于基础伤害(baseDamage), 不会受到其他词条修改后的伤害影响</li>
 *   <li>该效果为附加伤害, 与其他伤害加成可正常叠加</li>
 * </ul>
 */
public class Crude extends Modifier implements MeleeDamageModifierHook {
	@Override
	public float getMeleeDamage(@NotNull IToolStackView view, @NotNull ModifierEntry entry, @NotNull ToolAttackContext context, float baseDamage, float damage) {
		LivingEntity target = context.getLivingTarget();

		if (target == null) {
			return damage;
		}

		// 护甲 <= 5
		if (target.getArmorValue() <= 5) {
			// 额外伤害 = 基础伤害 × (5% × 等级)
			float bonus = baseDamage * (0.05f * entry.getLevel());
			return damage + bonus;
		}

		return damage;
	}

	@Override
	protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
		builder.addHook(this, ModifierHooks.MELEE_DAMAGE);
	}
}