package top.nebula.nebula_tinker.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

@Mixin(value = ModifiableBowItem.class, remap = true)
public class ModifiableBowItemMixin {
	@Redirect(
			method = "releaseUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V"
			),
			remap = true
	)
	private void nebulaTinker$redirectShootFromRotation(
			AbstractArrow arrow,
			Entity entity,
			float xRot,
			float yRot,
			float roll,
			float velocity,
			float inaccuracy
	) {
		if (!(entity instanceof LivingEntity living)) {
			arrow.shootFromRotation(entity, xRot, yRot, roll, velocity, inaccuracy);
			return;
		}

		ItemStack main = living.getMainHandItem();
		ItemStack off = living.getOffhandItem();

		if (nebulaTinker$isRapidShot(main) || nebulaTinker$isRapidShot(off)) {
			// 你想要的快速射击逻辑
			arrow.shootFromRotation(entity, living.getXRot(), yRot, roll, velocity, inaccuracy);
		} else {
			float direction = xRot - living.getXRot();
			arrow.shootFromRotation(entity, living.getXRot(), living.getYRot() + direction / 2, roll, velocity, inaccuracy);
		}
	}

	@Unique
	private boolean nebulaTinker$isRapidShot(ItemStack stack) {
		return SimpleTConUtils.hasModifier(
				stack,
				NebulaTinker.loadResource("rapid_shot").toString()
		);
	}
}
