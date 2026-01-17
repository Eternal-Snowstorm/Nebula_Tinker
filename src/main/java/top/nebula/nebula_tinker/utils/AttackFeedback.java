package top.nebula.nebula_tinker.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import top.nebula.nebula_tinker.common.register.ModParticle;

public class AttackFeedback {
	/**
	 * 生成粒子效果和挥刀暴击音效
	 *
	 * @param player 玩家
	 */
	public static void spawnAbuserCritEffect(Player player) {
		if (!(player.level() instanceof ServerLevel level)) {
			return;
		}

		// 玩家视线方向
		Vec3 look = player.getLookAngle();
		double distance = 1.2;

		// 粒子生成位置：眼前
		double x = player.getX() + look.x * distance;
		double y = player.getEyeY() - 0.1;
		double z = player.getZ() + look.z * distance;

		level.sendParticles(
				ModParticle.CROSS_CHOP.get(),
				x,
				y,
				z,
				1,
				0.0,
				0.0,
				0.0,
				0.0
		);
		level.playSound(
				null,
				player.blockPosition(),
				SoundEvents.PLAYER_ATTACK_SWEEP,
				SoundSource.PLAYERS,
				1.0F,
				1.5F
		);
	}

	/**
	 * 杀戮节奏激活效果
	 *
	 * @param player      玩家
	 * @param critPercent 暴击百分比
	 */
	public static void playKillingRhythmActivate(Player player, int critPercent) {
		Component message = Component.translatable(
				"message.nebula_tinker.modifier.killing_rhythm",
				critPercent
		);
		player.displayClientMessage(message, true);
		if (player.level() instanceof ServerLevel level) {
			level.playSound(
					null,
					player.blockPosition(),
					SoundEvents.PLAYER_LEVELUP,
					SoundSource.PLAYERS,
					1.0F,
					1.5F
			);
		}
	}

	/**
	 * 杀戮节奏叠加效果
	 *
	 * @param player           玩家
	 * @param critPercent      暴击百分比
	 * @param remainingSeconds 剩余秒数
	 */
	public static void playKillingRhythmStack(Player player, int critPercent, int remainingSeconds) {
		Component message = Component.translatable(
				"message.nebula_tinker.modifier.killing_rhythm.stack",
				critPercent,
				remainingSeconds
		);
		player.displayClientMessage(message, true);
		if (player.level() instanceof ServerLevel level) {
			level.playSound(
					null,
					player.blockPosition(),
					SoundEvents.EXPERIENCE_ORB_PICKUP,
					SoundSource.PLAYERS,
					1.0F,
					1.2F
			);
		}
	}
}
