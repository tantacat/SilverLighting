package com.tantacat.silverlighting.specialboost;

import java.util.List;
import java.util.Random;

import com.google.common.base.Predicate;
import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.common.entity.EntitySpelling;
import com.tantacat.silverlighting.network.PacketSpecialBoostHandler;
import com.tantacat.silverlighting.network.PacketSpecialShowSpell;
import com.tantacat.silverlighting.registers.RegisterBoosts;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfile.BoostType;
import com.tantacat.silverlighting.util.BoostProfileHelper;
import com.tantacat.silverlighting.util.OtherUtills;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialBoostSpelling implements IOnBoostSwitch 
{
public BoostProfile profile = new BoostProfile(getId(), false, BoostType.fight);
	
	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.entity;
		if (player.world.isRemote) return;
		ItemStack blade = event.blade;
		if (!BoostProfileHelper.isBoostEffective(blade, getId())) return;
		
		Random random = player.getRNG();
		if (random.nextFloat() < 0.05)
		{
			int need_exp = 0;
			for (NBTBase n : blade.getEnchantmentTagList())
				need_exp += ((NBTTagCompound)n).getShort("lvl");
			if (!OtherUtills.consumePlayerXP(player, need_exp))
			{
				BoostProfile boost = BoostProfileHelper.getBoostProfiles(blade).get(0);
				PacketSpecialBoostHandler.switchBoost(blade, boost, player);	
			}
		}
		
		if (random.nextFloat() < 0.001)
		{
			// 获取 Tooltip
			ItemStack last_blade = blade.copy();
			
			//抽取附魔
			Enchantment enchant = Enchantment.REGISTRY.getRandomObject(random);
			while (enchant.isCurse())
				enchant = Enchantment.REGISTRY.getRandomObject(random);
			NBTTagCompound tag_stack = blade.getTagCompound();
			NBTTagList stack_enchants = blade.getEnchantmentTagList();
			
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
				blade.addEnchantment(enchant, 1);
			else if (!up_success)
			{
				ItemStack ProudSoul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.ProudSoulStr, 1);
				ProudSoul.addEnchantment(enchant, max_lvl - 1);
				player.entityDropItem(ProudSoul, 0);
			}
			else
				tag_stack.setTag("ench", stack_enchants);
			
			ItemStack now_blade = blade.copy();
			SilverLightingMain.network.sendTo(new PacketSpecialShowSpell(last_blade, now_blade), (EntityPlayerMP)player);
		}
	}
	
	@SubscribeEvent
	public void onPlayerHurt(LivingDamageEvent event)
	{
		if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.getEntityLiving();
		if (player.world.isRemote) return;
		ItemStack blade = ItemStack.EMPTY;
		if (BoostProfileHelper.isBoostEffective(player.getHeldItemMainhand(), "Spelling"))
			blade = player.getHeldItemMainhand();
		else if (BoostProfileHelper.isBoostEffective(player.getHeldItemOffhand(), "Spelling"))
			blade = player.getHeldItemOffhand();
		if (blade.isEmpty()) return;
		
		String type = event.getSource().getDamageType();
		if (!type.equals(DamageSource.DROWN.damageType) &&
			!type.equals(DamageSource.STARVE.damageType) &&
			!type.equals(DamageSource.OUT_OF_WORLD.damageType))
		{
			player.heal(ItemAnimaSheath.AttackAmplifier.get(blade.getTagCompound(), 0) * 0.2f);
			event.setAmount(event.getAmount() * 0.5f);
		}
	}
	
	public void register()
	{
		RegisterBoosts.instance.BoostsHasSwitch.put(getId(), this);
		SlashBladeHooks.EventBus.register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public String getId()
	{
		return "Spelling";
	}

	@Override
	public void onBoostOpen(EntityPlayer player) {
		Vec3d pos = player.getPositionVector();
		AxisAlignedBB bb = new AxisAlignedBB(pos.x - 10, pos.y - 10, pos.z - 10,
				pos.x + 10, pos.y + 10, pos.z + 10);
		List<EntitySpelling> entitys = player.world.getEntitiesWithinAABB(EntitySpelling.class, bb, new Predicate<EntitySpelling>() {
			@Override
			public boolean apply(EntitySpelling input) {
				return input.getPlayerID() == player.getUniqueID();
			}
		});
		for (EntitySpelling n : entitys)
			n.setDead();
		
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("ench", player.getHeldItemMainhand().getEnchantmentTagList().copy());
		EntitySpelling Spelling = new EntitySpelling(player.world, player.getUniqueID(), nbt);
		Spelling.setPosition(player.posX, player.posY, player.posZ);
		player.world.spawnEntity(Spelling);		
	}

	@Override
	public void onBoostClose(EntityPlayer player) {
		Vec3d pos = player.getPositionVector();
		AxisAlignedBB bb = new AxisAlignedBB(pos.x - 10, pos.y - 10, pos.z - 10,
				pos.x + 10, pos.y + 10, pos.z + 10);
		List<EntitySpelling> entitys = player.world.getEntitiesWithinAABB(EntitySpelling.class, bb, new Predicate<EntitySpelling>() {
			@Override
			public boolean apply(EntitySpelling input) {
				return input.getPlayerID() == player.getUniqueID();
			}
		});		
		for (EntitySpelling n : entitys)
			n.setDead();
	}
	
	private boolean tryAddExperience(int amount, EntityPlayer player)
	{
		if (amount > 0)
		{
			player.addExperience(amount);
			return true;
		}
		else
		{
			if (player.experienceTotal < -amount)
				return false;
			
			player.experienceTotal += amount;
			while (player.experience * player.xpBarCap() < amount)
			{
				amount += player.experience * player.xpBarCap();
				player.addExperienceLevel(-1);
			}
			player.experience += amount / player.xpBarCap();
			return true;
		}
	}
	
}
