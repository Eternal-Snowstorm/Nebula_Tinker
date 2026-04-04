package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import dev.celestiacraft.tinker.api.CombatUtils;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;

public class DeathEcho extends BasicModifier {
	private static final double LIFE_THRESHOLD = 0.20;
	private static final float CRIT_MULTIPLIER = 1.5F;
	private static final double TRIGGER_PROBABILITY = 0.15;

	@Override
	public void onCriticalHit(Player player, LivingEntity entity, CriticalHitEvent event, int level) {
		// 玩家低血量才触发
		if (player.getHealth() > player.getMaxHealth() * LIFE_THRESHOLD) {
			return;
		}

		// 概率判定
		if (Math.random() >= TRIGGER_PROBABILITY) {
			return;
		}

		// 暴击
		CombatUtils.spawnAbuserCritEffect(player);
		event.setResult(Event.Result.ALLOW);
		event.setDamageModifier(CRIT_MULTIPLIER);
	}
}