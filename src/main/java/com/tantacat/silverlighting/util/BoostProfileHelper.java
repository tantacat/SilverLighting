package com.tantacat.silverlighting.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class BoostProfileHelper {
	
    public static void setBoostProfiles(ItemStack stack, List<BoostProfile> profiles) {
        NBTTagCompound root = stack.getTagCompound();
        NBTTagList profileList = new NBTTagList();
        
        for (BoostProfile profile : profiles) {
            profileList.appendTag(profile.serializeNBT());
        }
        
        root.setTag("BoostProfiles", profileList);
    }

    public static List<BoostProfile> getBoostProfiles(ItemStack stack) {
        List<BoostProfile> profiles = new ArrayList();
        if (!stack.hasTagCompound()) return profiles;
        
        NBTTagCompound root = stack.getTagCompound();
        if (!root.hasKey("BoostProfiles")) 
            return profiles;
        
        NBTTagList profileList = root.getTagList("BoostProfiles", 10);
        for (int i = 0; i < profileList.tagCount(); i++) {
            NBTTagCompound tag = profileList.getCompoundTagAt(i);
            profiles.add(BoostProfile.deserializeNBT(tag));
        }
        
        return profiles;
    }

    public static void addBoostProfile(ItemStack stack, BoostProfile profile) {
        List<BoostProfile> profiles = getBoostProfiles(stack);
        profiles.add(profile);
        setBoostProfiles(stack, profiles);
    }

    public static Optional<BoostProfile> getProfileById(ItemStack stack, String id) {
        return getBoostProfiles(stack).stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    public static boolean removeDamageProfile(ItemStack stack, String id) {
        if (!stack.hasTagCompound()) return false;
        
        List<BoostProfile> profiles = getBoostProfiles(stack);
        // ʹ�õ�������ȫɾ��
        Iterator<BoostProfile> iterator = profiles.iterator();
        boolean removed = false;
        
        while (iterator.hasNext()) {
        	BoostProfile profile = iterator.next();
            if (profile.getId().equals(id)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        
        if (removed) {
            setBoostProfiles(stack, profiles);
        }
        
        return removed;
    }
    
    public static boolean replaceDamageProfile(ItemStack stack, String id, BoostProfile profile)
    {
    	boolean result = false;
    	if (profile.getId().equals(id))
    	{
        	removeDamageProfile(stack, id);
    		addBoostProfile(stack, profile);
    		result = true;
    	}
    	return result;
    }
    
    public static boolean isBoostEffective(ItemStack blade, String id)
    {
    	Optional<BoostProfile> boost = getProfileById(blade, id);
    	if (boost.isPresent() && boost.get().getEnable())
    		return true;
    	else
    		return false;
    }
    
    public static boolean hasBoostProfile(ItemStack blade)
    {
    	return getBoostProfiles(blade).size() != 0;
    }
}
