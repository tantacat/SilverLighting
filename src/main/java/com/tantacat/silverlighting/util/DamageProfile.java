package com.tantacat.silverlighting.util;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class DamageProfile {

	private final String id;       // 唯一标识符
    private float base;            // 基础伤害
    private float extra;           // 额外伤害
    private float multiplier;      // 伤害倍率
    private float fit;             // 伤害修正
    private IExtraDamageFuction function = DamageProfileHelper.defualtFuction;

    public DamageProfile(String id, float base, float extra, float multiplier, float fit) {
        this.id = id;
        this.base = base;
        this.extra = extra;
        this.multiplier = multiplier;
        this.fit = fit;
    }

    // 序列化到 NBT
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", this.id);
        tag.setFloat("base", this.base);
        tag.setFloat("extra", this.extra);
        tag.setFloat("multiplier", this.multiplier);
        tag.setFloat("fit", this.fit);
        return tag;
    }

    // 从 NBT 反序列化
    public static DamageProfile deserializeNBT(NBTTagCompound tag) {
        return new DamageProfile(
            tag.getString("id"),
            tag.getFloat("base"),
            tag.getFloat("extra"),
            tag.getFloat("multiplier"),
            tag.getFloat("fit")
        );
    }

    // Getter 方法
    public String getId() { return id; }
    public float getBase() { return base; }
    public float getExtra() {return this.function.applayFuction(this.extra); }
    public float getMultiplier( ) {return multiplier; }
    public float getFit() {return fit; }
    public float getSum(ItemStack blade) 
    {
    	return (this.base + this.getExtra() + ItemSlashBlade.BaseAttackModifier.get(blade.getTagCompound(), 4.0f)) * this.multiplier + this.fit;
    }
    
    public void setFuction(IExtraDamageFuction fuc)
    {
    	this.function = fuc;
    }
}
