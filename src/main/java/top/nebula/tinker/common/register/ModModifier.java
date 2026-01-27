package top.nebula.tinker.common.register;

import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import top.nebula.tinker.NebulaTinker;
import top.nebula.tinker.common.modifier.*;

/**
 * 由于暂时没空写Json, 因此在这里写一下一些注意事项, 到时候写Json时避免忘记
 * 每个强化的暴击效果不可叠加, 例如施虐者和迅捷之刃不能叠加
 */
public class ModModifier {
	public static final ModifierDeferredRegister MODIFIERS;
	public static final StaticModifier<Abuser> ABUSER;
	public static final StaticModifier<Acupoint> ACUPOINT;
	public static final StaticModifier<CaptureKing> CAPTURE_KING;
	public static final StaticModifier<CausalTruncation> CAUSAL_TRUNCATION;
	public static final StaticModifier<Modifier> CONVERGE;
	public static final StaticModifier<DeathEcho> DEATH_ECHO;
	public static final StaticModifier<Demonization> DEMONIZATION;
	public static final StaticModifier<Divinization> DIVINIZATION;
	public static final DynamicModifier DIVINE_DEMONIC_HARMONY;
	public static final StaticModifier<SwiftBlade> SWIFT_BLADE;
	public static final StaticModifier<KillingRhythm> KILLING_RHYTHM;
	public static final StaticModifier<ForceLiberation> FORCE_LIBERATION;

	public static final StaticModifier<Frenzy> FRENZY;

	static {
		MODIFIERS = ModifierDeferredRegister.create(NebulaTinker.MODID);

		// 动态Modifiers
		ABUSER = MODIFIERS.register("abuser", Abuser::new);
		ACUPOINT = MODIFIERS.register("acupoint", Acupoint::new);
		CAPTURE_KING = MODIFIERS.register("capture_king", CaptureKing::new);
		CAUSAL_TRUNCATION = MODIFIERS.register("causal_truncation", CausalTruncation::new);
		CONVERGE = MODIFIERS.register("converge", Modifier::new);
		DEATH_ECHO = MODIFIERS.register("death_echo", DeathEcho::new);
		DEMONIZATION = MODIFIERS.register("demonization", Demonization::new);
		DIVINIZATION = MODIFIERS.register("divinization", Divinization::new);
		FRENZY = MODIFIERS.register("frenzy", Frenzy::new);
		SWIFT_BLADE = MODIFIERS.register("swift_blade", SwiftBlade::new);
		KILLING_RHYTHM = MODIFIERS.register("killing_rhythm", KillingRhythm::new);
		FORCE_LIBERATION = MODIFIERS.register("force_liberation", ForceLiberation::new);

		// 静态Modifiers
		DIVINE_DEMONIC_HARMONY = MODIFIERS.registerDynamic("divine_demonic_harmony");
	}

	public static void register(IEventBus bus) {
		NebulaTinker.LOGGER.info("Nebula Tinker TCon Modifiers Registered!");
		MODIFIERS.register(bus);
	}
}