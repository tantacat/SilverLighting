package com.tantacat.silverlighting.registers;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.network.PacketSendVoice;
import com.tantacat.silverlighting.util.OtherUtills;

import mods.flammpfeil.slashblade.TagPropertyAccessor.TagPropertyBoolean;
import mods.flammpfeil.slashblade.TagPropertyAccessor.TagPropertyInteger;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RegisterVoices {
	
	private RegisterVoices() {};
	public static RegisterVoices instance = new RegisterVoices();
	public void init() 
	{
		MinecraftForge.EVENT_BUS.register(this);
		SlashBladeHooks.EventBus.register(this);
	}
	
	public enum VoiceType
	{
		WAKE,
		LOWDAMAGE,
		BROKEN,
		KILL,
		CHARGED,
		IDLE,
		NOTOWNER
	}
	
	public TagPropertyBoolean RECIVE_VOICE = new TagPropertyBoolean("SL.recive_voice");
	public TagPropertyInteger LAST_VOICE_TIME = new TagPropertyInteger("SL.LastVoiceTime");
	private final int cool_time = 300;
	
	@SubscribeEvent
	public void onPlayerWakeUp(PlayerWakeUpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if (player.world.isRemote) return;
		
		ItemStack blade = player.getHeldItemMainhand();
		if (!(blade.getItem() instanceof ItemSlashBlade)) return;
		
		if (!canSendMessage(player)) return;
		
		sendMessage(player, event.updateWorld() ? "badnight" : "goodnight", blade);
	}
	
	@SubscribeEvent
	public void onBladeLowDamage(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer)event.entity;
		if (player.world.isRemote) return;
		
		ItemStack blade = event.blade;
		
		
		NBTTagCompound bladeTag = blade.getTagCompound();
		if (ItemSlashBlade.IsBroken.get(bladeTag)) return;
		NBTTagCompound voiceNBT = this.getVoiceData(bladeTag);
		
		
		if (!voiceNBT.hasKey("LastDamage"))
			voiceNBT.setInteger("LastDamage", blade.getItemDamage());			
				
		int lastDamage = voiceNBT.getInteger("LastDamage");
		int nowDamage = blade.getItemDamage();
		int halfDamage = blade.getMaxDamage() / 2;
		
		if (lastDamage < halfDamage && nowDamage > halfDamage)
		{
			if (canSendMessage(player))
				sendMessage(player, "lowdamage", blade);
		}	
		else if (nowDamage > halfDamage && player.getRNG().nextFloat() < 0.0001)
		{
			if (canSendMessage(player))
				sendMessage(player, "lowdamage", blade);
		}
			
		voiceNBT.setInteger("LastDamage", nowDamage);
	}
	
	//todo
	@SubscribeEvent
	public void onBladeBroken2(SlashBladeEvent.OnUpdateEvent event)
	{	
		if (!(event.entity instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer)event.entity;
		if (player.world.isRemote) return;
		
		ItemStack blade = event.blade;
		
		NBTTagCompound bladeTag = blade.getTagCompound();
		NBTTagCompound voiceNBT = this.getVoiceData(bladeTag);
		
		
		if (!voiceNBT.hasKey("LastDamage"))
			voiceNBT.setInteger("LastDamage", blade.getItemDamage());			
				
		int lastDamage = voiceNBT.getInteger("LastDamage");
		int nowDamage = blade.getItemDamage();
		
		if (lastDamage < blade.getMaxDamage() && nowDamage >= blade.getMaxDamage())
		{
			if (canSendMessage(player))
				sendMessage(player, "bladebroken", blade);
		}
			
		voiceNBT.setInteger("LastDamage", nowDamage);
		
	}
	@SubscribeEvent
	public void onBladeBroken1(PlayerDestroyItemEvent event)
	{
		if (event.getEntityPlayer().world.isRemote) return;
		
		ItemStack blade = event.getOriginal();
		if (!(blade.getItem() instanceof ItemSlashBlade)) return;
		
		EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
		if (canSendMessage(player))
			sendMessage(player, "bladebroken", blade);
	}

	
	@SubscribeEvent
	public void onBladeKill(SlashBladeEvent.ImpactEffectEvent event)
	{		
		if (!(event.user instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer)event.user;
		if (player.world.isRemote) return;
				
		ItemStack blade = event.blade;
		EntityLivingBase target = event.target;
		
		if (OtherUtills.isDirtyDead(target) && player.getRNG().nextFloat() < 0.2)
		{
			if (canSendMessage(player))
				sendMessage(player, "kill", blade);
		}
		
	}
	
	@SubscribeEvent
	public void onBladeCharged(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer)event.entity;
		if (player.world.isRemote) return;
		
		ItemStack blade = event.blade;
		
		
		NBTTagCompound bladeTag = blade.getTagCompound();
		NBTTagCompound voiceNBT = getVoiceData(bladeTag);
		
		
		if (!voiceNBT.hasKey("LastCharged"))
			voiceNBT.setBoolean("LastCharged", false);
		
		boolean lastCharged = voiceNBT.getBoolean("LastCharged");
		boolean nowCharged = ItemAnimaSheath.IsCharged.get(bladeTag);
		
		if (!lastCharged && nowCharged)
		{
			if (canSendMessage(player) && player.getRNG().nextFloat() < 0.5f)
				sendMessage(player, "charged", blade);
		}
		
		voiceNBT.setBoolean("LastCharged", nowCharged);
	}
	
	@SubscribeEvent
	public void onBladeAttack(SlashBladeEvent.ImpactEffectEvent event)
	{
		if (!(event.user instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer)event.user;
		if (player.world.isRemote) return;
		
		ItemStack blade = event.blade;
		if (canSendMessage(player) && player.getRNG().nextFloat() < 0.2f)
			sendMessage(player, "charged", blade);
		
	}
	
	@SubscribeEvent
	public void onBladeIdle(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer)event.entity;
		if (player.world.isRemote) return;
		
		if (!canSendMessage(player)) return;	
		
		ItemStack blade = event.blade;
		if (!isBladeIdle(blade, player)) return;
		
		
		if (player.getRNG().nextFloat() < 0.0001)
			sendMessage(player, "idle", blade);
	}
	
	
	private NBTTagCompound getVoiceData(NBTTagCompound bladeTag)
	{
		NBTTagCompound voiceNBT = new NBTTagCompound();
		if (bladeTag.hasKey("VoiceData"))
			voiceNBT = bladeTag.getCompoundTag("VoiceData");
		else
			bladeTag.setTag("VoiceData", voiceNBT);
		
		return voiceNBT; 
	}
	
	private boolean isBladeIdle(ItemStack blade, EntityPlayer player)
	{
		boolean result = true;
		
		if (blade == player.getHeldItemOffhand() || blade == player.getHeldItemMainhand()) return false;
		
		for (int i = 0; i < 9; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (stack == blade)
				return true;
		}
		
		return false;
	}
	
	public boolean canSendMessage(EntityPlayer player)
	{
		if (RECIVE_VOICE.get(player.getEntityData(), false) && player.ticksExisted - LAST_VOICE_TIME.get(player.getEntityData()) > cool_time)
			return true;
		else
			return false;
	}
	
	public static void sendMessage(EntityPlayer player, String type, ItemStack blade)
	{	
		
		//if (type.equals("badnight")) return;
		
		if (!blade.hasTagCompound()) return;
		
		if (blade.getTagCompound().hasUniqueId("Owner"))
		{
			if (!blade.getTagCompound().getUniqueId("Owner").equals(player.getUniqueID()))
				type = "notowner";
		}
		
		RegisterVoices.instance.LAST_VOICE_TIME.set(player.getEntityData(), player.ticksExisted);
		SilverLightingMain.network.sendTo(new PacketSendVoice(type, blade), (EntityPlayerMP)player);
	}
}
