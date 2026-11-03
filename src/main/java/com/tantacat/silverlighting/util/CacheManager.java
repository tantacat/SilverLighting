package com.tantacat.silverlighting.util;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class CacheManager {
	
	public static final CacheManager instance = new CacheManager();
	
	private CacheManager() {}

	private final Map<UUID, Object2IntOpenHashMap<ResourceLocation>> slot_cache = new ConcurrentHashMap<>();

	public ItemStack getSlotInPlayer(ResourceLocation name, EntityPlayer player, Predicate<ItemStack> predicate, @Nullable Integer start, @Nullable Integer end)
	{
		int slot = slot_cache.computeIfAbsent(player.getUniqueID(), k -> new Object2IntOpenHashMap<>()).getOrDefault(name, Integer.MAX_VALUE);
		int _slot = slot;
		
		if (slot != Integer.MAX_VALUE && predicate.test(player.inventory.getStackInSlot(slot)))
			return player.inventory.getStackInSlot(slot);
		
		if (start != null && end != null)
		{
			for (int i = start; i < end; i++)
			{
				if (predicate.test(player.inventory.getStackInSlot(i)))
				{
					slot = i;
					Object2IntOpenHashMap<ResourceLocation> map = slot_cache.get(player.getUniqueID());
					map.put(name, slot);
					break;
				}
			}
		}
		else
		{
			for (int i = 0; i < player.inventory.mainInventory.size(); i++)
			{
				ItemStack item = player.inventory.getStackInSlot(i);
				if (predicate.test(item))
				{
					slot = i;
					Object2IntOpenHashMap<ResourceLocation> map = slot_cache.get(player.getUniqueID());
					map.put(name, slot);
					break;
				}
			}
		}
		
		return slot == _slot ? ItemStack.EMPTY : player.inventory.getStackInSlot(slot);
	}
	
	public int getSlot(UUID id, ResourceLocation name)
	{
		return slot_cache.computeIfAbsent(id, k -> new Object2IntOpenHashMap<>()).getOrDefault(name, Integer.MAX_VALUE);
	}
	
	public void putSlot(UUID id, ResourceLocation name, int slot)
	{
		slot_cache.computeIfAbsent(id, k -> new Object2IntOpenHashMap<>()).put(name, slot);
	}
	
	@SubscribeEvent
	public void onPlayerLogOut(PlayerLoggedOutEvent event) 
	{
		slot_cache.remove(event.player.getUniqueID());
	}
}
