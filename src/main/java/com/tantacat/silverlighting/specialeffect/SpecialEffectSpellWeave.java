package com.tantacat.silverlighting.specialeffect;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterSEs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import com.tantacat.silverlighting.util.OtherUtills;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialeffect.IRemovable;
import mods.flammpfeil.slashblade.specialeffect.ISpecialEffect;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects.State;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialEffectSpellWeave implements ISpecialEffect, IRemovable{

	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		ItemStack blade = event.blade;
		EntityPlayer player = (EntityPlayer)event.entity;
		if (blade.getItem() instanceof ItemAnimaSheath)
		{
			if (SpecialEffects.isEffective(player, blade, RegisterSEs.instance.SpellWeave) == State.Effective)
			{
				int sum_ench_level = OtherUtills.getSumEnchantmentLevel(blade);
				int gift_level = blade.getTagCompound().getInteger("SL.Gift");
				float multipler = 0.05f * (sum_ench_level + 0.1f * gift_level);
				DamageProfileHelper.replaceDamageProfile(blade, getEffectKey(),
						new DamageProfile(getEffectKey(), 0, 0, multipler, 0));
			}
			else
				DamageProfileHelper.removeDamageProfile(blade, getEffectKey());
		}
		
		if (blade != player.getHeldItemMainhand() && blade != player.getHeldItemOffhand()) return;
		if (blade.getTagCompound().getCompoundTag("SB.SEffect").hasKey(getEffectKey()))
		{
			World world = player.world;
			if (world.getTotalWorldTime() % 15 == 0 ) {
	            int enchantCount = Math.min(blade.getEnchantmentTagList().tagCount(), 7);
	            for (int i = 0; i < enchantCount; i++) {
	                EnumParticleTypes particle = EnumParticleTypes.ENCHANTMENT_TABLE;
	                world.spawnParticle(particle, 
	                    player.posX + (world.rand.nextDouble()-0.5), 
	                    player.posY + 1.5 + world.rand.nextDouble(), 
	                    player.posZ + (world.rand.nextDouble()-0.5), 
	                    0, 0.1, 0);
	            }
	        }
		}
	}
	
	@SubscribeEvent
	public void onSlashBladeAttack(SlashBladeEvent.ImpactEffectEvent event)
	{
		if (!(event.user instanceof EntityPlayer)) return;
		ItemStack blade = event.blade;
		EntityPlayer player = (EntityPlayer)event.user;
		if (!OtherUtills.isNamedBlade(blade, "silverlighting.animasheath_gleam")) return;
		if (blade.getTagCompound().getInteger("SL.Gift") < 50) return;
		
		if (player.world.isRemote) return;
		if (player.getRNG().nextFloat() < 0.2)
		{
			int sum_ench_level = OtherUtills.getSumEnchantmentLevel(blade);
			ItemSlashBlade.ProudSoul.add(blade.getTagCompound(), sum_ench_level * 10);
			player.addExperience(sum_ench_level);
		}
	}
	
	private EnumParticleTypes getParticleForEnchantment(ItemStack blade, int i) {
		NBTTagCompound enchantTag = blade.getEnchantmentTagList().getCompoundTagAt(i);
        int enchantID = enchantTag.getShort("id");
        
        if (enchantID == Enchantment.getEnchantmentID(Enchantments.FIRE_ASPECT)) 
            return EnumParticleTypes.FLAME;
        if (enchantID == Enchantment.getEnchantmentID(Enchantments.SHARPNESS)) 
            return EnumParticleTypes.CRIT;
        return EnumParticleTypes.ENCHANTMENT_TABLE;
	}

	@Override
	public boolean canCopy(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canRemoval(ItemStack stack) {
		return true;
	}

	@Override
	public void register() {
		SpecialEffects.register(this);
		SlashBladeHooks.EventBus.register(this);
	}

	@Override
	public int getDefaultRequiredLevel() {
		return 70;
	}

	@Override
	public String getEffectKey() {
		return "SpellWeave";
	}

	
	
}
