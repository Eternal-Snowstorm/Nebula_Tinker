package top.nebula.nebula_tinker.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.AttackFeedback;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CritEventHandler {
	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		Player player = event.getEntity();
		LivingEntity target = event.getTarget() instanceof LivingEntity ? (LivingEntity) event.getTarget() : null;

		if (target == null) return;

		// 检查攻击冷却
		if (player.getAttackStrengthScale(0.5F) < 0.9F) {
			return;
		}

		ItemStack weapon = player.getItemInHand(InteractionHand.MAIN_HAND);
		boolean isJumpAttack = CritCalculator.isJumpAttack(player);

		// 使用CritCalculator计算暴击
		boolean shouldCrit = CritCalculator.shouldCrit(player, target, weapon, isJumpAttack);

		if (shouldCrit) {
			// 计算暴击伤害倍数
			float critMultiplier = CritCalculator.getCritDamageMultiplier(player, target, weapon, isJumpAttack);

			// 触发暴击效果
			AttackFeedback.spawnAbuserCritEffect(player);

			// 设置事件结果
			event.setResult(CriticalHitEvent.Result.ALLOW);
			event.setDamageModifier(critMultiplier);

			// 如果是跳劈，应用双倍暴击效果
			if (isJumpAttack) {
				applyJumpCritEffects(player, target, critMultiplier);
			}
		}
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			// 每5秒清理一次过期修饰符
			if (event.getServer().getTickCount() % 100 == 0) {
				CritCalculator.cleanupExpiredModifiers();
			}
		}
	}

	private static void applyJumpCritEffects(Player player, LivingEntity target, float critMultiplier) {
		// 可以在这里添加额外的跳劈特效
	}
}