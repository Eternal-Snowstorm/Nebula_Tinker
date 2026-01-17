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
public class Divinization extends Modifier {
	private static final ResourceLocation ATTRIBUTES_KEY = NebulaTinker.loadResource("divinization_attributes");
	private static final String LEVEL_KEY = "divinization_level";
	private static final double BASE_MULTIPLIER = 1.2;
	private static final double PER_LEVEL_BONUS = 0.15;
	private static final int ATTRIBUTES_COUNT = 3;
	
	private static final Map<UUID, Map<ItemStack, List<AttributeEntry>>> attributeCache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> tickCounter = new ConcurrentHashMap<>();
	private static final int PARTICLE_COOLDOWN = 20 * 5;
	private static final String GENERATED_KEY = "divinization_generated";
	
	public static List<AttributeEntry> getOrGenerateAttributes(ItemStack stack, Player player) {
		if (stack.isEmpty() || player == null) {
			return Collections.emptyList();
		}
		
		UUID playerId = player.getUUID();
		Map<ItemStack, List<AttributeEntry>> playerCache = attributeCache.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
		
		// 获取当前等级
		int currentLevel = SimpleTConUtils.getModifierLevel(stack, NebulaTinker.loadResource("divinization").toString());
		
		// 检查缓存中是否存在，并且等级是否匹配
		if (playerCache.containsKey(stack)) {
			List<AttributeEntry> cachedAttributes = playerCache.get(stack);
			CompoundTag tag = stack.getOrCreateTag();
			
			// 检查等级是否变化
			int savedLevel = tag.getInt(LEVEL_KEY);
			if (savedLevel == currentLevel && tag.contains(GENERATED_KEY) && tag.contains(ATTRIBUTES_KEY.toString())) {
				return cachedAttributes;
			} else {
				// 等级变化，清除缓存并重新生成
				playerCache.remove(stack);
				tag.remove(ATTRIBUTES_KEY.toString());
				tag.remove(GENERATED_KEY);
				tag.remove(LEVEL_KEY);
			}
		}
		
		CompoundTag tag = stack.getOrCreateTag();
		
		// 检查是否有保存的属性，并且等级匹配
		if (tag.contains(LEVEL_KEY)) {
			int savedLevel = tag.getInt(LEVEL_KEY);
			if (savedLevel == currentLevel && tag.contains(GENERATED_KEY) && tag.contains(ATTRIBUTES_KEY.toString())) {
				List<AttributeEntry> attributes = deserializeAttributes(tag.getCompound(ATTRIBUTES_KEY.toString()));
				playerCache.put(stack, attributes);
				return attributes;
			} else {
				// 等级不匹配，清除旧属性
				tag.remove(ATTRIBUTES_KEY.toString());
				tag.remove(GENERATED_KEY);
				tag.remove(LEVEL_KEY);
			}
		}
		
		ToolStack tool = ToolStack.from(stack);
		if (tool.isBroken()) {
			return Collections.emptyList();
		}
		
		if (currentLevel <= 0) {
			return Collections.emptyList();
		}
		
		EquipmentSlot slot = determineEquipmentSlot(stack, player);
		List<AttributeEntry> attributes = generateAttributes(tool, currentLevel, slot);
		
		if (!attributes.isEmpty()) {
			saveAttributes(stack, attributes, currentLevel);
			tag.putBoolean(GENERATED_KEY, true);
			
			playerCache.put(stack, attributes);
			
			if (player.level() != null && !player.level().isClientSide()) {
				MutableComponent message = Component.translatable("message.nebula_tinker.divinization.generate").withStyle(ChatFormatting.GOLD);
				player.displayClientMessage(message, true);
				
				player.level().playSound(null, player.blockPosition(),
						SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5f, 1.0f);
			}
		}
		
		return attributes;
	}
	
	private static void saveAttributes(ItemStack stack, List<AttributeEntry> attributes, int level) {
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(ATTRIBUTES_KEY.toString(), serializeAttributes(attributes));
		tag.putInt(LEVEL_KEY, level);
	}
	
	private static void applyDivinizationAttributes(Player player, ItemStack stack) {
		List<AttributeEntry> attributes = getOrGenerateAttributes(stack, player);
		
		if (attributes != null && !attributes.isEmpty()) {
			List<Demonization.AttributeEntry> convertedAttributes = new ArrayList<>();
			for (AttributeEntry entry : attributes) {
				convertedAttributes.add(new Demonization.AttributeEntry(
						entry.type, entry.value, entry.slot
				));
			}
			
			AttributeApplicator.applyAttributes(player, convertedAttributes, stack, "divinization");
		}
	}
	
