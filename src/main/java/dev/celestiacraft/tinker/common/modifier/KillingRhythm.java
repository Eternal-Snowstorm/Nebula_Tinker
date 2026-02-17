package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.api.CombatUtils;
import dev.celestiacraft.tinker.api.SimpleTConUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KillingRhythm extends Modifier {
	// 触发暴击所需的最低击杀数
	private static final int REQUIRED_KILLS = 2;
	// 基础暴击持续时间 6秒 = 120 ticks
	private static final long BASE_CRIT_DURATION_TICKS = 120L;
	// 每多杀一个敌人增加的持续时间 1秒 = 20 ticks
	private static final long EXTRA_DURATION_PER_KILL = 20L;
	// 击杀计数窗口时间 3秒内击杀才算"迅速"
	private static final long KILL_WINDOW_TICKS = 60L;
	// 基础暴击几率 25%
	private static final double BASE_CRIT_CHANCE = 0.25;
	// 每多杀一个敌人增加的暴击几率 5%
	private static final double EXTRA_CRIT_CHANCE_PER_KILL = 0.05;
	// 暴击伤害倍率
	private static final float CRIT_MULTIPLIER = 1.5F;

	private static final Map<UUID, KillData> KILL_DATA_MAP = new ConcurrentHashMap<>();
	private static final Map<UUID, CritState> CRIT_STATE_MAP = new ConcurrentHashMap<>();

	private static class KillData {
		int killCount;
		long lastKillTime;

		KillData(long time) {
			this.killCount = 1;
			this.lastKillTime = time;
		}

		void addKill(long time) {
			this.killCount++;
			this.lastKillTime = time;
		}

		void reset(long time) {
			this.killCount = 1;
			this.lastKillTime = time;
		}
	}

	private static class CritState {
		long endTime;
		double critChance;

		CritState(long endTime, double critChance) {
			this.endTime = endTime;
			this.critChance = critChance;
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		if (!(source.getEntity() instanceof Player player)) {
			return;
		}
		boolean hasModifier = SimpleTConUtils.hasModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				NebulaTinker.loadResource("killing_rhythm").toString()
		);
		if (!hasModifier) {
			return;
		}
		UUID playerId = player.getUUID();
		long currentTime = player.level().getGameTime();
		CritState currentState = CRIT_STATE_MAP.get(playerId);
		if (currentState != null && currentTime < currentState.endTime) {
			currentState.endTime += EXTRA_DURATION_PER_KILL;
			currentState.critChance = Math.min(1.0, currentState.critChance + EXTRA_CRIT_CHANCE_PER_KILL);
			int critPercent = (int) (currentState.critChance * 100);
			int remainingSeconds = (int) Math.ceil((currentState.endTime - currentTime) / 20.0);
			playKillingRhythmStack(player, critPercent, remainingSeconds);
			return;
		}
		KillData data = KILL_DATA_MAP.get(playerId);
		if (data == null) {
			KILL_DATA_MAP.put(playerId, new KillData(currentTime));
		} else {
			if (currentTime - data.lastKillTime <= KILL_WINDOW_TICKS) {
				data.addKill(currentTime);
				if (data.killCount >= REQUIRED_KILLS) {
					activateCrit(player, currentTime, data.killCount);
					KILL_DATA_MAP.remove(playerId);
				}
			} else {
				data.reset(currentTime);
			}
		}
	}

	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		Player player = event.getEntity();
		Entity target = event.getTarget();
		if (!(target instanceof LivingEntity)) {
			return;
		}
		if (CombatUtils.isAttackCooled(player)) {
			return;
		}
		boolean hasModifier = SimpleTConUtils.hasModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				NebulaTinker.loadResource("killing_rhythm").toString()
		);
		if (!hasModifier) {
			return;
		}
		UUID playerId = player.getUUID();
		long currentTime = player.level().getGameTime();
		CritState state = CRIT_STATE_MAP.get(playerId);
		if (state == null || currentTime >= state.endTime) {
			CRIT_STATE_MAP.remove(playerId);
			return;
		}
		if (Math.random() >= state.critChance) {
			return;
		}
		CombatUtils.spawnAbuserCritEffect(player);
		event.setResult(Event.Result.ALLOW);
		event.setDamageModifier(CRIT_MULTIPLIER);
	}

	private static void activateCrit(Player player, long currentTime, int killCount) {
		UUID playerId = player.getUUID();
		int extraKills = killCount - REQUIRED_KILLS;
		long duration = BASE_CRIT_DURATION_TICKS + (extraKills * EXTRA_DURATION_PER_KILL);
		double critChance = Math.min(1.0, BASE_CRIT_CHANCE + (extraKills * EXTRA_CRIT_CHANCE_PER_KILL));
		CRIT_STATE_MAP.put(playerId, new CritState(currentTime + duration, critChance));
		int critPercent = (int) (critChance * 100);
		playKillingRhythmActivate(player, critPercent);
	}

	/**
	 * 杀戮节奏激活效果
	 *
	 * @param player      玩家
	 * @param critPercent 暴击百分比
	 */
	private static void playKillingRhythmActivate(Player player, int critPercent) {
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
	private static void playKillingRhythmStack(Player player, int critPercent, int remainingSeconds) {
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
