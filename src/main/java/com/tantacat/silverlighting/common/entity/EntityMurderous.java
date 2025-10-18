package com.tantacat.silverlighting.common.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.tantacat.silverlighting.common.Item.ItemAnimaSheath;

import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityMurderous extends Entity{
		
	public Vec3d hit_pos;
	public ItemStack blade;
	public float pitch;
	public float yaw;
	public GameType game_type;
	public boolean isJustSA;
	private int lifetime;
	private UUID playerid;
	
	public EntityMurderous(World worldIn) {
		super(worldIn);
		// TODO Auto-generated constructor stub
	}

	public EntityMurderous(World world, UUID id, Vec3d hit_pos, ItemStack blade, float pitch, float yaw, boolean justSA)
	{
		super(world);
		setPlayerID(id);
		this.hit_pos = hit_pos;
		this.blade = blade;
		this.pitch = pitch;
		this.yaw = yaw;
		this.isJustSA = justSA;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (this.world.isRemote) return;
		
		EntityPlayer player = this.world.getPlayerEntityByUUID(getPlayerID());
		if (player == null) return;
		
		if (isJustSA)
		{
			if (game_type == null)
				game_type = ((EntityPlayerMP)player).interactionManager.getGameType();
			player.setGameType(GameType.SPECTATOR);
		}
		
		double dAmbit = 1.5D;
        AxisAlignedBB remove_bb = new AxisAlignedBB(
                player.posX - dAmbit, player.posY - dAmbit, player.posZ - dAmbit,
                player.posX + dAmbit, player.posY + dAmbit, player.posZ + dAmbit);

        List<Entity> list = this.world.getEntitiesInAABBexcluding(player, remove_bb, EntitySelectorDestructable.getInstance());

        StylishRankManager.setNextAttackType(player, StylishRankManager.AttackTypes.DestructObject);
        
        for(Entity curEntity : list){

            boolean isDestruction = true;

            if(curEntity instanceof EntityFireball){
                if((((EntityFireball)curEntity).shootingEntity != null && ((EntityFireball)curEntity).shootingEntity.getEntityId() == player.getEntityId())){
                    isDestruction = false;
                }else{
                    isDestruction = !curEntity.attackEntityFrom(DamageSource.causeMobDamage(player), 1.0f);
                }
            }else if(curEntity instanceof EntityArrow){
                if((((EntityArrow)curEntity).shootingEntity != null && ((EntityArrow)curEntity).shootingEntity.getEntityId() == player.getEntityId())){
                    isDestruction = false;
                }
            }else if(curEntity instanceof IThrowableEntity){
                if((((IThrowableEntity)curEntity).getThrower() != null && ((IThrowableEntity)curEntity).getThrower().getEntityId() == player.getEntityId())){
                    isDestruction = false;
                }
            }else if(curEntity instanceof EntityThrowable){
                if((((EntityThrowable)curEntity).getThrower() != null && ((EntityThrowable)curEntity).getThrower().getEntityId() == player.getEntityId())){
                    isDestruction = false;
                }
            }

            if(!isDestruction)
                continue;
            else{
                ReflectionAccessHelper.setVelocity(curEntity, 0, 0, 0);
                curEntity.setDead();

                for (int var1 = 0; var1 < 10; ++var1)
                {
                    Random rand = new Random();
                    double var2 = rand.nextGaussian() * 0.02D;
                    double var4 = rand.nextGaussian() * 0.02D;
                    double var6 = rand.nextGaussian() * 0.02D;
                    double var8 = 10.0D;
                    this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL
                            , curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8
                            , curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8
                            , curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8
                            , var2, var4, var6);
                }
            }

            StylishRankManager.doAttack(player);
        }
		
        Vec3d pos = hit_pos;
		AxisAlignedBB hit_bb = new AxisAlignedBB(pos.x - dAmbit, pos.y - dAmbit, pos.z - dAmbit,
				pos.x + dAmbit, pos.y + dAmbit, pos.z + dAmbit);
        float damage = ItemAnimaSheath.KillCount.get(blade.getTagCompound(), 0) * 0.01f + 1;
		List<Entity> entitys = this.world.getEntitiesInAABBexcluding(player, hit_bb, EntitySelectorAttackable.getInstance());
		for (Entity n : entitys)
		{		
			n.hurtResistantTime = 0;
			n.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
			ItemSlashBlade itemBlade = (ItemSlashBlade)blade.getItem();
            itemBlade.attackTargetEntity(blade, n, player, true);
            ReflectionAccessHelper.setVelocity(n, 0, 0, 0);
		}
	
		if (ticksExisted >= getlifetime())
		{
			if (isJustSA)
				player.setGameType(game_type);
			StylishRankManager.setNextAttackType(player ,StylishRankManager.AttackTypes.Spear);
			Vec3d look = getVector(player.rotationPitch, player.rotationYaw).scale(0.25);
	        ReflectionAccessHelper.setVelocity(player, -look.x, 1, -look.z);
	        setDead();
		}		
			
	}
	 
	
	@Override
	protected void entityInit() {
		
	}

	public UUID getPlayerID()
	{
		return playerid;
	}
	
	public void setPlayerID(UUID value)
	{
		playerid = value;
	}
	
	public int getlifetime()
	{
		return lifetime;
	}
	
	public void setlifetime(int time)
	{
		lifetime = time;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasUniqueId("playerid"))
			setPlayerID(compound.getUniqueId("playerid"));
		
		if (compound.hasKey("lifetime"))
			setlifetime(compound.getInteger("lifetime"));
		
		if (compound.hasKey("hit_pos"))
		{
			NBTTagCompound hit_pos = compound.getCompoundTag("hit_pos");
			this.hit_pos = new Vec3d(hit_pos.getDouble("x"), hit_pos.getDouble("y"), hit_pos.getDouble("z"));
		}
		
		if (compound.hasKey("blade"))
			this.blade = new ItemStack(compound.getCompoundTag("blade"));
	
		if (compound.hasKey("GameType"))
			this.game_type = GameType.parseGameTypeWithDefault(compound.getString("GameType"), GameType.SURVIVAL);
		
		if (compound.hasKey("isJustSA"))
			this.isJustSA = compound.getBoolean("isJustSA");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setUniqueId("playerid", playerid);
		
		compound.setInteger("lifetime", lifetime);
		
		NBTTagCompound hit_pos = new NBTTagCompound();
		hit_pos.setDouble("x", this.hit_pos.x);
		hit_pos.setDouble("y", this.hit_pos.y);
		hit_pos.setDouble("z", this.hit_pos.z);
		compound.setTag("hit_pos", hit_pos);
		
		NBTTagCompound blade = new NBTTagCompound();
		compound.setTag("blade", this.blade.writeToNBT(blade));
		
		if (this.game_type != null)
			compound.setString("GameType", this.game_type.getName());
		compound.setBoolean("isJustSA", this.isJustSA);
	}
	
	protected Vec3d getVector(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }
}
