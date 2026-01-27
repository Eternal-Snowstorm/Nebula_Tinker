package top.nebula.tinker.common.register.attribute;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.tinker.NebulaTinker;

import java.util.function.Supplier;

public class GlobalCritAttributes {
	public static final DeferredRegister<Attribute> ATTRIBUTES;

	public static final Supplier<Attribute> GLOBAL_CRITICAL_CHANCE;
	public static final Supplier<Attribute> GLOBAL_CRITICAL_DAMAGE;
	public static final Supplier<Attribute> CRITICAL_RESISTANCE;
	public static final Supplier<Attribute> CRITICAL_DAMAGE_REDUCTION;

	static {
		ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, NebulaTinker.MODID);

		// 攻击方属性
		GLOBAL_CRITICAL_CHANCE = register("global_critical_chance", 0.05, 0.0, 1.0);

		GLOBAL_CRITICAL_DAMAGE = register("global_critical_damage", 1.5, 1.0, 10.0);

		// 防御方属性
		CRITICAL_RESISTANCE = register("critical_resistance", 0.0, 0.0, 1.0);

		CRITICAL_DAMAGE_REDUCTION = register("critical_damage_reduction", 0.0, 0.0, 0.8);
	}

	private static Supplier<Attribute> register(String name, double defaultValue, double min, double max) {
		return ATTRIBUTES.register(name, () -> {
			return new RangedAttribute(setTranKey(name), defaultValue, min, max).setSyncable(true);
		});
	}

	private static String setTranKey(String key) {
		return String.format("attribute.%s.%s", NebulaTinker.MODID, key);
	}

	public static void register(IEventBus bus) {
		ATTRIBUTES.register(bus);
		NebulaTinker.LOGGER.info("Global Critical Attributes Registered!");
	}
}