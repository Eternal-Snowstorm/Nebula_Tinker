package dev.celestiacraft.tinker.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.api.SimpleTConUtils;

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

		for (InteractionHand hand : new InteractionHand[]{
				InteractionHand.MAIN_HAND,
				InteractionHand.OFF_HAND
		}) {
			ItemStack stack = player.getItemInHand(hand);
			if (SimpleTConUtils.hasModifier(stack, NebulaTinker.loadResource("demonization").toString())) {
				hasDemonization = true;
			}
			if (SimpleTConUtils.hasModifier(stack, NebulaTinker.loadResource("divinization").toString())) {
				hasDivinization = true;
			}
		}

		if (!hasDemonization) {
			player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
			player.removeEffect(MobEffects.WEAKNESS);
			player.removeEffect(MobEffects.WITHER);
		}

		if (!hasDivinization) {
			player.removeEffect(MobEffects.DIG_SLOWDOWN);
		}
	}
}