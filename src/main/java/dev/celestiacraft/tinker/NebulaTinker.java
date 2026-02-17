package dev.celestiacraft.tinker;

import dev.celestiacraft.tinker.common.register.*;
import dev.celestiacraft.tinker.config.ClientConfig;
import dev.celestiacraft.tinker.config.CommonConfig;
import dev.celestiacraft.tinker.config.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dev.celestiacraft.tinker.common.register.attribute.ModAttributes;
import dev.celestiacraft.tinker.config.*;
import dev.celestiacraft.tinker.common.register.*;
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
		
		// 注册修饰器
		ModModifier.register(bus);
		// 注册物品
		ModItem.register(bus);
		// 注册自定义属性
		ModAttributes.register(bus);
		// 注册全局暴击属性
		GlobalCritAttributes.register(bus);
		// 粒子效果
		ModParticle.register(bus);
		// 生物状态
		ModEffect.register(bus);
		// 标签页注册(一定要在最后)
		ModCreativeTab.register(bus);
		// 普通配置文件
		context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "nebula/tinker/common.toml");
		// 客户端配置文件
		context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "nebula/tinker/client.toml");
		// 服务端配置文件
		context.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "nebula/tinker/server.toml");
		
		LOGGER.info("Nebula Tinker is initialized!");
	}
}