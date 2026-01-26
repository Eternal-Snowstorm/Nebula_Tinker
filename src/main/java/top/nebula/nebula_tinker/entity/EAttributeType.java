package top.nebula.nebula_tinker.entity;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.register.attribute.ModAttributes;
import top.nebula.nebula_tinker.common.register.attribute.GlobalCritAttributes;

import java.util.*;

/**
 * 神魔化效果属性类型枚举
 * 定义所有可用的正面和负面属性
 */
public enum EAttributeType {
	// 正面属性

		// 通用战斗属性
		ATTACK_DAMAGE(
				Attributes.ATTACK_DAMAGE,
				0.5,
				setTranKey("attack_damage"),
				EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND),
				AttributeCategory.COMBAT,
				10,
				2
		),
		ATTACK_SPEED(
				Attributes.ATTACK_SPEED,
				0.1,
				setTranKey("attack_speed"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.COMBAT,
				12,
				2
		),
		CRITICAL_CHANCE(
				null,
				0.05,
				setTranKey("critical_chance"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.COMBAT,
				8,
				3
		),
		// 暴击伤害倍数增加
		CRITICAL_DAMAGE(
				null,
				0.2,
				setTranKey("critical_damage"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.COMBAT,
				7,
				3
		),
		
		// 元素伤害属性
		FIRE_ASPECT(
				null,
				2.0,
				setTranKey("fire_aspect"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.ELEMENTAL,
				6,
				2
		),
		FROST_ASPECT(
				null,
				2.0,
				setTranKey("frost_aspect"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.ELEMENTAL,
				6,
				2
		),
		LIGHTNING_ASPECT(
				null,
				2.0,
				setTranKey("lightning_aspect"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.ELEMENTAL,
				6,
				2
		),
		
		// 远程属性
		DRAW_SPEED(
				null,
				0.15,
				setTranKey("draw_speed"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.RANGED,
				10,
				2
		),
		ARROW_SPEED(
				null,
				0.2,
				setTranKey("arrow_speed"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.RANGED,
				8,
				2
		),
		ARROW_ACCURACY(
				null,
				0.1,
				setTranKey("arrow_accuracy"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.RANGED,
				9,
				1
		),
		PROJECTILE_DAMAGE(
				null,
				0.3,
				setTranKey("projectile_damage"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.RANGED,
				7,
				2
		),
		
		// 工具属性
		MINING_SPEED(
				null,
				0.5,
				setTranKey("mining_speed"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.TOOL,
				15,
				1
		),
		DURABILITY(
				null,
				100.0,
				setTranKey("durability"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.TOOL,
				12,
				1
		),
		HARVEST_LEVEL(
				null,
				1.0,
				setTranKey("harvest_level"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.TOOL,
				5,
				3
		),
		EFFICIENCY(
				null,
				0.2,
				setTranKey("efficiency"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.TOOL,
				10,
				2
		),
		
		// 防御属性
		ARMOR(
				Attributes.ARMOR,
				2.0,
				setTranKey("armor"),
				EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
				AttributeCategory.DEFENSE,
				14,
				1
		),
		MAX_HEALTH(
				Attributes.MAX_HEALTH,
				4.0,
				setTranKey("max_health"),
				EnumSet.of(EquipmentSlot.CHEST),
				AttributeCategory.DEFENSE,
				6,
				3
		),
		ARMOR_TOUGHNESS(
				Attributes.ARMOR_TOUGHNESS,
				1.0,
				setTranKey("armor_toughness"),
				EnumSet.of(EquipmentSlot.CHEST),
				AttributeCategory.DEFENSE,
				4,
				3
		),
		
		// 移动属性（整合为1个）
		MOVEMENT_SPEED(
				Attributes.MOVEMENT_SPEED,
				0.12,
				setTranKey("movement_speed"),
				EnumSet.of(EquipmentSlot.FEET),
				AttributeCategory.UTILITY,
				10,
				2
		),
		
		KNOCKBACK_RESISTANCE(
				Attributes.KNOCKBACK_RESISTANCE,
				0.15,
				setTranKey("knockback_resistance"),
				EnumSet.of(EquipmentSlot.CHEST),
				AttributeCategory.DEFENSE,
				8,
				2
		),
		
		// 特殊防御属性
		FEATHER_FALLING(
				null,
				2.0,
				setTranKey("feather_falling"),
				EnumSet.of(EquipmentSlot.FEET),
				AttributeCategory.DEFENSE,
				12,
				2
		),
		PROTECTION(
				null,
				1.0,
				setTranKey("protection"),
				EnumSet.allOf(EquipmentSlot.class),
				AttributeCategory.DEFENSE,
				10,
				2
		),
		
		// 全局暴击属性（新增）
		GLOBAL_CRITICAL_CHANCE(
				null,
				0.05,
				setTranKey("global_critical_chance"),
				EnumSet.allOf(EquipmentSlot.class),
				AttributeCategory.COMBAT,
				5,
				3
		),
		
		GLOBAL_CRITICAL_DAMAGE(
				null,
				0.5,
				setTranKey("global_critical_damage"),
				EnumSet.allOf(EquipmentSlot.class),
				AttributeCategory.COMBAT,
				4,
				3
		),
		
		CRITICAL_RESISTANCE(
				null,
				0.05,
				setTranKey("critical_resistance"),
				EnumSet.allOf(EquipmentSlot.class),
				AttributeCategory.DEFENSE,
				6,
				2
		),
		
		CRITICAL_DAMAGE_REDUCTION(
				null,
				0.1,
				setTranKey("critical_damage_reduction"),
				EnumSet.allOf(EquipmentSlot.class),
				AttributeCategory.DEFENSE,
				5,
				2
		),
		
		// ========== 负面属性 ==========
		
		// 血量降低（整合为一个）
		HEALTH_REDUCTION(
				Attributes.MAX_HEALTH,
				-3.0,
				setTranKey("health_reduction"),
				EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
				AttributeCategory.NEGATIVE,
				8,
				2
		),
		
		// 护甲降低（整合为一个）
		ARMOR_REDUCTION(
				Attributes.ARMOR,
				-1.5,
				setTranKey("armor_reduction"),
				EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND),
				AttributeCategory.NEGATIVE,
				7,
				2
		),
		
		// 移动速度降低（整合为一个）
		MOVEMENT_SLOW(
				Attributes.MOVEMENT_SPEED,
				-0.08,
				setTranKey("movement_slow"),
				EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND),
				AttributeCategory.NEGATIVE,
				6,
				2
		),
		
		// 攻击速度降低（整合为一个）
		ATTACK_SPEED_REDUCTION(
				Attributes.ATTACK_SPEED,
				-0.08,
				setTranKey("attack_speed_reduction"),
				EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
				AttributeCategory.NEGATIVE,
				5,
				2
		),
		
		// 攻击伤害降低（整合为一个）
		ATTACK_DAMAGE_REDUCTION(
				Attributes.ATTACK_DAMAGE,
				-0.4,
				setTranKey("attack_damage_reduction"),
				EnumSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
				AttributeCategory.NEGATIVE,
				4,
				2
		),
		
		// 暴击几率降低（整合为一个）
		CRITICAL_REDUCTION(
				null,
				-0.04,
				setTranKey("critical_reduction"),
				EnumSet.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET),
				AttributeCategory.NEGATIVE,
				3,
				2
		),
		
		// 工具负面属性
		MINING_SPEED_REDUCTION(
				null,
				-0.3,
				setTranKey("mining_speed_reduction"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.NEGATIVE,
				10,
				2
		),
		
		DURABILITY_REDUCTION(
				null,
				-75.0,
				setTranKey("durability_reduction"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.NEGATIVE,
				8,
				2
		),
		
		HARVEST_LEVEL_REDUCTION(
				null,
				-0.7,
				setTranKey("harvest_level_reduction"),
				EnumSet.of(EquipmentSlot.MAINHAND),
				AttributeCategory.NEGATIVE,
				3,
				2
		);
	
	// 属性分类
	public enum AttributeCategory {
		// 战斗属性
		COMBAT,
		// 元素属性
		ELEMENTAL,
		// 远程属性
		RANGED,
		// 工具属性
		TOOL,
		// 防御属性
		DEFENSE,
		// 通用属性
		UTILITY,
		// 负面属性（整合后）
		NEGATIVE
	}
	
	private final Attribute attribute;
	private final double baseValue;
	private final String translationKey;
	private final Set<EquipmentSlot> applicableSlots;
	private final AttributeCategory category;
	// 权重，用于随机选择
	private final int weight;
	// 分级，用于稀有度系统
	private final int tier;
	
	EAttributeType(Attribute attribute, double baseValue, String translationKey, Set<EquipmentSlot> applicableSlots, AttributeCategory category) {
		this(attribute, baseValue, translationKey, applicableSlots, category, 10, 1);
	}
	
	EAttributeType(Attribute attribute, double baseValue, String translationKey, Set<EquipmentSlot> applicableSlots, AttributeCategory category, int weight, int tier) {
		this.attribute = attribute;
		this.baseValue = baseValue;
		this.translationKey = translationKey;
		this.applicableSlots = applicableSlots;
		this.category = category;
		this.weight = weight;
		this.tier = tier;
	}
	
	public Attribute getAttribute() {
		if (this.attribute != null) {
			return this.attribute;
		}
		
		// 对于自定义属性，从ModAttributes获取
		try {
			Attribute customAttr = ModAttributes.getCustomAttribute(this.name());
			if (customAttr != null) {
				return customAttr;
			}
			
			// 检查是否是全局暴击属性
			switch (this) {
				case GLOBAL_CRITICAL_CHANCE:
					return GlobalCritAttributes.GLOBAL_CRITICAL_CHANCE.get();
				case GLOBAL_CRITICAL_DAMAGE:
					return GlobalCritAttributes.GLOBAL_CRITICAL_DAMAGE.get();
				case CRITICAL_RESISTANCE:
					return GlobalCritAttributes.CRITICAL_RESISTANCE.get();
				case CRITICAL_DAMAGE_REDUCTION:
					return GlobalCritAttributes.CRITICAL_DAMAGE_REDUCTION.get();
			}
			
			// 如果获取失败，记录警告并返回一个安全的占位符属性
			NebulaTinker.LOGGER.warn("Custom attribute {} not found, using placeholder", this.name());
			return Attributes.MAX_HEALTH; // 使用一个安全的默认属性
		} catch (Exception exception) {
			NebulaTinker.LOGGER.error("Error getting custom attribute {}: {}", this.name(), exception.getMessage(), exception);
			return Attributes.MAX_HEALTH; // 使用一个安全的默认属性
		}
	}
	
	static String setTranKey(String key) {
		return String.format("attribute.modifier.nebula_tinker.%s", key);
	}
	
	public double getBaseValue() {
		return baseValue;
	}
	
	public String getTranslationKey() {
		return translationKey;
	}
	
	public Set<EquipmentSlot> getApplicableSlots() {
		return applicableSlots;
	}
	
	public AttributeCategory getCategory() {
		return category;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public int getTier() {
		return tier;
	}
	
	public boolean isNegative() {
		return category == AttributeCategory.NEGATIVE;
	}
	
	/**
	 * 获取适用于指定装备槽位的属性类型
	 */
	public static List<EAttributeType> getApplicableAttributes(EquipmentSlot slot, boolean includeNegative) {
		List<EAttributeType> result = new ArrayList<>();
		for (EAttributeType type : values()) {
			if (type.applicableSlots.contains(slot) && (includeNegative || !type.isNegative())) {
				result.add(type);
			}
		}
		return result;
	}
	
	/**
	 * 检查两个属性是否冲突
	 */
	public static boolean areConflicting(EAttributeType type1, EAttributeType type2) {
		// 相同的基础属性冲突（如增加攻击力和减少攻击力）
		if (type1.getAttribute() != null && type1.getAttribute().equals(type2.getAttribute())) {
			// 如果都是负面的或都是正面的，则不冲突（允许叠加）
			// 但如果一个是负面一个是正面，则冲突
			return (type1.isNegative() && !type2.isNegative()) || (!type1.isNegative() && type2.isNegative());
		}
		
		// 移动速度增加和减少冲突
		if ((type1 == MOVEMENT_SPEED && type2 == MOVEMENT_SLOW) ||
				    (type1 == MOVEMENT_SLOW && type2 == MOVEMENT_SPEED)) {
			return true;
		}
		
		// 攻击伤害增加和减少冲突
		if ((type1 == ATTACK_DAMAGE && type2 == ATTACK_DAMAGE_REDUCTION) ||
				    (type1 == ATTACK_DAMAGE_REDUCTION && type2 == ATTACK_DAMAGE)) {
			return true;
		}
		
		// 攻击速度增加和减少冲突
		if ((type1 == ATTACK_SPEED && type2 == ATTACK_SPEED_REDUCTION) ||
				    (type1 == ATTACK_SPEED_REDUCTION && type2 == ATTACK_SPEED)) {
			return true;
		}
		
		// 暴击几率增加和减少冲突
		if ((type1 == CRITICAL_CHANCE && type2 == CRITICAL_REDUCTION) ||
				    (type1 == CRITICAL_REDUCTION && type2 == CRITICAL_CHANCE)) {
			return true;
		}
		
		// 暴击伤害增加和减少冲突
		if ((type1 == CRITICAL_DAMAGE && type2 == CRITICAL_DAMAGE_REDUCTION) ||
				    (type1 == CRITICAL_DAMAGE_REDUCTION && type2 == CRITICAL_DAMAGE)) {
			return true;
		}
		
		return false;
	}
}