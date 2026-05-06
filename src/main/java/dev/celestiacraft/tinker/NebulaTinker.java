package dev.celestiacraft.tinker;

import dev.celestiacraft.tinker.common.register.*;
import dev.celestiacraft.tinker.config.ClientConfig;
import dev.celestiacraft.tinker.config.CommonConfig;
import dev.celestiacraft.tinker.config.ServerConfig;
import dev.celestiacraft.tinker.datagen.loot.NTLootModifiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dev.celestiacraft.tinker.common.register.attribute.ModAttributes;
import dev.celestiacraft.tinker.common.register.attribute.GlobalCritAttributes;

@Mod(NebulaTinker.MODID)
public class NebulaTinker {
	public static final String MODID = "nebula_tinker";
	public static final String NAME = "Nebula Tinker";
	public static final Logger LOGGER = LogManager.getLogger(NAME);

	public static ResourceLocation loadResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public NebulaTinker(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		NTModifier.register(bus);
		NTItem.register(bus);
		ModAttributes.register(bus);
		GlobalCritAttributes.register(bus);
		NTParticle.register(bus);
		NTEffect.register(bus);
		NTLootModifiers.REGISTRY.register(bus);
		NTCreativeTab.register(bus);

		registerConfig(context);
		LOGGER.info("Nebula Tinker is initialized!");
	}

	private static void registerConfig(FMLJavaModLoadingContext context) {
		// 普通配置文件
		context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "nebula/tinker/common.toml");
		// 客户端配置文件
		context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "nebula/tinker/client.toml");
		// 服务端配置文件
		context.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "nebula/tinker/server.toml");
	}
}