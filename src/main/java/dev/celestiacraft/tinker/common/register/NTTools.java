package dev.celestiacraft.tinker.common.register;

import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.common.tools.mining.BlockAOEIterator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;

public class NTTools {
	public static void register(IEventBus bus) {
		bus.addListener(NTTools::onRegister);
	}

	private static void onRegister(RegisterEvent event) {
		ResourceKey<? extends Registry<?>> registryKey = event.getRegistryKey();

		if (registryKey.equals(Registries.RECIPE_SERIALIZER)) {
			AreaOfEffectIterator.register(NebulaTinker.loadResource("block_aoe"), BlockAOEIterator.LOADER);
		}
	}
}