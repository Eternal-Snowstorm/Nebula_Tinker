package top.nebula.nebula_tinker.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.config.CommonConfig;
import top.nebula.nebula_tinker.common.register.attribute.GlobalCritAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID)
public class CritCalculator {
	
	// 临时暴击修饰符的UUID（用于一次性效果）
	private static final UUID TEMP_CRIT_CHANCE_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
	private static final UUID TEMP_CRIT_DAMAGE_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
	private static final UUID TEMP_CRIT_RESISTANCE_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
	private static final UUID TEMP_CRIT_DAMAGE_REDUCTION_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174004");
	
	// 存储临时修饰符的到期时间
	private static final Map<UUID, Map<UUID, Long>> TEMP_MODIFIERS = new HashMap<>();
	
	/**
	 * 计算是否触发暴击
	 * @param attacker 攻击者
	 * @param target 目标
	 * @param baseCritChance 基础暴击率（来自物品、技能等）
	 * @return 是否暴击
	 */
	public static boolean shouldCrit(LivingEntity attacker, LivingEntity target, float baseCritChance) {
		if (attacker == null || target == null) {
			return false;
		}
		
		// 获取攻击者的暴击率
		double attackerCritChance = getCritChance(attacker);
		
		// 获取目标的暴击抵抗
		double targetCritResistance = getCritResistance(target);
		
		// 计算最终暴击率
		double finalCritChance = baseCritChance + attackerCritChance - targetCritResistance;
		
		// 确保在0-1范围内
		finalCritChance = Math.max(0, Math.min(CommonConfig.MAX_CRIT_RESISTANCE.get(), finalCritChance));
		
		// 随机判断
		return attacker.getRandom().nextFloat() < finalCritChance;
	}
	
	/**
	 * 计算暴击伤害倍数
	 * @param attacker 攻击者
	 * @param target 目标
	 * @param baseCritMultiplier 基础暴击倍数
	 * @return 最终暴击伤害倍数
	 */
	public static float getCritDamageMultiplier(LivingEntity attacker, LivingEntity target, float baseCritMultiplier) {
		if (attacker == null || target == null) {
			return baseCritMultiplier;
		}
		
		// 获取攻击者的暴击伤害
		double attackerCritDamage = getCritDamage(attacker);
		
		// 获取目标的暴击伤害减免
		double targetCritDamageReduction = getCritDamageReduction(target);
		
		// 计算最终暴击倍数
		float finalMultiplier = (float) (baseCritMultiplier + attackerCritDamage - targetCritDamageReduction);
		
		// 确保至少为1
		return Math.max(1.0f, finalMultiplier);
	}
	
	/**
	 * 获取实体的暴击率
	 */
	public static double getCritChance(LivingEntity entity) {
		if (entity == null) {
			return CommonConfig.BASE_CRIT_CHANCE.get();
		}
		
		AttributeInstance instance = entity.getAttribute(GlobalCritAttributes.GLOBAL_CRITICAL_CHANCE.get());
		return instance != null ? instance.getValue() : CommonConfig.BASE_CRIT_CHANCE.get();
	}
	
	/**
	 * 获取实体的暴击伤害
	 */
	public static double getCritDamage(LivingEntity entity) {
		if (entity == null) {
			return CommonConfig.BASE_CRIT_DAMAGE.get();
		}
		
		AttributeInstance instance = entity.getAttribute(GlobalCritAttributes.GLOBAL_CRITICAL_DAMAGE.get());
		return instance != null ? instance.getValue() : CommonConfig.BASE_CRIT_DAMAGE.get();
	}
	
	/**
	 * 获取实体的暴击抵抗
	 */
	public static double getCritResistance(LivingEntity entity) {
		if (entity == null) {
			return CommonConfig.BASE_CRIT_RESISTANCE.get();
		}
		
		AttributeInstance instance = entity.getAttribute(GlobalCritAttributes.CRITICAL_RESISTANCE.get());
		double value = instance != null ? instance.getValue() : CommonConfig.BASE_CRIT_RESISTANCE.get();
		return Math.min(value, CommonConfig.MAX_CRIT_RESISTANCE.get());
	}
	
	/**
	 * 获取实体的暴击伤害减免
	 */
	public static double getCritDamageReduction(LivingEntity entity) {
		if (entity == null) {
			return CommonConfig.BASE_CRIT_DAMAGE_REDUCTION.get();
		}
		
		AttributeInstance instance = entity.getAttribute(GlobalCritAttributes.CRITICAL_DAMAGE_REDUCTION.get());
		double value = instance != null ? instance.getValue() : CommonConfig.BASE_CRIT_DAMAGE_REDUCTION.get();
		return Math.min(value, CommonConfig.MAX_CRIT_DAMAGE_REDUCTION.get());
	}
	
	/**
	 * 为实体临时添加暴击率修饰符
	 */
	public static void addTempCritChanceModifier(LivingEntity entity, double amount, int durationTicks) {
		if (entity == null) return;
		
		Attribute attribute = GlobalCritAttributes.GLOBAL_CRITICAL_CHANCE.get();
		if (attribute != null) {
			addTempModifier(entity, attribute, TEMP_CRIT_CHANCE_UUID,
					"temp_crit_chance", amount, durationTicks);
		}
	}
	
	/**
	 * 为实体临时添加暴击伤害修饰符
	 */
	public static void addTempCritDamageModifier(LivingEntity entity, double amount, int durationTicks) {
		if (entity == null) return;
		
		Attribute attribute = GlobalCritAttributes.GLOBAL_CRITICAL_DAMAGE.get();
		if (attribute != null) {
			addTempModifier(entity, attribute, TEMP_CRIT_DAMAGE_UUID,
					"temp_crit_damage", amount, durationTicks);
		}
	}
	
