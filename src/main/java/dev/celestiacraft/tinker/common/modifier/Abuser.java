package dev.celestiacraft.tinker.common.modifier;

import dev.celestiacraft.tinker.api.CombatUtils;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.shared.TinkerEffects;

/**
 * 施虐者
 *
 * <p><b>类型:</b> 战斗类 Modifier (暴击强化)</p>
 *
 * <p><b>适用部位:</b> 武器</p>
 *
 * <p><b>核心机制:</b></p>
 * <ul>
 *   <li>当目标处于特定负面状态时, 强制触发暴击</li>
 *   <li>并应用固定暴击伤害倍率</li>
 * </ul>
 *
 * <hr>
 *
 * <p><b>触发条件:</b></p>
 *
 * <ul>
 *   <li>目标拥有以下任意效果之一:</li>
 *   <ul>
 *     <li>中毒 (Poison)</li>
 *     <li>凋零 (Wither)</li>
 *     <li>流血 (Bleeding, TConstruct)</li>
 *   </ul>
 * </ul>
 *
 * <hr>
 *
 * <p><b>效果:</b></p>
 *
 * <ul>
 *   <li>强制本次攻击判定为暴击</li>
 *   <li>暴击伤害倍率固定为 ×1.5</li>
 *   <li>播放专属暴击特效</li>
 * </ul>
 *
 * <hr>
 *
 * <p><b>实现说明:</b></p>
 *
 * <ul>
 *   <li>基于 CriticalHitEvent 实现</li>
 *   <li>通过 Event.Result.ALLOW 强制覆盖原版暴击判定</li>
 *   <li>不依赖玩家下落状态或攻击条件</li>
 * </ul>
 *
 * <hr>
 *
 * <p><b>注意:</b></p>
 *
 * <ul>
 *   <li>仅在目标存在指定负面效果时生效</li>
 *   <li>暴击倍率为固定值, 不随等级变化(除非额外扩展)</li>
 * </ul>
 */
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