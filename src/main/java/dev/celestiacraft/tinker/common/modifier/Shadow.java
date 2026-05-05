package dev.celestiacraft.tinker.common.modifier;

import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.LauncherHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Shadow extends BasicModifier implements MeleeHitModifierHook, LauncherHitModifierHook {
	@Override
	public void onLauncherHitEntity(IToolStackView view, ModifierEntry entry, Projectile projectile, LivingEntity living, Entity entity, @Nullable LivingEntity livingTarget, float damageDealt) {
		applyVoidDamage(living, livingTarget, entry.getLevel());
	}

	@Override
	public void afterMeleeHit(IToolStackView view, ModifierEntry entry, ToolAttackContext context, float damageDealt) {
		applyVoidDamage(context.getAttacker(), context.getLivingTarget(), entry.getLevel());
	}

	private void applyVoidDamage(LivingEntity attacker, LivingEntity target, int level) {
		if (attacker == null || target == null || target.level().isClientSide()) {
			return;
		}

		float extra = 2 + (level - 1);

		target.invulnerableTime = 0;
		target.hurt(attacker.damageSources().magic(), extra);
	}

	@Override
	protected void registerHooks(ModuleHookMap.Builder builder) {
		builder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.LAUNCHER_HIT);
	}
}