package com.tantacat.silverlighting.registers;

import com.tantacat.silverlighting.ModMain;
import com.tantacat.silverlighting.capability.IProudSoulStorage;
import com.tantacat.silverlighting.capability.ProudSoulStorageProvider;
import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.common.Item.ItemProudSoulBag;
import com.tantacat.silverlighting.common.entity.EntityUnswerving;
import com.tantacat.silverlighting.config.ConfigGeneral;
import com.tantacat.silverlighting.network.PacketEnergyExhaust;
import com.tantacat.silverlighting.network.PacketSwitchVoice;
import com.tantacat.silverlighting.recipe.RecipeHelper;
import com.tantacat.silverlighting.util.CacheManager;
import com.tantacat.silverlighting.util.OtherUtills;
import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.JustGuard;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.BladeCapabilityProvider;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.item.ItemProudSoul;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.SlashBladeEvent;
import mods.flammpfeil.slashblade.util.SlashBladeHooks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        if (event.getModID().equals(ModMain.MODID)) {
        	
            ConfigManager.sync(ModMain.MODID, Type.INSTANCE);
        
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
            
            ModMain.network.sendToServer(new PacketSwitchVoice(ConfigGeneral.canReciveVoice));
        }
    }
	
	//竹光残片获取
	private static final String TAG_SLEEP_GRANTED = "SilverLighting.sleepGranted";
	@SubscribeEvent
	public void onPlayerSleepTick(PlayerTickEvent event)
	{
		if (event.phase != Phase.END || event.side != Side.SERVER) return;

		EntityPlayer player = event.player;
		NBTTagCompound data = player.getEntityData();

		if (player.isPlayerSleeping())
		{
			// 成功入睡（白天/有怪/太远等失败尝试不会走到这里）且这一段还没结算过 → 给
			if (!data.getBoolean(TAG_SLEEP_GRANTED))
			{
				data.setBoolean(TAG_SLEEP_GRANTED, true);
				grantBrokenBamboo(player);
			}
		}
		else if (data.getBoolean(TAG_SLEEP_GRANTED))
		{
			// 醒来 / 被踢下床：这一段睡眠结束，下一觉可以再给一次
			data.setBoolean(TAG_SLEEP_GRANTED, false);
		}
	}
	private void grantBrokenBamboo(EntityPlayer player)
	{
		ItemStack item = player.getHeldItemMainhand();
		if ((item.getItem() instanceof ItemSlashBladeWrapper 
				&& !ItemSlashBladeWrapper.CurrentItemName.exists(item.getTagCompound())
			) || 
			(item.getItem() instanceof ItemAnimaSheath && 
					ItemAnimaSheath.CurrentItemName.get(item.getTagCompound()).equals("silverlighting.animasheath")
			))		
		{
			if (ItemSlashBladeWrapper.RepairCount.tryAdd(item.getTagCompound(), -1, false))
				player.dropItem(RegisterItems.brokenbamboo, 1);
		}
	}
	
	//竹光残片铁砧效果
	@SubscribeEvent
	public void onAnvilEvent1(AnvilUpdateEvent event)
	{
		ItemStack blade = event.getLeft();
		ItemStack brokenbamboo = event.getRight();
		if (!(blade.getItem() instanceof ItemSlashBlade)) return;
		if (!brokenbamboo.getItem().getRegistryName().equals(RegisterItems.brokenbamboo.getRegistryName())) return;
		
		event.setCost(2);
		event.setMaterialCost(1);
		
		NBTTagCompound bladetag = blade.getTagCompound();
		NBTTagCompound resulttag = bladetag.copy();
		if (resulttag.hasKey("isSealed") && resulttag.getBoolean("isSealed"))
		{
			resulttag.removeTag("isSealed");
			event.setCost(30);
		}
		if (resulttag.hasKey("isDestructable") && resulttag.getBoolean("isDestructable"))
		{
			resulttag.removeTag("isDestructable");
			event.setCost(30);
		}
		
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
		if (!forgerheart.getItem().getRegistryName().equals(RegisterItems.forgerheart.getRegistryName())) return;
		
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
			
			ItemStack anticrystal = new ItemStack(RegisterItems.anticrystal);
			anticrystal.setTagCompound(new NBTTagCompound());
			NBTTagCompound resulttag = anticrystal.getTagCompound();
			ItemSlashBlade.KillCount.add(resulttag, 100);
			ItemSlashBlade.ProudSoul.add(resulttag, 8000);
			ItemSlashBlade.RepairCount.add(resulttag, 5);
			
			event.setOutput(anticrystal);
		}
		
	}
	
	//逆质耀魂铁砧效果
	@SubscribeEvent
	public void onAnvilEvent3(AnvilUpdateEvent event)
	{
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();
		
		if (!right.getItem().getRegistryName().equals(RegisterItems.anticrystal.getRegistryName())) return;
		if (!(left.getItem() instanceof ItemAnimaSheath)) return;
		
		String name = ItemSlashBladeNamed.CurrentItemName.get(left.getTagCompound(), "");
		if (!name.equals("silverlighting.animasheath")) return;
		
		if (ItemSlashBlade.KillCount.get(left.getTagCompound()) < 1000) return;
		
		event.setCost(30);
		event.setMaterialCost(1);
		
		ItemStack result = RegisterBlades.instance.getCustomBlade("dokkaebisheath");
		RecipeHelper.inheritBladeData(result, left);
		
		event.setOutput(result);
	}
	
	//配置文件相关初始化
	@SubscribeEvent
	public void onPlayerLogin(EntityJoinWorldEvent event)
	{		
		if (!(event.getEntity() instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)event.getEntity();
		
		if (event.getWorld().isRemote)
			ModMain.network.sendToServer(new PacketSwitchVoice(ConfigGeneral.canReciveVoice));
		else 
		{
			EntityEntry playerentry = new EntityEntry(EntityLiving.class, "player");
			GameData.getEntityClassMap().put(EntityPlayerMP.class, playerentry);
			SlashBlade.manager.attackableTargets.put("player", ConfigGeneral.canAttackPlayer);
				
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
		
	//灵鞘的自动格挡
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerHurtEvent(LivingHurtEvent event)
	{
		if (event.getSource().getTrueSource() == null && event.getSource().isUnblockable()) return;

		EntityLivingBase living = event.getEntityLiving();
		if (!(living instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer)living;
		if (player.world.isRemote) return;	
		if (!(player.getHeldItemMainhand().getItem() instanceof ItemSlashBlade)) return;
		
		ItemStack ani = CacheManager.instance.getSlotInPlayer(new ResourceLocation(ModMain.MODID, "animasheath_hasRefine"), player,
				(x) -> {
					return x.getItem() instanceof ItemAnimaSheath && 
							ItemAnimaSheath.CurrentItemName.get(x.getTagCompound(), "").equals("silverlighting.animasheath") &&
							ItemAnimaSheath.RepairCount.get(x.getTagCompound(), 0) > 0;
				}, 0, player.inventory.getHotbarSize());
		ItemStack dok = CacheManager.instance.getSlotInPlayer(new ResourceLocation(ModMain.MODID, "dokkaebisheath_hasWraped"), player,
				(x) -> {
					return x.getItem() instanceof ItemAnimaSheath && 
							ItemAnimaSheath.CurrentItemName.get(x.getTagCompound(), "").equals("silverlighting.dokkaebisheath") &&
							ItemAnimaSheath.hasWrapedItem(x);
				}, 0, player.inventory.getHotbarSize());
		
		if (ani == ItemStack.EMPTY) return;
		int refine = ItemAnimaSheath.RepairCount.get(ani.getTagCompound(), 0);
		boolean canBlock = false;
		if (dok != ItemStack.EMPTY)
		{
			ItemAnimaSheath.damageItem(dok, (int)Math.max(dok.getMaxDamage()*0.4f, 40), player);
			canBlock = true;
		}
		else if (player.getRNG().nextFloat() < 0.75f * Math.exp(- Math.pow(refine, 0.5f) + 1)) {
			ItemAnimaSheath.RepairCount.add(ani.getTagCompound(), -1);
			canBlock = true;
		}
		
		//from SlashBlade
		if (canBlock)
		{
			player.setActiveHand(EnumHand.MAIN_HAND);
			JustGuard.setJustGuardState(player);
			//ModMain.network.sendTo(new PacketAutoBlock(), (EntityPlayerMP) player);
			/*event.setCanceled(true);
            event.setAmount(0);
            UntouchableTime.setUntouchableTime(player,20);

            NBTTagCompound tag = player.getHeldItemMainhand().getTagCompound();

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
            }*/
		}
	}
	
	//妖鞘的居合斩
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if (event.phase != Phase.END || event.side != Side.SERVER) return;

		EntityPlayer player = event.player;

		int lastMain = CacheManager.instance.getSlot(player.getUniqueID(),new ResourceLocation(ModMain.MODID, "mainHand"));
		if (lastMain == player.inventory.currentItem) return;
		CacheManager.instance.putSlot(player.getUniqueID(), new ResourceLocation(ModMain.MODID, "mainHand"), player.inventory.currentItem);
		if (lastMain == Integer.MAX_VALUE) return;
		
		ItemStack blade = CacheManager.instance.getSlotInPlayer(new ResourceLocation(ModMain.MODID, "dokkaebisheath_fullEnergy"), player,
				(x) -> {
					if(!x.hasCapability(BladeCapabilityProvider.ENERGY, null)) return false;
					return x.getItem() instanceof ItemAnimaSheath && 
							ItemAnimaSheath.CurrentItemName.get(x.getTagCompound(), "").equals("silverlighting.dokkaebisheath") &&
							x.getCapability(BladeCapabilityProvider.ENERGY, null).getEnergyStored() == x.getCapability(BladeCapabilityProvider.ENERGY, null).getMaxEnergyStored();
				}, 0, player.inventory.getHotbarSize());
		if (blade == ItemStack.EMPTY) return;
		
		if (player.getHeldItemMainhand().getItem() instanceof ItemSlashBlade) {
			double range = 24;
			AxisAlignedBB bb = new AxisAlignedBB(player.posX + range, player.posY + range, player.posZ + range,
					player.posX - range, player.posY - range, player.posZ - range);
			List<Entity> mobs = player.world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());
			for (Entity n : mobs) {
				if (!(n instanceof EntityLivingBase)) continue;

				EntityLivingBase target = (EntityLivingBase) n;
				float damage = target.getMaxHealth() * 0.25f + ItemAnimaSheath.AttackAmplifier.get(player.getHeldItemMainhand().getTagCompound(), 0);
				StunManager.setStun(target, 4 * 20);
				StunManager.setFreeze(target, 4 * 20);
				target.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
				target.hurtResistantTime = 0;
				target.motionY = 0f;
				ItemAnimaSheath.updateKillCount(player.getHeldItemMainhand(), player, target);
			}

			if (mobs.size() != 0) {
				/*IEnergyStorage storage = blade.getCapability(BladeCapabilityProvider.ENERGY, null);
				storage.extractEnergy(storage.getEnergyStored(), false);*/
				OtherUtills.changeBladeEnergyCap(blade, BladeCapabilityProvider.ENERGY, null, s -> {
					s.extractEnergy(s.getEnergyStored(), false);
				});
				ModMain.network.sendTo(new PacketEnergyExhaust(CacheManager.instance.getSlot(player.getUniqueID(),
						new ResourceLocation(ModMain.MODID, "dokkaebisheath_fullEnergy"))), (EntityPlayerMP)player);

				player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 0.5f, 1.8f);
			}
		}
	}
	@SubscribeEvent
	public void onSlashBladeUpdate(SlashBladeEvent.OnUpdateEvent event)
	{
		if (!(event.entity instanceof EntityPlayer)) return;
		ItemStack blade = event.blade;
		if (!(blade.getItem() instanceof ItemAnimaSheath)) return;
		if (!ItemAnimaSheath.CurrentItemName.get(blade.getTagCompound(),"").equals("silverlighting.dokkaebisheath")) return;

		OtherUtills.changeBladeEnergyCap(blade, BladeCapabilityProvider.ENERGY, null, s -> {
			s.receiveEnergy(250, false);
		});
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
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void replaceTinySoul(ItemTooltipEvent event)
	{
		if (!(event.getItemStack().getItem() instanceof ItemProudSoul)) return;
		NBTTagCompound nbt = event.getItemStack().getTagCompound();
		if (nbt == null) return;
		try {
			if (nbt.hasKey("SB.SEffect") && nbt.getTag("SB.SEffect").hasNoTags())
				nbt.removeTag("SB.SEffect");
			if (nbt.hasNoTags())
				event.getItemStack().setTagCompound(null);
		}
		catch (Exception e)
		{
			System.err.println("[SilverLighting]Somethings wrong when fix ProudSoul's NBT:" + e);
		}
	}

	@SubscribeEvent
	public void onTinyProudSoulPickUp(EntityItemPickupEvent event)
	{
		// 只在服务端处理：客户端没有真实的袋子存储，写了也只是幻影数据
		if (event.getEntityPlayer().world.isRemote) return;

		ItemStack item = event.getItem().getItem();
		if (!(item.getItem() instanceof ItemProudSoul)) return;
		if (event.getItem().getEntityData().hasKey("FloatingTimeout")) return;

		// 先看手上（主手/副手都算：界面本来就支持副手），找不到再退回到背包里搜
		ItemStack bag = event.getEntityPlayer().getHeldItemMainhand();
		if (!(bag.getItem() instanceof ItemProudSoulBag))
		{
			bag = event.getEntityPlayer().getHeldItemOffhand();
			if (!(bag.getItem() instanceof ItemProudSoulBag))
			{
				bag = CacheManager.instance.getSlotInPlayer(new ResourceLocation(ModMain.MODID, "proudsoulbag"),
						event.getEntityPlayer(), (x) -> {return x.getItem() instanceof ItemProudSoulBag;}, null, null);
			}
		}
		if (bag == ItemStack.EMPTY) return;

		IProudSoulStorage storage = ProudSoulStorageProvider.getStorage(bag);
		if (storage != null && storage.canStore(item))
		{
			// 只有真的装进去了才消耗掉落物，避免装不下时把耀魂弄丢
			if (storage.storeItem(item, item.getCount()) > 0) return;

			event.setCanceled(true);
			if (!event.getItem().isSilent()) {
				event.getItem().world.playSound(null, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ,
						SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
						((event.getItem().world.rand.nextFloat() - event.getItem().world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			}
			((EntityPlayerMP) event.getEntityPlayer()).connection.sendPacket(new SPacketCollectItem(event.getItem().getEntityId(), event.getEntityPlayer().getEntityId(), item.getCount()));
			// 袋子内容就在物品标签里，把袋子这一格立刻同步给客户端（开界面时数字会实时变化）
			ProudSoulStorageProvider.syncBagToClient(event.getEntityPlayer(), bag);

			item.setCount(0);
			event.getItem().setItem(item);
		}
	}

	//解决妖鞘模型问题
	@SubscribeEvent
	public void onBladeStandTakeDokkaebi(SlashBladeEvent.OnEntityBladeStandUpdateEvent event) {
		ItemStack blade = event.blade;
		if (!(blade.getItem() instanceof ItemAnimaSheath)) return;
		if (!ItemSlashBladeNamed.CurrentItemName.get(blade.getTagCompound(), "").equals("silverlighting.dokkaebisheath")) return;

		if (ItemSlashBlade.ModelName.get(blade.getTagCompound(), "").equals("silver/dokkaebisheath")) {
			ItemSlashBlade.ModelName.set(blade.getTagCompound(), "silver/dokkaebisheath_stand");
		}
		/*if (ItemSlashBlade.TextureName.get(blade.getTagCompound(), "").equals("silver/dokkaebisheath")) {
			ItemSlashBlade.TextureName.set(blade.getTagCompound(), "silver/dokkaebisheath_stand");
		}*/
	}
	@SubscribeEvent
	public void onTakeDokkaebiFromStand(SlashBladeEvent.OnUpdateEvent event) {
		if (!(event.entity instanceof EntityPlayer)) return;
		if (!(event.blade.getItem() instanceof ItemAnimaSheath)) return;

		ItemStack blade = event.blade;
		if (!ItemSlashBladeNamed.CurrentItemName.get(blade.getTagCompound(), "").equals("silverlighting.dokkaebisheath")) return;

		if (ItemSlashBlade.ModelName.get(blade.getTagCompound(), "").equals("silver/dokkaebisheath_stand")) {
			ItemSlashBlade.ModelName.set(blade.getTagCompound(), "silver/dokkaebisheath");
		}
		/*if (ItemSlashBlade.TextureName.get(blade.getTagCompound(), "").equals("silver/dokkaebisheath_stand")) {
			ItemSlashBlade.TextureName.set(blade.getTagCompound(), "silver/dokkaebisheath");
		}*/
	}

	//修复血量异常减少bug
	@SubscribeEvent
	public void onPotionEffectRefresh0(PotionEvent.PotionRemoveEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
		if (event.getPotion() != MobEffects.HEALTH_BOOST) return;

		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		NBTTagCompound playerData = event.getEntityLiving().getEntityData();
		if (playerData.getLong("SB.UntouchableTimeOut") >= player.world.getTotalWorldTime()
				|| JustGuard.ChargeStart.get(playerData) == -1) {
			event.setCanceled(true);
		}
	}
	@SubscribeEvent
	public void onPotionEffectRefresh1(PotionEvent.PotionApplicableEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
		if (event.getPotionEffect().getPotion() != MobEffects.HEALTH_BOOST) return;

		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		NBTTagCompound playerData = event.getEntityLiving().getEntityData();
		if (playerData.getLong("SB.UntouchableTimeOut") >= player.world.getTotalWorldTime()
				|| JustGuard.ChargeStart.get(playerData) == -1) {
			event.setResult(Event.Result.DENY);
		}
	}
}
