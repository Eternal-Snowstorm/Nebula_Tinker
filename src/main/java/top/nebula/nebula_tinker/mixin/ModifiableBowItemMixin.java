package top.nebula.nebula_tinker.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
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

		if (nebulaTinker$getConvergeLevel(main) > 3 || nebulaTinker$getConvergeLevel(off) > 3) {
			arrow.shootFromRotation(entity, living.getXRot(), yRot, roll, velocity, inaccuracy);
		} else if (nebulaTinker$getConvergeLevel(main) == 3 || nebulaTinker$getConvergeLevel(off) == 3) {
			float originalDirection = xRot - living.getXRot();
			float direction = originalDirection / 4;
			arrow.shootFromRotation(entity, living.getXRot(), living.getYRot() + direction, roll, velocity, inaccuracy);
		} else if (nebulaTinker$getConvergeLevel(main) == 2 || nebulaTinker$getConvergeLevel(off) == 2) {
			float originalDirection = xRot - living.getXRot();
			float direction = originalDirection / 2;
			arrow.shootFromRotation(entity, living.getXRot(), living.getYRot() + direction, roll, velocity, inaccuracy);
		} else if (nebulaTinker$getConvergeLevel(main) == 1 || nebulaTinker$getConvergeLevel(off) == 1) {
			float originalDirection = xRot - living.getXRot();
			float direction = 3 * originalDirection / 4;
			arrow.shootFromRotation(entity, living.getXRot(), living.getYRot() + direction, roll, velocity, inaccuracy);
		} else {
			float direction = xRot - living.getXRot();
			arrow.shootFromRotation(entity, living.getXRot(), living.getYRot() + direction, roll, velocity, inaccuracy);
		}
	}

	@Unique
	private int nebulaTinker$getConvergeLevel(ItemStack stack) {
		return SimpleTConUtils.getModifierLevel(
				stack,
				NebulaTinker.loadResource("converge").toString()
		);
	}
}
