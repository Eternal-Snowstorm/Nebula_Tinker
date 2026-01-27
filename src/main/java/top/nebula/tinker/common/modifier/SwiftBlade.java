package top.nebula.tinker.common.modifier;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import top.nebula.tinker.NebulaTinker;
import top.nebula.tinker.entity.CritCalculator;
import top.nebula.tinker.utils.AttackFeedback;
import top.nebula.tinker.utils.CritUtils;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SwiftBlade extends Modifier {
	
	// 速度效果对暴击的影响常量
	private static final float CRIT_CHANCE_PER_SPEED_LEVEL = 0.05f; // 每级速度增加5%暴击率
	private static final float CRIT_DAMAGE_PER_SPEED_LEVEL = 0.1f;  // 每级速度增加0.1倍暴击伤害
	private static final int MODIFIER_DURATION = 40; // 效果持续2秒（40 ticks）
	
	@SubscribeEvent
	public static void onCriticalHit(CriticalHitEvent event) {
		Player player = event.getEntity();
		
		if (player.getAttackStrengthScale(0.5F) < 0.9F) {
			return;
		}
		
		boolean hasModifier = CritUtils.hasCritModifier(
				player.getItemInHand(InteractionHand.MAIN_HAND),
				CritUtils.MODIFIER_SWIFT_BLADE
		);
		
		if (!hasModifier) {
			return;
		}
		
		if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
			return;
		}
		
		// 获取速度等级
		int speedLevel = player.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1;
		
		// 计算暴击加成
		float critChanceBonus = speedLevel * CRIT_CHANCE_PER_SPEED_LEVEL;
		float critDamageBonus = speedLevel * CRIT_DAMAGE_PER_SPEED_LEVEL;
		
		// 添加临时暴击修饰符
		CritCalculator.addTempCritModifier(player, critChanceBonus, critDamageBonus, MODIFIER_DURATION);
		
		// 触发暴击效果
		AttackFeedback.spawnAbuserCritEffect(player);
	}
}