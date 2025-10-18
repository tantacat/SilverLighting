package com.tantacat.silverlighting.specialattack;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.network.PacketSpecialShowSpell;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.registers.RegisterSEs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.specialattack.IJustSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.ISuperSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class SpecialAttackSpell extends SpecialAttackBase implements IJustSpecialAttack, ISuperSpecialAttack{

	public int id;
	
	public SpecialAttackSpell() {
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "spell";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
		
		if (player.world.isRemote) return;
		
		// 获取 Tooltip
		ItemStack last_blade = stack.copy();
		
		NBTTagCompound tag_stack = stack.getTagCompound();
		if (ItemAnimaSheath.ProudSoul.tryAdd(tag_stack, -500, false))
		{
			//抽取武器附魔
			Random random = player.getRNG();
			Enchantment enchant = Enchantment.REGISTRY.getRandomObject(random);
			while(enchant.type != EnumEnchantmentType.WEAPON || enchant.isCurse())
				enchant = Enchantment.REGISTRY.getRandomObject(random);
			
			//检查是否已有此附魔
			boolean has_enchant = false;
			NBTTagList enchants = stack.getEnchantmentTagList();
			for (NBTBase n : enchants)
			{
				if (((NBTTagCompound)n).getShort("id") ==  enchant.getEnchantmentID(enchant))
				{
					has_enchant = true;
					break;
				}
			}
			
			//添加附魔或给予经验
			if (has_enchant)
			{
				player.addExperience(random.nextInt(500));
			}
			else
				stack.addEnchantment(enchant, 1);
		}
		
		ItemStack now_blade = stack.copy();
		SilverLightingMain.network.sendTo(new PacketSpecialShowSpell(last_blade, now_blade), (EntityPlayerMP)player);
		
	}

	@Override
	public void doJustSpacialAttack(ItemStack stack, EntityPlayer player) {
		
		if (player.world.isRemote) return;
		
		// 获取 Tooltip
		ItemStack last_blade = stack.copy();
		
		//检查玩家等级 >= 总附魔等级
		int sum_lvl = 0;
		NBTTagList stack_enchants = stack.getEnchantmentTagList();
		for (NBTBase n : stack_enchants)
			sum_lvl += ((NBTTagCompound)n).getShort("lvl");
		if (sum_lvl > player.experienceLevel) return;
		
		NBTTagCompound tag_stack = stack.getTagCompound();
		if (ItemAnimaSheath.ProudSoul.get(tag_stack, 0) >= 1000)
		{
			//附魔池，拔刀剑可有的附魔
			List<Enchantment> enchant_pool = Lists.newArrayList();
			enchant_pool.addAll(EnchantHelper.normal);
			enchant_pool.addAll(EnchantHelper.rare);
			
			//抽取附魔
			Random random = player.getRNG();
			Enchantment enchant = enchant_pool.get(random.nextInt(enchant_pool.size()));
			while(enchant.isCurse())
				enchant = enchant_pool.get(random.nextInt(enchant_pool.size()));
			
			//具有附魔时提升等级
			boolean has_enchant = false;
			boolean up_success = false;
			int max_lvl = enchant.getMaxLevel(); 
			if (ItemAnimaSheath.CurrentItemName.get(tag_stack).equals("silverlighting.animasheath_gleam"))
			{
				int repair_lvl = 0;
				int repaircount = ItemAnimaSheath.RepairCount.get(tag_stack, 0);
				while (repaircount / 10 >= 1)
				{
					repair_lvl ++;
					repaircount /= 10;
				}
				max_lvl += stack_enchants.tagCount() / 5 + repair_lvl;
				max_lvl += tag_stack.getInteger("SpellLove");
			}
			for (NBTBase n_ : stack_enchants)
			{
				NBTTagCompound n = (NBTTagCompound)n_;
				if (n.getShort("id") ==  enchant.getEnchantmentID(enchant))
				{
					has_enchant = true;
					if (n.getShort("lvl") + 1 <= max_lvl)
					{
						//具有附魔且能升级
						n.setShort("lvl", (short)(n.getShort("lvl") + 1));
						up_success = true;
						break;
					}
				}
			}	
			
			//不具有附魔则添加附魔，具有附魔且不能升级则掉落附魔耀魂碎片，具有附魔且能升级时升级
			if (!has_enchant)
				stack.addEnchantment(enchant, 1);
			else if (!up_success)
			{
				ItemStack ProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);
				ProudSoul.addEnchantment(enchant, max_lvl - 1);
				player.entityDropItem(ProudSoul, 0);
			}
			else
				tag_stack.setTag("ench", stack_enchants);
			
			player.addExperienceLevel(-sum_lvl);
			ItemAnimaSheath.ProudSoul.add(tag_stack, -1000);	
		}
		
		ItemStack now_blade = stack.copy();
		SilverLightingMain.network.sendTo(new PacketSpecialShowSpell(last_blade, now_blade), (EntityPlayerMP)player);
	
	}
	
	@Override
	public void doSuperSpecialAttack(ItemStack stack, EntityPlayer player) {

		if (player.world.isRemote) return;
		
		NBTTagCompound tag_stack = stack.getTagCompound();
		
		if (ItemAnimaSheath.CurrentItemName.get(tag_stack).equals("silverlighting.animasheath_gleam"))
		{
			// 获取 Tooltip
			ItemStack last_blade = stack.copy();
			World world = player.world;
			
			//抽取附魔并附魔
			Random random = player.getRNG();
			Enchantment enchant = Enchantment.REGISTRY.getRandomObject(random);
			while(enchant.isCurse())
				enchant = Enchantment.REGISTRY.getRandomObject(random);
			stack.addEnchantment(enchant, 1);
			
			//造成伤害
			AxisAlignedBB bb = new AxisAlignedBB(player.posX, player.posY, player.posZ,
					player.posX, player.posY, player.posZ);
			List<Entity> targets = world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
			
			int enchantCount = stack.getEnchantmentTagList().tagCount();
			DamageProfile profile = DamageProfileHelper.getSumDamageProfile(stack);
			float damage = (profile.getBase() + profile.getExtra() + 10 + ItemAnimaSheath.RepairCount.get(tag_stack, 0))
					* profile.getMultiplier() + profile.getFit();
			int sum_lvl = 0;
			NBTTagList stack_enchants = stack.getEnchantmentTagList();
			for (NBTBase n : stack_enchants)
				sum_lvl += ((NBTTagCompound)n).getShort("lvl");
			damage *= (1 + sum_lvl * 0.005);
			
			for (Entity n : targets)
            {
				if (n instanceof EntityLivingBase)
				{
					if (world.isRemote)
	            	{
	            		for (int i = 0; i < enchantCount; i++) 
			            {
			                EnumParticleTypes particle = EnumParticleTypes.ENCHANTMENT_TABLE;
			                world.spawnParticle(particle, 
			                    n.posX + (world.rand.nextDouble()-0.5), 
			                    n.posY + 1.5 + world.rand.nextDouble(), 
			                    n.posZ + (world.rand.nextDouble()-0.5), 
			                    0, 0.1, 0);
			            }
	            	}
	            	else
	            	{
	            		((EntityLivingBase)n).attackEntityFrom(DamageSource.causeMobDamage(player), damage);
	            		ItemAnimaSheath.updateKillCount(stack, (EntityLivingBase)n, player);
	            	}
				}	
            }
			
			//魔纹病
			SpecialEffects.addEffect(stack, RegisterSEs.instance.SpellWeak);
		
			ItemStack now_blade = stack.copy();
			SilverLightingMain.network.sendTo(new PacketSpecialShowSpell(last_blade, now_blade), (EntityPlayerMP)player);
		}
		else
		{
			int id_despell = RegisterSAs.instance.despell.id;
			ItemAnimaSheath.SpecialAttackType.set(tag_stack, id_despell);
		}
		
	}
}
