package top.nebula.nebula_tinker.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.utils.SimpleTConUtils;

@Mixin(ModifiableBowItem.class)
public class ModifiableBowItemMixin {

	@ModifyArgs(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V"))
	public void releaseUsing(Args args) {
		LivingEntity living = args.get(0);
		ItemStack bowInMainHand = living.getMainHandItem();
		ItemStack bowInOffhand = living.getOffhandItem();
		if (isRapidShot(bowInMainHand) || isRapidShot(bowInOffhand)) {
			args.set(1, living.getXRot());
		} else {
			float spread = args.get(1);
			float direction = spread - living.getXRot();
			args.set(1, living.getXRot());
			args.set(2, living.getYRot() + direction);
		}
	}

	private boolean isRapidShot(ItemStack stack) {
		return SimpleTConUtils.hasModifier(
				stack,
				NebulaTinker.loadResource("rapid_shot").toString()
		);
	}
}
