package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import dev.celestiacraft.tinker.api.CombatUtils;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;

public class CaptureKing extends BasicModifier {
	private static final float CRIT_MULTIPLIER = 1.5F;
	private static final double BASE_CRIT_CHANCE = 0.15;
	private static final double CRIT_PER_LEVEL = 0.05d;

	@Override
	public void onCriticalHit(Player player, LivingEntity entity, CriticalHitEvent event, int level) {
		boolean isBoss = entity.getType().is(Tags.EntityTypes.BOSSES);

		// 只对 BOSS 生效
		if (!isBoss) {
			return;
		}

		// 计算暴击率
		double critChance = BASE_CRIT_CHANCE + level * CRIT_PER_LEVEL;
		critChance = Math.min(critChance, 1.0D);

		if (player.getRandom().nextDouble() > critChance) {
			return;
		}

		MutableComponent text = Component.translatable("message.nebula_tinker.modifier.capture_king")
				.withStyle(ChatFormatting.RED)
				.withStyle(ChatFormatting.BOLD);

		player.displayClientMessage(text, true);
		CombatUtils.spawnAbuserCritEffect(player);

		event.setResult(Event.Result.ALLOW);
		event.setDamageModifier(CRIT_MULTIPLIER);
	}
}