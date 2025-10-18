package com.tantacat.silverlighting.util;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTHelper {

	private NBTHelper() {};
	
	public static NBTHelper instance = new NBTHelper(); 
	
	public enum EnchantMode {merge, enhance, add}
	public enum ItemType {SlashBlade, ProudSoul, Other}
	
	public void clearNBT(ItemStack item)
	{
		item.setTagCompound(new NBTTagCompound());
	}
	
	public NBTTagCompound mergeProudSoul(NBTTagCompound main, NBTTagCompound... sub)
	{
		int proudsoul_main = ItemAnimaSheath.ProudSoul.get(main, 0);
		int proudsoul_sub = 0;
		for (NBTTagCompound n : sub)
			proudsoul_sub += ItemAnimaSheath.ProudSoul.get(n, 0);
		NBTTagCompound result = main.copy();
		ItemAnimaSheath.ProudSoul.set(result, proudsoul_main + proudsoul_sub);
		return result;
	}
	
	public NBTTagCompound mergeKillCount(NBTTagCompound main, NBTTagCompound... sub)
	{
		int killcount_main = ItemAnimaSheath.KillCount.get(main, 0);
		int killcount_sub = 0;
		for (NBTTagCompound n : sub)
			killcount_sub += ItemAnimaSheath.KillCount.get(n, 0);
		NBTTagCompound result = main.copy();
		ItemAnimaSheath.KillCount.set(result, killcount_main + killcount_sub);
		return result;
	}
	
	public NBTTagCompound mergeRepairCount(NBTTagCompound main, NBTTagCompound... sub)
	{
		int repaircount_main = ItemAnimaSheath.RepairCount.get(main, 0);
		int repaircount_sub = 0;
		for (NBTTagCompound n : sub)
			repaircount_sub += ItemAnimaSheath.RepairCount.get(n, 0);
		NBTTagCompound result = main.copy();
		ItemAnimaSheath.RepairCount.set(result, repaircount_main + repaircount_sub);
		return result;
	}
	
	public NBTTagCompound mergeSE(NBTTagCompound main, NBTTagCompound... sub)
	{
		NBTTagCompound SE_main = main.getCompoundTag("SB.SEffect");
		for (NBTTagCompound n : sub)
		{
			NBTTagCompound SE_sub = n.getCompoundTag("SB.SEffect");
			SE_main.merge(SE_sub);
		}
		NBTTagCompound result = main.copy();
		result.setTag("SB.SEffect", SE_main);
		return result;
	}
	
	public NBTTagCompound mergeOthers(NBTTagCompound main, NBTTagCompound... sub)
	{
		if (!main.hasKey("Owner"))
		{
			for (NBTTagCompound n : sub)
			{
				if (n.hasKey("Owner"))
				{
					main.setUniqueId("Owner", n.getUniqueId("Owner"));
					break;
				}
			}
		}
		return main;
	}
	
	public NBTTagCompound mergeEnchantment(NBTTagCompound main, NBTTagCompound... sub)
	{
		NBTTagList enchants_main = main.getTagList("ench", 10);//main{...{"id":short, "lvl":short}};
		for (NBTTagCompound n : sub)
		{
			NBTTagList enchants = n.getTagList("ench", 10);//sub{...{"id":short, "lvl":short}}
			for (int i = 0; i < enchants.tagCount(); i++)
			{
				NBTTagCompound enchant = enchants.getCompoundTagAt(i);//sub{"id":short, "lvl":short}
				short id = enchant.getShort("id");
				short lvl = enchant.getShort("lvl");
				boolean has_enchant = false;
				for (int j = 0; j < enchants_main.tagCount(); j++)
				{
					NBTTagCompound enchant_main = enchants_main.getCompoundTagAt(j);//main{"id":short, "lvl":short}
					if (enchant_main.getShort("id") == id)
					{
						enchant_main.setShort("lvl", (short)Math.max(lvl, enchant_main.getShort("lvl")));
						has_enchant = true;
					}
				}
				if (!has_enchant)
					enchants_main.appendTag(enchant);
			}
		}
		NBTTagCompound result = main.copy();
		result.setTag("ench", enchants_main);
		return result;
	}
	
	public NBTTagCompound enhanceEnchantment(NBTTagCompound main, NBTTagCompound... sub)
	{
		NBTTagList enchants_main = main.getTagList("ench", 10);//main{...{"id":short, "lvl":short}};
		for (NBTTagCompound n : sub)
		{
			NBTTagList enchants = n.getTagList("ench", 10);//sub{...{"id":short, "lvl":short}}
			for (int i = 0; i < enchants.tagCount(); i++)
			{
				NBTTagCompound enchant = enchants.getCompoundTagAt(i);//sub{"id":short, "lvl":short}
				short id = enchant.getShort("id");
				short lvl = enchant.getShort("lvl");
				boolean has_enchant = false;
				for (int j = 0; j < enchants_main.tagCount(); j++)
				{
					NBTTagCompound enchant_main = enchants_main.getCompoundTagAt(j);//main{"id":short, "lvl":short}
					if (enchant_main.getShort("id") == id)
					{
						enchant_main.setShort("lvl", lvl == enchant_main.getShort("lvl") ?
								(short)(lvl + 1) : (short)Math.max(lvl, enchant_main.getShort("lvl")));
						has_enchant = true;
					}
				}
				if (!has_enchant)
					enchants_main.appendTag(enchant);
			}
		}
		NBTTagCompound result = main.copy();
		result.setTag("ench", enchants_main);
		return result;
	}
	
	public NBTTagCompound addEnchantment(NBTTagCompound main, NBTTagCompound... sub)
	{
		NBTTagList enchants_main = main.getTagList("ench", 10);//main{...{"id":short, "lvl":short}};
		for (NBTTagCompound n : sub)
		{
			NBTTagList enchants = n.getTagList("ench", 10);//sub{...{"id":short, "lvl":short}}
			for (int i = 0; i < enchants.tagCount(); i++)
			{
				NBTTagCompound enchant = enchants.getCompoundTagAt(i);//sub{"id":short, "lvl":short}
				enchants_main.appendTag(enchant);
			}
		}
		NBTTagCompound result = main.copy();
		result.setTag("ench", enchants_main);
		return result;
	}
	
	public NBTTagCompound mergeNBTTagCompound(EnchantMode mode, NBTTagCompound main, NBTTagCompound... sub)
	{
		NBTTagCompound result;
		result = mergeProudSoul(main, sub);
		result = mergeKillCount(result, sub);
		result = mergeRepairCount(result, sub);
		result = mergeSE(result, sub);
		result = mergeOthers(result, sub);
		switch(mode)
		{
		case enhance:
		{
			result = enhanceEnchantment(result, sub);
			break;
		}
		case add:
		{
			result = addEnchantment(result, sub);
			break;
		}
		case merge:
		default:
		{
			result = mergeEnchantment(result, sub);
			break;
		}
		}
		return result;
	}
	
	public boolean fitCurrentItemName(NBTTagCompound main, NBTTagCompound sub)
	{
		return ItemAnimaSheath.CurrentItemName.get(main).equals(ItemAnimaSheath.CurrentItemName.get(sub));
	}
	
	public boolean fitProudSoul(NBTTagCompound main, NBTTagCompound sub)
	{
		return ItemAnimaSheath.ProudSoul.get(main, 0) <= ItemAnimaSheath.ProudSoul.get(sub, 0);
	}
	
	public boolean fitKillCount(NBTTagCompound main, NBTTagCompound sub)
	{
		return ItemAnimaSheath.KillCount.get(main, 0) <= ItemAnimaSheath.KillCount.get(sub, 0);
	}
	
	public boolean fitRepairCount(NBTTagCompound main, NBTTagCompound sub)
	{
		return ItemAnimaSheath.RepairCount.get(main, 0) <= ItemAnimaSheath.RepairCount.get(sub, 0);
	}
	
	public boolean fitSA(NBTTagCompound main, NBTTagCompound sub)
	{
		if (!ItemAnimaSheath.SpecialAttackType.exists(main))
			return true;
		else if (ItemAnimaSheath.SpecialAttackType.exists(sub))
			return ItemAnimaSheath.SpecialAttackType.get(main).equals(ItemAnimaSheath.SpecialAttackType.get(sub));
		else
			return false;
	}
	
	public boolean fitSE(NBTTagCompound main, NBTTagCompound sub)
	{
		boolean result = true; 
		NBTTagCompound SE_main = main.getCompoundTag("SB.SEffect");
		NBTTagCompound SE_sub = sub.getCompoundTag("SB.SEffect");
		for (String name : SE_main.getKeySet())
		{
			if (!SE_sub.hasKey(name))
			{
				result = false;
				break;
			}
		}
		return result;
	}
	
	public boolean fitEnchantment(NBTTagCompound main, NBTTagCompound sub)
	{
		NBTTagList enchants_main = main.getTagList("ench", 10);//main{...{"id":short, "lvl":short}};
		NBTTagList enchants_sub = sub.getTagList("ench", 10);//sub{...{"id":short, "lvl":short}}
		boolean result = true;
		for (int i = 0; i < enchants_main.tagCount(); i++)
		{
			boolean fit_enchant = false;
			NBTTagCompound enchant_main = enchants_main.getCompoundTagAt(i);
			for (int j = 0; j < enchants_sub.tagCount(); j++)
			{
				NBTTagCompound enchant_sub = enchants_sub.getCompoundTagAt(j);
				if (enchant_sub.getShort("id") == enchant_main.getShort("id") && 
					enchant_sub.getShort("lvl") >= enchant_main.getShort("lvl"))
				{
					fit_enchant = true;
					break;
				}
			}
			if (!fit_enchant)
			{
				result = false;
				break;
			}
		}
		return result;
	}
	
	public boolean fitNBTTagCompound(ItemType type, NBTTagCompound main, NBTTagCompound sub)
	{
		boolean result = false;
		switch(type)
		{
		case SlashBlade:
		{
			result = fitCurrentItemName(main, sub) && fitProudSoul(main, sub) && fitKillCount(main, sub) && 
					 fitRepairCount(main, sub) && fitEnchantment(main, sub);
			break;
		}
		case ProudSoul:
		{
			result = fitProudSoul(main, sub) && fitKillCount(main, sub) && fitRepairCount(main, sub) &&
					 fitEnchantment(main, sub) && fitSA(main, sub) && fitSE(main, sub);
			break;
		}
		case Other:
		default :
		{
			result = fitProudSoul(main, sub) && fitKillCount(main, sub) && fitRepairCount(main, sub) &&
					 fitEnchantment(main, sub);
		}
		}
		return result;
	}
	
}
