package com.tantacat.silverlighting.specialattack;

import java.util.List;

import com.google.common.collect.Lists;
import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.network.PacketSpecialShowSpell;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.registers.RegisterSEs;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import com.tantacat.silverlighting.util.OtherUtills;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialattack.IJustSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.ISuperSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import mods.flammpfeil.slashblade.specialeffect.SpecialEffects;
import mods.flammpfeil.slashblade.util.EnchantHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

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
			
		boolean isGleam = OtherUtills.isNamedBlade(stack, "silverlighting.animasheath_gold") || 
				OtherUtills.isNamedBlade(stack, "silverlighting.animasheath_gleam");
		
		if (isGleam && player.isSneaking())
			doSpacialAttackGift(stack, player);
		else
		{
			NBTTagCompound bladetag = stack.getTagCompound();

			if (ItemSlashBlade.ProudSoul.get(bladetag, 0) < 50 || player.experienceTotal < 500)
			{
				if (player.world.isRemote)
					player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, 1, 1);
				return;
			}
			
			if (player.world.isRemote) return;

			ItemStack last_blade = stack.copy();

			List<Enchantment> sword = Lists.newArrayList();
			sword.addAll(EnchantHelper.normal);
			sword.removeAll(EnchantHelper.rare);
			Enchantment ench = sword.get(player.getRNG().nextInt(sword.size()));
			while(ench.isCurse())
				ench = sword.get(player.getRNG().nextInt(sword.size()));
			int gift_level = isGleam ? bladetag.getInteger("SL.Gift") : 0;
			OtherUtills.addEnchantment(stack, ench, gift_level, false);
			
			OtherUtills.removePlayerXP(player, 500);
			ItemSlashBlade.ProudSoul.add(bladetag, -50);	
			
			ItemStack now_blade = stack.copy();
			SilverLightingMain.network.sendTo(new PacketSpecialShowSpell(last_blade, now_blade), (EntityPlayerMP)player);
		}
	}

	@Override
	public void doJustSpacialAttack(ItemStack stack, EntityPlayer player) {
		
		boolean isGleam = OtherUtills.isNamedBlade(stack, "silverlighting.animasheath_gold") || 
				OtherUtills.isNamedBlade(stack, "silverlighting.animasheath_gleam");
		
		NBTTagCompound bladetag = stack.getTagCompound();

		if (ItemSlashBlade.ProudSoul.get(bladetag, 0) < 1000 || player.experienceTotal < 100)
		{
			if (player.world.isRemote)
				player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, 1, 1);
			return;
		}
		
		if (player.world.isRemote) return;

		ItemStack last_blade = stack.copy();

		Enchantment ench = EnchantHelper.getEnchantmentNormal(player.getRNG());
		while(ench.isCurse())
			ench = EnchantHelper.getEnchantmentNormal(player.getRNG());
		int ench_level = EnchantmentHelper.getEnchantmentLevel(ench, stack);
		int max_level = ench.getMaxLevel() + (isGleam ? bladetag.getInteger("SL.Gift") / 10 : 0);
		if (ench_level >= max_level)
		{
			ItemStack enchantProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);
			enchantProudSoul.addEnchantment(ench, 1);
			player.entityDropItem(enchantProudSoul, 0.0f);
		}
		int gift_level = isGleam ? bladetag.getInteger("SL.Gift") : 0;
		OtherUtills.addEnchantment(stack, ench, gift_level, false);
		
		OtherUtills.removePlayerXP(player, 100);
		ItemSlashBlade.ProudSoul.add(bladetag, -1000);	
		
		ItemStack now_blade = stack.copy();
		SilverLightingMain.network.sendTo(new PacketSpecialShowSpell(last_blade, now_blade), (EntityPlayerMP)player);
	
	}
	
	@Override
	public void doSuperSpecialAttack(ItemStack stack, EntityPlayer player) {

		boolean isGleam = OtherUtills.isNamedBlade(stack, "silverlighting.animasheath_gold") || 
				OtherUtills.isNamedBlade(stack, "silverlighting.animasheath_gleam");
		
		NBTTagCompound bladetag = stack.getTagCompound();
		
		if (isGleam)
		{
			AxisAlignedBB bb = new AxisAlignedBB(player.posX - 32, player.posY - 16, player.posZ - 32, 
					player.posX + 32, player.posY + 16, player.posZ + 32);
			
			boolean shouldIll = ItemSlashBlade.RepairCount.get(bladetag, 0) < 1;
			
			List<Entity> targets = player.world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
			
			if (shouldIll)
			{
				for (Entity n : targets)
				{
					boolean has_enchanted_item = false;
					if (!(n instanceof EntityLivingBase)) continue;
					EntityLivingBase living = (EntityLivingBase)n;
					Enchantment unbreaking = Enchantment.getEnchantmentByID(34);
					Iterable<ItemStack> items = unbreaking.getEntityEquipment(living);
					for (ItemStack item : items)
					{
						if (item.isItemEnchanted())
						{
							has_enchanted_item = true;
							break;
						}
					}
					if (has_enchanted_item)
					{
						shouldIll = false;
						break;
					}
				}
			}
			
			ItemStack last_blade = stack.copy();
			
			UntouchableTime.setUntouchableTime(player, 30, true);
			for (Entity n : targets)
			{
				if (!(n instanceof EntityLivingBase)) continue;
				EntityLivingBase living = (EntityLivingBase)n;
				DamageProfile sum = DamageProfileHelper.getSumDamageProfile(stack);
				float damage = sum.getSum(stack) * (1 + 0.05f * OtherUtills.getSumEnchantmentLevel(stack));
				living.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
				ItemSlashBlade.updateKillCount(stack, living, player);
				StunManager.setStun(living, 40);
				StunManager.setFreeze(living, 40);
			}
			
			Enchantment ench = Enchantment.REGISTRY.getRandomObject(player.getRNG());
			while(ench.isCurse())
				ench = Enchantment.REGISTRY.getRandomObject(player.getRNG());
			OtherUtills.addEnchantment(stack, ench, 0, true);
			
			if (shouldIll)
				SpecialEffects.addEffect(stack, RegisterSEs.instance.SpellWeak);
			else
				ItemSlashBlade.RepairCount.add(bladetag, -1);
			
			ItemStack now_blade = stack.copy();
			SilverLightingMain.network.sendTo(new PacketSpecialShowSpell(last_blade, now_blade), (EntityPlayerMP)player);
		}
		else
		{
			int SA_id = ItemSlashBlade.SpecialAttackType.get(bladetag, 0);
			if (SA_id == this.id)
				ItemSlashBlade.SpecialAttackType.set(bladetag, RegisterSAs.instance.despell.id);
		}
	}
	
	private void doSpacialAttackGift(ItemStack stack, EntityPlayer player){
		
		NBTTagCompound bladetag = stack.getTagCompound();

		if (ItemSlashBlade.ProudSoul.get(bladetag, 0) < 300 || player.experienceLevel < 3)
		{
			if (player.world.isRemote)
				player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, 1, 1);
			return;
		}
		
		if (player.world.isRemote) return;
		if (player.getHeldItemOffhand().isEmpty()) return;
				
		NBTTagList enchants = stack.getEnchantmentTagList();
		int index = player.getRNG().nextInt(enchants.tagCount());
		int ench_id = enchants.getCompoundTagAt(index).getShort("id");
		int ench_level = enchants.getCompoundTagAt(index).getShort("lvl");
		Enchantment ench = Enchantment.getEnchantmentByID(ench_id);
		ItemStack offhand = player.getHeldItemOffhand();
		int gift_level = bladetag.getInteger("SL.Gift");
		ItemStack result = OtherUtills.addEnchantment(offhand, ench, gift_level, false);
		player.entityDropItem(result, 0.0f);
		bladetag.setInteger("SL.Gift", gift_level + ench_level);
		
		enchants.removeTag(index);
		bladetag.setTag("ench", enchants);
		player.addExperienceLevel(-3);
		ItemSlashBlade.ProudSoul.add(bladetag, -300);	
		
		player.sendMessage(new TextComponentString(new TextComponentTranslation("silverlighting.loseline").getFormattedText()+ ":" + ench.getTranslatedName(ench_level)));
	}
}
