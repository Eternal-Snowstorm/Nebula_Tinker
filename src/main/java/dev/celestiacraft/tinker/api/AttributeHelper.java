package dev.celestiacraft.tinker.api;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

/**
 * <p>
 * 一个极简的 Attribute 临时修改工具类, 用于在运行时
 * 为实体动态添加 / 移除属性加成. 
 * <p>
 * 设计目标: 
 * <ul>
 *     <li>不关心"词条 / 状态"的概念</li>
 *     <li>调用方完全控制加成内容</li>
 *     <li>支持直接加值或百分比加成</li>
 *     <li>可随时安全移除</li>
 * </ul>
 * <p>
 * 内部通过 Attribute => 稳定 UUID 映射,
 * 保证同一 Attribute 的加成不会重复叠加,
 * 且 {@link #remove(LivingEntity, Attribute)} 一定能正确移除. 
 *
 * <h2>使用示例</h2>
 *
 * <pre>{@code
 * // 条件成立时添加加成
 * if (active) {
 *     AttributeHelper.apply(
 *             player,
 *             Attributes.ATTACK_DAMAGE,
 *             0.3, // +30%
 *             AttributeHelper.AdditionType.MULTIPLY_TOTAL
 *     );
 * } else {
 *     // 条件不成立时移除加成
 *     AttributeHelper.remove(
 *             player,
 *             Attributes.ATTACK_DAMAGE
 *     );
 * }
 * }</pre>
 *
 * <p>
 * 注意: 
 * <ul>
 *     <li>该工具使用 {@code addTransientModifier}, 加成不会被存档</li>
 *     <li>实体重载 / 重新登录后加成会自动消失</li>
 *     <li>同一 Attribute 同一时间只允许存在一个加成</li>
 * </ul>
 */
public class AttributeHelper {
	/**
	 * 根据 Attribute 生成稳定的 UUID. 
	 * <p>
	 * 同一 Attribute 在任何时间都会生成相同的 UUID,
	 * 用于保证: 
	 * <ul>
	 *     <li>加成不会重复叠加</li>
	 *     <li>移除时可以精确匹配</li>
	 * </ul>
	 */
	private static UUID uuid(Attribute attribute) {
		return UUID.nameUUIDFromBytes(("attribute_helper:" + attribute.getDescriptionId()).getBytes());
	}

	/**
	 * 属性加成类型
	 */
	public enum AdditionType {
		/**
		 * 直接数值加成(ADDITION)
		 * <p>
		 * 示例: 
		 * <pre>{@code
		 * +4 攻击力
		 * }</pre>
		 */
		ADDITION,
		/**
		 * 百分比加成(MULTIPLY_TOTAL)
		 * <p>
		 * 示例: 
		 * <pre>{@code
		 * +0.3 => 最终值 × 1.3
		 * }</pre>
		 */
		MULTIPLY_TOTAL
	}

	/**
	 * 为指定实体添加一个属性加成. 
	 * <p>
	 * 若该 Attribute 已存在由 AttributeHelper 添加的加成,
	 * 则不会重复添加. 
	 *
	 * @param entity    目标实体
	 * @param attribute 要修改的属性
	 * @param value     加成数值
	 * @param type      加成类型(直接加值 / 百分比)
	 */
	public static void apply(LivingEntity entity, Attribute attribute, double value, AdditionType type) {
		AttributeInstance instance = entity.getAttribute(attribute);
		if (instance == null) {
			return;
		}

		UUID uuid = uuid(attribute);
		if (instance.getModifier(uuid) != null) {
			return;
		}

		AttributeModifier.Operation operation = type == AdditionType.ADDITION
				? AttributeModifier.Operation.ADDITION
				: AttributeModifier.Operation.MULTIPLY_TOTAL;

		instance.addTransientModifier(new AttributeModifier(
				uuid,
				String.format("attribute_helper_%s", attribute.getDescriptionId()),
				value,
				operation
		));
	}

	/**
	 * 移除由 AttributeHelper 添加的属性加成. 
	 * <p>
	 * 若该 Attribute 当前没有对应加成, 则该方法不会产生任何影响. 
	 *
	 * @param entity    目标实体
	 * @param attribute 要移除加成的属性
	 */
	public static void remove(LivingEntity entity, Attribute attribute) {
		AttributeInstance instance = entity.getAttribute(attribute);
		if (instance == null) {
			return;
		}

		instance.removeModifier(uuid(attribute));
	}
}