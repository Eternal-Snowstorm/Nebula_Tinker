package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;

import java.util.Objects;

public class Frenzy extends BasicModifier {
	@Override
	public void onLivingHurt(Player player, LivingEntity target, LivingHurtEvent event, int level) {
		// 必须有速度效果
		if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
			return;
		}

		// 获取速度等级
		int speedLevel = player.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1;

		// 指数成长
		double exponent = 1.5;
		double baseMin = 0.05;
		double baseMax = 0.10;

		double scale = Math.pow(speedLevel, exponent);

		double minPercent = baseMin * scale;
		double maxPercent = baseMax * scale;

		double heal = event.getAmount() * (minPercent + Math.random() * (maxPercent - minPercent));

		// 保留两位小数
		heal = Math.round(heal * 100.0) / 100.0;

		// 防止溢出
		float newHealth = Math.min(player.getMaxHealth(), (float) (player.getHealth() + heal));
		player.setHealth(newHealth);
	}
}