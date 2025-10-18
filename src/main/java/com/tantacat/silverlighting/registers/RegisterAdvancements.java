package com.tantacat.silverlighting.registers;

import java.util.Iterator;

import com.tantacat.silverlighting.util.OtherUtills;

import mods.flammpfeil.slashblade.ItemSlashBladeDetune;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.entity.EntitySummonedBlade;
import mods.flammpfeil.slashblade.entity.EntitySummonedSwordBase;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade.SwordType;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class RegisterAdvancements {

	private RegisterAdvancements() {}
	public static RegisterAdvancements instance = new RegisterAdvancements();
	public void init() 
	{
		MinecraftForge.EVENT_BUS.register(this);
		SlashBladeHooks.EventBus.register(this);
	}
	
	//成就rank,namedsoulcystal,saheresoulsphere,enchantmentsoul
	@Deprecated
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if (event.side == Side.CLIENT) return;
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		
		int rank = StylishRankManager.getStylishRank(player);
		ResourceLocation id = new ResourceLocation("silverlighting:rank/rank_"+StylishRankManager.getRankText(rank));
		OtherUtills.grantAdvancement(player, id);
		
		boolean has_named = false;
		for (ItemStack item : player.inventoryContainer.inventoryItemStacks)
		{
			if (!(item.getItem() instanceof ItemProudSoul)) continue;
			if (!item.hasTagCompound()) continue;
			
			int meta = item.getMetadata();
			NBTTagCompound tag = item.getTagCompound();
			
			if (meta == 4 && tag.hasKey("CurrentItemName"))
			{
				OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "proudsoul/namedsoulcrystal"));
			}
			
			if (meta == 2 && tag.hasKey("SpecialAttackType"))
			{
				OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "proudsoul/saheresoulsphere"));
			}
			
			if (item.isItemEnchanted())
			{
				OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "proudsoul/enchantmentsoul"));
			}
		}
		
	}
	
	//成就bladestand
	@Deprecated
	@SubscribeEvent
	public void onPlayerSpawnBladeStand(PlayerInteractEvent.RightClickBlock event)
	{
		if (event.getSide() == Side.CLIENT) return;
		EntityPlayer player = event.getEntityPlayer();
		
		ItemStack main = player.getHeldItemMainhand();
		boolean is_proudsoul = main.getItem() instanceof ItemProudSoul; 
		
		IBlockState block = player.world.getBlockState(event.getPos());
		boolean is_fence = (block.getBlock() == Blocks.OAK_FENCE);
		
		if (is_proudsoul && is_fence && player.isSneaking())
			OtherUtills.grantAdvancement((EntityPlayerMP)player, new ResourceLocation("silverlighting", "bladestand"));
	}
	
	//成就enchanted,bewitched,noname,saya
	@Deprecated
	@SubscribeEvent
	public void onPlayerUpdateBlade(SlashBladeEvent.OnUpdateEvent event)
	{
		if (event.world.isRemote) return;
		EntityPlayerMP player = (EntityPlayerMP)event.entity;	
	
		ItemStack blade = event.blade;
		
		if (blade.getItem() instanceof ItemSlashBladeWrapper)
		{
			if (!ItemSlashBladeWrapper.CurrentItemName.exists(blade.getTagCompound()))
				OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "wrape/saya"));
		}
		else if (blade.getItem() instanceof ItemSlashBladeNamed)
		{
			if (!ItemSlashBladeNamed.CurrentItemName.exists(blade.getTagCompound()))
				OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "named/noname"));
		}
		
		if (blade.getItem() != SlashBlade.weapon) return;
		if (((ItemSlashBlade)blade.getItem()).getSwordType(blade).contains(SwordType.Enchanted))
			OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "enchanted"));
		if (((ItemSlashBlade)blade.getItem()).getSwordType(blade).contains(SwordType.Bewitched))
			OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "bewitched"));
	}
	
	//成就hundredkill,thousand_kill,soul_eater
	@Deprecated
	@SubscribeEvent
	public void onBladeKill(SlashBladeEvent.ImpactEffectEvent event)
	{
		if (!(event.user instanceof EntityPlayer)) return;
		if (event.user.world.isRemote) return;
		EntityPlayerMP player = (EntityPlayerMP)event.user;	
		
		ItemStack blade = event.blade;
		if (!(blade.getItem() instanceof ItemSlashBlade)) return;
		
		NBTTagCompound tag = blade.getTagCompound();
		if (((ItemSlashBlade)blade.getItem()).getSwordType(blade).contains(SwordType.SoulEeater))
			OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "soul_eater"));
		
		if (!OtherUtills.isDirtyDead(event.target)) return;
		
		if (ItemSlashBlade.KillCount.get(tag, 0) == 99)
			OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "hundred_kill"));
	
		if (ItemSlashBlade.KillCount.get(tag, 0) == 999)
			OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "thousand_kill"));
	}
	
	//成就phantomsword和phantomblade
	@Deprecated
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (event.getEntity().world.isRemote) return;
		if (!(event.getEntity() instanceof EntitySummonedSwordBase)) return;
		
		EntitySummonedSwordBase sword = (EntitySummonedSwordBase)event.getEntity();
		if (sword.getThrower() == null || !(sword.getThrower() instanceof EntityPlayerMP)) return;
		
		EntityPlayerMP player = (EntityPlayerMP)sword.getThrower();
		String swordType = sword instanceof EntitySummonedBlade ? "phantomblade" : "phantomsword";
		
		OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", swordType));
	}	
	
	//成就unhundredkill_silverbamboo和enchant_simpleblade
	@Deprecated
	@SubscribeEvent
	public void onPlayerDestoryItem(PlayerDestroyItemEvent event)
	{
		if (event.getEntityPlayer().world.isRemote) return;
		EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
		
		ItemStack blade = event.getOriginal();
		if (!(blade.getItem() instanceof ItemSlashBladeDetune)) return;
		
		if (blade.getItem() == SlashBlade.bladeSilverBambooLight && ItemSlashBlade.KillCount.get(blade.getTagCompound(), 0) < 100)
			OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "accident/unhundredkill_silverbamboo"));
		
		if (blade.isItemEnchanted())
			OtherUtills.grantAdvancement(player, new ResourceLocation("silverlighting", "accident/enchant_simpleblade"));
	}
	
	//成就unenchant_soulsphere	
	@Deprecated
	@SubscribeEvent
	public void onPlayerSmelted(PlayerInteractEvent event)
	{
		if (event.getEntityPlayer().world.isRemote) return;
		EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
		NBTTagCompound player_tag = player.getEntityData();
		
		Advancement adv = player.world.getMinecraftServer().getAdvancementManager().getAdvancement(new ResourceLocation("silverlighting", "accident/unenchant_soulsphere"));
		if (adv == null) return;
		if (player.getAdvancements().getProgress(adv).isDone()) return;
		
		TileEntity tile = player.world.getTileEntity(event.getPos());
		if (!(tile instanceof TileEntityFurnace)) return;
		
		if (!player_tag.hasKey("SL.Furnaces"))
			player_tag.setTag("SL.Furnaces", new NBTTagList());
			
		NBTTagCompound Furnace = new NBTTagCompound();
		tile.writeToNBT(Furnace);
		Furnace.setInteger("dimension", player.dimension);
		NBTTagList Furnaces = player_tag.getTagList("SL.Furnaces", 10);
		
		boolean saved = false;
		Iterator<NBTBase> lterator = Furnaces.iterator();
		while (lterator.hasNext())
		{
			NBTTagCompound nbt = (NBTTagCompound)lterator.next();
			if (nbt.hasKey("dimension") && nbt.hasKey("x") && nbt.hasKey("y") && nbt.hasKey("z"))
			{
				if (nbt.getInteger("dimension") == Furnace.getInteger("dimension") &&
					nbt.getInteger("x") == Furnace.getInteger("x") && 
					nbt.getInteger("y") == Furnace.getInteger("y")	&&
					nbt.getInteger("z") == Furnace.getInteger("z"))
				{
					saved = true;
					break;
				}
			}
		}
		
		if (!saved)
			Furnaces.appendTag(Furnace);
	}
	@Deprecated
	@SubscribeEvent
	public void onPlayerUsedFurnace(PlayerTickEvent event)
	{
		if (event.player.world.isRemote) return;
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		NBTTagCompound player_tag = player.getEntityData();
		
		if (!player_tag.hasKey("SL.Furnaces")) return;
		ResourceLocation loc = new ResourceLocation("silverlighting", "accident/unenchant_soulsphere");
		
		Advancement adv = player.world.getMinecraftServer().getAdvancementManager().getAdvancement(loc);
		if (adv == null) return;
		if (player.getAdvancements().getProgress(adv).isDone())
		{
			if (player_tag.hasKey("SL.Furnaces"))
				player_tag.removeTag("SL.Furnaces");
			return;
		}
		
		NBTTagList Furnaces = player_tag.getTagList("SL.Furnaces", 10);
		Iterator<NBTBase> lterator = Furnaces.iterator();
		while (lterator.hasNext())
		{
			NBTTagCompound nbt = (NBTTagCompound)lterator.next();
			
			if (nbt.getInteger("dimension") != player.dimension) continue;
			
			BlockPos pos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
			TileEntity tile = player.world.getTileEntity(pos);
			if (tile == null || !(tile instanceof TileEntityFurnace))
			{
				lterator.remove();
				continue;
			}
			
			TileEntityFurnace nowFurnace = (TileEntityFurnace)tile;
			TileEntityFurnace lastFurnace = new TileEntityFurnace();
			lastFurnace.readFromNBT(nbt);
			
			ItemStack last_input = lastFurnace.getStackInSlot(0);
			ItemStack now_output = nowFurnace.getStackInSlot(2);
			if (last_input.getItem() == SlashBlade.proudSoul && last_input.getMetadata() == 1 && last_input.isItemEnchanted() &&
				now_output.getItem() == SlashBlade.proudSoul && now_output.getMetadata() == 2)
			{
				OtherUtills.grantAdvancement(player, loc);
				ItemStack loot = last_input.copy();
				loot.setCount(1);
				player.addItemStackToInventory(loot);
				break;
			}
			
			nowFurnace.writeToNBT(nbt);
		}
		
	}
	
	//成就奖励(补偿)
	@Deprecated
	@SubscribeEvent
	public void onPlayerGrantAdvancement(AdvancementEvent event)
	{
		if (event.getEntityPlayer().world.isRemote) return;
		Advancement adv = event.getAdvancement();
		EntityPlayer player = event.getEntityPlayer();
	
		if (adv.getId().getResourcePath().equals("accident/broken_sealedblade"))
			player.addItemStackToInventory(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.IngotBladeSoulStr, 2));
		
		if (adv.getId().getResourcePath().equals("function/hidden_first_login"))
		{
			if (Loader.isModLoaded("patchouli"))
			{
				ItemStack book = SlashBlade.findItemStack("patchouli", "guide_book", 1);
				NBTTagCompound book_tag = new NBTTagCompound();
				book_tag.setString("patchouli:book", "silverlighting:forge_diary");
				book.setTagCompound(book_tag);
				player.addItemStackToInventory(book);
			}
		}
	}
	
}