	/**
	 * 为实体临时添加暴击抵抗修饰符
	 */
	public static void addTempCritResistanceModifier(LivingEntity entity, double amount, int durationTicks) {
		if (entity == null) return;
		
		Attribute attribute = GlobalCritAttributes.CRITICAL_RESISTANCE.get();
		if (attribute != null) {
			addTempModifier(entity, attribute, TEMP_CRIT_RESISTANCE_UUID,
					"temp_crit_resistance", amount, durationTicks);
		}
	}
	
	/**
	 * 为实体临时添加暴击伤害减免修饰符
	 */
	public static void addTempCritDamageReductionModifier(LivingEntity entity, double amount, int durationTicks) {
		if (entity == null) return;
		
		Attribute attribute = GlobalCritAttributes.CRITICAL_DAMAGE_REDUCTION.get();
		if (attribute != null) {
			addTempModifier(entity, attribute, TEMP_CRIT_DAMAGE_REDUCTION_UUID,
					"temp_crit_damage_reduction", amount, durationTicks);
		}
	}
	
	private static void addTempModifier(LivingEntity entity, Attribute attribute,
	                                    UUID uuid, String name, double amount, int durationTicks) {
		AttributeInstance instance = entity.getAttribute(attribute);
		if (instance != null) {
			// 移除旧的同名修饰符
			instance.removeModifier(uuid);
			
			// 添加新的修饰符
			AttributeModifier modifier = new AttributeModifier(
					uuid,
					"nebula_tinker_" + name,
					amount,
					AttributeModifier.Operation.ADDITION
			);
			instance.addTransientModifier(modifier);
			
			// 记录到期时间
			UUID entityId = entity.getUUID();
			long expiryTime = System.currentTimeMillis() + (durationTicks * 50); // 转换为毫秒
			
			TEMP_MODIFIERS.computeIfAbsent(entityId, k -> new HashMap<>())
					.put(uuid, expiryTime);
		}
	}
	
	/**
	 * 移除所有临时修饰符
	 */
	public static void removeAllTempModifiers(LivingEntity entity) {
		if (entity == null) return;
		
		AttributeInstance critChance = entity.getAttribute(GlobalCritAttributes.GLOBAL_CRITICAL_CHANCE.get());
		if (critChance != null) critChance.removeModifier(TEMP_CRIT_CHANCE_UUID);
		
		AttributeInstance critDamage = entity.getAttribute(GlobalCritAttributes.GLOBAL_CRITICAL_DAMAGE.get());
		if (critDamage != null) critDamage.removeModifier(TEMP_CRIT_DAMAGE_UUID);
		
		AttributeInstance critResistance = entity.getAttribute(GlobalCritAttributes.CRITICAL_RESISTANCE.get());
		if (critResistance != null) critResistance.removeModifier(TEMP_CRIT_RESISTANCE_UUID);
		
		AttributeInstance critDamageReduction = entity.getAttribute(GlobalCritAttributes.CRITICAL_DAMAGE_REDUCTION.get());
		if (critDamageReduction != null) critDamageReduction.removeModifier(TEMP_CRIT_DAMAGE_REDUCTION_UUID);
		
		// 从缓存中移除
		TEMP_MODIFIERS.remove(entity.getUUID());
	}
	
	/**
	 * 清理过期的临时修饰符
	 */
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.getServer() != null && event.getServer().getTickCount() % 20 == 0) {
			long currentTime = System.currentTimeMillis();
			
			// 复制一份避免并发修改
			Map<UUID, Map<UUID, Long>> tempModifiersCopy = new HashMap<>(TEMP_MODIFIERS);
			
			for (Map.Entry<UUID, Map<UUID, Long>> entityEntry : tempModifiersCopy.entrySet()) {
				UUID entityId = entityEntry.getKey();
				Map<UUID, Long> modifiers = entityEntry.getValue();
				
				// 检查每个修饰符是否过期
				modifiers.entrySet().removeIf(entry -> {
					if (entry.getValue() < currentTime) {
						// 查找对应的实体
						var player = event.getServer().getPlayerList().getPlayer(entityId);
						if (player != null) {
							// 移除过期的修饰符
							removeTempModifier(player, entry.getKey());
						}
						return true;
					}
					return false;
				});
				
				// 如果没有修饰符了，从主映射中移除
				if (modifiers.isEmpty()) {
					TEMP_MODIFIERS.remove(entityId);
				}
			}
		}
	}
	
	private static void removeTempModifier(LivingEntity entity, UUID modifierId) {
		if (entity == null) return;
		
		if (modifierId.equals(TEMP_CRIT_CHANCE_UUID)) {
			AttributeInstance instance = entity.getAttribute(GlobalCritAttributes.GLOBAL_CRITICAL_CHANCE.get());
			if (instance != null) instance.removeModifier(modifierId);
		} else if (modifierId.equals(TEMP_CRIT_DAMAGE_UUID)) {
			AttributeInstance instance = entity.getAttribute(GlobalCritAttributes.GLOBAL_CRITICAL_DAMAGE.get());
			if (instance != null) instance.removeModifier(modifierId);
		} else if (modifierId.equals(TEMP_CRIT_RESISTANCE_UUID)) {
			AttributeInstance instance = entity.getAttribute(GlobalCritAttributes.CRITICAL_RESISTANCE.get());
			if (instance != null) instance.removeModifier(modifierId);
		} else if (modifierId.equals(TEMP_CRIT_DAMAGE_REDUCTION_UUID)) {
			AttributeInstance instance = entity.getAttribute(GlobalCritAttributes.CRITICAL_DAMAGE_REDUCTION.get());
			if (instance != null) instance.removeModifier(modifierId);
		}
	}
}