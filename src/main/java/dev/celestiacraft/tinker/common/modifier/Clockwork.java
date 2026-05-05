package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import dev.celestiacraft.tinker.api.CombatUtils;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;
import dev.celestiacraft.tinker.entity.CritCalculator;

import java.util.Objects;

public class Clockwork extends BasicModifier {
	private static final float CRIT_CHANCE_PER_SPEED_LEVEL = 0.05f;
	private static final float CRIT_DAMAGE_PER_SPEED_LEVEL = 0.1f;
	private static final int MODIFIER_DURATION = 40;

	@Override
	public void onCriticalHit(Player player, LivingEntity target, CriticalHitEvent event, int level) {
		// 没有速度效果直接跳过
		if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
			return;
		}

		// 获取速度等级
		int speedLevel = player.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1;

		// 计算加成
		float critChanceBonus = speedLevel * CRIT_CHANCE_PER_SPEED_LEVEL;
		float critDamageBonus = speedLevel * CRIT_DAMAGE_PER_SPEED_LEVEL;

		// 添加临时暴击修饰符
		CritCalculator.addTempCritModifier(player, critChanceBonus, critDamageBonus, MODIFIER_DURATION);
		CombatUtils.spawnAbuserCritEffect(player);
	}
}