package com.ollieread.technomagi.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.util.ResourceLocation;

public class GuiSpecialiseButton extends GuiButton
{

    protected ResourceLocation texture;

    public GuiSpecialiseButton(int par1, int par2, int par3, String par4Str, ResourceLocation texture)
    {
        super(par1, par2, par3, 32, 32, par4Str);

        this.texture = texture;
    }

    @Override
    public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
    {
        p_146112_1_.getTextureManager().bindTexture(this.texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.func_146110_a(this.xPosition, this.yPosition, 0, 0, this.width, this.height, 32, 32);
    }

}
