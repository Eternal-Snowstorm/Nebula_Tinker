package top.nebula.tinker.api;

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

/**
 * <p>
 * 该类用于处理战斗中的时间轴与表现效果, 主要功能包括:
 * <ul>
 *     <li>攻击冷却完成判定</li>
 *     <li>自定义暴击粒子与音效</li>
 *     <li>实体最近一次受伤时间记录</li>
 *     <li>基于 tick 的受伤窗口判断</li>
 * </ul>
 *
 * <p>
 * 所有受伤时间均以世界 {@code gameTime} 为基准, 单位为 tick.
 * 实体受伤时间会通过 {@link LivingHurtEvent} 自动记录.
 */
public class CombatUtils {
	/**
	 * 存储实体最后一次受伤的世界时间.
	 *
	 * <p>
	 * Key: 实体 UUID
	 * <br>
	 * Value: 实体所在世界的 gameTime(tick)
	 */
	public static final Map<UUID, Long> LAST_HURT_TIME = new HashMap<>();

	/**
	 * 在玩家视线前方生成一次暴击视觉与音效反馈.
	 *
	 * <p>
	 * 行为说明:
	 * <ul>
	 *     <li>在玩家视线前方生成一次粒子</li>
	 *     <li>播放横扫攻击音效</li>
	 * </ul>
	 *
	 * <p>
	 * 仅在服务端生效, 客户端调用将直接返回.
	 *
	 * @param player 触发暴击效果的玩家
	 */
	public static void spawnAbuserCritEffect(Player player) {
		if (!(player.level() instanceof ServerLevel level)) {
			return;
		}

		Vec3 look = player.getLookAngle();
		double distance = 1.5D;

		double x = player.getX() + look.x * distance;
		double y = player.getEyeY() - 0.1D;
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
	 * 判断玩家攻击冷却是否基本完成.
	 *
	 * <p>
	 * 内部使用 {@link Player#getAttackStrengthScale(float)} 进行判断.
	 * 当返回值 >= 0.9 时认为攻击已冷却完成.
	 *
	 * @param player 要检测的玩家
	 * @return true 表示可以进行完整强度攻击
	 */
	public static boolean isAttackCooled(Player player) {
		return !(player.getAttackStrengthScale(0.5F) >= 0.9F);
	}

	/**
	 * 手动记录实体受伤时间.
	 *
	 * <p>
	 * 一般情况下不需要主动调用,
	 * 受伤时间已由 {@link LivingHurtEvent} 自动记录.
	 *
	 * @param entity 受伤的实体
	 */
	public static void recordHurt(LivingEntity entity) {
		LAST_HURT_TIME.put(entity.getUUID(), entity.level().getGameTime());
	}

	/**
	 * 判断实体是否处于受伤后的指定 tick 时间内.
	 *
	 * <p>
	 * 适用于:
	 * <ul>
	 *     <li>连击系统</li>
	 *     <li>受击增伤</li>
	 *     <li>短时间状态判定</li>
	 * </ul>
	 *
	 * @param entity 目标实体
	 * @param ticks  判定时间(tick)
	 * @return true 表示仍处于受伤后的窗口期
	 */
	public static boolean hurtAfter(LivingEntity entity, int ticks) {
		Long last = LAST_HURT_TIME.get(entity.getUUID());
		if (last == null) {
			return false;
		}
		return entity.level().getGameTime() - last <= ticks;
	}

	/**
	 * 判断实体是否已经连续指定 tick 时间未受到伤害.
	 *
	 * <p>
	 * 适用于:
	 * <ul>
	 *     <li>脱战判定</li>
	 *     <li>状态恢复</li>
	 *     <li>战斗结束逻辑</li>
	 * </ul>
	 *
	 * @param entity 目标实体
	 * @param ticks  判定时间(tick)
	 * @return true 表示在该时间内未受伤
	 */
	public static boolean notHurtFor(LivingEntity entity, int ticks) {
		Long last = LAST_HURT_TIME.get(entity.getUUID());
		if (last == null) {
			return true;
		}
		return entity.level().getGameTime() - last >= ticks;
	}

	/**
	 * 判断是否仍处于以最后一次受伤为起点的持续窗口中.
	 *
	 * <p>
	 * 语义上等价于 {@link #hurtAfter(LivingEntity, int)},
	 * 更强调"窗口期仍然有效".
	 *
	 * @param entity 目标实体
	 * @param ticks  窗口持续时间(tick)
	 * @return true 表示窗口仍然有效
	 */
	public static boolean hurtWindowActive(LivingEntity entity, int ticks) {
		return hurtAfter(entity, ticks);
	}

	/**
	 * 判断是否刚刚进入受伤窗口.
	 *
	 * <p>
	 * 仅在受伤发生的那个 tick 返回 true,
	 * 后续 tick 均返回 false.
	 *
	 * @param entity 目标实体
	 * @param ticks  窗口长度(tick), 必须 > 0
	 * @return true 表示受伤发生的瞬间
	 */
	public static boolean hurtWindowBegin(LivingEntity entity, int ticks) {
		Long last = LAST_HURT_TIME.get(entity.getUUID());
		if (last == null || ticks <= 0) {
			return false;
		}
		long now = entity.level().getGameTime();
		return now == last;
	}

	/**
	 * 战斗时间相关事件监听器.
	 *
	 * <p>
	 * 自动监听实体受伤事件并记录受伤时间,
	 * 不建议在其他地方重复记录以避免覆盖.
	 */
	@Mod.EventBusSubscriber
	public static class CombatTimeEvents {
		/**
		 * 实体受到伤害时记录其受伤时间.
		 *
		 * @param event 实体受伤事件
		 */
		@SubscribeEvent
		public static void onLivingHurt(LivingHurtEvent event) {
			LivingEntity entity = event.getEntity();
			LAST_HURT_TIME.put(entity.getUUID(), entity.level().getGameTime());
		}
	}
}