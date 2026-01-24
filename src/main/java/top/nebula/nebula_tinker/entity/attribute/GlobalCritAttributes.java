package top.nebula.nebula_tinker.entity.attribute;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.nebula.nebula_tinker.NebulaTinker;

public class GlobalCritAttributes {
	public static final DeferredRegister<Attribute> ATTRIBUTES =
			DeferredRegister.create(ForgeRegistries.ATTRIBUTES, NebulaTinker.MODID);
	
	// 攻击方属性
	public static final RegistryObject<Attribute> GLOBAL_CRITICAL_CHANCE = ATTRIBUTES.register(
			"global_critical_chance",
			() -> new RangedAttribute(
					"attribute.nebula_tinker.global_critical_chance",
					0.05,  // 默认5%暴击率
					0.0,
					1.0
			).setSyncable(true)
	);
	
	public static final RegistryObject<Attribute> GLOBAL_CRITICAL_DAMAGE = ATTRIBUTES.register(
			"global_critical_damage",
			() -> new RangedAttribute(
					"attribute.nebula_tinker.global_critical_damage",
					1.5,  // 默认1.5倍暴击伤害
					1.0,
					10.0
			).setSyncable(true)
	);
	
	// 防御方属性（降低被暴击）
	public static final RegistryObject<Attribute> CRITICAL_RESISTANCE = ATTRIBUTES.register(
			"critical_resistance",
			() -> new RangedAttribute(
					"attribute.nebula_tinker.critical_resistance",
					0.0,  // 默认0%暴击抵抗
					0.0,
					1.0
			).setSyncable(true)
	);
	
	public static final RegistryObject<Attribute> CRITICAL_DAMAGE_REDUCTION = ATTRIBUTES.register(
			"critical_damage_reduction",
			() -> new RangedAttribute(
					"attribute.nebula_tinker.critical_damage_reduction",
					0.0,  // 默认0%暴击伤害减免
					0.0,
					0.8   // 最多减免80%
			).setSyncable(true)
	);
	
	public static void register(IEventBus bus) {
		ATTRIBUTES.register(bus);
		NebulaTinker.LOGGER.info("Global Critical Attributes Registered!");
	}
}