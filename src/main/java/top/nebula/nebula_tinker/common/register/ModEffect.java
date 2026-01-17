package top.nebula.nebula_tinker.common.register;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.nebula_tinker.NebulaTinker;
import top.nebula.nebula_tinker.common.effect.CurseOfTheWarGod;

import java.util.function.Supplier;

public class ModEffect {
	public static final DeferredRegister<MobEffect> EFFECTS;
	public static final Supplier<MobEffect> CURSE_OF_THE_WAR_GOD;

	static {
		EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, NebulaTinker.MODID);

		CURSE_OF_THE_WAR_GOD = EFFECTS.register("curse_of_war_god", CurseOfTheWarGod::new);
	}

	public static void register(IEventBus bus) {
		NebulaTinker.LOGGER.info("Nebula Tinker MobEffects Registered!");
		EFFECTS.register(bus);
	}
}