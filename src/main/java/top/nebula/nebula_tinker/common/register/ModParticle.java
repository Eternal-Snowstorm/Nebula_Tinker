package top.nebula.nebula_tinker.common.register;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.nebula_tinker.NebulaTinker;

import java.util.function.Supplier;

public class ModParticle {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;
	public static final Supplier<SimpleParticleType> CROSS_CHOP;

	static {
		PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, NebulaTinker.MODID);

		CROSS_CHOP = register("cross_chop");
	}

	private static Supplier<SimpleParticleType> register(String name, boolean overrideLimiter) {
		return PARTICLE_TYPES.register(name, () -> {
			return new SimpleParticleType(overrideLimiter);
		});
	}

	private static Supplier<SimpleParticleType> register(String name) {
		return PARTICLE_TYPES.register(name, () -> {
			return new SimpleParticleType(false);
		});
	}

	public static void register(IEventBus bus) {
		NebulaTinker.LOGGER.info("Nebula Tinker Particles Registered!");
		PARTICLE_TYPES.register(bus);
	}
}