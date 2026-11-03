package com.tantacat.silverlighting.registers;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.specialattack.*;

public class RegisterSAs {

	private RegisterSAs() {}
	
	public static RegisterSAs instance = new RegisterSAs();
	
	public SpecialAttackDress dress = new SpecialAttackDress();
	public SpecialAttackDedication dedication = new SpecialAttackDedication();
	public SpecialAttackHope hope = new SpecialAttackHope();
	public SpecialAttackLeafSwim leafswim = new SpecialAttackLeafSwim();
	public SpecialAttackDestory destory = new SpecialAttackDestory();
	public SpecialAttackMurderous murderous = new SpecialAttackMurderous();
	public SpecialAttackSpell spell = new SpecialAttackSpell();
	public SpecialAttackDespell despell = new SpecialAttackDespell();
	public SpecialAttackRecrystal recrystal = new SpecialAttackRecrystal();
	public SpecialAttackNoutou noutou = new SpecialAttackNoutou();
	
	public void init()
	{
		ItemAnimaSheath.specialAttacks.put(dress.id, dress);
		ItemAnimaSheath.specialAttacks.put(dedication.id, dedication);
		ItemAnimaSheath.specialAttacks.put(hope.id, hope);
		ItemAnimaSheath.specialAttacks.put(leafswim.id, leafswim);
		ItemAnimaSheath.specialAttacks.put(destory.id, destory);
		ItemAnimaSheath.specialAttacks.put(murderous.id, murderous);
		ItemAnimaSheath.specialAttacks.put(spell.id, spell);
		ItemAnimaSheath.specialAttacks.put(despell.id, despell);
		ItemAnimaSheath.specialAttacks.put(recrystal.id, recrystal);
		ItemAnimaSheath.specialAttacks.put(noutou.id, noutou);
	}
	
}
