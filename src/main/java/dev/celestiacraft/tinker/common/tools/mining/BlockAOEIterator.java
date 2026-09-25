package dev.celestiacraft.tinker.common.tools.mining;

import com.google.common.collect.AbstractIterator;
import dev.celestiacraft.tinker.NebulaTinker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator.AOEMatchType;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * 连锁挖掘 (Block Chaining AOE)。
 * <p>
 * 行为约定:
 * <ul>
 *     <li>只挖掘与目标方块同种 (即 {@link Block} 相同) 的方块;</li>
 *     <li>只沿六个正交方向扩散, 不含对角;</li>
 *     <li>单次连锁破坏的方块总数 (含被点击的那个方块) 默认是 {@link #HARD_LIMIT} (16), 可用 JSON 的
 *         {@code max_blocks} 调整 (范围 1~16);</li>
 *     <li>工具带有 {@code tconstruct:expanded} 强化时, 每一级再额外增加 {@link #EXPANDED_BONUS} (8) 格上限,
 *         即 1 级 24 格、2 级 32 格, 以此类推;</li>
 *     <li>没有最大曼哈顿距离限制, 只要连着就继续向外找;</li>
 *     <li>选取顺序按曼哈顿距离升序 (近的优先), 距离相同时按 x/y/z 字典序固定先后,
 *         因此碰到上限时被舍弃的一定是更远的方块。</li>
 * </ul>
 * 选取策略参照同目录下的 InterlockedEncase 项目
 * ({@code InterlockHelper.getConnectedTargets} + {@code ItemApplicationHandler} 的曼哈顿排序)。
 * <p>
 * 作为数据包词条的用法: {@code {"type": "nebula_tinker:block_aoe", "max_blocks": 16}}
 */
public record BlockAOEIterator(int maxBlocks) implements AreaOfEffectIterator.Loadable {
	/** 基础硬上限: JSON 里的 max_blocks 最多只会取到这个值 */
	public static final int HARD_LIMIT = 16;

	/** {@code tconstruct:expanded} 每一级额外增加的连锁上限 */
	public static final int EXPANDED_BONUS = 8;

	public static final RecordLoadable<BlockAOEIterator> LOADER = RecordLoadable.create(
			IntLoadable.FROM_ZERO.defaultField("max_blocks", HARD_LIMIT, true, BlockAOEIterator::maxBlocks),
			BlockAOEIterator::new
	);

	/**
	 * 收敛基础上限, 任何构造路径 (包含 JSON 反序列化) 都会经过这里。
	 * 这里允许 JSON 写 0 或负数 (由构造器收敛到 1), 避免一个字段写错就让整份 tool definition 解析失败
	 */
	public BlockAOEIterator {
		maxBlocks = Math.min(Math.max(maxBlocks, 1), HARD_LIMIT);
	}

	@Override
	public RecordLoadable<BlockAOEIterator> getLoader() {
		return LOADER;
	}

	/**
	 * 读取工具的 expanded 等级。
	 * <p>
	 * tconstruct:expanded 的强化定义里用的是 tconstruct:volatile_int (each_level: 1),
	 * 也就是每一级都会把 volatile int "tconstruct:expanded" 加 1,
	 * 所以直接读这个值就等价于读它的等级 (TConstruct 自己的各种 AOE 迭代器也这么读)
	 *
	 * @param tool 工具
	 * @return expanded 等级, 没有该强化时为 0
	 */
	public static int getExpandedLevel(IToolStackView tool) {
		return Math.max(tool.getVolatileData().getInt(IModifiable.EXPANDED), 0);
	}

	/**
	 * 计算本次连锁的实际方块上限
	 *
	 * @param expandedLevel expanded 等级
	 * @return 基础上限 + expanded 等级 * {@link #EXPANDED_BONUS}
	 */
	public int getLimit(int expandedLevel) {
		return Math.max(maxBlocks + expandedLevel * EXPANDED_BONUS, 1);
	}

	@Override
	public Iterable<BlockPos> getBlocks(IToolStackView tool, UseOnContext context, BlockState state, AOEMatchType matchType) {
		int expanded = getExpandedLevel(tool);
		int limit = getLimit(expanded);

		// 结果包含被点击的方块本身: ToolHarvestLogic 会先破坏它, 再由 isAir 检查跳过重复项,
		// 所以最终破坏数量正好是 limit
		Iterable<BlockPos> blocks = calculate(context.getLevel(), context.getClickedPos(), state.getBlock(), limit);

		// ---- 诊断日志 (排查完成后可以删除这几行) ----
		// 客户端描边是 DISPLAY, 每帧都会调用, 所以这里只对非 DISPLAY (实际破坏/转换) 打印。
		// 挖方块后若看不到这条日志, 说明服务端根本没走到 AOE 迭代器, 问题在 tool definition 配置上
		// (最常见: 缺少 tconstruct:is_effective 模块, 使 ToolHarvestContext#isEffective 为 false)。
		if (matchType != AOEMatchType.DISPLAY) {
			List<BlockPos> materialized = new ArrayList<>();
			for (BlockPos pos : blocks) {
				materialized.add(pos);
			}
			NebulaTinker.LOGGER.debug("[block_aoe] matchType={}, origin={}, limit={} (base={}, expanded={}), chained={}",
					matchType, context.getClickedPos(), limit, maxBlocks, expanded, materialized.size());
			return materialized;
		}
		// ---- 诊断日志结束 ----

		return blocks;
	}

	/**
	 * 计算连锁结果
	 *
	 * @param world  世界
	 * @param origin 起始位置 (会被包含在结果里)
	 * @param target 目标方块
	 * @param limit  结果数量上限
	 */
	public static Iterable<BlockPos> calculate(Level world, BlockPos origin, Block target, int limit) {
		return () -> new ChainingIterator(world, origin, target, limit);
	}

	private static final Direction[] DIRECTIONS = Direction.values();

	/**
	 * 只沿六个正交方向在同类方块之间扩散 (不相邻就不连接, 因此不会穿过异种方块),
	 * 并以曼哈顿距离为优先级由近及远吐出方块
	 */
	private static class ChainingIterator extends AbstractIterator<BlockPos> {
		private static final Comparator<DistancePos> ORDER = Comparator
				.comparingInt(DistancePos::distance)
				.thenComparingInt(entry -> entry.pos().getX())
				.thenComparingInt(entry -> entry.pos().getY())
				.thenComparingInt(entry -> entry.pos().getZ());

		private final Set<BlockPos> visited = new HashSet<>();
		private final Queue<DistancePos> queue = new PriorityQueue<>(ORDER);

		private final Level world;
		private final Block target;
		private final int limit;
		private int found = 0;

		private ChainingIterator(Level world, BlockPos origin, Block target, int limit) {
			this.world = world;
			this.target = target;
			this.limit = limit;
			if (limit > 0) {
				// 起点以距离 0 入队, 它就是第一个被返回的方块
				this.visited.add(origin);
				this.queue.add(new DistancePos(origin, 0));
			}
		}

		/**
		 * 把六个正交方向的邻居加入队列 (不含对角)
		 *
		 * @param pos      当前位置
		 * @param distance 邻居的曼哈顿距离
		 */
		private void enqueueNeighbors(BlockPos pos, int distance) {
			for (Direction direction : DIRECTIONS) {
				BlockPos offset = pos.relative(direction);
				// 标记已访问, 避免在真正处理到它之前被重复入队
				if (visited.add(offset)) {
					queue.add(new DistancePos(offset, distance));
				}
			}
		}

		@Override
		protected BlockPos computeNext() {
			while (found < limit && !queue.isEmpty()) {
				DistancePos next = queue.poll();
				BlockPos pos = next.pos();
				// 只有同种方块才会被挖掉, 也才会继续向外扩散
				if (world.getBlockState(pos).is(target)) {
					found++;
					enqueueNeighbors(pos, next.distance() + 1);
					return pos;
				}
			}
			return endOfData();
		}
	}

	/** 辅助数据: 位置 + 曼哈顿距离 */
	private record DistancePos(BlockPos pos, int distance) {
	}
}
