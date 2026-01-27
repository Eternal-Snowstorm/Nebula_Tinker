package top.nebula.tinker.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.tinker.common.register.ModParticle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatUtils {
	public static final Map<UUID, Long> LAST_HURT_TIME = new HashMap<>();

	/**
	 * 生成粒子效果和挥刀暴击音效
	 *
	 * @param player
	 */
	public static void spawnAbuserCritEffect(Player player) {
		if (!(player.level() instanceof ServerLevel level)) return;

		Vec3 look = player.getLookAngle();
		double distance = 1.5;

		double x = player.getX() + look.x * distance;
		double y = player.getEyeY() - 0.1;
		double z = player.getZ() + look.z * distance;

		level.sendParticles(
				ModParticle.CROSS_CHOP.get(),
				x,
				y,
				z,
				1,
				0,
				0,
				0,
				0
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
	 * 攻击是否已冷却完成
	 *
	 * @param player
	 * @return
	 */
	public static boolean isAttackCooled(Player player) {
		return player.getAttackStrengthScale(0.5F) >= 0.9F;
	}

	public static void recordHurt(LivingEntity entity) {
		LAST_HURT_TIME.put(entity.getUUID(), entity.level().getGameTime());
	}

	/**
	 * 是否处于受伤后的 ticks 时间内
	 *
	 * @param entity
	 * @param ticks
	 * @return
	 */
	public static boolean hurtAfter(LivingEntity entity, int ticks) {
		Long last = LAST_HURT_TIME.get(entity.getUUID());
		if (last == null) {
			return false;
		}
		return entity.level().getGameTime() - last <= ticks;
	}

	/**
	 * 是否已经连续 ticks 没有受伤
	 *
	 * @param entity
	 * @param ticks
	 * @return
	 */
	public static boolean notHurtFor(LivingEntity entity, int ticks) {
		Long last = LAST_HURT_TIME.get(entity.getUUID());
		if (last == null) {
			return true;
		}
		return entity.level().getGameTime() - last >= ticks;
	}

	/**
	 * 以"最后受伤时间"为起点，
	 * 是否仍处于一个持续窗口中
	 * <p>
	 * 受伤那一刻开始，持续 ticks
	 *
	 * @param entity
	 */
	public static boolean hurtWindowActive(LivingEntity entity, int ticks) {
		return hurtAfter(entity, ticks);
	}

	/**
	 * 是否刚刚进入受伤窗口(仅一刻为 true)
	 *
	 * @param entity
	 * @param ticks
	 * @return
	 */
	public static boolean hurtWindowBegin(LivingEntity entity, int ticks) {
		Long last = LAST_HURT_TIME.get(entity.getUUID());
		if (last == null) {
			return false;
		}
		long now = entity.level().getGameTime();
		return now == last && ticks > 0;
	}

	@Mod.EventBusSubscriber
	public static class CombatTimeEvents {
		@SubscribeEvent
		public static void onLivingHurt(LivingHurtEvent event) {
			LivingEntity entity = event.getEntity();
			LAST_HURT_TIME.put(entity.getUUID(), entity.level().getGameTime());
		}
	}
}