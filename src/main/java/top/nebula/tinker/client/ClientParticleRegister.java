package top.nebula.tinker.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.nebula.tinker.NebulaTinker;
import top.nebula.tinker.common.particle.provider.CrossChopParticleProvider;
import top.nebula.tinker.common.register.ModParticle;

@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientParticleRegister {
	@SubscribeEvent
	public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticle.CROSS_CHOP.get(), CrossChopParticleProvider::new);
	}
}