package top.nebula.nebula_tinker.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

@Mixin(ModifiableBowItem.class)
public class ModifiableBowItemMixin {
	@Redirect(
			method = "releaseUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V"
			)
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
		int mainHandConvergeLevel = SimpleTConUtils.getModifierLevel(
				main,
				NebulaTinker.loadResource("converge").toString());
		int offhandConvergeLevel = SimpleTConUtils.getModifierLevel(
				off,
				NebulaTinker.loadResource("converge").toString());
		if (mainHandConvergeLevel > 4 || offhandConvergeLevel > 4) {
			arrow.shootFromRotation(entity, living.getXRot(), yRot, roll, velocity, inaccuracy);
		} else {
			float originalDirection = xRot - living.getXRot();

			int actualConvergence = Math.max(mainHandConvergeLevel, offhandConvergeLevel);
			float convergedResult = 5 - actualConvergence;
			float direction = convergedResult * originalDirection / 5;
			arrow.shootFromRotation(entity, living.getXRot(), living.getYRot() + direction, roll, velocity, inaccuracy);
		}
	}
}
