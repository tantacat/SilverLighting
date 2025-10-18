package com.tantacat.silverlighting.registers;

import com.tantacat.silverlighting.client.keybinding.KeyBindingSpeicalBoost;

public class RegisterKeyBindings {

	private RegisterKeyBindings() {};
	
	public static RegisterKeyBindings instance = new RegisterKeyBindings();
	
	public KeyBindingSpeicalBoost key_specialboost = new KeyBindingSpeicalBoost();
	
	public void init()
	{
		key_specialboost.register();
	}
}
