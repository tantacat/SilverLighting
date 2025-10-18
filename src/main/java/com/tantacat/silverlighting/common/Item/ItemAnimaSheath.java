package com.tantacat.silverlighting.common.Item;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;
import com.tantacat.silverlighting.registers.RegisterBlades;
import com.tantacat.silverlighting.registers.RegisterVoices;
import com.tantacat.silverlighting.util.BoostProfile;
import com.tantacat.silverlighting.util.BoostProfileHelper;
import com.tantacat.silverlighting.util.DamageProfile;
import com.tantacat.silverlighting.util.DamageProfileHelper;
import com.tantacat.silverlighting.util.NBTHelper;
import com.tantacat.silverlighting.util.NBTHelper.EnchantMode;
import com.tantacat.silverlighting.util.OtherUtills;

import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.ability.StylishRankManager;
import mods.flammpfeil.slashblade.ability.StylishRankManager.AttackTypes;
import mods.flammpfeil.slashblade.entity.EntityDrive;
import mods.flammpfeil.slashblade.entity.EntityLumberManager;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorAttackable;
import mods.flammpfeil.slashblade.entity.selector.EntitySelectorDestructable;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.specialattack.IJustSpecialAttack;
import mods.flammpfeil.slashblade.specialattack.SpecialAttackBase;
import mods.flammpfeil.slashblade.util.ReflectionAccessHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemAnimaSheath extends ItemSlashBladeWrapper{
	
	/*
	 * 修改了攻击组成和耐久损耗
	*/
	public ItemAnimaSheath(ToolMaterial par2EnumToolMaterial) {
		super(par2EnumToolMaterial);
	}
	
	@Override
	public void updateAttackAmplifier(EnumSet<SwordType> swordType,NBTTagCompound tag,EntityLivingBase el,ItemStack sitem)
	{
		float tagAttackAmplifier = this.AttackAmplifier.get(tag);

		DamageProfile sum = DamageProfileHelper.getSumDamageProfile(sitem);
		
        float baseModif = getBaseAttackModifiers(tag);
        float attackAmplifier = sum.getBase();

        int rank = StylishRankManager.getStylishRank(el);

        if(rank < 3 || swordType.contains(SwordType.Broken) || swordType.contains(SwordType.Sealed)){
            attackAmplifier = attackAmplifier - baseModif;
        }else if( rank == 7 || 5 <= rank && swordType.contains(SwordType.FiercerEdge)){
            float level;
            if(el instanceof EntityPlayer)
                level = ((EntityPlayer)el).experienceLevel;
            else
                level = el.getHealth();

            float max = RefineBase + RepairCount.get(tag);

            attackAmplifier = (attackAmplifier + Math.min(level, max) + sum.getExtra()) * sum.getMultiplier();
        }
        else
        	attackAmplifier = attackAmplifier + sum.getExtra();
        attackAmplifier += sum.getFit();
        
        if(tagAttackAmplifier != attackAmplifier)
        {
            this.AttackAmplifier.set(tag, attackAmplifier);

            NBTTagList attrTag = null;

            attrTag = new NBTTagList();
            tag.setTag("AttributeModifiers",attrTag);

            attrTag.appendTag(
                    getAttrTag(
                            SharedMonsterAttributes.ATTACK_DAMAGE.getName()
                            , new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)(attackAmplifier + baseModif), 0)
                            , EntityEquipmentSlot.MAINHAND)
            );
            attrTag.appendTag(
                    getAttrTag(SharedMonsterAttributes.ATTACK_SPEED.getName()
                            , new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4000000953674316D, 0)
                            , EntityEquipmentSlot.MAINHAND)
            );

            el.getAttributeMap().removeAttributeModifiers(sitem.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
            el.getAttributeMap().applyAttributeModifiers(sitem.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        }
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformationMaxAttack(ItemStack ItemStack,
                                        EntityPlayer player, List par3List, boolean par4) {

        NBTTagCompound tag = getItemTagCompound(ItemStack);
        float repair = RepairCount.get(tag);
        EnumSet<SwordType> swordType = getSwordType(ItemStack);

        par3List.add("");
        par3List.add("§4RankAttackDamage");
        String header;
        String template;

        if(swordType.contains(SwordType.FiercerEdge)){
            header = "§e<B§r/§6B-A§r/§4S-SSS§r/§5Limit";
            template = "§e+%.1f§r/§6+%.1f§r/§4+%.1f§r/§5+%.1f";
        }else{
        	header = "§e<B§r/§6B-SS§r/§4SSS§r/§5Limit";
            template = "§e+%.1f§r/§6+%.1f§r/§4+%.1f§r/§5+%.1f";
        }

        DamageProfile sum = DamageProfileHelper.getSumDamageProfile(ItemStack);
        float seal = sum.getBase();
        float ba = seal + sum.getExtra() + getBaseAttackModifiers(tag);
        
        float level = player.experienceLevel;
        float refine = RefineBase + RepairCount.get(tag);
        float sss = (ba + Math.min(level, refine) ) * sum.getMultiplier();
        float limit = (ba + refine) * sum.getMultiplier();
        float fit_attack = sum.getFit();
        
        par3List.add(header);
        par3List.add(String.format(template, seal + fit_attack, ba + fit_attack, sss + fit_attack, limit + fit_attack));

    }
	
	@Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase)
    {
		NBTTagCompound tag = getItemTagCompound(par1ItemStack);

        updateKillCount(par1ItemStack, par2EntityLivingBase, par3EntityLivingBase);

    	ComboSequence comboSec = getComboSequence(tag);

        setImpactEffect(par1ItemStack, par2EntityLivingBase, par3EntityLivingBase, comboSec);

        if((!comboSec.useScabbard && comboSec.mainHandCombo == null) || IsNoScabbard.get(tag)) {
            this.damageItem(par1ItemStack, 1, par3EntityLivingBase);

            /*
            if(par1ItemStack.getCount() <= 0) {
                ItemSlashBlade blade = (ItemSlashBlade)par1ItemStack.getItem();

                if(!this.isDestructable(par1ItemStack)){
                    par1ItemStack.setCount(1);
                    IsBroken.set(tag,true);

                    if(blade instanceof ItemSlashBladeWrapper){
                        if(!ItemSlashBladeWrapper.TrueItemName.exists(tag)){
                            ((ItemSlashBladeWrapper)blade).removeWrapItem(par1ItemStack);
                        }
                    }

                    if(blade == SlashBlade.bladeWhiteSheath && par3EntityLivingBase instanceof EntityPlayer){
                        AchievementList.triggerAchievement((EntityPlayer) par3EntityLivingBase, "brokenWhiteSheath");
                    }

                    blade.dropItemDestructed(par3EntityLivingBase, par1ItemStack);
                }
            }*/
        }

        StylishRankManager.doAttack(par3EntityLivingBase);

		return true;
    }
	
	@Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState blockIn, BlockPos pos, EntityLivingBase entityLiving)
	{
        if (blockIn.getBlockHardness(worldIn, pos) != 0.0)
            this.damageItem(stack, 1, entityLiving);

        if((entityLiving instanceof EntityPlayer) && blockIn.getBlock().isWood(worldIn, pos)){
            NBTTagCompound tag = getItemTagCompound(stack);
            int id = tag.getInteger("lumbmanager");
            Entity prevEntity = worldIn.getEntityByID(id);
            if((prevEntity == null || !(prevEntity instanceof EntityLumberManager)) && stack.getItem() instanceof ItemSlashBlade){
                ItemSlashBlade bladeItem = (ItemSlashBlade)stack.getItem();

                ItemSlashBlade.setComboSequence(tag, ItemSlashBlade.ComboSequence.Battou);
                bladeItem.doSwingItem(stack,(EntityPlayer)entityLiving);

                if(!worldIn.isRemote) {
                    EntityLumberManager manager = new EntityLumberManager(worldIn, blockIn.getBlock());
                    manager.setOwner((EntityPlayer) entityLiving);
                    manager.setLifeTime(20 * 30);

                    manager.setPosition(pos.getX(), pos.getY(), pos.getZ());

                    worldIn.spawnEntity(manager);

                    tag.setInteger("lumbmanager", manager.getEntityId());
                }
            }
        }

        return true;
    }
	
	@Override
	public void doAttack(ItemStack stack, ComboSequence comboSeq, EntityPlayer player)
	{
        World world = player.getEntityWorld();
        NBTTagCompound tag = getItemTagCompound(stack);
        EnumSet<SwordType> swordType = getSwordType(stack);

        long currentTime = world.getTotalWorldTime();
        LastActionTime.set(tag, currentTime);

        OnClick.set(tag,true);
        setPlayerEffect(stack, comboSeq, player);
        setComboSequence(tag, comboSeq);


        //par3EntityPlayer.swingItem();
        doSwingItem(stack, player);

        updateStyleAttackType(stack, player);

        AxisAlignedBB bb = getBBofCombo(stack, comboSeq, player);

        int rank = StylishRankManager.getStylishRank(player);

        List<Entity> list = world.getEntitiesInAABBexcluding(player, bb, EntitySelectorAttackable.getInstance());

        StylishRankManager.Whiffs(player, list.isEmpty());

        for(Entity curEntity : list){
            if(stack.isEmpty()) break;
            
            switch (comboSeq) {
            case Saya1:
            case Saya2:
            case Force3:
            case Force4:
            	float attack = 4.0f;
                if(rank < 3 || swordType.contains(SwordType.Broken)){
                    attack = 2.0f;
                }else{
                    attack += Item.ToolMaterial.STONE.getAttackDamage(); //stone like
                    if(swordType.contains(SwordType.FiercerEdge) && player instanceof EntityPlayer){
                        attack += AttackAmplifier.get(tag) * 0.5f;
                    }
                }

                if (curEntity instanceof EntityLivingBase)
                {
                    float var4 = 0;
                    var4 = EnchantmentHelper.getModifierForCreature(stack, ((EntityLivingBase)curEntity).getCreatureAttribute());
                    if(var4 > 0)
                        attack += var4;
                }

                if (curEntity instanceof EntityLivingBase){
                    attack = Math.min(attack,((EntityLivingBase)curEntity).getHealth()-1);
                }

                curEntity.hurtResistantTime = 0;
                curEntity.attackEntityFrom(DamageSource.causeMobDamage(player), attack);


                if (curEntity instanceof EntityLivingBase){
                    this.hitEntity(stack, (EntityLivingBase)curEntity, player);
                }

                break;

            case None:
                break;

            default:
                this.attackTargetEntity(stack, curEntity, player, true);
                //player.attackTargetEntityWithCurrentItem(curEntity);
                player.onCriticalHit(curEntity);
                break;
            }
        }
        OnClick.set(tag, false);


        if (swordType.containsAll(SwordType.BewitchedPerfect) && comboSeq.equals(ComboSequence.Battou)) {
            this.damageItem(stack, 10, player);
        }
    }
	
	@Override
	public void doAddAttack(ItemStack stack, EntityPlayer player, ComboSequence setCombo)
	{

        NBTTagCompound tag = getItemTagCompound(stack);
        World world = player.world;
        if(!world.isRemote){

            final int cost = -10;
            if(!ProudSoul.tryAdd(tag, cost, false)){
                this.damageItem(stack, 5, player);
            }

            float baseModif = getBaseAttackModifiers(tag);
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            float magicDamage = baseModif;
            int rank = StylishRankManager.getStylishRank(player);
            if(5 <= rank){
                magicDamage += AttackAmplifier.get(tag) * (0.5f + (level / 5.0f));
            }

            EntityDrive entityDrive = new EntityDrive(world, player, magicDamage, false, 90.0f - setCombo.swingDirection);
            if (entityDrive != null) {
                entityDrive.setInitialSpeed(0.75f);
                entityDrive.setLifeTime(20);
                world.spawnEntity(entityDrive);
            }

            setComboSequence(tag, setCombo);
            return;
        }
    }
	
	@Override
	public void doSlashBladeAttack(ItemStack stack, EntityLivingBase player, ComboSequence setCombo){

        NBTTagCompound tag = getItemTagCompound(stack);
        World world = player.world;

        player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                0.8F, 0.01F);

        if(!world.isRemote){

            float baseModif = getBaseAttackModifiers(tag);
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            float magicDamage = baseModif;
            int rank = StylishRankManager.getStylishRank(player);
            if(5 <= rank){
                magicDamage += AttackAmplifier.get(tag) * (0.5f + (level / 5.0f));
            }
            boolean disableMultiHit = rank <= 5;
            EntityDrive entityDrive = new EntityDrive(world, player, magicDamage, disableMultiHit, 90.0f - Math.abs(setCombo.swingDirection));
            if (entityDrive != null) {
                entityDrive.setInitialSpeed(0.1f);
                entityDrive.setLifeTime(20);

                EnumSet<SwordType> type = getSwordType(stack);
                entityDrive.setIsSlashDimension(type.contains(SwordType.FiercerEdge));

                world.spawnEntity(entityDrive);
            }

            setComboSequence(tag, setCombo);
            return;
        }
    }
	
	@Override
	public void doChargeAttack(ItemStack stack, EntityPlayer par3EntityPlayer,boolean isJust){

        NBTTagCompound tag = getItemTagCompound(stack);
		IsCharged.set(tag, true);
		
        SpecialAttackBase sa = getSpecialAttack(stack);
        if(isJust && sa instanceof IJustSpecialAttack){
            ((IJustSpecialAttack)sa).doJustSpacialAttack(stack,par3EntityPlayer);
        }else {
            sa.doSpacialAttack(stack, par3EntityPlayer);
        }

    }
	
	@Override
	public void DestructEntity(EntityLivingBase entityLiving, ItemStack stack) 
	{

        ComboSequence comboSeq = getComboSequence(getItemTagCompound(stack));

        if(!comboSeq.equals(ComboSequence.None))
        {
            int destructedCount = 0;

            AxisAlignedBB bb = getBBofCombo(
                    stack,
                    comboSeq,
                    entityLiving);

            StylishRankManager.setNextAttackType(entityLiving ,AttackTypes.DestructObject);

            List<Entity> list = entityLiving.world.getEntitiesInAABBexcluding(entityLiving, bb, EntitySelectorDestructable.getInstance());
            for(Entity curEntity : list){
                if(stack.isEmpty()) break;

                boolean isDestruction = true;

                EnumSet<SwordType> swordType =getSwordType(stack);

                if(curEntity instanceof EntityFireball){
                    if((((EntityFireball)curEntity).shootingEntity != null && ((EntityFireball)curEntity).shootingEntity.getEntityId() == entityLiving.getEntityId())){
                        isDestruction = false;
                    }else if(!swordType.contains(SwordType.Bewitched)){
                        isDestruction = !curEntity.attackEntityFrom(DamageSource.causeMobDamage(entityLiving),this.defaultBaseAttackModifier);
                    }

                    if(isDestruction && swordType.contains(SwordType.Bewitched)){
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack)){
                            ReflectionProjecTile(curEntity,entityLiving);
                        }else{
                            InductionProjecTile(curEntity,entityLiving);
                        }
                        isDestruction = false;
                    }

                }else if(curEntity instanceof EntityArrow){
                    if((((EntityArrow)curEntity).shootingEntity != null && ((EntityArrow)curEntity).shootingEntity.getEntityId() == entityLiving.getEntityId())){
                        isDestruction = false;
                    }

                    if(isDestruction && swordType.contains(SwordType.Bewitched)){
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack)){
                            ReflectionProjecTile(curEntity,entityLiving);
                        }else{
                            Entity target = null;

                            NBTTagCompound tag = stack.getTagCompound();
                            int eId = TargetEntityId.get(tag);
                            if(eId != 0){
                                Entity tmp = entityLiving.world.getEntityByID(eId);
                                if(tmp != null){
                                    if(tmp.getDistance(entityLiving) < 30.0f)
                                        target = tmp;
                                }
                            }
                            if(target != null && target instanceof EntityCreeper){
                                InductionProjecTile(curEntity, null, entityLiving.getLookVec());
                            }else{
                                InductionProjecTile(curEntity, entityLiving);
                            }
                        }
                        isDestruction = false;
                    }
                }else if(curEntity instanceof IThrowableEntity){
                    if((((IThrowableEntity)curEntity).getThrower() != null && ((IThrowableEntity)curEntity).getThrower().getEntityId() == entityLiving.getEntityId())){
                        isDestruction = false;
                    }

                    if(isDestruction && swordType.contains(SwordType.Bewitched)){
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack)){
                            ReflectionProjecTile(curEntity,entityLiving);
                        }else{
                            InductionProjecTile(curEntity,entityLiving);
                        }
                        isDestruction = false;
                    }
                }else if(curEntity instanceof EntityThrowable){
                    if((((EntityThrowable)curEntity).getThrower() != null && ((EntityThrowable)curEntity).getThrower().getEntityId() == entityLiving.getEntityId())){
                        isDestruction = false;
                    }

                    if(isDestruction && swordType.contains(SwordType.Bewitched)){
                        if(0 < EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack)){
                            ReflectionProjecTile(curEntity,entityLiving);
                        }else{
                            InductionProjecTile(curEntity,entityLiving);
                        }
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
                        Random rand = entityLiving.getRNG();
                        double var2 = rand.nextGaussian() * 0.02D;
                        double var4 = rand.nextGaussian() * 0.02D;
                        double var6 = rand.nextGaussian() * 0.02D;
                        double var8 = 10.0D;
                        entityLiving.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL
                                , curEntity.posX + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var2 * var8
                                , curEntity.posY + (double)(rand.nextFloat() * curEntity.height) - var4 * var8
                                , curEntity.posZ + (double)(rand.nextFloat() * curEntity.width * 2.0F) - (double)curEntity.width - var6 * var8
                                , var2, var4, var6);
                    }

                    destructedCount++;
                }

                StylishRankManager.doAttack(entityLiving);

                StylishRankManager.WhiffsRecover(entityLiving);

            }

            if(0 < destructedCount){
                this.damageItem(stack, 1,entityLiving);
            }
        }
    }
	
	@Override
	public void attackTargetEntity(ItemStack stack, Entity target, EntityPlayer player, Boolean isRightClick){
        NBTTagCompound tag = getItemTagCompound(stack);
        OnClick.set(tag, isRightClick);
        ComboSequence combo = getComboSequence(tag);

        ItemStack mainHand = player.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack offhand = player.getHeldItem(EnumHand.OFF_HAND);
        NBTTagCompound offTag = null;
        if(!offhand.isEmpty())
            offTag = getItemTagCompound(offhand);

        if(combo.mainHandCombo != null && offTag != null) {
            OnClick.set(offTag,true);
            player.setHeldItem(EnumHand.MAIN_HAND, offhand);
        }

        player.attackTargetEntityWithCurrentItem(target);

        if(combo.mainHandCombo != null && offTag != null) {
            this.damageItem(offhand, 1, player);
            if(target instanceof EntityLivingBase)
                stack.hitEntity((EntityLivingBase) target, player);
            OnClick.set(offTag,false);

            if(offhand.getCount() <= 0)
                player.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
        }

        player.setHeldItem(EnumHand.MAIN_HAND, mainHand);

        OnClick.set(tag, false);
    }
	
	static public void damageItem(ItemStack stack, int damage, EntityLivingBase user)
	{
		
        NBTTagCompound tag = getItemTagCompound(stack);
        ItemSlashBlade blade = (ItemSlashBlade)stack.getItem();

        ItemStack copy = stack.copy();

        boolean imotal = !blade.isDestructable(stack);
        if(imotal)
            stack.setCount(2);
        
        tag.setBoolean("IsManagedDamage", true);
        if (stack.getItemDamage() == stack.getMaxDamage())
            OtherUtills.damageItemIgnoreUnbreaking(stack, damage, user);
        else
        	stack.damageItem(damage, user);
        tag.setBoolean("IsManagedDamage", false);
        boolean doDrop = stack.isEmpty(); //isNull
        
        if(imotal){
            doDrop = stack.getCount() == 1;
            stack.setCount(1);

            if(doDrop) {
                boolean setBroken = true;

                if (!blade.isDestructable(stack)) {
                    stack.setCount(1);
                    stack.setItemDamage(stack.getMaxDamage());

                    if (blade instanceof ItemAnimaSheath)
                    {
                    	doDrop = hasWrapedItem(stack);
                        if (!ItemSlashBladeWrapper.TrueItemName.exists(tag)) {
                			if (user instanceof EntityPlayer)
                			{
                				RegisterVoices.instance.sendMessage((EntityPlayer)user, "bladebroken", stack);
                			}
                			((ItemAnimaSheath)stack.getItem()).removeWrapItem(stack);
                	        setBroken = false;
                        }                        	
                    }
                }
                
                if (ItemAnimaSheath.CurrentItemName.get(tag).equals("silverlighting.dokkaebisheath"))
                	ItemAnimaSheath.IsSealed.set(tag, true);

                IsBroken.set(tag, setBroken);
            }
        }

        if(doDrop && !IsBroken.get(getItemTagCompound(copy)) && !copy.getTagCompound().getBoolean("Unbreakable"))
            blade.dropItemDestructed(user, copy);
    }
	
	
	Map<ComboSequence, String> attackTypeMap = crateAttackTypeMap();
    public Map crateAttackTypeMap()
    {
        attackTypeMap = Maps.newHashMap();

        attackTypeMap.put(ComboSequence.Kiriage,AttackTypes.Kiriage);
        attackTypeMap.put(ComboSequence.Kiriorosi,AttackTypes.Kiriorosi);

        attackTypeMap.put(ComboSequence.Iai,AttackTypes.Iai);

        attackTypeMap.put(ComboSequence.Saya1,AttackTypes.Saya1);
        attackTypeMap.put(ComboSequence.Saya2,AttackTypes.Saya2);


        attackTypeMap.put(ComboSequence.HiraTuki,AttackTypes.Kiriage);


        attackTypeMap.put(ComboSequence.SlashEdge,AttackTypes.SlashEdge);
        attackTypeMap.put(ComboSequence.ReturnEdge,AttackTypes.ReturnEdge);

        attackTypeMap.put(ComboSequence.SIai,AttackTypes.SIai);
        attackTypeMap.put(ComboSequence.SSlashEdge,AttackTypes.SSlashEdge);
        attackTypeMap.put(ComboSequence.SReturnEdge,AttackTypes.SReturnEdge);
        attackTypeMap.put(ComboSequence.SSlashBlade,AttackTypes.SSlashBlade);


        attackTypeMap.put(ComboSequence.ASlashEdge,AttackTypes.ASlashEdge);
        attackTypeMap.put(ComboSequence.AKiriorosi,AttackTypes.AKiriorosi);


        attackTypeMap.put(ComboSequence.AKiriage,AttackTypes.AKiriage);
        attackTypeMap.put(ComboSequence.AKiriorosiFinish,AttackTypes.AKiriorosiFinish);

        attackTypeMap.put(ComboSequence.HelmBraker,AttackTypes.HelmBraker);

        attackTypeMap.put(ComboSequence.Calibur,AttackTypes.Calibur);

        attackTypeMap.put(ComboSequence.RapidSlash,AttackTypes.RapidSlash);
        attackTypeMap.put(ComboSequence.RisingStar,AttackTypes.RisingStar);

        attackTypeMap.put(ComboSequence.Force1,AttackTypes.Force1);
        attackTypeMap.put(ComboSequence.Force2,AttackTypes.Force2);
        attackTypeMap.put(ComboSequence.Force3,AttackTypes.Force3);
        attackTypeMap.put(ComboSequence.Force4,AttackTypes.Force4);
        attackTypeMap.put(ComboSequence.Force5,AttackTypes.Force5);
        attackTypeMap.put(ComboSequence.Force6,AttackTypes.Force6);

        attackTypeMap.put(ComboSequence.Stinger,AttackTypes.RapidSlash);

        return attackTypeMap;
    }
    
	protected void updateStyleAttackType(ItemStack stack, EntityLivingBase e)
	{
        NBTTagCompound tag = getItemTagCompound(stack);

        ComboSequence combo = getComboSequence(tag);

        String key = attackTypeMap.get(combo);

        if(key == null){
            switch (combo) {
                case Battou:
                    EnumSet<SwordType> swordType = getSwordType(stack);
                    if (swordType.containsAll(SwordType.BewitchedPerfect)) {
                        /* todo: advancement
                        if (e instanceof EntityPlayer)
                            AchievementList.triggerAchievement((EntityPlayer) e, "bewitched");
                        */
                        key = AttackTypes.IaiBattou;
                    } else if (e.onGround)
                        key = AttackTypes.Battou;
                    else
                        key = AttackTypes.JumpBattou;
                    break;

                default:
                    key = AttackTypes.SimpleAttack;
            }
        }
        StylishRankManager.setNextAttackType(e, key);
    }
	
	public void addInformationSpecialBoost(ItemStack stack,
            EntityPlayer player, List lines, boolean advanced)
	{
		List<BoostProfile> boosts = BoostProfileHelper.getBoostProfiles(stack);

        if(boosts.size() == 0) return;

        lines.add("");

        for (BoostProfile boost : boosts)
        {
        	lines.add(I18n.format("silverlighting.boost.name." + boost.getId())
        			+ "§r "
        			+ (boost.getEnable() ? "§2" : "§4")
        			+ boost.getEnable());
        }
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			World world, List par3List, ITooltipFlag inFlag) {

        EntityPlayer par2EntityPlayer = Minecraft.getMinecraft().player;
        boolean par4 = inFlag.isAdvanced();

        if(par2EntityPlayer == null) return;

        addInformationOwner(par1ItemStack, par2EntityPlayer, par3List, par4);

		addInformationSwordClass(par1ItemStack, par2EntityPlayer, par3List, par4);

		addInformationKillCount(par1ItemStack, par2EntityPlayer, par3List, par4);

		addInformationProudSoul(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationSpecialAttack(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationRepairCount(par1ItemStack, par2EntityPlayer, par3List, par4);
        
        addInformationSpecialBoost(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationRangeAttack(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationSpecialEffec(par1ItemStack, par2EntityPlayer, par3List, par4);

        addInformationMaxAttack(par1ItemStack, par2EntityPlayer, par3List, par4);

		NBTTagCompound tag = getItemTagCompound(par1ItemStack);
        if(tag.hasKey(adjustXStr)){
            float ax = tag.getFloat(adjustXStr);
            float ay = tag.getFloat(adjustYStr);
            float az = tag.getFloat(adjustZStr);
            par3List.add(String.format("adjust x:%.1f y:%.1f z:%.1f", ax,ay,az));
        }

        addInformationEnergy(par1ItemStack, par2EntityPlayer, par3List, par4);
	}
	
	@Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
        	for(String bladename : RegisterBlades.instance.SlNamedBlades){
                ItemStack blade = RegisterBlades.instance.getCustomBlade(bladename);
                if(blade.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    blade.setItemDamage(0);
                }
                if(!blade.isEmpty()) {
                    subItems.add(blade);
                }
            }
        }
    }
	
	@Override
	public void removeWrapItem(ItemStack stack){
        NBTTagCompound tag = getItemTagCompound(stack);
        
        String name = (ItemAnimaSheath.CurrentItemName.get(tag).equals("silverlighting.silverlighting") && 
    			ItemSlashBlade.KillCount.get(tag, 0) >= 1000) ? "dokkaebisheath" : "animasheath";
        stack.setTagCompound(NBTHelper.instance.mergeNBTTagCompound(EnchantMode.merge, 
        		RegisterBlades.instance.getCustomBlade("animasheath").getTagCompound(), tag));
        stack.setItemDamage(0);
        ItemSlashBlade.IsCharged.set(stack.getTagCompound(), false);
    }
	
	@Override
	public void onUpdate(ItemStack sitem, World par2World,
			Entity par3Entity, int indexOfMainSlot, boolean isCurrent) 
	{
		super.onUpdate(sitem, par2World, par3Entity, indexOfMainSlot, isCurrent);
		if (!CurrentItemName.exists(sitem.getTagCompound()))
			removeWrapItem(sitem);
	}
}
