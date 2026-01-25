package top.nebula.nebula_tinker.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import top.nebula.nebula_tinker.common.modifier.Demonization;
import top.nebula.nebula_tinker.common.modifier.Divinization;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeApplicator {
	private static final Map<UUID, Map<EquipmentSlot, List<AppliedModifier>>> appliedModifiers = new ConcurrentHashMap<>();

	// 新增：用于清理过期缓存
	private static final Map<UUID, Long> lastAccessTime = new ConcurrentHashMap<>();
	private static final long CACHE_TIMEOUT = 60000; // 60秒

	/**
	 * 为实体应用属性
	 */
	public static void applyAttributes(LivingEntity entity, List<Demonization.AttributeEntry> attributes, ItemStack stack, String modifierId) {
		if (entity == null || attributes == null || attributes.isEmpty()) {
			return;
		}

		UUID entityId = entity.getUUID();
		Map<EquipmentSlot, List<AppliedModifier>> entityModifiers =
				appliedModifiers.computeIfAbsent(entityId, (uuid) -> {
					return new ConcurrentHashMap<>();
				});

		for (Demonization.AttributeEntry entry : attributes) {
			EAttributeType type = entry.type();
			double value = entry.value();
			EquipmentSlot slot = entry.slot();

			// 跳过没有映射到游戏属性的自定义属性
			if (type.getAttribute() == null) {
				continue;
			}

			// 生成唯一的UUID
			UUID modifierUuid = UUID.nameUUIDFromBytes((stack.getDescriptionId() + type.name() + modifierId + slot.getName()).getBytes());

			// 应用属性修饰符
			AttributeInstance attributeInstance = entity.getAttribute(type.getAttribute());
			if (attributeInstance != null) {
				// 移除旧的相同UUID的修饰符
				attributeInstance.removeModifier(modifierUuid);

				// 确定操作类型（加法或乘法）
				AttributeModifier.Operation operation = AttributeModifier.Operation.ADDITION;

				// 对于某些属性使用乘法操作
				if (type.name().contains("SPEED") && !type.isNegative()) {
					operation = AttributeModifier.Operation.MULTIPLY_TOTAL;
				}

				// 添加新的修饰符
				AttributeModifier modifier = new AttributeModifier(
						modifierUuid,
						"nebula_tinker_" + modifierId + "_" + type.name().toLowerCase(),
						value,
						operation
				);
				attributeInstance.addPermanentModifier(modifier);

				// 记录已应用的修饰符
				AppliedModifier appliedModifier = new AppliedModifier(
						modifierUuid, type.getAttribute(), slot, modifierId, type, value
				);
				List<AppliedModifier> slotModifiers = entityModifiers
						.computeIfAbsent(slot, (slot1) -> {
							return new ArrayList<>();
						});
				slotModifiers.add(appliedModifier);
			}
		}

		// 更新最后访问时间
		lastAccessTime.put(entityId, System.currentTimeMillis());
	}

	/**
	 * 移除指定槽位的所有属性
	 */
	public static void removeAttributes(Player player, EquipmentSlot slot) {
		UUID playerId = player.getUUID();
		if (!appliedModifiers.containsKey(playerId)) {
			return;
		}

		Map<EquipmentSlot, List<AppliedModifier>> playerModifiers = appliedModifiers.get(playerId);
		if (!playerModifiers.containsKey(slot)) {
			return;
		}

		List<AppliedModifier> slotModifiers = playerModifiers.get(slot);
		for (AppliedModifier modifier : slotModifiers) {
			AttributeInstance attributeInstance = player.getAttribute(modifier.attribute);
			if (attributeInstance != null) {
				attributeInstance.removeModifier(modifier.uuid);
			}
		}

		// 从缓存中移除
		playerModifiers.remove(slot);
		if (playerModifiers.isEmpty()) {
			appliedModifiers.remove(playerId);
			lastAccessTime.remove(playerId);
		}
	}

	/**
	 * 移除玩家的所有属性
	 */
	public static void removeAllAttributes(Player player) {
		UUID playerId = player.getUUID();
		if (!appliedModifiers.containsKey(playerId)) {
			return;
		}

		Map<EquipmentSlot, List<AppliedModifier>> playerModifiers = appliedModifiers.get(playerId);
		for (Map.Entry<EquipmentSlot, List<AppliedModifier>> entry : playerModifiers.entrySet()) {
			for (AppliedModifier modifier : entry.getValue()) {
				AttributeInstance attributeInstance = player.getAttribute(modifier.attribute);
				if (attributeInstance != null) {
					attributeInstance.removeModifier(modifier.uuid);
				}
			}
		}

		appliedModifiers.remove(playerId);
		lastAccessTime.remove(playerId);
	}

	// 检查指定槽位是否有特定修饰符的属性
	public static boolean hasModifierAttributesInSlot(Player player, EquipmentSlot slot, String modifierId) {
		UUID playerId = player.getUUID();
		if (!appliedModifiers.containsKey(playerId)) {
			return false;
		}

		Map<EquipmentSlot, List<AppliedModifier>> playerModifiers = appliedModifiers.get(playerId);
		if (!playerModifiers.containsKey(slot)) {
			return false;
		}

		List<AppliedModifier> slotModifiers = playerModifiers.get(slot);
		for (AppliedModifier modifier : slotModifiers) {
			if (modifier.modifierId.equals(modifierId)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 清理过期缓存
	 */
	public static void cleanupExpiredCache() {
		long currentTime = System.currentTimeMillis();
		List<UUID> toRemove = new ArrayList<>();

		for (Map.Entry<UUID, Long> entry : lastAccessTime.entrySet()) {
			if (currentTime - entry.getValue() > CACHE_TIMEOUT) {
				toRemove.add(entry.getKey());
			}
		}

		for (UUID playerId : toRemove) {
			appliedModifiers.remove(playerId);
			lastAccessTime.remove(playerId);
		}
	}

	/**
	 * 获取物品的属性描述
	 */
	public static List<Component> getAttributeTooltips(ItemStack stack, Player player) {
		List<Component> tooltips = new ArrayList<>();

		if (stack.isEmpty() || player == null) {
			return tooltips;
		}

		// 检查是否有魔化或神化修饰符
		boolean hasDemonization = SimpleTConUtils.hasModifier(stack,
				top.nebula.nebula_tinker.NebulaTinker.loadResource("demonization").toString());
		boolean hasDivinization = SimpleTConUtils.hasModifier(stack,
				top.nebula.nebula_tinker.NebulaTinker.loadResource("divinization").toString());

		if (!hasDemonization && !hasDivinization) {
			return tooltips;
		}

		// 获取属性
		if (hasDemonization) {
			Demonization.AttributePack attributes = Demonization.getOrGenerateAttributes(stack, player);

			// 添加正面属性
			if (attributes.positive() != null && !attributes.positive().isEmpty()) {
				tooltips.add(Component.translatable("tooltip.nebula_tinker.demonization.title").withStyle(ChatFormatting.GOLD));
				for (Demonization.AttributeEntry entry : attributes.positive()) {
					tooltips.add(Component.literal("  §a" + getAttributeDisplay(entry)).withStyle(ChatFormatting.GREEN));
				}
			}

			// 添加负面属性
			if (attributes.negative() != null && !attributes.negative().isEmpty()) {
				tooltips.add(Component.translatable("tooltip.nebula_tinker.negative.title").withStyle(ChatFormatting.RED));
				for (Demonization.AttributeEntry entry : attributes.negative()) {
					tooltips.add(Component.literal("  §c" + getAttributeDisplay(entry)).withStyle(ChatFormatting.RED));
				}
			}
		} else {
			List<Divinization.AttributeEntry> attributes = Divinization.getOrGenerateAttributes(stack, player);

			if (!attributes.isEmpty()) {
				tooltips.add(Component.translatable("tooltip.nebula_tinker.divinization.title").withStyle(ChatFormatting.AQUA));
				for (Divinization.AttributeEntry entry : attributes) {
					tooltips.add(Component.literal("  §b" + getAttributeDisplay(entry)).withStyle(ChatFormatting.AQUA));
				}
			}
		}

		return tooltips;
	}

	/**
	 * 获取属性显示字符串
	 */
	private static String getAttributeDisplay(Demonization.AttributeEntry entry) {
		String displayName = getAttributeDisplayName(entry.type());
		String formattedValue = formatAttributeValue(entry.type(), entry.value());
		return String.format("%s §f%s", displayName, formattedValue);
	}

	private static String getAttributeDisplay(Divinization.AttributeEntry entry) {
		String displayName = getAttributeDisplayName(entry.type());
		String formattedValue = formatAttributeValue(entry.type(), entry.value());
		return String.format("%s §f%s", displayName, formattedValue);
	}

	/**
	 * 获取属性显示名称
	 */
	private static String getAttributeDisplayName(EAttributeType type) {
		String key = "attribute.nebula_tinker." + type.name().toLowerCase();
		return Component.translatable(key).getString();
	}


	/**
	 * 格式化属性值
	 */
	private static String formatAttributeValue(EAttributeType type, double value) {
		// 根据属性类型格式化显示
		if (type == EAttributeType.CRITICAL_CHANCE ||
				type == EAttributeType.CRITICAL_REDUCTION ||
				type == EAttributeType.ARROW_ACCURACY ||
				type == EAttributeType.GLOBAL_CRITICAL_CHANCE ||
				type == EAttributeType.CRITICAL_RESISTANCE) {
			// 百分比显示
			return String.format("%+.1f%%", value * 100);
		} else if (type == EAttributeType.CRITICAL_DAMAGE ||
				type == EAttributeType.CRITICAL_DAMAGE_REDUCTION ||
				type == EAttributeType.GLOBAL_CRITICAL_DAMAGE) {
			// 暴击伤害倍数显示
			return String.format("%+.1f倍", value);
		} else if (type == EAttributeType.FEATHER_FALLING) {
			// 整数值
			return String.format("+%.0f格", value);
		} else if (type == EAttributeType.DURABILITY ||
				type == EAttributeType.DURABILITY_REDUCTION) {
			// 整数值
			return String.format("%+.0f", value);
		} else if (type == EAttributeType.HARVEST_LEVEL ||
				type == EAttributeType.HARVEST_LEVEL_REDUCTION) {
			// 整数值
			return String.format("%+.0f", value);
		} else if (type == EAttributeType.MOVEMENT_SPEED ||
				type == EAttributeType.MOVEMENT_SLOW) {
			// 百分比显示
			return String.format("%+.1f%%", value * 100);
		} else if (type == EAttributeType.ATTACK_SPEED ||
				type == EAttributeType.ATTACK_SPEED_REDUCTION) {
			// 攻击速度特殊格式
			return String.format("%+.2f", value);
		} else if (type.getCategory() == EAttributeType.AttributeCategory.ELEMENTAL) {
			// 元素伤害：显示秒数
			return String.format("%+.1f秒", Math.abs(value) / 2);
		} else if (type == EAttributeType.PROTECTION) {
			// 保护
			return String.format("+%.1f", value);
		} else if (type == EAttributeType.DRAW_SPEED ||
				type == EAttributeType.ARROW_SPEED ||
				type == EAttributeType.PROJECTILE_DAMAGE ||
				type == EAttributeType.EFFICIENCY) {
			// 远程属性
			return String.format("%+.1f", value);
		} else if (type == EAttributeType.ARMOR_TOUGHNESS || type == EAttributeType.KNOCKBACK_RESISTANCE) {
			// 护甲韧性和击退抗性
			return String.format("%+.1f", value);
		} else {
			// 默认格式
			return String.format("%+.1f", value);
		}
	}

	/**
	 * 已应用的修饰符记录（扩展）
	 */
	private record AppliedModifier(
			UUID uuid,
			Attribute attribute,
			EquipmentSlot slot,
			String modifierId,
			EAttributeType EAttributeType,
			double value
	) {
	}
}