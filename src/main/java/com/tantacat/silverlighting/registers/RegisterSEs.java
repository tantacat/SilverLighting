package com.tantacat.silverlighting.registers;

import com.tantacat.silverlighting.specialeffect.*;

public class RegisterSEs {

	private RegisterSEs() {};
	
	public static RegisterSEs instance = new RegisterSEs();
	
	public SpecialEffectSoulForge SoulForge = new SpecialEffectSoulForge();
	public SpecialEffectPneuma Pneuma = new SpecialEffectPneuma();
	public SpecialEffectTremor Tremor = new SpecialEffectTremor();
	public SpecialEffectBlooded Blooded = new SpecialEffectBlooded();
	public SpecialEffectSpellWeave SpellWeave = new SpecialEffectSpellWeave();
	public SpecialEffectSpellWeak SpellWeak = new SpecialEffectSpellWeak();
	public SpecialEffectPseudoForm PseudoForm = new SpecialEffectPseudoForm();
	
	public void init()
	{
		SoulForge.register();
		Pneuma.register();
		Tremor.register();
		Blooded.register();
		SpellWeave.register();
		SpellWeak.register();
		PseudoForm.register();
	}
	
}
