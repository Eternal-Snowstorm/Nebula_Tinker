package top.nebula.nebula_tinker.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.nebula_tinker.NebulaTinker;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID)
public class AttributeEventHandler {
	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = event.getEntity();
		AttributeApplicator.removeAllAttributes(player);
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		Player player = event.getEntity();
		AttributeApplicator.removeAllAttributes(player);
	}
	
	@SubscribeEvent
	public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		Player player = event.getEntity();
		AttributeApplicator.removeAllAttributes(player);
	}
	
	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		Player original = event.getOriginal();
		AttributeApplicator.removeAllAttributes(original);
		Player newPlayer = event.getEntity();
		AttributeApplicator.removeAllAttributes(newPlayer);
	}
	
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			if (event.getServer().getTickCount() % 400 == 0) {
				AttributeApplicator.cleanupExpiredCache();
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Player player = event.player;
			
			if (player.level().getGameTime() % 20 == 0) {
				checkAndRemoveOrphanedEffects(player);
			}
		}
	}
	
	private static void checkAndRemoveOrphanedEffects(Player player) {
		boolean hasDemonization = false;
		boolean hasDivinization = false;
		
		for (var hand : new net.minecraft.world.InteractionHand[]{net.minecraft.world.InteractionHand.MAIN_HAND, net.minecraft.world.InteractionHand.OFF_HAND}) {
			var stack = player.getItemInHand(hand);
			if (SimpleTConUtils.hasModifier(stack, NebulaTinker.loadResource("demonization").toString())) {
				hasDemonization = true;
			}
			if (SimpleTConUtils.hasModifier(stack, NebulaTinker.loadResource("divinization").toString())) {
				hasDivinization = true;
			}
		}
		
		if (!hasDemonization) {
			player.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
			player.removeEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
			player.removeEffect(net.minecraft.world.effect.MobEffects.WITHER);
		}
		
		if (!hasDivinization) {
			player.removeEffect(net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN);
		}
	}
}