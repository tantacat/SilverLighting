package com.tantacat.silverlighting.specialboost;

import java.util.Collections;
import java.util.List;

import com.tantacat.silverlighting.network.PacketSpecialBoostHandler;
import com.tantacat.silverlighting.registers.RegisterBoosts;
import com.tantacat.silverlighting.registers.RegisterSEs;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfile.BoostType;
import com.tantacat.silverlighting.util.BoostProfileHelper;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import com.tantacat.silverlighting.util.OtherUtills;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects.State;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialBoostSpelling implements IOnBoostSwitch 
{
	public BoostProfile profile = new BoostProfile(getId(), false, BoostType.fight);
	
	public static final String active_count = "SL.ActiveCount";
	public static final String loot_count = "SL.LootCount";
	
	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.entity;
		if (player.world.isRemote) return;
		ItemStack blade = event.blade;
		if (!BoostProfileHelper.isBoostEffective(blade, getId())) return;
		
		int sum_level = 0;
		for (ItemStack item : player.inventoryContainer.inventoryItemStacks)
		{
			sum_level += OtherUtills.getSumEnchantmentLevel(item);
		}
		DamageProfile spelling = new DamageProfile(getId(), sum_level, 0, 0, 0);
		DamageProfileHelper.replaceDamageProfile(blade, getId(), spelling);
		
		if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.SpellWeak) == State.None) return;
		if (player.ticksExisted % 20 == 0)
		{
			NBTTagCompound tag = blade.getTagCompound();
			tag.setInteger(active_count, Math.min(350, tag.getInteger(active_count) + 1));
		}
	}
	
	@SubscribeEvent
	public void onSlashBladeImpact(SlashBladeEvent.ImpactEffectEvent event)
	{
		if (!(event.user instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.user;
		if (player.world.isRemote) return;
		ItemStack blade = event.blade;
		if (!BoostProfileHelper.isBoostEffective(blade, getId())) return;
		
		
		NBTTagCompound bladetag = blade.getTagCompound();
		int active = bladetag.getInteger(active_count);
		float chance = 0.01f * (5 + active / 10);
		
		if (player.getRNG().nextFloat() < chance)
		{
			EntityLivingBase target = event.target;
			Enchantment unbreaking = Enchantment.getEnchantmentByID(34);
			List<ItemStack> items = unbreaking.getEntityEquipment(target);
			Collections.shuffle(items);
			int target_sum_level = 0;
			for (ItemStack n : items)
			{
				NBTTagList enchants = n.getEnchantmentTagList();
				for (NBTBase nbt : enchants)
				{
					NBTTagCompound ench = (NBTTagCompound)nbt;
					target_sum_level += ench.getShort("lvl");
				}
			}
			
			for (ItemStack n : items)
			{
				if (!n.isItemEnchanted()) continue;
				
				NBTTagList enchants = n.getEnchantmentTagList();
				int index = player.getRNG().nextInt(enchants.tagCount());
				int ench_id = enchants.getCompoundTagAt(index).getShort("id");
				Enchantment ench = Enchantment.getEnchantmentByID(ench_id);
				
				OtherUtills.addEnchantment(blade, ench, 0, true);
				player.sendMessage(new TextComponentString(new TextComponentTranslation("silverlighting.newline").getFormattedText()
						+ ":" + ench.getTranslatedName(1)));
				
				enchants.removeTag(index);
				n.getTagCompound().setTag("ench", enchants);
				break;
			}
			
			int add_level = 1 + (int)(target_sum_level / 25) + (int)(active / 100);
			player.addExperienceLevel(add_level);
			
			bladetag.setInteger(loot_count, bladetag.getInteger(loot_count) + 1);
			bladetag.setInteger(active_count, Math.max(0, bladetag.getInteger(active_count) - 15));
		}
		
	}
	
	public void register()
	{
		RegisterBoosts.instance.BoostsHasSwitch.put(getId(), this);
		SlashBladeHooks.EventBus.register(this);
	}
	
	public String getId()
	{
		return "Spelling";
	}

	@Override
	public void onBoostOpen(ItemStack blade, EntityPlayer player) {
		
		if (player.world.isRemote) return;
				
		ISpecialEffect spellweak = RegisterSEs.instance.SpellWeak;
		if (SpecialEffects.isEffective(player, blade, spellweak) == State.None)
		{
			SpecialEffects.addEffect(blade, spellweak);
			player.sendMessage(new TextComponentString(new TextComponentTranslation("silverlighting.newline").getFormattedText()
					+ ": "
					+ new TextComponentTranslation("slashblade.seffect.name." + spellweak.getEffectKey()).getFormattedText()
					+ "§r " + (spellweak.getDefaultRequiredLevel() <= player.experienceLevel ? "§c" : "§8") 
					+ spellweak.getDefaultRequiredLevel()));
		}
		else
		{
			BoostProfile boost = BoostProfileHelper.getBoostProfiles(blade).get(0);
			PacketSpecialBoostHandler.switchBoost(blade, boost, player);
			return;
		}
		NBTTagCompound tag = blade.getTagCompound();
		tag.setInteger(active_count, 0);
		tag.setInteger(loot_count, 0);
				
	}

	@Override
	public void onBoostClose(ItemStack blade, EntityPlayer player) {
		
		if (player.world.isRemote) return;
				
		NBTTagCompound tag = blade.getTagCompound();
		int loot = tag.getInteger(active_count);
		int active = tag.getInteger(loot_count);
		tag.removeTag(active_count);
		tag.removeTag(loot_count);
		DamageProfileHelper.removeDamageProfile(blade, getId());
		float chance = 0.01f * (50 + loot * 5 - active * 0.1f);
		if (player.getRNG().nextFloat() < chance)
		{
			ISpecialEffect spellweak = RegisterSEs.instance.SpellWeak;
			NBTTagCompound se = ItemSlashBlade.getSpecialEffect(blade);
			if (se.hasKey(spellweak.getEffectKey()))
			{
				se.removeTag(spellweak.getEffectKey());
				player.sendMessage(new TextComponentString(new TextComponentTranslation("silverlighting.loseline").getFormattedText()
						+ ": "
						+ new TextComponentTranslation("slashblade.seffect.name." + spellweak.getEffectKey()).getFormattedText()
						+ "§r " + (spellweak.getDefaultRequiredLevel() <= player.experienceLevel ? "§c" : "§8") 
						+ spellweak.getDefaultRequiredLevel()));
			
			}
		}
	}
	
}
