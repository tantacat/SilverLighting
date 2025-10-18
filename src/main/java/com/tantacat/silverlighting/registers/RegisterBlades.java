package com.tantacat.silverlighting.registers;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.blade.BladeAnimaSheath;
import com.tantacat.silverlighting.blade.BladeAnimaSheathDiamond;
import com.tantacat.silverlighting.blade.BladeAnimaSheathDrape;
import com.tantacat.silverlighting.blade.BladeAnimaSheathEdge;
import com.tantacat.silverlighting.blade.BladeAnimaSheathGleam;
import com.tantacat.silverlighting.blade.BladeAnimaSheathGold;
import com.tantacat.silverlighting.blade.BladeAnimaSheathIron;
import com.tantacat.silverlighting.blade.BladeAnimaSheathPhos;
import com.tantacat.silverlighting.blade.BladeAnimaSheathSpite;
import com.tantacat.silverlighting.blade.BladeAnimaSheathStone;
import com.tantacat.silverlighting.blade.BladeAnimaSheathWood;
import com.tantacat.silverlighting.blade.BladeDokkaebiSheath;
import com.tantacat.silverlighting.blade.BladePureSilver;
import com.tantacat.silverlighting.blade.BladeSilverLighting;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RegisterBlades {

	private RegisterBlades() {}
	
	public static RegisterBlades instance = new RegisterBlades();
	
	public Map<ResourceLocationRaw, ItemStack> SlBladeRegistry = Maps.newHashMap();
    public List<String> SlNamedBlades = Lists.newArrayList();
    
    public void init()
	{
    	BladeAnimaSheath.instance.registerBlade();
    	BladeDokkaebiSheath.instance.registerBlade();
    	BladeSilverLighting.instance.registerBlade();
    	BladePureSilver.instance.registerBlade();
    	BladeAnimaSheathWood.instance.registerBlade();
    	BladeAnimaSheathStone.instance.registerBlade();
    	BladeAnimaSheathIron.instance.registerBlade();
    	BladeAnimaSheathGold.instance.registerBlade();
    	BladeAnimaSheathDiamond.instance.registerBlade();
    	BladeAnimaSheathDrape.instance.registerBlade();
    	BladeAnimaSheathSpite.instance.registerBlade();
    	BladeAnimaSheathEdge.instance.registerBlade();
    	BladeAnimaSheathGleam.instance.registerBlade();
    	BladeAnimaSheathPhos.instance.registerBlade();
    	
    	BladeAnimaSheath.instance.registerRecipe();
    	BladeDokkaebiSheath.instance.registerRecipe();
    	BladeSilverLighting.instance.registerRecipe();
    	BladePureSilver.instance.registerRecipe();
    	BladeAnimaSheathWood.instance.registerRecipe();
    	BladeAnimaSheathStone.instance.registerRecipe();
    	BladeAnimaSheathIron.instance.registerRecipe();
    	BladeAnimaSheathGold.instance.registerRecipe();
    	BladeAnimaSheathDiamond.instance.registerRecipe();
    	BladeAnimaSheathDrape.instance.registerRecipe();
    	BladeAnimaSheathSpite.instance.registerRecipe();
    	BladeAnimaSheathEdge.instance.registerRecipe();
    	BladeAnimaSheathGleam.instance.registerRecipe();
    	BladeAnimaSheathPhos.instance.registerRecipe();
	}
	
	public void registerCustomItemStack(String name, ItemStack blade)
	{
		SlBladeRegistry.put(new ResourceLocationRaw(SilverLightingMain.MODID ,name), blade);
	}
	
	public ItemStack findItemStack(String modid, String name, int count){
        ResourceLocationRaw key = new ResourceLocationRaw(modid, name);
        ItemStack stack = ItemStack.EMPTY;

        if(SlBladeRegistry.containsKey(key)) {
            stack = SlBladeRegistry.get(key).copy();

        }else if(SlashBlade.BladeRegistry.containsKey(key)){
            stack = SlashBlade.BladeRegistry.get(key).copy();
        }else{
            Item item = Item.REGISTRY.getObject(key);
            if (item != null){
                stack = new ItemStack(item);
            }
        }

        if(!stack.isEmpty()) {
            stack.setCount(count);
        }

        return stack;
    }
	
    public ItemStack getCustomBlade(String modid,String name){
        return findItemStack(modid, name, 1);
    }
    
    public ItemStack getCustomBlade(String key){
        String modid;
        String name;
        {
            String str[] = key.split(":",2);
            if(str.length == 2){
                modid = str[0];
                name = str[1];
            }else{
                modid = SilverLightingMain.MODID;
                name = key;
            }
        }

        return getCustomBlade(modid,name);
    }

    public ItemStack getMcItemStack(String name){
        ResourceLocationRaw key = new ResourceLocationRaw("minecraft", name);
        Item item = Item.REGISTRY.getObject(key);
        ItemStack stack = ItemStack.EMPTY;
        if (item != null){
            stack = new ItemStack(item);
        }
        return stack;
    }
	
}