	private static void removeDivinizationAttributes(Player player, EquipmentSlot slot) {
		AttributeApplicator.removeAttributes(player, slot);
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
	
	private static List<AttributeEntry> generateAttributes(ToolStack tool, int level, EquipmentSlot slot) {
		List<AttributeEntry> attributes = new ArrayList<>();
		Random random = new Random();
		
		List<EAttributeType> attributePool = getAttributePoolForSlot(tool, slot);
		if (attributePool.isEmpty()) {
			return attributes;
		}
		
		Set<EAttributeType> selectedTypes = new HashSet<>();
		int maxAttempts = attributePool.size() * 2;
		int attempts = 0;
		
		while (selectedTypes.size() < Math.min(ATTRIBUTES_COUNT, attributePool.size()) && attempts < maxAttempts) {
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
	
	private static List<EAttributeType> getAttributePoolForSlot(ToolStack tool, EquipmentSlot slot) {
		List<EAttributeType> attributePool = new ArrayList<>();
		String toolName = tool.getItem().toString().toLowerCase();
		
		if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
			if (toolName.contains("bow") || toolName.contains("crossbow")) {
				attributePool.addAll(Arrays.asList(
						EAttributeType.DRAW_SPEED,
						EAttributeType.ARROW_SPEED,
						EAttributeType.ARROW_ACCURACY,
						EAttributeType.PROJECTILE_DAMAGE
				));
			} else if (toolName.contains("sword") || toolName.contains("axe") || toolName.contains("mace")) {
				attributePool.addAll(Arrays.asList(
						EAttributeType.ATTACK_DAMAGE,
						EAttributeType.ATTACK_SPEED,
						EAttributeType.CRITICAL_CHANCE,
						EAttributeType.CRITICAL_DAMAGE,
						EAttributeType.FIRE_ASPECT,
						EAttributeType.FROST_ASPECT,
						EAttributeType.LIGHTNING_ASPECT
				));
			} else if (toolName.contains("pickaxe") || toolName.contains("shovel") || toolName.contains("mattock")) {
				attributePool.addAll(Arrays.asList(
						EAttributeType.MINING_SPEED,
						EAttributeType.DURABILITY,
						EAttributeType.HARVEST_LEVEL,
						EAttributeType.EFFICIENCY
				));
			} else {
				attributePool.addAll(Arrays.asList(
						EAttributeType.ATTACK_DAMAGE,
						EAttributeType.ATTACK_SPEED,
						EAttributeType.CRITICAL_CHANCE,
						EAttributeType.CRITICAL_DAMAGE
				));
			}
		} else {
			attributePool.addAll(Arrays.asList(
					EAttributeType.ARMOR,
					EAttributeType.MAX_HEALTH,
					EAttributeType.ARMOR_TOUGHNESS,
					EAttributeType.MOVEMENT_SPEED,
					EAttributeType.KNOCKBACK_RESISTANCE,
					EAttributeType.FEATHER_FALLING,
					EAttributeType.PROTECTION
			));
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
			} catch (Exception e) {
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
		
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("divinization").toString())) {
			applyDivinizationAttributes(player, mainHand);
			handleDivinizedItem(player, mainHand, true);
		} else {
			if (AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.MAINHAND, "divinization")) {
				removeDivinizationAttributes(player, EquipmentSlot.MAINHAND);
			}
		}
		
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
		if (SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("divinization").toString())) {
			applyDivinizationAttributes(player, offHand);
			handleDivinizedItem(player, offHand, false);
		} else {
			if (AttributeApplicator.hasModifierAttributesInSlot(player, EquipmentSlot.OFFHAND, "divinization")) {
				removeDivinizationAttributes(player, EquipmentSlot.OFFHAND);
			}
		}
		
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() == EquipmentSlot.Type.ARMOR) {
				ItemStack armor = player.getItemBySlot(slot);
				if (SimpleTConUtils.hasModifier(armor, NebulaTinker.loadResource("divinization").toString())) {
					applyDivinizationAttributes(player, armor);
					getOrGenerateAttributes(armor, player);
				} else {
					if (AttributeApplicator.hasModifierAttributesInSlot(player, slot, "divinization")) {
						removeDivinizationAttributes(player, slot);
					}
				}
			}
		}
	}
	
	private static void handleDivinizedItem(Player player, ItemStack item, boolean isMainHand) {
		List<AttributeEntry> attributes = getOrGenerateAttributes(item, player);
		
		if (attributes.isEmpty()) {
			return;
		}
		
		long gameTime = player.level().getGameTime();
		if (gameTime % PARTICLE_COOLDOWN == 0 && player.level() instanceof ServerLevel serverLevel) {
			int particleCount = Math.min(3, attributes.size());
			for (int i = 0; i < particleCount; i++) {
				double offsetX = player.getRandom().nextDouble() - 0.5;
				double offsetY = player.getRandom().nextDouble() * 2.0;
				double offsetZ = player.getRandom().nextDouble() - 0.5;
				
				double handOffset = isMainHand ? -0.5 : 0.5;
				serverLevel.sendParticles(ParticleTypes.ENCHANT,
						player.getX() + offsetX + handOffset,
						player.getY() + offsetY,
						player.getZ() + offsetZ,
						1, 0, 0.1, 0, 0);
			}
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
		
		boolean hasDivinizationMain = SimpleTConUtils.hasModifier(mainHand, NebulaTinker.loadResource("divinization").toString());
		boolean hasDivinizationOff = SimpleTConUtils.hasModifier(offHand, NebulaTinker.loadResource("divinization").toString());
		
		if (!hasDivinizationMain && !hasDivinizationOff) {
			return;
		}
		
		ItemStack weapon = hasDivinizationMain ? mainHand : offHand;
		List<AttributeEntry> attributes = getOrGenerateAttributes(weapon, player);
		
		if (attributes.isEmpty()) {
			return;
		}
		
		int level = SimpleTConUtils.getModifierLevel(weapon, NebulaTinker.loadResource("divinization").toString());
		float extraDamage = 0.0f;
		boolean hasCriticalChance = false;
		boolean hasCriticalDamage = false;
		double criticalChanceValue = 0;
		double criticalDamageValue = 0;
		
		for (AttributeEntry attribute : attributes) {
			EAttributeType type = attribute.type;
			if (type == EAttributeType.ATTACK_DAMAGE) {
				extraDamage += (float) (attribute.value * (1.0f + level * 0.1f));
			} else if (type == EAttributeType.CRITICAL_CHANCE) {
				hasCriticalChance = true;
				criticalChanceValue = attribute.value * (1.0f + level * 0.05f);
			} else if (type == EAttributeType.CRITICAL_DAMAGE) {
				hasCriticalDamage = true;
				criticalDamageValue = attribute.value * (1.0f + level * 0.05f);
			} else if (type.getCategory() == EAttributeType.AttributeCategory.ELEMENTAL) {
				applyElementalEffects(event.getEntity(), attribute, player);
			}
		}
		
		float baseDamageBonus = level * 1.5f + 0.5f;
		float finalDamage = event.getAmount() + extraDamage + baseDamageBonus;
		
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
					serverLevel.sendParticles(ParticleTypes.CRIT,
							event.getEntity().getX(),
							event.getEntity().getY() + event.getEntity().getBbHeight() / 2,
							event.getEntity().getZ(),
							10, 0.3, 0.3, 0.3, 0);
					
					serverLevel.playSound(null, player.blockPosition(),
							SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 1.0f);
				}
			}
		}
		
		event.setAmount(finalDamage);
		
		if (player.level() instanceof ServerLevel serverLevel && finalDamage > event.getAmount()) {
			serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
					event.getEntity().getX(),
					event.getEntity().getY() + event.getEntity().getBbHeight(),
					event.getEntity().getZ(),
					(int) (finalDamage - event.getAmount()), 0, 0, 0, 0);
			
			serverLevel.playSound(null, player.blockPosition(),
					SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.3f, 1.5f);
		}
	}
	
	private static void applyElementalEffects(LivingEntity target, AttributeEntry attribute, Player attacker) {
		EAttributeType type = attribute.type;
		double value = attribute.value;
		
		switch (type) {
			case FIRE_ASPECT:
				target.setSecondsOnFire((int) (value / 2));
				spawnParticles(target, ParticleTypes.FLAME, 10);
				break;
			case FROST_ASPECT:
				target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
						(int) (value * 20), (int) (value / 2)));
				target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,
						(int) (value * 10), 0));
				spawnParticles(target, ParticleTypes.SNOWFLAKE, 12);
				break;
			case LIGHTNING_ASPECT:
				spawnParticles(target, ParticleTypes.ELECTRIC_SPARK, 15);
				if (!target.level().isClientSide() && attacker.getRandom().nextFloat() < 0.1f) {
					target.hurt(target.damageSources().lightningBolt(), (float) value);
					spawnParticles(target, ParticleTypes.ELECTRIC_SPARK, 30);
					
					target.level().playSound(null, target.blockPosition(),
							SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.5f, 0.8f);
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
			
			serverLevel.sendParticles(
					particle,
					entity.getX() + offsetX,
					entity.getY() + offsetY,
					entity.getZ() + offsetZ,
					1,
					0,
					0,
					0,
					0
			);
		}
	}
	
	public record AttributeEntry(EAttributeType type, double value, EquipmentSlot slot) {
		public Component getDescription() {
			String key = type.getTranslationKey();
			return Component.translatable(key, String.format("+%.1f", value));
		}
	}
}