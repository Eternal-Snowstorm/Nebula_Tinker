package dev.celestiacraft.tinker.common.modifier;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.api.attribute.AttributeHelper;
import dev.celestiacraft.tinker.api.CombatUtils;
import dev.celestiacraft.tinker.api.SimpleTConUtils;

@Mod.EventBusSubscriber
public class ForceLiberation extends Modifier {
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		DamageSource source = event.getSource();

		if (!(entity instanceof Player player)) {
			return;
		}
		if (player.level().isClientSide()) {
			return;
		}

		// 只在受伤状态检测是否有modifier
		if (hasModifier(player, NebulaTinker.loadResource("force_liberation").toString())) {
			CombatUtils.recordHurt(player);
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Player player = event.player;
		if (player.level().isClientSide()) {
			return;
		}

		// 只看 combat 状态
		if (CombatUtils.hurtWindowActive(player, 20 * 3)) {
			AttributeHelper.apply(
					player,
					Attributes.ATTACK_DAMAGE,
					0.3,
					AttributeHelper.AdditionType.MULTIPLY_TOTAL
			);
			AttributeHelper.apply(
					player,
					Attributes.ARMOR,
					0.4,
					AttributeHelper.AdditionType.MULTIPLY_TOTAL
			);
		} else {
			AttributeHelper.remove(player, Attributes.ATTACK_DAMAGE);
			AttributeHelper.remove(player, Attributes.ARMOR);
		}
	}

	private static boolean hasModifier(Player player, String modifierId) {
		// 主手
		ItemStack mainHand = player.getMainHandItem();
		if (!mainHand.isEmpty() && SimpleTConUtils.hasModifier(mainHand, modifierId)) {
			return true;
		}

		// 副手
		ItemStack offHand = player.getOffhandItem();
		if (!offHand.isEmpty() && SimpleTConUtils.hasModifier(offHand, modifierId)) {
			return true;
		}

		// 盔甲
		for (ItemStack armor : player.getArmorSlots()) {
			if (!armor.isEmpty() && SimpleTConUtils.hasModifier(armor, modifierId)) {
				return true;
			}
		}

		return false;
	}
}