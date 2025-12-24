package com.tantacat.silverlighting.registers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tantacat.silverlighting.SilverLightingMain;
import com.tantacat.silverlighting.client.gui.ContainerProudSoulBag;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.common.entity.EntityUnswerving;
import com.tantacat.silverlighting.config.ConfigGeneral;
import com.tantacat.silverlighting.network.PacketSwitchVoice;
import com.tantacat.silverlighting.util.OtherUtills;

import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.JustGuard;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.ability.UntouchableTime;
import mods.flammpfeil.slashblade.entity.EntityJustGuardManager;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.GameData;

public class RegisterEvents {

	private RegisterEvents() {}
	
	public static RegisterEvents instance = new RegisterEvents();
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
    	SlashBladeHooks.EventBus.register(this);
	}
	
	//同步配置文件
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(SilverLightingMain.MODID)) {
        	
            ConfigManager.sync(SilverLightingMain.MODID, Type.INSTANCE);
        
            if (SlashBlade.manager.attackableTargets.containsKey("player"))
            	SlashBlade.manager.attackableTargets.put("player", ConfigGeneral.canAttackPlayer);
            
            if (ConfigGeneral.canAttackAllMob)
            {
            	for (String name : SlashBlade.manager.attackableTargets.keySet())
    			{
    				if (!name.equals("player"))
    					SlashBlade.manager.attackableTargets.replace(name, true);	
    			}
            }
            else
            	MinecraftForge.EVENT_BUS.register(SlashBlade.manager);
            
            SilverLightingMain.network.sendToServer(new PacketSwitchVoice(ConfigGeneral.canReciveVoice));
        }
    }
	
	//竹光残片铁砧效果
	@SubscribeEvent
	public void onAnvilEvent1(AnvilUpdateEvent event)
	{
		ItemStack blade = event.getLeft();
		ItemStack brokenbamboo = event.getRight();
		if (!(blade.getItem() instanceof ItemSlashBlade)) return;
		if (!brokenbamboo.getItem().getRegistryName().equals(RegisterItems.instance.brokenbamboo.getRegistryName())) return;
		
		event.setCost(1);
		event.setMaterialCost(1);
		
		NBTTagCompound bladetag = blade.getTagCompound();
		NBTTagCompound resulttag = bladetag.copy();
		if (resulttag.hasKey("isSealed") && resulttag.getBoolean("isSealed"))
			resulttag.setBoolean("isSealed", false);
		ItemSlashBlade.RepairCount.add(resulttag, 1);
		ItemStack newblade = blade.copy();
		newblade.setTagCompound(resulttag);
		
		event.setOutput(newblade);
	}

	//锻铸者之心铁砧效果
	@SubscribeEvent
	public void onAnvilEvent2(AnvilUpdateEvent event)
	{
		ItemStack blade = event.getLeft();
		ItemStack forgerheart = event.getRight();
		if (!forgerheart.getItem().getRegistryName().equals(RegisterItems.instance.forgerheart.getRegistryName())) return;
		
		event.setCost(1);
		event.setMaterialCost(1);
		
		if (blade.getItem() instanceof ItemSlashBlade)
		{
			NBTTagCompound bladetag = blade.getTagCompound();
			NBTTagCompound resulttag = bladetag.copy();
			
			Iterator<NBTBase> enchants = resulttag.getTagList("ench", 10).iterator();
			while (enchants.hasNext())
			{
				NBTTagCompound enchant = (NBTTagCompound)enchants.next();
				if (Enchantment.getEnchantmentByID(enchant.getShort("id")).isCurse())
					enchants.remove();
			}
			
			ItemSlashBladeNamed.IsDefaultBewitched.set(resulttag, true);
			ItemSlashBlade.KillCount.add(resulttag, 100);
			ItemSlashBlade.ProudSoul.add(resulttag, 8000);
			ItemSlashBlade.RepairCount.add(resulttag, 5);
			ItemStack newblade = blade.copy();
			newblade.setTagCompound(resulttag);
			newblade.setItemDamage(0);
			
			event.setOutput(newblade);
		}
		else if (blade.getItem() instanceof ItemProudSoul)
		{
			if (blade.getMetadata() != 4) return;
			
			if (!ItemSlashBlade.getSpecialEffect(blade).hasKey("SpellWeak")) return;
			
			ItemStack anticrystal = new ItemStack(RegisterItems.instance.anticrystal);
			anticrystal.setTagCompound(new NBTTagCompound());
			NBTTagCompound resulttag = anticrystal.getTagCompound();
			ItemSlashBlade.KillCount.add(resulttag, 100);
			ItemSlashBlade.ProudSoul.add(resulttag, 8000);
			ItemSlashBlade.RepairCount.add(resulttag, 5);
			
			event.setOutput(anticrystal);
		}
		
	}
	
	//配置文件相关初始化
	@SubscribeEvent
	public void onPlayerLogin(EntityJoinWorldEvent event)
	{		
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.getEntity();
		
		if (event.getWorld().isRemote)
			SilverLightingMain.network.sendToServer(new PacketSwitchVoice(ConfigGeneral.canReciveVoice));
		else 
		{
			if (!SlashBlade.manager.attackableTargets.containsKey("player"))
			{
				EntityEntry playerentry = new EntityEntry(EntityLiving.class, "player");
				GameData.getEntityClassMap().put(EntityPlayerMP.class, playerentry);
				SlashBlade.manager.attackableTargets.put("player", ConfigGeneral.canAttackPlayer);
			}
				
			if (ConfigGeneral.canAttackAllMob)
			{
				for (String name : SlashBlade.manager.attackableTargets.keySet())
				{
					if (!name.equals("player"))
						SlashBlade.manager.attackableTargets.replace(name, true);	
				}
			}
			
			RegisterVoices.instance.LAST_VOICE_TIME.set(player.getEntityData(), 0);
		}
	}
	
	//鞘和灵鞘的自动格挡
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerHurtEvent(LivingHurtEvent event)
	{
		
		EntityLivingBase living = event.getEntityLiving();
		if (!(living instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)living;
		if (player.world.isRemote) return;	
		
		boolean has_item_in_slot = false;
		int repaircount = 0;
		ItemStack blade = ItemStack.EMPTY;
		for (int i = 0; i < 9; i++)
		{
			blade = player.inventory.getStackInSlot(i);
			if (blade.getUnlocalizedName().equals(
					RegisterBlades.instance.getCustomBlade("silverlighting.dokkaebisheath")
					.getUnlocalizedName()))
				continue;
			if (blade == player.getHeldItemMainhand())
			{
				if (blade.getUnlocalizedName().equals(SlashBlade.wrapBlade.getUnlocalizedName()) ||
					blade.getItem() instanceof ItemAnimaSheath)
				{
					repaircount = ItemSlashBlade.RepairCount.get(blade.getTagCompound(), 0);
					if (repaircount == 0) continue;
					has_item_in_slot = true;
					break;
				}
			}
			else
			{
				if (blade.getItem() instanceof ItemAnimaSheath)
				{
					repaircount = ItemSlashBlade.RepairCount.get(blade.getTagCompound(), 0);
					if (repaircount == 0) continue;
					has_item_in_slot = true;
					break;
				}
			}
		}
		
		if (has_item_in_slot && repaircount > 0)
		{
			if (player.getRNG().nextFloat() <= Math.exp(-(repaircount - 1)))
			{
				ItemSlashBlade.RepairCount.add(blade.getTagCompound(), -1);
				
				//from SlashBlade
				{
					event.setCanceled(true);
	                event.setAmount(0);
	                UntouchableTime.setUntouchableTime(player,20);


	                NBTTagCompound tag = blade.getTagCompound();

	                player.setArrowCountInEntity(-1);

	                ReflectionAccessHelper.setVelocity(player,0,0,0);


	                double yOffset = 0;
	                if(player.onGround){
	                    yOffset = 0.5;
	                }
	                player.getEntityData().setDouble("SBLastPosY", player.posY + yOffset);

	                //ItemSlashBlade.IsCharged.set(tag,true);
	                ItemSlashBlade.OnClick.set(tag,true);
	                ItemSlashBlade.OnJumpAttacked.set(tag,false);

	                JustGuard.ChargeStart.set(player.getEntityData(), -5l);
	                player.playSound(SoundEvents.ENTITY_BLAZE_HURT, 1.0F, 1.0F);


	                StylishRankManager.addRankPoint(player, StylishRankManager.AttackTypes.JustGuard);


	                EntityJustGuardManager entityManager = new EntityJustGuardManager(player.world, player);
	                if (entityManager != null) {
	                    player.world.spawnEntity(entityManager);
	                }
				}
				
				player.inventory.addItemStackToInventory(new ItemStack(RegisterItems.instance.brokenbamboo));
			}
		}	
	}
	
	//妖鞘的居合斩
	@SubscribeEvent
	public void onSlashBladeUpdate(PlayerTickEvent event)
	{
		if (event.side != Side.SERVER) return;
		EntityPlayer player = event.player;		
		ItemStack blade = ItemStack.EMPTY;
		int index = Integer.MAX_VALUE;
		for (int i = 0; i < 9; i++)
		{
			ItemStack item = player.inventory.getStackInSlot(i);
			if (!(item.getItem() instanceof ItemAnimaSheath)) continue;
			if (ItemAnimaSheath.CurrentItemName.get(item.getTagCompound()).equals("silverlighting.dokkaebisheath"))
			{
				blade = item;
				index = i;
				break;
			}
		}
		if (blade == ItemStack.EMPTY) return;
		
		ItemStack mainHand = player.getHeldItemMainhand();
		if (!(mainHand.getItem() instanceof ItemSlashBlade)) return;
		int mainSlot = OtherUtills.getSlotFor(player, mainHand);
		NBTTagCompound bladetag = blade.getTagCompound();
		
		if (index != mainSlot && player.ticksExisted % 20 == 0)
			blade.setItemDamage(blade.getItemDamage() - 1);
		
		if (!bladetag.hasKey("laido")) 
			bladetag.setInteger("laido", mainSlot);
		if (bladetag.getInteger("laido") == mainSlot) return;
		bladetag.setInteger("laido", mainSlot);
		if (blade.getItemDamage() == 0 && !ItemSlashBlade.IsBroken.get(bladetag))
		{
			double range = 24;
			AxisAlignedBB bb = new AxisAlignedBB(player.posX + range, player.posY + range, player.posZ + range, 
					player.posX - range, player.posY - range, player.posZ - range);
			List<Entity> mobs = player.world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
			for (Entity n : mobs)
			{
				if (n instanceof EntityLivingBase)
				{
					EntityLivingBase target = (EntityLivingBase)n;
					float damage = target.getMaxHealth() * 0.25f + ItemAnimaSheath.AttackAmplifier.get(mainHand.getTagCompound(), 0);
					target.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
					ItemAnimaSheath.updateKillCount(mainHand, player, target);
				}
			}
			blade.setItemDamage(blade.getMaxDamage());
		}
	}
		
	//不渝，玩家死亡时
	@SubscribeEvent
	public void onPlayerDeath(PlayerDropsEvent event)
	{
		if (!ConfigGeneral.isUnswervingEnable) return;
		if (event.getEntityPlayer().world.isRemote) return;
		EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
		
		List<ItemStack> bladeCache = new ArrayList<>();
		Iterator<EntityItem> drops = event.getDrops().listIterator();
		while (drops.hasNext())
		{
			EntityItem item = drops.next();
			if (item.getItem().getItem() instanceof ItemAnimaSheath)
			{
				bladeCache.add(item.getItem());
				drops.remove();
			}
		}

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		World player_world = player.world;
		int dimension = player.getSpawnDimension();
		WorldServer target_world = server.getWorld(dimension);
		if (target_world != null)
		{
			EntityUnswerving entity = new EntityUnswerving(target_world, player.getUniqueID(), bladeCache);
			BlockPos pos = player.getBedLocation(player.getSpawnDimension());
			if (pos == null)
				pos = target_world.provider.getSpawnPoint();
			entity.posX = pos.getX();
			entity.posY = pos.getY();
			entity.posZ = pos.getZ();
			target_world.spawnEntity(entity);
		}
	}
	
	
	//修复破碎的耀魂修刀的bug
	@SubscribeEvent
	public void replaceTinySoul(PlayerLoggedInEvent event)
	{					
		ItemStack right_tiny_soul = SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1);
		ItemSlashBlade.getSpecialEffect(right_tiny_soul);
		SlashBlade.registerCustomItemStack(SlashBlade.TinyBladeSoulStr, right_tiny_soul);
	}
	
	@SubscribeEvent
	public void onTinyProudSoulPickUp(EntityItemPickupEvent event)
	{
		if (event.getEntityPlayer().openContainer instanceof ContainerProudSoulBag)
			return;
		
		ItemStack item = event.getItem().getItem();
		if (item.isItemEnchanted() && item.isItemEqual(SlashBlade.findItemStack(SlashBlade.modid, SlashBlade.TinyBladeSoulStr, 1)))
		{
			if (item.getEnchantmentTagList().tagCount() > 1) return;
			
			for (int i = 0; i < event.getEntityPlayer().inventory.getSizeInventory(); i++)
			{
				ItemStack bag = event.getEntityPlayer().inventory.getStackInSlot(i);
				if (bag.getItem() == RegisterItems.instance.proudsoulbag)
				{
					
					NBTTagCompound nbt_bag = bag.getTagCompound();
					short ench = ((NBTTagCompound)item.getEnchantmentTagList().get(0)).getShort("id");
					short lvl = ((NBTTagCompound)item.getEnchantmentTagList().get(0)).getShort("lvl");
					if (lvl != 1) return;
					
					int max_page = nbt_bag.getInteger("max_page");
					for (int j = 1; j <= max_page; j++)
					{
						NBTTagList page = nbt_bag.getTagList("page"+j, 10);
						for (int k = 0; k < page.tagCount(); k++)
						{
							ItemStack soul = new ItemStack(page.getCompoundTagAt(k));
							short id = ((NBTTagCompound)soul.getEnchantmentTagList().get(0)).getShort("id");
							if (id != ench) continue;
							
							int pickup_count = (item.getCount() + soul.getCount() > 64) ? 64 - soul.getCount() : item.getCount();
							
							page.getCompoundTagAt(k).setByte("Count", (byte)(soul.getCount() + pickup_count));
							
							item.setCount(item.getCount() - pickup_count);
							event.getItem().setItem(item);
							
							if(pickup_count > 0) {
								event.setCanceled(true);
								if (!event.getItem().isSilent()) {
									event.getItem().world.playSound(null, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ,
											SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
											((event.getItem().world.rand.nextFloat() - event.getItem().world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
								}
								((EntityPlayerMP) event.getEntityPlayer()).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId(), pickup_count));
								event.getEntityPlayer().openContainer.detectAndSendChanges();

								return;
							}
							
						}						
					}
					
					for (int j = 1; j <= max_page; j++)
					{
						NBTTagList page = nbt_bag.getTagList("page"+j, 10);
						if (page.tagCount() < 3 * 9)//此页满
						{
							int pickup_count = item.getCount();
							page.appendTag(item.writeToNBT(new NBTTagCompound()));
							
							
							item.setCount(item.getCount() - pickup_count);
							event.getItem().setItem(item);
							event.setCanceled(true);
							if (!event.getItem().isSilent()) {
								event.getItem().world.playSound(null, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ,
										SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
										((event.getItem().world.rand.nextFloat() - event.getItem().world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
							}
							((EntityPlayerMP) event.getEntityPlayer()).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId(), pickup_count));
							event.getEntityPlayer().openContainer.detectAndSendChanges();
							
							break;
						}
					}
				}
			}
		}
	}
}
