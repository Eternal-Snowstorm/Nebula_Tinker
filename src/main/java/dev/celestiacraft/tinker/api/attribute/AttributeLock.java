package dev.celestiacraft.tinker.api.attribute;

import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * 简易 Attribute 锁数据结构. 
 * <p>
 * 仅记录属性、锁定值与持续时间, 
 * 不负责自动恢复或实体操作. 
 * <p>
 * 通常用于轻量级逻辑或数据传输. 
 */
public class AttributeLock {
	/**
	 * 被锁定的属性
	 */
	public final Attribute attribute;

	/**
	 * 锁定的数值
	 */
	public final double value;

	/**
	 * 剩余持续时间, 单位 tick
	 */
	public int duration;

	/**
	 * 创建一个简单 Attribute 锁. 
	 *
	 * @param attribute 属性
	 * @param value     锁定值
	 * @param duration  持续时间
	 */
	public AttributeLock(Attribute attribute, double value, int duration) {
		this.attribute = attribute;
		this.value = value;
		this.duration = duration;
	}

	/**
	 * 递减持续时间. 
	 *
	 * @return true 表示锁已结束
	 */
	public boolean tick() {
		if (duration > 0) duration--;
		return duration == 0;
	}
}