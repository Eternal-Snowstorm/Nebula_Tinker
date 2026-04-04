package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import dev.celestiacraft.tinker.api.CombatUtils;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KillingRhythm extends BasicModifier {
	// 参数
	private static final int REQUIRED_KILLS = 2;
	private static final long BASE_CRIT_DURATION_TICKS = 120L;
	private static final long EXTRA_DURATION_PER_KILL = 20L;
	private static final long KILL_WINDOW_TICKS = 60L;

	private static final double BASE_CRIT_CHANCE = 0.25;
	private static final double EXTRA_CRIT_CHANCE_PER_KILL = 0.05;

	private static final float CRIT_MULTIPLIER = 1.5F;

	// 数据
	private static final Map<UUID, KillData> KILL_DATA_MAP = new ConcurrentHashMap<>();
	private static final Map<UUID, CritState> CRIT_STATE_MAP = new ConcurrentHashMap<>();

	@Override
	public void onLivingDeath(Player player, LivingEntity target, LivingDeathEvent event, int level) {
		UUID playerId = player.getUUID();
		long currentTime = player.level().getGameTime();

		CritState currentState = CRIT_STATE_MAP.get(playerId);

		if (currentState != null && currentTime < currentState.endTime) {
			currentState.endTime += EXTRA_DURATION_PER_KILL;
			currentState.critChance = Math.min(1.0, currentState.critChance + EXTRA_CRIT_CHANCE_PER_KILL);

			int critPercent = (int) (currentState.critChance * 100);
			int remainingSeconds = (int) Math.ceil((currentState.endTime - currentTime) / 20.0);

			playStack(player, critPercent, remainingSeconds);
			return;
		}

		KillData data = KILL_DATA_MAP.get(playerId);

		if (data == null) {
			KILL_DATA_MAP.put(playerId, new KillData(currentTime));
			return;
		}

		if (currentTime - data.lastKillTime <= KILL_WINDOW_TICKS) {
			data.addKill(currentTime);

			if (data.killCount >= REQUIRED_KILLS) {
				activate(player, currentTime, data.killCount);
				KILL_DATA_MAP.remove(playerId);
			}
		} else {
			data.reset(currentTime);
		}
	}

	@Override
	public void onCriticalHit(Player player, LivingEntity target, CriticalHitEvent event, int level) {
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

	private static void activate(Player player, long currentTime, int killCount) {
		UUID playerId = player.getUUID();

		int extraKills = killCount - REQUIRED_KILLS;

		long duration = BASE_CRIT_DURATION_TICKS + extraKills * EXTRA_DURATION_PER_KILL;
		double critChance = Math.min(1.0, BASE_CRIT_CHANCE + extraKills * EXTRA_CRIT_CHANCE_PER_KILL);

		CRIT_STATE_MAP.put(playerId, new CritState(currentTime + duration, critChance));

		int critPercent = (int) (critChance * 100);
		playActivate(player, critPercent);
	}

	private static void playActivate(Player player, int critPercent) {
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

	private static void playStack(Player player, int critPercent, int remainingSeconds) {
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
}