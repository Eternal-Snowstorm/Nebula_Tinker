package top.nebula.tinker.common.modifier;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.shared.TinkerEffects;
import top.nebula.tinker.NebulaTinker;
import top.nebula.tinker.api.CombatUtils;
import top.nebula.tinker.api.SimpleTConUtils;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Abuser extends Modifier {
	/**
	 * 基础暴击倍率(原本 1.5x 伤害)
	 */
	private static final float CRIT_MULTIPLIER = 1.5F;

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		Player player = event.getEntity();
		Entity target = event.getTarget();

		if (!(target instanceof LivingEntity entity)) {
			return;
		}

		// 攻击冷却检查(防止连触发)
		if (CombatUtils.isAttackCooled(player)) {
			return;
		}

		boolean hasModifier = SimpleTConUtils.hasModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				NebulaTinker.loadResource("abuser").toString()
		);

		if (!hasModifier) {
			return;
		}

		if (!hasEffect(entity)) {
			return;
		}

		// 强制暴击
		CombatUtils.spawnAbuserCritEffect(player);
		event.setResult(Event.Result.ALLOW);
		event.setDamageModifier(CRIT_MULTIPLIER);
	}

	private static boolean hasEffect(LivingEntity entity) {
		return entity.hasEffect(MobEffects.POISON)
				|| entity.hasEffect(MobEffects.WITHER)
				|| entity.hasEffect(TinkerEffects.bleeding.get());
	}
}