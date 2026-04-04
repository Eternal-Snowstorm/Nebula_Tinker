package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.shared.TinkerEffects;
import dev.celestiacraft.tinker.api.CombatUtils;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;

public class Abuser extends BasicModifier {
	private static final float CRIT_MULTIPLIER = 1.5F;

	@Override
	public void onCriticalHit(Player player, LivingEntity entity, CriticalHitEvent event, int level) {
		if (!hasEffect(entity)) {
			return;
		}

		// 触发暴击
		CombatUtils.spawnAbuserCritEffect(player);
		event.setResult(Event.Result.ALLOW);
		event.setDamageModifier(CRIT_MULTIPLIER);
	}

	private boolean hasEffect(LivingEntity entity) {
		return entity.hasEffect(MobEffects.POISON)
				|| entity.hasEffect(MobEffects.WITHER)
				|| entity.hasEffect(TinkerEffects.bleeding.get());
	}
}