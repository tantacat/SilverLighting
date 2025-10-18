package com.tantacat.silverlighting.specialattack;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;
import com.tantacat.silverlighting.common.entity.EntityHope;
import com.tantacat.silverlighting.registers.RegisterSAs;
import com.tantacat.silverlighting.registers.RegisterVoices;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialattack.ISuperSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpecialAttackHope extends SpecialAttackBase implements ISuperSpecialAttack{

	public int id;
	
	public SpecialAttackHope() {
		id = toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "hope";
	}

	@Override
	public void doSpacialAttack(ItemStack stack, EntityPlayer player) {
						
		if (player.world.isRemote) return;
		
		World world = player.world;
		Vec3d look = player.getLookVec();
		Vec3d place_pos = new Vec3d(player.posX + look.x, player.posY, player.posZ + look.z);
		BlockPos block_pos = new BlockPos(place_pos);
		if (!world.isAirBlock(block_pos))
			place_pos = player.getPositionVector();
		if (RegisterVoices.instance.canSendMessage(player))
			RegisterVoices.instance.sendMessage(player, "charged", stack);
		ItemSlashBlade.IsCharged.set(stack.getTagCompound(), false);
		EntityHope hope = new EntityHope(world, place_pos.x, place_pos.y, place_pos.z, stack.copy());
		hope.player_id = player.getUniqueID();
		hope.setStandType((int)-1);
		hope.setFlip((int)2);
		world.spawnEntity(hope);
		stack.setCount(0);
	}

	@Override
	public void doSuperSpecialAttack(ItemStack stack, EntityPlayer player) {
		NBTTagCompound tag_blade = stack.getTagCompound();
		if (ItemAnimaSheath.CurrentItemName.get(tag_blade).equals("silverlighting.puresilver"))
		{
			int id_dedication = RegisterSAs.instance.dedication.id;
			ItemAnimaSheath.SpecialAttackType.set(tag_blade, id_dedication);
		}
	}
	
}
