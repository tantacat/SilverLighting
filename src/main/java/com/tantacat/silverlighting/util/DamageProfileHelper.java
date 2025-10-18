package com.tantacat.silverlighting.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class DamageProfileHelper {
    // 存储伤害配置列表
    public static void setDamageProfiles(ItemStack stack, List<DamageProfile> profiles) {
        NBTTagCompound root = stack.getTagCompound();
        NBTTagList profileList = new NBTTagList();
        
        for (DamageProfile profile : profiles) {
            profileList.appendTag(profile.serializeNBT());
        }
        
        root.setTag("DamageProfiles", profileList);
    }

    // 获取伤害配置列表
    public static List<DamageProfile> getDamageProfiles(ItemStack stack) {
        List<DamageProfile> profiles = new ArrayList();
        if (!stack.hasTagCompound()) return profiles;
        
        NBTTagCompound root = stack.getTagCompound();
        if (!root.hasKey("DamageProfiles")) 
            return profiles;
        
        NBTTagList profileList = root.getTagList("DamageProfiles", 10);
        for (int i = 0; i < profileList.tagCount(); i++) {
            NBTTagCompound tag = profileList.getCompoundTagAt(i);
            profiles.add(DamageProfile.deserializeNBT(tag));
        }
        
        return profiles;
    }

    // 添加单个配置
    public static void addDamageProfile(ItemStack stack, DamageProfile profile) {
        List<DamageProfile> profiles = getDamageProfiles(stack);
        profiles.add(profile);
        setDamageProfiles(stack, profiles);
    }

    // 查找特定配置
    public static Optional<DamageProfile> getProfileById(ItemStack stack, String id) {
        return getDamageProfiles(stack).stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    // 移除特定配置
    public static boolean removeDamageProfile(ItemStack stack, String id) {
        if (!stack.hasTagCompound()) return false;
        
        List<DamageProfile> profiles = getDamageProfiles(stack);
        // 使用迭代器安全删除
        Iterator<DamageProfile> iterator = profiles.iterator();
        boolean removed = false;
        
        while (iterator.hasNext()) {
            DamageProfile profile = iterator.next();
            if (profile.getId().equals(id)) {
                iterator.remove();
                removed = true;
                break;  // 如果只需要删除一个，找到后立即跳出
            }
        }
        
        if (removed) {
        	// 更新回 NBT
            setDamageProfiles(stack, profiles);
        }
        
        return removed;
    }
    
    // 替换特定配置
    public static boolean replaceDamageProfile(ItemStack stack, String id, DamageProfile profile)
    {
    	boolean result = false;
    	if (profile.getId().equals(id))
    	{
        	removeDamageProfile(stack, id);
    		addDamageProfile(stack, profile);
    		result = true;
    	}
    	return result;
    }
    
    public static DamageProfile getSumDamageProfile(ItemStack blade)
    {
    	float base_attack = 0;
		float extra_attack = 0;
		float attack_mulitipler = 0;
		float fit_attack = 0;
		
		List<DamageProfile> damages = DamageProfileHelper.getDamageProfiles(blade);

		for (DamageProfile n : damages)
		{
			base_attack += n.getBase();
			extra_attack += n.getExtra();
			attack_mulitipler += n.getMultiplier();
			fit_attack += n.getFit();
		}

		return new DamageProfile("sum", base_attack, extra_attack, attack_mulitipler, fit_attack);
    }
    
}