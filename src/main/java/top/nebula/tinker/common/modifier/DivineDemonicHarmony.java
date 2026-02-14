package top.nebula.tinker.common.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.nebula.tinker.NebulaTinker;
import top.nebula.tinker.api.SimpleTConUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DivineDemonicHarmony extends Modifier {

	private static final ResourceLocation MODIFIER_ID = NebulaTinker.loadResource("divine_demonic_harmony");
	private static final ResourceLocation DEMONIZATION_ID = NebulaTinker.loadResource("demonization");
	private static final ResourceLocation DIVINIZATION_ID = NebulaTinker.loadResource("divinization");
	private static final UUID HEALTH_REDUCTION_UUID = UUID.fromString("a6c5d9b8-1e2f-4a3b-8c7d-9e0f1a2b3c4d");

	private static final String HEALTH_REDUCTION_MODIFIER_KEY = "divine_demonic_harmony_health_reduction";

	private static final int REQUIRED_LEVEL = 9;

	private static final int MIN_DAMAGE_INTERVAL = 4 * 20; // 4秒
	private static final int MAX_DAMAGE_INTERVAL = 7 * 20; // 7秒

	private static final float BASE_DAMAGE_FACTOR = 0.1f;
	private static final float MAX_DAMAGE_FACTOR = 0.6f;

	private static final float ZERO_LEVEL_DIFF_HEALTH_REDUCTION = 0.5f;
	private static final float ZERO_LEVEL_DIFF_DAMAGE_FACTOR = 0.6f;

	private static final float DAMAGE_INCREASE_PER_LEVEL = 0.05f;

	private static final Map<UUID, PlayerHarmonyData> playerDataMap = new ConcurrentHashMap<>();

	private static final boolean DEBUG = true;

	private static final Vector3f PURPLE_COLOR = new Vector3f(0.8f, 0.0f, 1.0f);
	private static final Vector3f GOLD_COLOR = new Vector3f(1.0f, 0.8f, 0.0f);

	private static class PlayerHarmonyData {
		UUID playerId;
		ItemStack weapon;
		int levelDiff;
		boolean healthReduced = false;
		float originalMaxHealth;
		float healthReductionAmount;
		int nextDamageTick = 0;
		int damageInterval = 0;
		int lastLevelDiff = -1;
		int lastDamageTick = 0;
		float lastDamageAmount = 0;
		long lastUpdateTime = 0;
		boolean isActive = false;
		ScheduledDamage scheduledDamage;

		PlayerHarmonyData(UUID playerId, ItemStack weapon, int levelDiff) {
			this.playerId = playerId;
			this.weapon = weapon.copy();
			this.levelDiff = levelDiff;
			this.lastLevelDiff = levelDiff;
			this.lastUpdateTime = System.currentTimeMillis();
		}

		void update(ItemStack newWeapon, int newLevelDiff) {
			this.weapon = newWeapon.copy();
			this.levelDiff = newLevelDiff;
			this.lastLevelDiff = newLevelDiff;
			this.lastUpdateTime = System.currentTimeMillis();
		}

		boolean shouldBeCleaned() {
			return System.currentTimeMillis() - lastUpdateTime > 60000; // 60秒无更新则清理
		}
	}

	private static class ScheduledDamage {
		UUID playerId;
		int scheduledTick;
		float damageAmount;
		int levelDiff;
		boolean executed = false;

		ScheduledDamage(UUID playerId, int scheduledTick, float damageAmount, int levelDiff) {
			this.playerId = playerId;
			this.scheduledTick = scheduledTick;
			this.damageAmount = damageAmount;
			this.levelDiff = levelDiff;
		}
	}

	public static boolean canCoexist(ItemStack stack, Player player) {
		if (stack.isEmpty() || player == null) return false;

		int harmonyLevel = SimpleTConUtils.getModifierLevel(stack, MODIFIER_ID.toString());
		if (harmonyLevel <= 0) {
//			logDebug("No harmony modifier found on item");
			return false;
		}

		int demonLevel = SimpleTConUtils.getModifierLevel(stack, DEMONIZATION_ID.toString());
		int divinLevel = SimpleTConUtils.getModifierLevel(stack, DIVINIZATION_ID.toString());

		debugLog("Checking coexist conditions - demonLevel: " + demonLevel + ", divinLevel: " + divinLevel);

		boolean canCoexist = demonLevel > 0 && divinLevel > 0 && (demonLevel >= REQUIRED_LEVEL || divinLevel >= REQUIRED_LEVEL);

		if (!canCoexist) {
			debugLog("Cannot coexist - conditions not met");
		}

		return canCoexist;
	}

	public static int getLevelDifference(ItemStack stack) {
		int demonLevel = SimpleTConUtils.getModifierLevel(stack, DEMONIZATION_ID.toString());
		int divinLevel = SimpleTConUtils.getModifierLevel(stack, DIVINIZATION_ID.toString());

		if (demonLevel <= 0 || divinLevel <= 0) {
			return -1;
		}

		return Math.abs(demonLevel - divinLevel);
	}

	public static float calculateDamageFactor(int levelDiff) {
		if (levelDiff == 0) {
			return ZERO_LEVEL_DIFF_DAMAGE_FACTOR;
		}

		if (levelDiff < 0 || levelDiff > 9) {
			return 0;
		}

		float progress = 1.0f - (levelDiff - 1) / 8.0f;
		float factor = BASE_DAMAGE_FACTOR + (MAX_DAMAGE_FACTOR - BASE_DAMAGE_FACTOR) * progress;

		float levelBonus = (9 - levelDiff) * DAMAGE_INCREASE_PER_LEVEL;
		factor *= (1.0f + levelBonus);

		return Math.max(BASE_DAMAGE_FACTOR, Math.min(MAX_DAMAGE_FACTOR * 1.5f, factor));
	}

	private static int calculateDamageInterval(int levelDiff) {
		if (levelDiff == 0) {
			return MIN_DAMAGE_INTERVAL; // 等级差为0时使用最小间隔
		}

		float progress = levelDiff / 9.0f;
		int interval = MIN_DAMAGE_INTERVAL + (int) ((MAX_DAMAGE_INTERVAL - MIN_DAMAGE_INTERVAL) * progress);
		return Math.max(MIN_DAMAGE_INTERVAL, Math.min(MAX_DAMAGE_INTERVAL, interval));
	}

	private static void applyHarmonyEffect(Player player, ItemStack stack, boolean isMainHand) {
		if (!canCoexist(stack, player)) {
			debugLog("Cannot apply harmony effect for player: " + player.getName().getString());
			clearPlayerState(player);
			return;
		}

		int levelDiff = getLevelDifference(stack);
		if (levelDiff < 0) {
			debugLog("Invalid level diff for player: " + player.getName().getString());
			clearPlayerState(player);
			return;
		}

		UUID playerId = player.getUUID();
		PlayerHarmonyData data = playerDataMap.get(playerId);

		boolean isNewData = false;
		if (data == null) {
			// 新数据
			data = new PlayerHarmonyData(playerId, stack, levelDiff);
			data.isActive = true;
			playerDataMap.put(playerId, data);
			isNewData = true;

			debugLog("New harmony data created for player " + player.getName().getString() +
					", levelDiff=" + levelDiff);
		} else {
			// 更新现有数据
			boolean weaponChanged = !ItemStack.isSameItemSameTags(data.weapon, stack);
			boolean levelDiffChanged = data.levelDiff != levelDiff;

			if (weaponChanged || levelDiffChanged) {
				if (data.healthReduced && levelDiff != 0) {
					restorePlayerHealth(player, data);
				}

				data.update(stack, levelDiff);

				if (levelDiffChanged) {
					debugLog("Level diff changed for player " + player.getName().getString() +
							", old=" + data.lastLevelDiff + ", new=" + levelDiff);
				}
			}
		}

		// 更新伤害间隔
		data.damageInterval = calculateDamageInterval(levelDiff);

		// 如果是新数据或等级差变化，安排第一次伤害
		if (isNewData || data.nextDamageTick == 0) {
			data.nextDamageTick = player.tickCount + data.damageInterval;
			debugLog("Scheduling first damage for player " + player.getName().getString() +
					" in " + data.damageInterval + " ticks");
		}

		// 检查是否应该扣血
		checkAndApplyDamage(player, data);

		// 处理等级差为0的特殊效果
		if (levelDiff == 0) {
			handleZeroLevelDifference(player, data);
		} else {
			if (data.healthReduced) {
				restorePlayerHealth(player, data);
			}
		}

		// 视觉效果
		spawnHarmonyParticles(player, isMainHand, levelDiff);
	}

	private static void checkAndApplyDamage(Player player, PlayerHarmonyData data) {
		if (player.isCreative() || player.isSpectator() || !player.isAlive()) {
			return;
		}

		// 检查是否到达伤害时间
		if (player.tickCount >= data.nextDamageTick) {
			applyDamageToPlayer(player, data);
			// 安排下一次伤害
			data.nextDamageTick = player.tickCount + data.damageInterval;
			debugLog("Scheduled next damage for player " + player.getName().getString() +
					" in " + data.damageInterval + " ticks (tick " + data.nextDamageTick + ")");
		}
	}

	private static void applyDamageToPlayer(Player player, PlayerHarmonyData data) {
		if (player.isRemoved() || !player.isAlive() || player.isCreative() || player.isSpectator()) {
			debugLog("Player not valid for damage, skipping: " + player.getName().getString());
			return;
		}

		float damageFactor = calculateDamageFactor(data.levelDiff);
		float damage = player.getMaxHealth() * damageFactor;
		damage = Math.max(1.0f, damage);

		float originalHealth = player.getHealth();
		player.hurt(player.damageSources().magic(), damage);

		debugLog("Applied harmony damage to player " + player.getName().getString() +
				": " + originalHealth + " -> " + player.getHealth() + " (-" + damage + ")" +
				", levelDiff=" + data.levelDiff + ", factor=" + damageFactor);

		// 粒子效果
		if (player.level() instanceof ServerLevel serverLevel) {
			spawnGrandPurpleGoldParticles(serverLevel, player, damage);

			serverLevel.playSound(null, player.getOnPos(),
					SoundEvents.GENERIC_HURT, SoundSource.PLAYERS, 1.0f, 0.8f);

			serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
					player.getX(),
					player.getY() + player.getBbHeight(),
					player.getZ(),
					(int) damage,
					0.1, 0.1, 0.1, 0);

			// 显示伤害数字
			player.displayClientMessage(
					Component.literal("§c神魔反噬: -" + String.format("%.1f", damage) + " HP")
							.withStyle(ChatFormatting.DARK_PURPLE),
					true
			);
		}

		data.lastDamageTick = player.tickCount;
		data.lastDamageAmount = damage;
	}

	private static void handleZeroLevelDifference(Player player, PlayerHarmonyData data) {
		if (!data.healthReduced) {
			data.originalMaxHealth = player.getMaxHealth();
			data.healthReductionAmount = data.originalMaxHealth * ZERO_LEVEL_DIFF_HEALTH_REDUCTION;

			safeApplyHealthModifier(player, HEALTH_REDUCTION_UUID, HEALTH_REDUCTION_MODIFIER_KEY, -data.healthReductionAmount);

			if (player.getHealth() > player.getMaxHealth()) {
				player.setHealth(player.getMaxHealth());
			}

			data.healthReduced = true;

			if (player.level() instanceof ServerLevel serverLevel) {
				serverLevel.playSound(null, player.getOnPos(),
						SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.0f, 0.5f);

				player.displayClientMessage(
						Component.literal("§5神魔平衡: 最大生命值降低50%")
								.withStyle(ChatFormatting.DARK_PURPLE),
						true
				);
			}

			debugLog("Health reduced for player " + player.getName().getString() +
					", original=" + data.originalMaxHealth + ", reduction=" + data.healthReductionAmount +
					", new max health=" + player.getMaxHealth());
		}
	}

	private static void safeApplyHealthModifier(Player player, UUID uuid, String name, double amount) {
		var attribute = player.getAttribute(Attributes.MAX_HEALTH);
		if (attribute == null) return;

		attribute.removeModifier(uuid);

		if (Math.abs(amount) > 0.001) {
			AttributeModifier modifier = new AttributeModifier(
					uuid,
					name,
					amount,
					AttributeModifier.Operation.ADDITION
			);

			if (!attribute.hasModifier(modifier)) {
				attribute.addTransientModifier(modifier);
				debugLog("Applied health modifier: " + name + ", amount=" + amount);
			}
		}
	}

	private static void restorePlayerHealth(Player player, PlayerHarmonyData data) {
		var attribute = player.getAttribute(Attributes.MAX_HEALTH);
		if (attribute != null) {
			attribute.removeModifier(HEALTH_REDUCTION_UUID);
		}

		if (data.originalMaxHealth > 0 && player.getHealth() > data.originalMaxHealth) {
			player.setHealth(data.originalMaxHealth);
		}

		data.healthReduced = false;
		data.healthReductionAmount = 0;

		debugLog("Health restored for player " + player.getName().getString() + ", original max health=" + data.originalMaxHealth);

		if (player.level() instanceof ServerLevel serverLevel) {
			player.displayClientMessage(
					Component.literal("§a神魔平衡解除: 生命值恢复")
							.withStyle(ChatFormatting.GREEN),
					true
			);
		}
	}

	/**
	 * 生成紫金色粒子效果
	 */
	private static void spawnGrandPurpleGoldParticles(ServerLevel serverLevel, Player player, float damage) {
		int particleCount = 30 + (int) (damage * 3);
		particleCount = Math.min(150, particleCount);

		Vec3 playerPos = player.position();
		double centerX = playerPos.x;
		double centerY = playerPos.y + player.getBbHeight() * 0.5;
		double centerZ = playerPos.z;

		RandomSource random = player.getRandom();

		for (int i = 0; i < particleCount; i++) {
			double theta = random.nextDouble() * 2 * Math.PI;
			double phi = random.nextDouble() * Math.PI;
			double radius = 0.8 + random.nextDouble() * 1.2;

			double dx = radius * Math.sin(phi) * Math.cos(theta);
			double dy = radius * Math.cos(phi);
			double dz = radius * Math.sin(phi) * Math.sin(theta);

			double vx = dx * (0.08 + random.nextDouble() * 0.15);
			double vy = dy * (0.08 + random.nextDouble() * 0.15);
			double vz = dz * (0.08 + random.nextDouble() * 0.15);

			ParticleOptions particle;
			float particleScale = random.nextFloat() * 0.2f + 0.05f;

			if (random.nextBoolean()) {
				particle = new DustParticleOptions(PURPLE_COLOR, particleScale);
			} else {
				particle = new DustParticleOptions(GOLD_COLOR, particleScale);
			}

			// 添加一些发光粒子
			if (i % 8 == 0) {
				serverLevel.sendParticles(ParticleTypes.GLOW,
						centerX + dx, centerY + dy, centerZ + dz,
						1, 0, 0, 0, 0);
			}

			serverLevel.sendParticles(particle,
					centerX + dx, centerY + dy, centerZ + dz,
					1, vx, vy, vz, 0.05);
		}

		serverLevel.playSound(null, player.getOnPos(),
				SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.8f, 0.9f);
	}

	/**
	 * 生成暴击紫金色粒子效果
	 */
	private static void spawnCritPurpleGoldParticles(ServerLevel serverLevel, LivingEntity target, float damage) {
		int particleCount = 60 + (int) (damage * 6);
		particleCount = Math.min(250, particleCount);

		Vec3 targetPos = target.position();
		double centerX = targetPos.x;
		double centerY = targetPos.y + target.getBbHeight() * 0.5;
		double centerZ = targetPos.z;

		RandomSource random = target.getRandom();

		for (int i = 0; i < particleCount; i++) {
			double theta = random.nextDouble() * 2 * Math.PI;
			double phi = random.nextDouble() * Math.PI;
			double radius = 0.4 + random.nextDouble() * 0.8;

			double dx = radius * Math.sin(phi) * Math.cos(theta);
			double dy = radius * Math.cos(phi);
			double dz = radius * Math.sin(phi) * Math.sin(theta);

			double vx = dx * (0.12 + random.nextDouble() * 0.25);
			double vy = dy * (0.12 + random.nextDouble() * 0.25);
			double vz = dz * (0.12 + random.nextDouble() * 0.25);

			ParticleOptions particle;
			float particleScale = random.nextFloat() * 0.25f + 0.05f;

			if (random.nextBoolean()) {
				particle = new DustParticleOptions(PURPLE_COLOR, particleScale);
			} else {
				particle = new DustParticleOptions(GOLD_COLOR, particleScale);
			}

			// 添加更多发光粒子
			if (i % 6 == 0) {
				serverLevel.sendParticles(ParticleTypes.GLOW,
						centerX + dx, centerY + dy, centerZ + dz,
						1, 0, 0, 0, 0);
			}

			if (i % 15 == 0) {
				serverLevel.sendParticles(ParticleTypes.GLOW_SQUID_INK,
						centerX + dx, centerY + dy, centerZ + dz,
						1, vx * 0.2, vy * 0.2, vz * 0.2, 0.1);
			}

			serverLevel.sendParticles(particle,
					centerX + dx, centerY + dy, centerZ + dz,
					1, vx, vy, vz, 0.07);
		}

		serverLevel.playSound(null, target.getOnPos(),
				SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.HOSTILE, 1.5f, 0.8f);
		serverLevel.playSound(null, target.getOnPos(),
				SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, 1.2f, 0.9f);
	}

	private static void clearPlayerState(Player player) {
		UUID playerId = player.getUUID();
		PlayerHarmonyData data = playerDataMap.get(playerId);

		if (data != null) {
			if (data.healthReduced) {
				restorePlayerHealth(player, data);
			}

			playerDataMap.remove(playerId);
			debugLog("Player state cleared: " + player.getName().getString());
		}
	}

	private static boolean playerHasHarmony(Player player) {
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (canCoexist(mainHand, player)) {
			debugLog("Player has harmony in main hand: " + player.getName().getString());
			return true;
		}

		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
		if (canCoexist(offHand, player)) {
			debugLog("Player has harmony in off hand: " + player.getName().getString());
			return true;
		}

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() == EquipmentSlot.Type.ARMOR) {
				ItemStack armor = player.getItemBySlot(slot);
				if (canCoexist(armor, player)) {
					debugLog("Player has harmony in armor slot " + slot.getName() + ": " + player.getName().getString());
					return true;
				}
			}
		}

