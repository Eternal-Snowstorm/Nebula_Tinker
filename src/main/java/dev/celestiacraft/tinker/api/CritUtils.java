package dev.celestiacraft.tinker.api;

import net.minecraft.world.item.ItemStack;
import dev.celestiacraft.tinker.NebulaTinker;

public class CritUtils {
	// 修饰符ID常量
	public static final String MODIFIER_ABUSER = "abuser";
	public static final String MODIFIER_SWIFT_BLADE = "swift_blade";
	public static final String MODIFIER_CAPTURE_KING = "capture_king";
	public static final String MODIFIER_DEATH_ECHO = "death_echo";
	public static final String MODIFIER_KILLING_RHYTHM = "killing_rhythm";

	/**
	 * 检查武器是否有指定的暴击修饰符
	 */
	public static boolean hasCritModifier(ItemStack stack, String modifierId) {
		return SimpleTConUtils.hasModifier(stack, NebulaTinker.loadResource(modifierId).toString());
	}

	/**
	 * 获取暴击修饰符等级
	 */
	public static int getCritModifierLevel(ItemStack stack, String modifierId) {
		return SimpleTConUtils.getModifierLevel(stack, NebulaTinker.loadResource(modifierId).toString());
	}

	/**
	 * 获取武器的暴击加成
	 */
	public static CritBonus getWeaponCritBonus(ItemStack stack) {
		CritBonus bonus = new CritBonus();

		if (stack.isEmpty()) return bonus;

		// 检查各种暴击修饰符
		if (hasCritModifier(stack, MODIFIER_ABUSER)) {
			// 施虐者增加15%暴击率
			bonus.critChance += 0.15f;
			// 增加0.3倍暴击伤害
			bonus.critDamage += 0.3f;
		}

		if (hasCritModifier(stack, MODIFIER_SWIFT_BLADE)) {
			int level = getCritModifierLevel(stack, MODIFIER_SWIFT_BLADE);
			bonus.critChance += level * 0.05f;
			bonus.critDamage += level * 0.1f;
		}

		if (hasCritModifier(stack, MODIFIER_CAPTURE_KING)) {
			int level = getCritModifierLevel(stack, MODIFIER_CAPTURE_KING);
			// 每级8%对BOSS暴击率
			bonus.critChance += level * 0.08f;
		}

		if (hasCritModifier(stack, MODIFIER_DEATH_ECHO)) {
			// 濒死时增加20%暴击率
			bonus.critChance += 0.2f;
		}

		if (hasCritModifier(stack, MODIFIER_KILLING_RHYTHM)) {
			int level = getCritModifierLevel(stack, MODIFIER_KILLING_RHYTHM);
			bonus.critChance += level * 0.06f;
			bonus.critDamage += level * 0.15f;
		}

		return bonus;
	}

	/**
	 * 暴击加成数据类
	 */
	public static class CritBonus {
		public float critChance = 0.0f;
		public float critDamage = 0.0f;

		public CritBonus() {
		}

		public CritBonus(float critChance, float critDamage) {
			this.critChance = critChance;
			this.critDamage = critDamage;
		}
	}
}