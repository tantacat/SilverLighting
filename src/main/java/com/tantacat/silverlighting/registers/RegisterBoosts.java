package com.tantacat.silverlighting.registers;

import java.util.Map;

import com.google.common.collect.Maps;
import com.tantacat.silverlighting.specialboost.IOnBoostSwitch;
import com.tantacat.silverlighting.specialboost.SpecialBoostBreak;
import com.tantacat.silverlighting.specialboost.SpecialBoostFightTypeAuto;
import com.tantacat.silverlighting.specialboost.SpecialBoostPressure;
import com.tantacat.silverlighting.specialboost.SpecialBoostPureclear;
import com.tantacat.silverlighting.specialboost.SpecialBoostShardstorm;
import com.tantacat.silverlighting.specialboost.SpecialBoostSound;
import com.tantacat.silverlighting.specialboost.SpecialBoostSpelling;

public class RegisterBoosts {

	private RegisterBoosts() {};
	
	public static RegisterBoosts instance = new RegisterBoosts();
	public SpecialBoostFightTypeAuto autoclose = new SpecialBoostFightTypeAuto();
	public Map<String, IOnBoostSwitch> BoostsHasSwitch = Maps.newHashMap();
	
	public SpecialBoostSound Sound = new SpecialBoostSound();
	public SpecialBoostBreak Break = new SpecialBoostBreak();
	public SpecialBoostPressure Pressure = new SpecialBoostPressure();
	public SpecialBoostSpelling Spelling = new SpecialBoostSpelling();
	public SpecialBoostShardstorm Shardstorm = new SpecialBoostShardstorm();
	public SpecialBoostPureclear Pureclear = new SpecialBoostPureclear();
	
	public void init()
	{
		autoclose.register();
		Sound.register();
		Break.register();
		Pressure.register();
		Spelling.register();
		Shardstorm.register();
		Pureclear.register();
	}
	
}
