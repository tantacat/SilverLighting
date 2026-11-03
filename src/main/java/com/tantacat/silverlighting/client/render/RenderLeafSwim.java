package com.tantacat.silverlighting.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderLeafSwim implements LayerRenderer<EntityPlayer>{
    
	// 旋风纹理 - 需要准备一个透明PNG（如圆环状纹理）
	private static final ResourceLocation WHIRLWIND_TEXTURE = 
	        new ResourceLocation("silverlighting:textures/leafswim.png");
	
	@Override
	public void doRenderLayer(EntityPlayer player, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		
        // 检查是否应该渲染旋风（例如玩家有特定状态或条件）
		if (player.getEntityData().hasKey("renderleafswim") && 
				player.getEntityData().getBoolean("renderleafswim"))
		{
	        // 绑定旋风纹理
	        Minecraft.getMinecraft().getTextureManager().bindTexture(WHIRLWIND_TEXTURE);
	        
	        // 保存当前GL状态
	        GlStateManager.pushMatrix();
	        GlStateManager.enableBlend();
	        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, 
	                               GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	        // 禁用光照（使效果自发光）
	        GlStateManager.disableLighting();
	        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
	        
	        // 定位到玩家中心
	        GlStateManager.translate(0.0F, player.height / 2.0F, 0.0F);
	        
	        // 计算动态参数
	        float rotationSpeed = 10.0F;  // 基础旋转速度
	        float scaleFactor = 3.0F;     // 基础缩放
	        float heightOffset = 0.5F;    // 高度偏移
	        
	        // 随时间变化的动画参数
	        float time = (player.ticksExisted + partialTicks) / 20.0F; // 秒为单位
	        
	        // 旋风动画参数
	        float rotation = time * 360.0F * rotationSpeed; // 旋转角度
	        float pulse = 0.5F + 0.3F * (float)Math.sin(time * 2.0F); // 脉动效果
	        float verticalMovement = (float)Math.sin(time * 1.5F) * 0.2F; // 垂直移动
	        
	        // 渲染多层旋风
	        for (int i = 0; i < 3; i++) {
	            GlStateManager.pushMatrix();
	            
	            // 设置每层属性
	            float layerScale = scaleFactor * (0.8F + i * 0.2F);
	            float layerRotation = rotation * (1.0F - i * 0.2F);
	            float layerHeight = heightOffset + verticalMovement + i * 0.2F;
	            
	            // 定位和变换
	            GlStateManager.translate(0.0F, layerHeight, 0.0F);
	            GlStateManager.rotate(layerRotation, 0.0F, 1.0F, 0.0F);
	            GlStateManager.scale(layerScale * pulse, layerScale * pulse, layerScale * pulse);
	            
	            // 设置透明度（外层更透明）
	            float alpha = 0.7F - i * 0.2F;
	            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
	            
	            // 渲染平面（实际实现需要自定义模型或Tessellator）
	            renderWhirlwindQuad();
	            
	            GlStateManager.popMatrix();
	        }
	        
	        // 恢复GL状态
	        GlStateManager.enableLighting();
	        GlStateManager.disableBlend();
	        GlStateManager.popMatrix();
		}
	}

	private void renderWhirlwindQuad() {
		// 使用Tessellator绘制一个平面（简化示例）
		// 实际开发中建议使用OBJ模型或更复杂的几何体
	    float size = 1.0F;
	        
	    net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
	    net.minecraft.client.renderer.BufferBuilder buffer = tessellator.getBuffer();
	        
	    buffer.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
	    buffer.pos(-size, 0, -size).tex(0, 0).endVertex();
	    buffer.pos(-size, 0, size).tex(0, 1).endVertex();
	    buffer.pos(size, 0, size).tex(1, 1).endVertex();
	    buffer.pos(size, 0, -size).tex(1, 0).endVertex();
	    tessellator.draw();
	}	
	
	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
	
}
