package dev.celestiacraft.tinker.common.tools;

import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.common.tools.mining.BlockAOEIterator;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;

/**
 * Tinkers' Construct 工具行为注册。
 * <p>
 * 这里注册的是可以写进 tool definition JSON 的 AOE 词条,
 * 注册完成后即可在 JSON 中使用 {@code "type": "nebula_tinker:block_aoe"}。
 */
public class NTTools {
	/**
	 * 挂接注册事件。
	 * <p>
	 * AOE 迭代器注册在 TConstruct 自己的写入时机 (RECIPE_SERIALIZER 注册阶段) 进行,
	 * 与 TConstruct 的 vein_aoe / box_aoe 等词条保持一致。
	 *
	 * @param bus mod 事件总线, 即 {@code FMLJavaModLoadingContext#getModEventBus()}
	 */
	public static void register(IEventBus bus) {
		bus.addListener(NTTools::onRegister);
	}

	private static void onRegister(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {
			registerAOE();
		}
	}

	/**
	 * 注册 AOE 迭代器本身, 必须在数据包 (tool definition JSON) 被加载之前执行
	 */
	public static void registerAOE() {
		AreaOfEffectIterator.register(NebulaTinker.loadResource("block_aoe"), BlockAOEIterator.LOADER);
	}
}
