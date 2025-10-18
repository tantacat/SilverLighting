package com.tantacat.silverlighting.specialeffect;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.registers.RegisterSEs;
import com.tantacat.silverlighting.util.BoostProfileHelper;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;

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
import net.minecraft.nbt.NBTBase;
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
				boolean has_SpellLove = player.getEntityData().hasKey("SpellLove");
				boolean is_Spellling = BoostProfileHelper.isBoostEffective(blade, "Spelling");
				float multipler = 0;
				float num_enchant = is_Spellling ? 0.1f : 0.05f;
				float num_player = 0;
				num_player += has_SpellLove ? 0.25f : 0;
				num_player += is_Spellling && has_SpellLove ? 0.1f : 0;
				for (NBTBase n : blade.getEnchantmentTagList())
					multipler += ((NBTTagCompound)n).getShort("lvl") * num_enchant;
				multipler += num_player;
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
