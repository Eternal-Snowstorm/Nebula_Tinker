package dev.celestiacraft.tinker.api.attribute;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.*;

/**
 * <p>
 * Attribute 锁定管理器.
 * <p>
 * 用于在指定时间内强制锁定某个 Attribute 的 base value,
 * 并在持续时间结束后自动恢复原始数值.
 * <p>
 * 设计目标:
 * <ul>
 *     <li>同一实体同一 Attribute 只允许存在一个锁</li>
 *     <li>支持持续时间刷新</li>
 *     <li>自动恢复原始基础值</li>
 *     <li>服务端执行, 不产生客户端脏数据</li>
 * </ul>
 * <p>
 * 使用方式:
 * <pre>{@code
 * AttributeLockManager.lock(entity, attribute, value, duration);
 * }</pre>
 * <p>
 * 必须在 LivingTickEvent 中调用 {@link #tick(LivingEntity)}.
 */
public final class AttributeLockManager {

	private AttributeLockManager() {
	}

	/**
	 * UUID -> Attribute -> Lock
	 */
	private static final Map<UUID, Map<Attribute, AttributeLock>> LOCKS = new HashMap<>();

	/**
	 * 添加或刷新锁.
	 * <p>
	 * 如果该 Attribute 已存在锁, 则刷新持续时间并更新锁定值.
	 *
	 * @param entity    目标实体
	 * @param attribute 要锁定的属性
	 * @param value     锁定的 base value
	 * @param duration  持续时间, 单位 tick
	 */
	public static void lock(LivingEntity entity, Attribute attribute, double value, int duration) {
		if (entity.level().isClientSide()) {
			return;
		}

		UUID uuid = entity.getUUID();
		LOCKS.computeIfAbsent(uuid, (id) -> {
			return new HashMap<>();
		});

		Map<Attribute, AttributeLock> map = LOCKS.get(uuid);
		AttributeLock existing = map.get(attribute);

		if (existing != null) {
			existing.duration = duration;
			existing.value = value;
			return;
		}

		AttributeLock lock = new AttributeLock(entity, attribute, value, duration);
		map.put(attribute, lock);
	}

	/**
	 * 每 tick 更新锁.
	 * <p>
	 * 应在 LivingTickEvent 中调用.
	 *
	 * @param entity 当前实体
	 */
	public static void tick(LivingEntity entity) {
		if (entity.level().isClientSide()) {
			return;
		}

		UUID uuid = entity.getUUID();
		Map<Attribute, AttributeLock> map = LOCKS.get(uuid);

		if (map == null || map.isEmpty()) {
			return;
		}

		Iterator<Map.Entry<Attribute, AttributeLock>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			AttributeLock lock = iterator.next().getValue();
			if (lock.tick(entity)) {
				iterator.remove();
			}
		}

		if (map.isEmpty()) {
			LOCKS.remove(uuid);
		}
	}

	/**
	 * 清理实体的所有锁.
	 * <p>
	 * 通常在实体死亡或离开世界时调用.
	 *
	 * @param entity 目标实体
	 */
	public static void clear(LivingEntity entity) {
		if (entity.level().isClientSide()) {
			return;
		}

		UUID uuid = entity.getUUID();
		Map<Attribute, AttributeLock> map = LOCKS.remove(uuid);

		if (map == null) {
			return;
		}

		for (AttributeLock lock : map.values()) {
			lock.restore(entity);
		}
	}

	/**
	 * 内部锁结构.
	 * <p>
	 * 负责记录原始值, 并在持续时间结束时恢复.
	 */
	private static class AttributeLock {
		private final Attribute attribute;
		private final double originalBase;
		private double value;
		private int duration;

		/**
		 * 创建锁并立即覆盖 base value.
		 */
		private AttributeLock(LivingEntity entity, Attribute attribute, double value, int duration) {
			this.attribute = attribute;
			this.value = value;
			this.duration = duration;

			AttributeInstance instance = entity.getAttribute(attribute);
			if (instance != null) {
				this.originalBase = instance.getBaseValue();
				instance.setBaseValue(value);
			} else {
				this.originalBase = 0;
			}
		}

		/**
		 * 更新锁状态.
		 *
		 * @return true 表示锁已结束, 需要移除
		 */
		private boolean tick(LivingEntity entity) {
			if (duration-- <= 0) {
				restore(entity);
				return true;
			}

			AttributeInstance instance = entity.getAttribute(attribute);
			if (instance != null && instance.getBaseValue() != value) {
				instance.setBaseValue(value);
			}

			return false;
		}

		/**
		 * 恢复原始 base value.
		 */
		private void restore(LivingEntity entity) {
			AttributeInstance instance = entity.getAttribute(attribute);
			if (instance != null) {
				instance.setBaseValue(originalBase);
			}
		}
	}
}