package top.nebula.nebula_tinker.common.item;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.nebula_tinker.common.register.ModItem;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InvincibleFrameEgregatorItem extends Item {
	public InvincibleFrameEgregatorItem(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity target = event.getEntity();
		DamageSource source = event.getSource();

		// 伤害来源实体
		Entity srcEntity = source.getEntity();
		if (!(srcEntity instanceof Player player)) {
			return;
		}

		// 判断是否是箭
		if (!source.is(DamageTypeTags.IS_PROJECTILE)) {
			return;
		}

		// 进一步限制为箭(可选)
		if (source.getDirectEntity() != null && source.getDirectEntity().getType() != EntityType.ARROW) {
			return;
		}

		if (!hasItem(player)) {
			return;
		}

		// 清除无敌帧
		target.invulnerableTime = 0;
	}

	private static boolean hasItem(Player player) {
		return CuriosApi.getCuriosInventory(player)
				.map((handler) -> {
					return handler.findFirstCurio((stack) -> {
								return stack.is(ModItem.INVINCIBLE_FRAME_EGREGATOR.get());
							})
							.isPresent();
				}).orElse(false);
	}
}