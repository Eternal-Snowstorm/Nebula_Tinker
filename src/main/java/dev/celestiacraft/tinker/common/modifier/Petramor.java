package dev.celestiacraft.tinker.common.modifier;

import dev.celestiacraft.tinker.tags.ModBlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * 爱石
 * <p>
 * 挖掘 {@link ModBlockTags#PETRAMOR} 时有一定概率回复耐久
 * <p>
 * 回复量为5-10随机
 * <p>
 * 回复的概率默认为 10%, 每升一级提高 10%, 最高 50%
 * <p>
 * 如果挖掘的对象是 {@link Tags.Blocks#ORES} 并且成功触发则可以回复双倍的耐久
 */
public class Petramor extends Modifier implements BlockBreakModifierHook {
	@Override
	public void afterBlockBreak(@NotNull IToolStackView view, @NotNull ModifierEntry entry, @NotNull ToolHarvestContext context) {
		Level level = context.getWorld();
		BlockState state = context.getState();

		if (!state.is(ModBlockTags.PETRAMOR)) {
			return;
		}

		// 每级提高10%, 最大50%
		float chance = Math.min(entry.getLevel() * 0.1f, 0.5f);
		// 回复的耐久量(5-10随机)
		int repairValue = 5 + level.random.nextInt(6);

		if (level.random.nextFloat() < chance) {
			// 如果是矿石回复2倍
			if (state.is(Tags.Blocks.ORES)) {
				repairValue *= 2;
			}

			ToolDamageUtil.repair(view, repairValue);
		}
	}

	@Override
	protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
		builder.addHook(this, ModifierHooks.BLOCK_BREAK);
	}
}