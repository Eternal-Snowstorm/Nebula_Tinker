package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;

public class Acupoint extends BasicModifier {
	private static final double LIFE_THRESHOLD = 0.5;
	private static final double TRIGGER_PROBABILITY = 1;

	@Override
	public void onLivingHurt(Player player, LivingEntity entity, LivingHurtEvent event, int level) {
		boolean isBoss = entity.getType().is(Tags.EntityTypes.BOSSES);

		MutableComponent tranKey = Component.translatable("message.nebula_tinker.modifier.acupoint")
				.withStyle(ChatFormatting.RED)
				.withStyle(ChatFormatting.BOLD);

		// BOSS: 阈值减半
		if (isBoss) {
			if (entity.getHealth() <= entity.getMaxHealth() * LIFE_THRESHOLD / 2) {
				trigger(player, entity, event, tranKey);
			}
			return;
		}

		// 普通生物
		if (entity.getHealth() <= entity.getMaxHealth() * LIFE_THRESHOLD) {
			trigger(player, entity, event, tranKey);
		}
	}

	private void trigger(Player player, LivingEntity entity, LivingHurtEvent event, Component component) {
		player.displayClientMessage(component, true);
		spawnSonicBoom(entity);
		event.setAmount(entity.getHealth());
	}

	private void spawnSonicBoom(LivingEntity entity) {
		if (!(entity.level() instanceof ServerLevel level)) {
			return;
		}

		level.sendParticles(
				ParticleTypes.SONIC_BOOM,
				entity.getX(),
				entity.getY() + entity.getBbHeight() * 0.5,
				entity.getZ(),
				1,
				0,
				0,
				0,
				0
		);

		level.playSound(
				null,
				entity.blockPosition(),
				SoundEvents.WARDEN_SONIC_BOOM,
				SoundSource.PLAYERS,
				1.0F,
				1.0F
		);
	}
}