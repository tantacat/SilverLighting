package com.tantacat.silverlighting.registers;

import com.google.common.collect.Maps;
import com.tantacat.silverlighting.specialboost.*;

import java.util.Map;

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