//		logDebug("Player has no harmony items: " + player.getName().getString());
		return false;
	}

	/**
	 * 生成和谐粒子效果
	 */
	private static void spawnHarmonyParticles(Player player, boolean isMainHand, int levelDiff) {
		if (!(player.level() instanceof ServerLevel serverLevel)) return;

		long gameTime = player.level().getGameTime();

		// 每3秒生成一次普通粒子
		if (gameTime % 60 == 0) {
			int particleCount = 2 + (9 - levelDiff) / 2;

			for (int i = 0; i < particleCount; i++) {
				double offsetX = player.getRandom().nextDouble() - 0.5;
				double offsetY = player.getRandom().nextDouble() * 1.8;
				double offsetZ = player.getRandom().nextDouble() - 0.5;

				double handOffset = isMainHand ? -0.4 : 0.4;

				if (levelDiff <= 3) {
					if (i % 2 == 0) {
						serverLevel.sendParticles(ParticleTypes.ENCHANT,
								player.getX() + offsetX + handOffset,
								player.getY() + offsetY,
								player.getZ() + offsetZ,
								1, 0, 0.08, 0, 0);
					} else {
						serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
								player.getX() + offsetX + handOffset,
								player.getY() + offsetY,
								player.getZ() + offsetZ,
								1, 0, 0.04, 0, 0);
					}
				} else {
					serverLevel.sendParticles(
							ParticleTypes.GLOW,
							player.getX() + offsetX + handOffset,
							player.getY() + offsetY,
							player.getZ() + offsetZ,
							1,
							0,
							0.06,
							0,
							0
					);
				}
			}
		}
	}

	private static void cleanupUnusedPlayerData() {
		List<UUID> toRemove = new ArrayList<>();

		for (Map.Entry<UUID, PlayerHarmonyData> entry : playerDataMap.entrySet()) {
			if (entry.getValue().shouldBeCleaned()) {
				toRemove.add(entry.getKey());
			}
		}

		for (UUID playerId : toRemove) {
			PlayerHarmonyData data = playerDataMap.remove(playerId);
			if (data != null) {
				debugLog("Cleaned up unused player data for UUID: " + playerId);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;

		Player player = event.player;

		// 每10秒清理一次未使用的玩家数据
		if (player.tickCount % 200 == 0) {
			cleanupUnusedPlayerData();
		}

		// 检查玩家是否有共存强化
		if (!playerHasHarmony(player)) {
			clearPlayerState(player);
			return;
		}

		boolean hasProcessed = false;

		// 检查主手
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (canCoexist(mainHand, player)) {
			applyHarmonyEffect(player, mainHand, true);
			hasProcessed = true;
		}

		if (!hasProcessed) {
			// 检查副手
			ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
			if (canCoexist(offHand, player)) {
				applyHarmonyEffect(player, offHand, false);
				hasProcessed = true;
			}
		}

		if (!hasProcessed) {
			// 检查装备
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot.getType() == EquipmentSlot.Type.ARMOR) {
					ItemStack armor = player.getItemBySlot(slot);
					if (canCoexist(armor, player)) {
						applyHarmonyEffect(player, armor, false);
						hasProcessed = true;
						break;
					}
				}
			}
		}

		// 如果没有处理任何物品，清除状态
		if (!hasProcessed) {
			clearPlayerState(player);
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		if (!(source.getEntity() instanceof Player player)) return;

		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

		boolean hasHarmonyMain = canCoexist(mainHand, player);
		boolean hasHarmonyOff = canCoexist(offHand, player);

		if (!hasHarmonyMain && !hasHarmonyOff) return;

		ItemStack weapon = hasHarmonyMain ? mainHand : offHand;

		if (canCoexist(weapon, player)) {
			int levelDiff = getLevelDifference(weapon);

			float levelBonus = (9 - levelDiff) * DAMAGE_INCREASE_PER_LEVEL;
			float damageBonus = 1.0f + levelBonus;

			float extraDamage = event.getAmount() * (damageBonus - 1.0f);

			event.setAmount(event.getAmount() + extraDamage);

			float critChance = 0.1f + (9 - levelDiff) * 0.05f;
			boolean isCrit = player.getRandom().nextFloat() < critChance;

			if (isCrit) {
				float critMultiplier = 1.5f + (9 - levelDiff) * 0.1f;
				event.setAmount(event.getAmount() * critMultiplier);

				if (player.level() instanceof ServerLevel serverLevel) {
					spawnCritPurpleGoldParticles(serverLevel, event.getEntity(), event.getAmount());

					serverLevel.playSound(null, event.getEntity().getOnPos(),
							SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.5f, 0.6f);

					player.displayClientMessage(
							Component.literal("§6神魔暴击!")
									.withStyle(ChatFormatting.GOLD),
							true
					);
				}
			}

			debugLog("Attack damage bonus applied: levelDiff=" + levelDiff +
					", bonus=" + (damageBonus * 100) + "%, extra=" + extraDamage +
					", crit=" + isCrit + ", critChance=" + (critChance * 100) + "%");
		}
	}

	private static void debugLog(String message) {
		if (DEBUG) {
			NebulaTinker.LOGGER.debug("[DivineDemonicHarmony] " + message);
		}
	}

	public Component getDisplayName(IToolStackView tool, int level) {
		return Component.translatable("modifier.nebula_tinker.divine_demonic_harmony")
				.withStyle(ChatFormatting.LIGHT_PURPLE);
	}
}