package top.nebula.nebula_tinker.common.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.AttributeApplicator;
import top.nebula.nebula_tinker.utils.EAttributeType;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Demonization extends Modifier {
	private static final ResourceLocation POSITIVE_ATTRIBUTES_KEY = NebulaTinker.loadResource("demonization_positive");
	private static final ResourceLocation NEGATIVE_ATTRIBUTES_KEY = NebulaTinker.loadResource("demonization_negative");
	private static final String LEVEL_KEY = "demonization_level"; //保存等级的键
	private static final int POSITIVE_ATTRIBUTES_COUNT = 3;
	private static final int NEGATIVE_ATTRIBUTES_COUNT = 1; //负面效果数量改为1
	private static final double BASE_MULTIPLIER = 1.5;
	private static final double PER_LEVEL_BONUS = 0.2;
	private static final double NEGATIVE_MULTIPLIER = 0.8;
	
	private static final Map<UUID, Map<ItemStack, AttributePack>> attributeCache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> tickCounter = new ConcurrentHashMap<>();
	private static final int PARTICLE_COOLDOWN = 100;
	private static final Map<UUID, Map<EquipmentSlot, HealthReductionData>> healthReductionCache = new ConcurrentHashMap<>();
	
	private static class HealthReductionData {
		final ItemStack stack;
		final float reductionAmount;
		final long applyTime;
		
		HealthReductionData(ItemStack stack, float reductionAmount) {
			this.stack = stack;
			this.reductionAmount = reductionAmount;
			this.applyTime = System.currentTimeMillis();
		}
	}
	
	public static AttributePack getOrGenerateAttributes(ItemStack stack, Player player) {
		if (stack.isEmpty() || player == null) {
			return new AttributePack(Collections.emptyList(), Collections.emptyList());
		}
		
		UUID playerId = player.getUUID();
		Map<ItemStack, AttributePack> playerCache = attributeCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
		
		// 获取当前等级
		int currentLevel = SimpleTConUtils.getModifierLevel(stack, NebulaTinker.loadResource("demonization").toString());
		
		// 检查缓存中是否存在，并且等级是否匹配
		if (playerCache.containsKey(stack)) {
			AttributePack cachedPack = playerCache.get(stack);
			CompoundTag tag = stack.getOrCreateTag();
			
			// 检查等级是否变化
			int savedLevel = tag.getInt(LEVEL_KEY);
			if (savedLevel == currentLevel) {
				return cachedPack;
			} else {
				// 等级变化，清除缓存并重新生成
				playerCache.remove(stack);
				tag.remove(POSITIVE_ATTRIBUTES_KEY.toString());
				tag.remove(NEGATIVE_ATTRIBUTES_KEY.toString());
				tag.remove(LEVEL_KEY);
			}
		}
		
		CompoundTag tag = stack.getOrCreateTag();
		
		// 检查是否有保存的属性，并且等级匹配
		if (tag.contains(LEVEL_KEY)) {
			int savedLevel = tag.getInt(LEVEL_KEY);
			if (savedLevel == currentLevel &&
					    tag.contains(POSITIVE_ATTRIBUTES_KEY.toString()) &&
					    tag.contains(NEGATIVE_ATTRIBUTES_KEY.toString())) {
				
				AttributePack pack = new AttributePack(
						deserializeAttributes(tag.getCompound(POSITIVE_ATTRIBUTES_KEY.toString())),
						deserializeAttributes(tag.getCompound(NEGATIVE_ATTRIBUTES_KEY.toString()))
				);
				playerCache.put(stack, pack);
				return pack;
			} else {
				// 等级不匹配，清除旧属性
				tag.remove(POSITIVE_ATTRIBUTES_KEY.toString());
				tag.remove(NEGATIVE_ATTRIBUTES_KEY.toString());
				tag.remove(LEVEL_KEY);
			}
		}
		
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) {
			return new AttributePack(Collections.emptyList(), Collections.emptyList());
		}
		
		if (currentLevel <= 0) {
			return new AttributePack(Collections.emptyList(), Collections.emptyList());
		}
		
		EquipmentSlot slot = determineEquipmentSlot(stack, player);
		List<AttributeEntry> positiveAttributes = generatePositiveAttributes(tool, currentLevel, slot);
		List<AttributeEntry> negativeAttributes = generateNegativeAttributes(tool, currentLevel, slot);
		
		if (!positiveAttributes.isEmpty() || !negativeAttributes.isEmpty()) {
			saveAttributes(stack, positiveAttributes, negativeAttributes, currentLevel);
			
			AttributePack pack = new AttributePack(positiveAttributes, negativeAttributes);
			playerCache.put(stack, pack);
			
			if (player.level() != null && !player.level().isClientSide()) {
				MutableComponent message = Component.translatable("message.nebula_tinker.demonization.generate").withStyle(ChatFormatting.DARK_RED);
				player.displayClientMessage(message, true);
				
				player.level().playSound(null, player.blockPosition(),
						SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.5f, 0.8f);
			}
		}
		
		return new AttributePack(positiveAttributes, negativeAttributes);
	}
	
	private static void saveAttributes(ItemStack stack, List<AttributeEntry> positiveAttributes, List<AttributeEntry> negativeAttributes, int level) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(POSITIVE_ATTRIBUTES_KEY.toString(), serializeAttributes(positiveAttributes));
		tag.put(NEGATIVE_ATTRIBUTES_KEY.toString(), serializeAttributes(negativeAttributes));
		tag.putInt(LEVEL_KEY, level); // 保存当前等级
	}
	
	private static void applyDemonizationAttributes(Player player, ItemStack stack, EquipmentSlot slot) {
		AttributePack attributes = getOrGenerateAttributes(stack, player);
		
		if (attributes.positive != null && !attributes.positive.isEmpty()) {
			AttributeApplicator.applyAttributes(player, attributes.positive, stack, "demonization_positive");
		}
		
		if (attributes.negative != null && !attributes.negative.isEmpty()) {
			AttributeApplicator.applyAttributes(player, attributes.negative, stack, "demonization_negative");
			
			applyHealthReductionEffect(player, attributes.negative, slot, stack);
		}
	}
	
	private static void applyHealthReductionEffect(Player player, List<AttributeEntry> negativeAttributes, EquipmentSlot slot, ItemStack stack) {
		float totalHealthReduction = 0.0f;
		
		for (AttributeEntry entry : negativeAttributes) {
			if (entry.type == EAttributeType.HEALTH_REDUCTION) {
				totalHealthReduction += (float) Math.abs(entry.value);
			}
		}
		
		if (totalHealthReduction > 0) {
			UUID playerId = player.getUUID();
			Map<EquipmentSlot, HealthReductionData> playerHealthCache = healthReductionCache
					                                                            .computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
			
			HealthReductionData existing = playerHealthCache.get(slot);
			if (existing == null || !ItemStack.isSameItemSameTags(existing.stack, stack)) {
				playerHealthCache.put(slot, new HealthReductionData(stack.copy(), totalHealthReduction));
				
				float currentMaxHealth = player.getMaxHealth();
				float newMaxHealth = Math.max(1.0f, currentMaxHealth - totalHealthReduction);
				
				if (newMaxHealth < currentMaxHealth) {
					if (newMaxHealth < player.getHealth()) {
						player.setHealth(newMaxHealth);
					}
				}
			}
		}
	}
	
	private static void removeHealthReductionEffect(Player player, EquipmentSlot slot) {
		UUID playerId = player.getUUID();
		if (healthReductionCache.containsKey(playerId)) {
			Map<EquipmentSlot, HealthReductionData> playerHealthCache = healthReductionCache.get(playerId);
			HealthReductionData data = playerHealthCache.remove(slot);
			
			if (data != null) {
				float currentMaxHealth = player.getMaxHealth();
				float restoredMaxHealth = currentMaxHealth + data.reductionAmount;
				
				if (restoredMaxHealth > 0) {
					player.setHealth(Math.min(player.getHealth(), restoredMaxHealth));
				}
			}
			
			if (playerHealthCache.isEmpty()) {
				healthReductionCache.remove(playerId);
			}
		}
	}
	
	private static void removeDemonizationAttributes(Player player, EquipmentSlot slot) {
		AttributeApplicator.removeAttributes(player, slot);
		removeHealthReductionEffect(player, slot);
	}
	
	private static EquipmentSlot determineEquipmentSlot(ItemStack stack, Player player) {
		String itemName = stack.getItem().toString().toLowerCase();
		
		if (itemName.contains("helmet") || itemName.contains("head")) {
			return EquipmentSlot.HEAD;
		} else if (itemName.contains("chestplate") || itemName.contains("chest")) {
			return EquipmentSlot.CHEST;
		} else if (itemName.contains("leggings") || itemName.contains("leg")) {
			return EquipmentSlot.LEGS;
		} else if (itemName.contains("boots") || itemName.contains("feet")) {
			return EquipmentSlot.FEET;
		} else if (player.getOffhandItem() == stack) {
			return EquipmentSlot.OFFHAND;
		}
		return EquipmentSlot.MAINHAND;
	}
	
	public static List<AttributeEntry> generatePositiveAttributes(ToolStack tool, int level, EquipmentSlot slot) {
		List<AttributeEntry> attributes = new ArrayList<>();
		Random random = new Random();
		
		List<EAttributeType> attributePool = getAttributePoolForSlot(tool, slot, true);
		if (attributePool.isEmpty()) {
			return attributes;
		}
		
		Set<EAttributeType> selectedTypes = new HashSet<>();
		int maxAttempts = attributePool.size() * 2;
		int attempts = 0;
		
		while (selectedTypes.size() < Math.min(POSITIVE_ATTRIBUTES_COUNT, attributePool.size()) && attempts < maxAttempts) {
			attempts++;
			EAttributeType type = attributePool.get(random.nextInt(attributePool.size()));
			
			if (isAttributeApplicable(type, slot) && !selectedTypes.contains(type)) {
				selectedTypes.add(type);
				double baseValue = type.getBaseValue();
				double multiplier = BASE_MULTIPLIER + (level - 1) * PER_LEVEL_BONUS;
				double finalValue = baseValue * multiplier;
				attributes.add(new AttributeEntry(type, finalValue, slot));
			}
		}
		
		return attributes;
	}
	
	private static List<AttributeEntry> generateNegativeAttributes(ToolStack tool, int level, EquipmentSlot slot) {
		List<AttributeEntry> attributes = new ArrayList<>();
		Random random = new Random();
		
		List<EAttributeType> negativePool = getAttributePoolForSlot(tool, slot, false);
		if (negativePool.isEmpty()) {
			return attributes;
		}
		
		Set<EAttributeType> selectedTypes = new HashSet<>();
		int maxAttempts = negativePool.size() * 2;
		int attempts = 0;
		
		// 只选择1个负面属性
		while (selectedTypes.size() < Math.min(NEGATIVE_ATTRIBUTES_COUNT, negativePool.size()) && attempts < maxAttempts) {
			attempts++;
			EAttributeType type = negativePool.get(random.nextInt(negativePool.size()));
			
			if (isAttributeApplicable(type, slot)) {
				selectedTypes.add(type);
				double baseValue = type.getBaseValue();
				double multiplier = NEGATIVE_MULTIPLIER * (1 + (level - 1) * 0.25);
				double finalValue = baseValue * multiplier;
				attributes.add(new AttributeEntry(type, finalValue, slot));
			}
		}
		
		return attributes;
	}
	
	private static List<EAttributeType> getAttributePoolForSlot(ToolStack tool, EquipmentSlot slot, boolean positive) {
		List<EAttributeType> attributePool = new ArrayList<>();
		String toolName = tool.getItem().toString().toLowerCase();
		
		if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
			if (toolName.contains("bow") || toolName.contains("crossbow")) {
				if (positive) {
					attributePool.addAll(Arrays.asList(
							EAttributeType.DRAW_SPEED,
							EAttributeType.ARROW_SPEED,
							EAttributeType.ARROW_ACCURACY,
							EAttributeType.PROJECTILE_DAMAGE
					));
				} else {
					attributePool.addAll(Arrays.asList(
							EAttributeType.HEALTH_REDUCTION, // 使用整合后的血量降低
							EAttributeType.ARMOR_REDUCTION,
							EAttributeType.MOVEMENT_SLOW,
							EAttributeType.ATTACK_SPEED_REDUCTION
					));
				}
			} else if (toolName.contains("sword") || toolName.contains("axe") || toolName.contains("mace")) {
				if (positive) {
					attributePool.addAll(Arrays.asList(
							EAttributeType.ATTACK_DAMAGE,
							EAttributeType.ATTACK_SPEED,
							EAttributeType.CRITICAL_CHANCE,
							EAttributeType.CRITICAL_DAMAGE,
							EAttributeType.FIRE_ASPECT,
							EAttributeType.FROST_ASPECT,
							EAttributeType.LIGHTNING_ASPECT
					));
				} else {
					attributePool.addAll(Arrays.asList(
							EAttributeType.HEALTH_REDUCTION, // 使用整合后的血量降低
							EAttributeType.ARMOR_REDUCTION,
							EAttributeType.MOVEMENT_SLOW,
							EAttributeType.ATTACK_DAMAGE_REDUCTION
					));
				}
			} else if (toolName.contains("pickaxe") || toolName.contains("shovel") || toolName.contains("mattock")) {
				if (positive) {
					attributePool.addAll(Arrays.asList(
							EAttributeType.MINING_SPEED,
							EAttributeType.DURABILITY,
							EAttributeType.HARVEST_LEVEL,
							EAttributeType.EFFICIENCY
					));
				} else {
					attributePool.addAll(Arrays.asList(
							EAttributeType.DURABILITY_REDUCTION,
							EAttributeType.HARVEST_LEVEL_REDUCTION,
							EAttributeType.MINING_SPEED_REDUCTION,
							EAttributeType.MOVEMENT_SLOW
					));
				}
			} else {
				if (positive) {
					attributePool.addAll(Arrays.asList(
							EAttributeType.ATTACK_DAMAGE,
							EAttributeType.ATTACK_SPEED,
							EAttributeType.CRITICAL_CHANCE,
							EAttributeType.CRITICAL_DAMAGE
					));
				} else {
					attributePool.addAll(Arrays.asList(
							EAttributeType.HEALTH_REDUCTION, // 使用整合后的血量降低
							EAttributeType.ARMOR_REDUCTION,
							EAttributeType.MOVEMENT_SLOW
					));
				}
			}
		} else {
			if (positive) {
				attributePool.addAll(Arrays.asList(
						EAttributeType.ARMOR,
						EAttributeType.MAX_HEALTH,
						EAttributeType.ARMOR_TOUGHNESS,
						EAttributeType.MOVEMENT_SPEED,
						EAttributeType.KNOCKBACK_RESISTANCE,
						EAttributeType.FEATHER_FALLING,
						EAttributeType.PROTECTION
				));
			} else {
				attributePool.addAll(Arrays.asList(
						EAttributeType.ATTACK_DAMAGE_REDUCTION,
						EAttributeType.ATTACK_SPEED_REDUCTION,
						EAttributeType.CRITICAL_REDUCTION,
						EAttributeType.CRITICAL_DAMAGE_REDUCTION,
						EAttributeType.HEALTH_REDUCTION
				));
			}
		}
		
		return attributePool;
	}
	
	private static boolean isAttributeApplicable(EAttributeType type, EquipmentSlot slot) {
		for (EquipmentSlot applicableSlot : type.getApplicableSlots()) {
			if (applicableSlot == slot || slot.getType() == EquipmentSlot.Type.ARMOR && type.getApplicableSlots().contains(slot)) {
				return true;
			}
		}
		return false;
	}
	
	private static CompoundTag serializeAttributes(List<AttributeEntry> attributes) {
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();
		for (AttributeEntry entry : attributes) {
			CompoundTag entryTag = new CompoundTag();
			entryTag.putString("type", entry.type.name());
			entryTag.putDouble("value", entry.value);
			entryTag.putString("slot", entry.slot.getName().toLowerCase(Locale.ROOT));
			list.add(entryTag);
		}
		tag.put("attributes", list);
		return tag;
	}
	
	private static List<AttributeEntry> deserializeAttributes(CompoundTag tag) {
		List<AttributeEntry> attributes = new ArrayList<>();
		if (!tag.contains("attributes", CompoundTag.TAG_LIST)) {
			return attributes;
		}
		
		ListTag list = tag.getList("attributes", CompoundTag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entryTag = list.getCompound(i);
			try {
				EAttributeType type = EAttributeType.valueOf(entryTag.getString("type"));
				double value = entryTag.getDouble("value");
				String slotName = entryTag.getString("slot").toUpperCase(Locale.ROOT);
				EquipmentSlot slot;
				
				try {
					slot = EquipmentSlot.valueOf(slotName);
				} catch (IllegalArgumentException e) {
					if (slotName.contains("HAND")) {
						slot = slotName.contains("MAIN") ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
					} else if (slotName.contains("ARMOR")) {
						slot = EquipmentSlot.CHEST;
					} else {
						slot = EquipmentSlot.MAINHAND;
					}
				}
				attributes.add(new AttributeEntry(type, value, slot));
			} catch (Exception exception) {
				// 忽略无效的属性条目
			}
		}
		return attributes;
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		
		Player player = event.player;
		UUID playerId = player.getUUID();
		
		int counter = tickCounter.getOrDefault(playerId, 0);
		tickCounter.put(playerId, counter + 1);
		
		if (counter % 10 != 0) {
			return;
		}
		
		cleanupExpiredHealthReductions(player);
		
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		boolean hasDemonizationMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("demonization").toString());
		boolean hasDivinizationMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("divinization").toString());
		boolean hasHarmonyMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("divine_demonic_harmony").toString());
		
		if (hasDemonizationMain) {
			applyDemonizationAttributes(player, mainHand, EquipmentSlot.MAINHAND);
			handleDemonizedItem(player, mainHand, true);
		} else {
			// 检查是否有共存强化且是否有另一个强化
			if (!(hasHarmonyMain && hasDivinizationMain)) {
				if (AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.MAINHAND, "demonization_positive") ||
						    AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.MAINHAND, "demonization_negative")) {
					removeDemonizationAttributes(player, EquipmentSlot.MAINHAND);
				}
			}
		}
		
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
		boolean hasDemonizationOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("demonization").toString());
		boolean hasDivinizationOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("divinization").toString());
		boolean hasHarmonyOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("divine_demonic_harmony").toString());
		
		if (hasDemonizationOff) {
			applyDemonizationAttributes(player, offHand, EquipmentSlot.OFFHAND);
			handleDemonizedItem(player, offHand, false);
		} else {
			if (!(hasHarmonyOff && hasDivinizationOff)) {
				if (AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.OFFHAND, "demonization_positive") ||
						    AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.OFFHAND, "demonization_negative")) {
					removeDemonizationAttributes(player, EquipmentSlot.OFFHAND);
				}
			}
		}
		
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() == EquipmentSlot.Type.ARMOR) {
				ItemStack armor = player.getItemBySlot(slot);
				boolean hasDemonizationArmor = SimpleTConUtils.hasModifier(armor, NebulaTinker.loadResource("demonization").toString());
				boolean hasDivinizationArmor = SimpleTConUtils.hasModifier(armor, NebulaTinker.loadResource("divinization").toString());
				boolean hasHarmonyArmor = SimpleTConUtils.hasModifier(armor, NebulaTinker.loadResource("divine_demonic_harmony").toString());
				
				if (hasDemonizationArmor) {
					applyDemonizationAttributes(player, armor, slot);
					getOrGenerateAttributes(armor, player);
				} else {
					if (!(hasHarmonyArmor && hasDivinizationArmor)) {
						if (AttributeApplicator.hasModifierAttributesInSlot(player, slot, "demonization_positive") ||
								    AttributeApplicator.hasModifierAttributesInSlot(player, slot, "demonization_negative")) {
							removeDemonizationAttributes(player, slot);
						}
					}
				}
			}
		}
	}
	
	private static void cleanupExpiredHealthReductions(Player player) {
		UUID playerId = player.getUUID();
		if (healthReductionCache.containsKey(playerId)) {
			Map<EquipmentSlot, HealthReductionData> playerCache = healthReductionCache.get(playerId);
			List<EquipmentSlot> toRemove = new ArrayList<>();
			
			for (Map.Entry<EquipmentSlot, HealthReductionData> entry : playerCache.entrySet()) {
				long elapsed = System.currentTimeMillis() - entry.getValue().applyTime;
				if (elapsed > 30000) {
					toRemove.add(entry.getKey());
				}
			}
			
			for (EquipmentSlot slot : toRemove) {
				playerCache.remove(slot);
			}
			
			if (playerCache.isEmpty()) {
				healthReductionCache.remove(playerId);
			}
		}
	}
	
	private static void handleDemonizedItem(Player player, ItemStack item, boolean isMainHand) {
		AttributePack attributes = getOrGenerateAttributes(item, player);
		
		long gameTime = player.level().getGameTime();
		
		if (gameTime % PARTICLE_COOLDOWN == 0 && player.level() instanceof ServerLevel serverLevel) {
			int particleCount = Math.min(5, attributes.positive.size() + attributes.negative.size());
			for (int i = 0; i < particleCount; i++) {
				double offsetX = player.getRandom().nextDouble() - 0.5;
				double offsetY = player.getRandom().nextDouble() * 2.5;
				double offsetZ = player.getRandom().nextDouble() - 0.5;
				
				double handOffset = isMainHand ? -0.5 : 0.5;
				serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
						player.getX() + offsetX + handOffset,
						player.getY() + offsetY,
						player.getZ() + offsetZ,
						1, 0, 0.05, 0, 0);
			}
			
			serverLevel.playSound(null, player.blockPosition(),
					SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.3f, 0.9f);
		}
	}
	
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		if (!(source.getEntity() instanceof Player player)) {
			return;
		}
		
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
		
		boolean hasDemonizationMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("demonization").toString());
		boolean hasDemonizationOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("demonization").toString());
		
		if (!hasDemonizationMain && !hasDemonizationOff) {
			return;
		}
		
		ItemStack weapon = hasDemonizationMain ? mainHand : offHand;
		AttributePack attributes = getOrGenerateAttributes(weapon, player);
		
		if (attributes.positive.isEmpty()) {
			return;
		}
		
		int level = SimpleTConUtils.getModifierLevel(weapon, NebulaTinker.loadResource("demonization").toString());
		float extraDamage = 0.0f;
		boolean hasCriticalChance = false;
		boolean hasCriticalDamage = false;
		double criticalChanceValue = 0;
		double criticalDamageValue = 0;
		
		for (AttributeEntry attribute : attributes.positive) {
			EAttributeType type = attribute.type;
			if (type == EAttributeType.ATTACK_DAMAGE) {
				extraDamage += (float) (attribute.value * (1.2f + level * 0.1f));
			} else if (type == EAttributeType.CRITICAL_CHANCE) {
				hasCriticalChance = true;
				criticalChanceValue = attribute.value * (1.3f + level * 0.05f);
			} else if (type == EAttributeType.CRITICAL_DAMAGE) {
				hasCriticalDamage = true;
				criticalDamageValue = attribute.value * (1.3f + level * 0.05f);
			} else if (type.getCategory() == EAttributeType.AttributeCategory.ELEMENTAL) {
				applyDemonicEffects(event.getEntity(), attribute, player);
			}
		}
		
		float finalDamage = event.getAmount() + extraDamage;
		if (hasCriticalChance) {
			boolean isJumpCritical = player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable() &&
					                         !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger();
			
			double totalCriticalChance = criticalChanceValue;
			if (isJumpCritical) {
				totalCriticalChance += 1.0;
			}
			
			if (player.getRandom().nextDouble() < totalCriticalChance) {
				float criticalMultiplier = 1.5f;
				if (hasCriticalDamage) {
					criticalMultiplier += (float) criticalDamageValue;
				}
				
				finalDamage *= criticalMultiplier;
				
				if (player.level() instanceof ServerLevel serverLevel) {
					serverLevel.sendParticles(
							ParticleTypes.SOUL_FIRE_FLAME,
							event.getEntity().getX(),
							event.getEntity().getY() + event.getEntity().getBbHeight() / 2,
							event.getEntity().getZ(),
							15,
							0.4,
							0.4,
							0.4,
							0
					);
					
					serverLevel.playSound(null, player.blockPosition(),
							SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 0.7f, 1.2f);
				}
			}
		}
		
		event.setAmount(finalDamage);
		
		if (player.level() instanceof ServerLevel serverLevel && finalDamage > event.getAmount()) {
			serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
					event.getEntity().getX(),
					event.getEntity().getY() + event.getEntity().getBbHeight(),
					event.getEntity().getZ(),
					(int) (finalDamage - event.getAmount()),
					0,
					0,
					0,
					0
			);
		}
	}
	
	private static void applyDemonicEffects(LivingEntity target, AttributeEntry attribute, Player player) {
		EAttributeType type = attribute.type;
		double value = attribute.value;
		
		switch (type) {
			case FIRE_ASPECT:
				target.setSecondsOnFire((int) (value / 1.5));
				target.addEffect(new MobEffectInstance(MobEffects.WITHER,
						(int) (value * 15), 0));
				spawnParticles(target, ParticleTypes.SOUL_FIRE_FLAME, 15);
				break;
			case FROST_ASPECT:
				target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
						(int) (value * 40), (int) (value / 1.5)));
				target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
						(int) (value * 30), 1));
				spawnParticles(target, ParticleTypes.SNOWFLAKE, 12);
				break;
			case LIGHTNING_ASPECT:
				spawnParticles(target, ParticleTypes.ELECTRIC_SPARK, 20);
				if (!target.level().isClientSide() && player.getRandom().nextFloat() < 0.2f) {
					target.hurt(target.damageSources().lightningBolt(), (float) (value * 1.5f));
					spawnParticles(target, ParticleTypes.ELECTRIC_SPARK, 40);
					
					target.level().playSound(null, target.blockPosition(),
							SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.8f, 0.8f);
				}
				break;
		}
	}
	
	private static void spawnParticles(LivingEntity entity, ParticleOptions particle, int count) {
		if (!(entity.level() instanceof ServerLevel serverLevel)) {
			return;
		}
		
		for (int i = 0; i < count; i++) {
			double offsetX = entity.getRandom().nextDouble() - 0.5;
			double offsetY = entity.getRandom().nextDouble() * entity.getBbHeight();
			double offsetZ = entity.getRandom().nextDouble() - 0.5;
			
			serverLevel.sendParticles(particle,
					entity.getX() + offsetX,
					entity.getY() + offsetY,
					entity.getZ() + offsetZ,
					1, 0, 0, 0, 0);
		}
	}
	
	public record AttributeEntry(EAttributeType type, double value, EquipmentSlot slot) {
		public Component getDescription() {
			String key = type.getTranslationKey();
			return Component.translatable(key, String.format("+%.1f", value))
					       .withStyle(type.getCategory() == EAttributeType.AttributeCategory.ELEMENTAL ?
							                  ChatFormatting.DARK_RED : ChatFormatting.RED);
		}
	}
	
	public record AttributePack(List<AttributeEntry> positive, List<AttributeEntry> negative) {
	}
}