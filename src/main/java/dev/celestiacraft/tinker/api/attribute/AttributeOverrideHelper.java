package dev.celestiacraft.tinker.api.attribute;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * <p>
 * 用于直接覆盖实体 Attribute 的 base value 的工具类.
 * <p>
 * 该工具不会创建 AttributeModifier,
 * 而是直接修改 AttributeInstance 的基础值.
 * <p>
 * 适用场景:
 * <ul>
 *     <li>强制锁定某个属性的基础数值</li>
 *     <li>实现属性禁用或压制效果</li>
 *     <li>在自定义系统中接管原版属性计算入口</li>
 * </ul>
 * <p>
 * 注意:
 * <ul>
 *     <li>该修改是直接写入 base value</li>
 *     <li>不会自动恢复原始值</li>
 *     <li>应配合 AttributeLockManager 使用</li>
 * </ul>
 */
public class AttributeOverrideHelper {
	private AttributeOverrideHelper() {
	}

	/**
	 * 设置指定 Attribute 的 base value.
	 *
	 * @param entity    目标实体
	 * @param attribute 要修改的属性
	 * @param value     新的基础数值
	 */
	public static void setBase(LivingEntity entity, Attribute attribute, double value) {
		AttributeInstance instance = entity.getAttribute(attribute);
		if (instance == null) {
			return;
		}

		instance.setBaseValue(value);
	}

	/**
	 * 设置实体的最大生命值. 
	 * <p>
	 * 同时会将当前生命值调整为新的最大生命值. 
	 *
	 * @param entity 目标实体
	 * @param value  新的最大生命值
	 */
	public static void setMaxHealth(LivingEntity entity, double value) {
		setBase(entity, Attributes.MAX_HEALTH, value);

		// 补满生命
		entity.setHealth(entity.getMaxHealth());
	}

	/**
	 * 设置实体的攻击力. 
	 *
	 * @param entity 目标实体
	 * @param value  新的攻击力基础值
	 */
	public static void setAttackDamage(LivingEntity entity, double value) {
		setBase(entity, Attributes.ATTACK_DAMAGE, value);
	}

	/**
	 * 设置实体的移动速度. 
	 * <p>
	 * 原版玩家默认约为 0.1. 
	 *
	 * @param entity 目标实体
	 * @param value  新的移动速度基础值
	 */
	public static void setMovementSpeed(LivingEntity entity, double value) {
		setBase(entity, Attributes.MOVEMENT_SPEED, value);
	}

	/**
	 * 设置实体的护甲值. 
	 *
	 * @param entity 目标实体
	 * @param value  新的护甲基础值
	 */
	public static void setArmor(LivingEntity entity, double value) {
		setBase(entity, Attributes.ARMOR, value);
	}

	/**
	 * 设置实体的护甲韧性. 
	 *
	 * @param entity 目标实体
	 * @param value  新的护甲韧性基础值
	 */
	public static void setArmorToughness(LivingEntity entity, double value) {
		setBase(entity, Attributes.ARMOR_TOUGHNESS, value);
	}
}