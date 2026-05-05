package dev.celestiacraft.tinker.common.modifier;

import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.api.SimpleTConUtils;
import dev.celestiacraft.tinker.api.modifier.BasicModifier;
import dev.celestiacraft.tinker.common.register.NTModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 喜水
 *
 * <p><b>类型:</b> 全局环境倍率 Modifier (基于 Forge Event 实现)</p>
 *
 * <p><b>适用部位:</b> 护甲 / 工具 / 武器</p>
 *
 * <p><b>核心机制:</b></p>
 * <ul>
 *   <li>基于玩家当前环境(水 / 天气)与药水状态动态计算倍率</li>
 *   <li>所有效果统一通过倍率系统作用于: 挖掘速度 / 伤害 / 减伤</li>
 *   <li>除"涉水"与"完全浸没"互斥外, 其余效果均可叠加</li>
 * </ul>
 *
 * <hr>
 *
 * <p><b>环境效果:</b></p>
 *
 * <ul>
 *   <li>当玩家处于水中(身体接触水):</li>
 *   <ul>
 *     <li>挖掘速度 × 2.5</li>
 *     <li>伤害倍率 × 1.5</li>
 *   </ul>
 *
 *   <li>当玩家完全浸没于水中(视线位于水中):</li>
 *   <ul>
 *     <li>挖掘速度 × 5.0</li>
 *     <li>伤害倍率 × 3.0</li>
 *     <li>受到伤害 -30%</li>
 *   </ul>
 *
 *   <li>当玩家处于降雨环境中:</li>
 *   <ul>
 *     <li>挖掘速度 × 2.0</li>
 *     <li>伤害倍率 × 1.5</li>
 *   </ul>
 *
 *   <li>当玩家处于雷暴环境中:</li>
 *   <ul>
 *     <li>挖掘速度 × 5.0</li>
 *     <li>伤害倍率 × 3.0</li>
 *     <li>受到伤害 -30%</li>
 *   </ul>
 * </ul>
 *
 * <hr>
 *
 * <p><b>药水加成:</b></p>
 *
 * <ul>
 *   <li>设玩家拥有的药水效果数量为 x</li>
 *   <li>挖掘速度倍率 × x</li>
 *   <li>伤害倍率 × (x × 0.75)</li>
 *   <li>受到伤害额外降低 x × 30%</li>
 *   <li>减伤上限为 90%</li>
 * </ul>
 *
 * <hr>
 *
 * <p><b>叠加规则:</b></p>
 *
 * <ul>
 *   <li>涉水 与 完全浸没 互斥, 仅取更高倍率</li>
 *   <li>天气效果(雨 / 雷暴)可与水环境叠加</li>
 *   <li>药水效果与所有环境倍率完全叠加</li>
 *   <li>最终倍率 = 环境倍率 × 药水倍率</li>
 * </ul>
 *
 * <hr>
 *
 * <p><b>实现说明:</b></p>
 *
 * <ul>
 *   <li>使用 Forge Event 统一处理, 而非 TCon Hook</li>
 *   <li>影响范围为玩家全局, 而非单一工具</li>
 *   <li>挖掘速度: PlayerEvent.BreakSpeed</li>
 *   <li>伤害与减伤: LivingHurtEvent</li>
 * </ul>
 *
 * <hr>
 *
 * <p><b>注意:</b></p>
 *
 * <ul>
 *   <li>该 Modifier 仅在玩家持有对应装备时生效(需额外判断)</li>
 *   <li>多来源倍率为乘法叠加, 数值可能快速增长</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Aquadynamic extends BasicModifier {
	/**
	 * 挖掘速度事件
	 *
	 * @param event 事件
	 */
	@SubscribeEvent
	public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();

		if (!hasModifier(player)) {
			return;
		}

		float multiplier = AquaLogic.getFinalMultiplier(player);
		event.setNewSpeed(event.getNewSpeed() * multiplier);
	}

	/**
	 * 实体受伤事件
	 *
	 * @param event 事件
	 */
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		// 攻击者加成
		if (event.getSource().getEntity() instanceof Player attacker) {
			if (hasModifier(attacker)) {
				float multiplier = AquaLogic.getDamageMultiplier(attacker);
				event.setAmount(event.getAmount() * multiplier);
			}
		}

		// 受击减伤
		if (event.getEntity() instanceof Player player) {
			if (hasModifier(player)) {
				float reduction = AquaLogic.getDamageReduction(player);
				event.setAmount(event.getAmount() * (1.0f - reduction));
			}
		}
	}

	/**
	 * 是否拥有词条
	 *
	 * @param player
	 * @return
	 */
	private static boolean hasModifier(Player player) {
		String modifier = NTModifier.AQUADYNAMIC.getId().toString();
		// 主手
		if (!player.getMainHandItem().isEmpty() && SimpleTConUtils.hasModifier(player.getMainHandItem(), modifier)) {
			return true;
		}

		// 副手
		if (!player.getOffhandItem().isEmpty() && SimpleTConUtils.hasModifier(player.getOffhandItem(), modifier)) {
			return true;
		}

		// 盔甲
		for (ItemStack armor : player.getArmorSlots()) {
			if (!armor.isEmpty() && SimpleTConUtils.hasModifier(armor, modifier)) {
				return true;
			}
		}

		return false;
	}

	public static final class AquaLogic {
		/**
		 * 获得最终倍率
		 *
		 * @param player 玩家
		 * @return
		 */
		public static float getFinalMultiplier(Player player) {
			return getEnvironmentMultiplier(player) * getPotionMultiplier(player);
		}

		/**
		 * 获得环境倍率
		 *
		 * @param player 玩家
		 * @return
		 */
		public static float getEnvironmentMultiplier(Player player) {
			Level level = player.level();
			float multiplier = 1.0f;

			if (isSubmerged(player)) {
				multiplier *= 5.0f;
			} else if (isInWater(player)) {
				multiplier *= 2.5f;
			}

			if (isThunder(level, player)) {
				multiplier *= 5.0f;
			} else if (isRaining(level, player)) {
				multiplier *= 2.0f;
			}

			return multiplier;
		}

		/**
		 * 获取药水倍率
		 *
		 * @param player 玩家
		 * @return
		 */
		public static float getPotionMultiplier(Player player) {
			int x = player.getActiveEffects().size();
			return x <= 0 ? 1.0f : x;
		}

		/**
		 * 获取伤害倍率
		 *
		 * @param player 玩家
		 * @return
		 */
		public static float getDamageMultiplier(Player player) {
			return getFinalMultiplier(player) * 0.75f;
		}

		/**
		 * 获取减伤
		 *
		 * @param player 玩家
		 * @return
		 */
		public static float getDamageReduction(Player player) {
			Level level = player.level();

			float reduction = 0.0f;

			// 环境减伤
			if (isSubmerged(player) || isThunder(level, player)) {
				reduction += 0.3f;
			}

			// 药水减伤
			reduction += player.getActiveEffects().size() * 0.3f;

			return Math.min(reduction, 0.9f);
		}

		/**
		 * 是否完全泡入水中
		 *
		 * @param player 玩家
		 * @return
		 */
		private static boolean isSubmerged(Player player) {
			return player.isEyeInFluidType(Fluids.WATER.getFluidType());
		}

		/**
		 * 是否半身入水
		 *
		 * @param player 玩家
		 * @return
		 */
		private static boolean isInWater(Player player) {
			return !isSubmerged(player) && player.isInWater();
		}

		/**
		 * 是否下雨
		 *
		 * @param level  大世界
		 * @param player 玩家
		 * @return
		 */
		private static boolean isRaining(Level level, Player player) {
			return level.isRainingAt(player.blockPosition());
		}

		/**
		 * 是否雷雨
		 *
		 * @param level  大世界
		 * @param player 玩家
		 * @return
		 */
		private static boolean isThunder(Level level, Player player) {
			return level.isThundering() && isRaining(level, player);
		}
	}
}