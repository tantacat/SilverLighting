package com.tantacat.silverlighting.util;

import net.minecraft.nbt.NBTTagCompound;

public class BoostProfile {

	public enum BoostType
	{
		fight,
		help,
		add
	}
	
	private final String id;
    private boolean enable;
    private BoostType type;
 
    public BoostProfile(String id, boolean enable, String type)
    {
    	BoostType boosttype = BoostType.valueOf(type);
    	this.id = id;
        this.enable = enable;
        this.type = boosttype;
    }

    public BoostProfile(String id, boolean enable, BoostType type) 
    {
        this.id = id;
        this.enable = enable;
        this.type = type;
    }
    
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", id);
        tag.setBoolean("enable", enable);
        tag.setString("type", type.toString());
        return tag;
    }

    public static BoostProfile deserializeNBT(NBTTagCompound tag) {
        return new BoostProfile(
        		tag.getString("id"), 
        		tag.getBoolean("enable"), 
        		tag.getString("type"));
    }

    public String getId() {return id;}
    public boolean getEnable() {return enable;}
	public BoostType getType() {return type;}
}
