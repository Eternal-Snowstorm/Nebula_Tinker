package top.nebula.tinker.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;
import top.nebula.tinker.config.CommonConfig;
import top.nebula.tinker.common.register.attribute.GlobalCritAttributes;
import top.nebula.tinker.common.register.attribute.ModAttributes;
import top.nebula.tinker.utils.SimpleTConUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CritCalculator {

	// 暴击计算常量
	private static final float BASE_CRIT_MULTIPLIER = 1.5F;
	private static final float JUMP_CRIT_MULTIPLIER = 2.0F;

	// 临时暴击修饰符存储
	private static final ConcurrentHashMap<UUID, CritModifierData> TEMP_CRIT_MODIFIERS = new ConcurrentHashMap<>();

	// 临时暴击修饰符数据类
	private record CritModifierData(float critChanceBonus, float critDamageBonus, long expiryTime) {
		private CritModifierData(float critChanceBonus, float critDamageBonus, int expiryTime) {
			this(critChanceBonus, critDamageBonus, System.currentTimeMillis() + (expiryTime * 50L));
		}

		public boolean isExpired() {
			return System.currentTimeMillis() > expiryTime;
		}
	}

	/**
	 * 计算攻击是否触发暴击
	 */
	public static boolean shouldCrit(LivingEntity attacker, LivingEntity target, ItemStack weapon, boolean isJumpAttack) {
		if (attacker == null || target == null) {
			return false;
		}

		// 基础暴击率（从CommonConfig.CritSystem获取）
		float baseChance = isJumpAttack ?
				CommonConfig.CritSystem.JUMP_CRIT_CHANCE.get().floatValue() :
				CommonConfig.CritSystem.BASE_CRIT_CHANCE.get().floatValue();

		// 获取攻击方暴击率属性
		float attackerCritChance = getAttackerCritChance(attacker, weapon);

		// 获取目标暴击抵抗
		float targetCritResistance = getTargetCritResistance(target);

		// 计算最终暴击率
		float finalCritChance = baseChance + attackerCritChance - targetCritResistance;

		// 应用临时修饰符
		finalCritChance += getTempCritChanceBonus(attacker);

		// 确保在0-1范围内
		finalCritChance = Math.max(0, Math.min(finalCritChance, CommonConfig.CritSystem.MAX_CRIT_CHANCE.get().floatValue()));

		// 使用确定的随机数种子
		long seed = calculateCritSeed(attacker, target, weapon, isJumpAttack);
		RandomSource random = attacker.getRandom();
		random.setSeed(seed);

		return random.nextFloat() < finalCritChance;
	}

	/**
	 * 计算暴击伤害倍数
	 */
	public static float getCritDamageMultiplier(LivingEntity attacker, LivingEntity target, ItemStack weapon, boolean isJumpAttack) {
		if (attacker == null || target == null) {
			return isJumpAttack ? JUMP_CRIT_MULTIPLIER : BASE_CRIT_MULTIPLIER;
		}

		// 基础暴击倍数（从CommonConfig.CritSystem获取）
		float baseMultiplier = isJumpAttack ?
				CommonConfig.CritSystem.JUMP_CRIT_MULTIPLIER.get().floatValue() :
				CommonConfig.CritSystem.BASE_CRIT_MULTIPLIER.get().floatValue();

		// 获取攻击方暴击伤害
		float attackerCritDamage = getAttackerCritDamage(attacker, weapon);

		// 获取目标暴击伤害减免
		float targetCritDamageReduction = getTargetCritDamageReduction(target);

		// 计算最终暴击倍数
		float finalMultiplier = baseMultiplier + attackerCritDamage - targetCritDamageReduction;

		// 应用临时修饰符
		finalMultiplier += getTempCritDamageBonus(attacker);

		// 确保至少为1
		return Math.max(1.0f, Math.min(finalMultiplier,
				CommonConfig.CritSystem.MAX_CRIT_MULTIPLIER.get().floatValue()));
	}

	/**
	 * 获取攻击方暴击率
	 */
	private static float getAttackerCritChance(LivingEntity attacker, ItemStack weapon) {
		float critChance = 0.0f;

		// 1. 获取全局暴击率属性
		AttributeInstance globalCritChance = attacker.getAttribute(GlobalCritAttributes.GLOBAL_CRITICAL_CHANCE.get());
		if (globalCritChance != null) {
			critChance += (float) globalCritChance.getValue();
		}

		// 2. 获取自定义暴击率属性
		AttributeInstance customCritChance = attacker.getAttribute(ModAttributes.CRITICAL_CHANCE.get());
		if (customCritChance != null) {
			critChance += (float) customCritChance.getValue();
		}

		// 3. 检查武器上的暴击修饰符
		if (!weapon.isEmpty() && SimpleTConUtils.hasModifier(weapon, "critical_chance")) {
			int level = SimpleTConUtils.getModifierLevel(weapon, "critical_chance");
			critChance += level * 0.05f; // 每级增加5%暴击率
		}

		return Math.min(critChance, CommonConfig.CritSystem.MAX_CRIT_CHANCE.get().floatValue());
	}

	/**
	 * 获取攻击方暴击伤害
	 */
	private static float getAttackerCritDamage(LivingEntity attacker, ItemStack weapon) {
		float critDamage = 0.0f;

		// 1. 获取全局暴击伤害属性
		AttributeInstance globalCritDamage = attacker.getAttribute(GlobalCritAttributes.GLOBAL_CRITICAL_DAMAGE.get());
		if (globalCritDamage != null) {
			critDamage += (float) globalCritDamage.getValue();
		}

		// 2. 获取自定义暴击伤害属性
		AttributeInstance customCritDamage = attacker.getAttribute(ModAttributes.CRITICAL_DAMAGE.get());
		if (customCritDamage != null) {
			critDamage += (float) customCritDamage.getValue();
		}

		// 3. 检查武器上的暴击伤害修饰符
		if (!weapon.isEmpty() && SimpleTConUtils.hasModifier(weapon, "critical_damage")) {
			int level = SimpleTConUtils.getModifierLevel(weapon, "critical_damage");
			critDamage += level * 0.1f; // 每级增加0.1倍暴击伤害
		}

		return critDamage;
	}

	/**
	 * 获取目标暴击抵抗
	 */
	private static float getTargetCritResistance(LivingEntity target) {
		float resistance = 0.0f;

		AttributeInstance critResistance = target.getAttribute(GlobalCritAttributes.CRITICAL_RESISTANCE.get());
		if (critResistance != null) {
			resistance = (float) critResistance.getValue();
		}

		return Math.min(resistance, CommonConfig.CritSystem.MAX_CRIT_RESISTANCE.get().floatValue());
	}

	/**
	 * 获取目标暴击伤害减免
	 */
	private static float getTargetCritDamageReduction(LivingEntity target) {
		float reduction = 0.0f;

		AttributeInstance critDamageReduction = target.getAttribute(GlobalCritAttributes.CRITICAL_DAMAGE_REDUCTION.get());
		if (critDamageReduction != null) {
			reduction = (float) critDamageReduction.getValue();
		}

		return Math.min(reduction, CommonConfig.CritSystem.MAX_CRIT_DAMAGE_REDUCTION.get().floatValue());
	}

	/**
	 * 添加临时暴击修饰符
	 */
	public static void addTempCritModifier(LivingEntity entity, float critChanceBonus,
	                                       float critDamageBonus, int durationTicks) {
		if (entity == null) return;

		UUID entityId = entity.getUUID();
		TEMP_CRIT_MODIFIERS.put(entityId, new CritModifierData(critChanceBonus, critDamageBonus, durationTicks));
	}

	/**
	 * 获取临时暴击率加成
	 */
	private static float getTempCritChanceBonus(LivingEntity entity) {
		UUID entityId = entity.getUUID();
		CritModifierData data = TEMP_CRIT_MODIFIERS.get(entityId);

		if (data != null) {
			if (data.isExpired()) {
				TEMP_CRIT_MODIFIERS.remove(entityId);
				return 0.0f;
			}
			return data.critChanceBonus;
		}

		return 0.0f;
	}

	/**
	 * 获取临时暴击伤害加成
	 */
	private static float getTempCritDamageBonus(LivingEntity entity) {
		UUID entityId = entity.getUUID();
		CritModifierData data = TEMP_CRIT_MODIFIERS.get(entityId);

		if (data != null) {
			if (data.isExpired()) {
				TEMP_CRIT_MODIFIERS.remove(entityId);
				return 0.0f;
			}
			return data.critDamageBonus;
		}

		return 0.0f;
	}

	/**
	 * 清理过期临时修饰符
	 */
	public static void cleanupExpiredModifiers() {
		TEMP_CRIT_MODIFIERS.entrySet().removeIf(entry -> entry.getValue().isExpired());
	}

	/**
	 * 计算暴击随机数种子
	 */
	private static long calculateCritSeed(LivingEntity attacker, LivingEntity target,
	                                      ItemStack weapon, boolean isJumpAttack) {
		long seed = attacker.level().getGameTime();
		seed = seed * 31 + attacker.getUUID().hashCode();
		seed = seed * 31 + (target != null ? target.getUUID().hashCode() : 0);
		seed = seed * 31 + (weapon != null ? weapon.hashCode() : 0);
		seed = seed * 31 + (isJumpAttack ? 1 : 0);
		seed = seed * 31 + CommonConfig.CritSystem.RANDOM_SEED_MODIFIER.get();

		return seed;
	}

	/**
	 * 检查是否为跳劈
	 */
	public static boolean isJumpAttack(LivingEntity attacker) {
		if (!(attacker instanceof net.minecraft.world.entity.player.Player player)) {
			return false;
		}

		return player.fallDistance > 0.0F &&
				!player.onGround() &&
				!player.onClimbable() &&
				!player.isInWater() &&
				!player.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS) &&
				!player.isPassenger();
	}
}