package dev.celestiacraft.tinker.api.modifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.api.CombatUtils;

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

	/**
	 * 暴击处理入口
	 *
	 * <p>会自动: </p>
	 * <ul>
	 *   <li>筛选玩家主手 {@link IModifiable} 工具</li>
	 *   <li>获取 {@link ToolStack}</li>
	 *   <li>按优先级排序 Modifier</li>
	 *   <li>逐个调用 {@link #onCriticalHit}</li>
	 * </ul>
	 */
	@SubscribeEvent
	public static void onCriticalHitEvent(CriticalHitEvent event) {
	}

	/**
	 * 伤害处理入口
	 *
	 * <p>会自动: </p>
	 * <ul>
	 *   <li>判断伤害来源是否为玩家</li>
	 *   <li>筛选玩家主手 {@link IModifiable} 工具</li>
	 *   <li>逐个调用 {@link #onLivingHurt}</li>
	 * </ul>
	 */
	@SubscribeEvent
	public static void onLivingHurtEvent(LivingHurtEvent event) {
	}

	/**
	 * 是否需要攻击冷却完成后才触发 Modifier
	 *
	 * <p>默认实现: 只有当攻击冷却完成时才会触发(即"满蓄力攻击")</p>
	 *
	 * <p>子类可以覆盖此方法实现: </p>
	 * <ul>
	 *   <li>忽略冷却(始终触发)</li>
	 *   <li>自定义冷却逻辑</li>
	 * </ul>
	 *
	 * @param player 玩家
	 * @return {@code true} 表示当前不应触发(冷却未完成), {@code false} 表示可以触发
	 */
	public boolean requiresCooldown(Player player) {
		return CombatUtils.isAttackCooled(player);
	}
}