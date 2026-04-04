package dev.celestiacraft.tinker.api.modifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.api.CombatUtils;

import java.util.Comparator;

/**
 * 基础 Modifier 抽象类
 *
 * <p>用于统一处理战斗相关事件(暴击, 伤害, 击杀), 并自动分发给具体 Modifier 实现
 * 所有继承该类的 Modifier 都可以通过重写对应方法来响应事件, 而无需手动注册 Forge 事件</p>
 *
 * <p>支持基于 {@link #getPriority()} 的优先级排序(仅暴击事件), 以及简单的攻击冷却判断</p>
 *
 * <p>当前支持事件: </p>
 * <ul>
 *   <li>{@link CriticalHitEvent} → {@link #onCriticalHit(Player, LivingEntity, CriticalHitEvent, int)}</li>
 *   <li>{@link LivingHurtEvent} → {@link #onLivingHurt(Player, LivingEntity, LivingHurtEvent, int)}</li>
 *   <li>{@link LivingDeathEvent} → {@link #onLivingDeath(Player, LivingEntity, LivingDeathEvent, int)}</li>
 * </ul>
 *
 * <p>仅当玩家手持 {@link IModifiable} 工具时才会触发</p>
 */
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class BasicModifier extends Modifier {
	/**
	 * 当玩家触发暴击时调用
	 *
	 * <p>按 Modifier 优先级({@link #getPriority()})从高到低执行</p>
	 *
	 * <p>如果某个 Modifier 在此事件中调用 {@code event.setResult(Event.Result.ALLOW)},
	 * 则后续 Modifier 将不再执行</p>
	 *
	 * @param player 攻击玩家
	 * @param target 被攻击目标
	 * @param event  Forge 暴击事件
	 * @param level  Modifier 等级
	 */
	protected void onCriticalHit(Player player, LivingEntity target, CriticalHitEvent event, int level) {
	}

	/**
	 * 当玩家造成伤害时调用(命中但未必击杀)
	 *
	 * <p>不会进行优先级排序, 按工具中 Modifier 顺序执行</p>
	 *
	 * @param player 攻击玩家
	 * @param target 被攻击目标
	 * @param event  Forge 伤害事件
	 * @param level  Modifier 等级
	 */
	protected void onLivingHurt(Player player, LivingEntity target, LivingHurtEvent event, int level) {
	}

	/**
	 * 当玩家击杀实体时调用
	 *
	 * @param player 攻击玩家
	 * @param target 被击杀目标
	 * @param event  Forge 死亡事件
	 * @param level  Modifier 等级
	 */
	protected void onLivingDeath(Player player, LivingEntity target, LivingDeathEvent event, int level) {
	}

	@SubscribeEvent
	public static void onCriticalHitEvent(CriticalHitEvent event) {
		Player player = event.getEntity();

		if (!(event.getTarget() instanceof LivingEntity target)) {
			return;
		}

		ItemStack stack = player.getMainHandItem();
		if (!(stack.getItem() instanceof IModifiable)) {
			return;
		}

		ToolStack tool = ToolStack.from(stack);

		tool.getModifierList()
				.stream()
				.sorted(Comparator.comparingInt((ModifierEntry entry) -> {
					if (entry.getModifier() instanceof BasicModifier modifier) {
						return modifier.getPriority();
					}
					return 0;
				}).reversed())
				.forEach((entry) -> {
					if (!(entry.getModifier() instanceof BasicModifier modifier)) {
						return;
					}

					int level = entry.getLevel();
					if (level <= 0) {
						return;
					}

					// 冷却判断(修正：未冷却好才跳过)
					if (modifier.requiresCooldown(player)) {
						return;
					}

					modifier.onCriticalHit(player, target, event, level);

					// 已有Modifier强制暴击后, 后面的不再执行
					if (event.getResult() == Event.Result.ALLOW) {
						return;
					}
				});
	}

	@SubscribeEvent
	public static void onLivingHurtEvent(LivingHurtEvent event) {
		if (!(event.getSource().getEntity() instanceof Player player)) {
			return;
		}

		LivingEntity target = event.getEntity();

		ItemStack stack = player.getMainHandItem();
		if (!(stack.getItem() instanceof IModifiable)) {
			return;
		}

		ToolStack tool = ToolStack.from(stack);

		for (ModifierEntry entry : tool.getModifierList()) {
			if (!(entry.getModifier() instanceof BasicModifier modifier)) {
				continue;
			}

			int level = entry.getLevel();
			if (level <= 0) {
				continue;
			}

			modifier.onLivingHurt(player, target, event, level);
		}
	}

	/**
	 * 是否需要处理攻击冷却
	 *
	 * @return
	 */
	public boolean requiresCooldown(Player player) {
		return CombatUtils.isAttackCooled(player);
	}
}