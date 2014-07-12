package com.ollieread.technomagi.client.renderer.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBook;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.ollieread.technomagi.client.model.ModelMachineConstruct;
import com.ollieread.technomagi.common.Reference;

public class RenderArchiveItem implements IItemRenderer
{

    private final ModelMachineConstruct construct;
    private final ModelBook book;

    public RenderArchiveItem()
    {
        construct = new ModelMachineConstruct();
        book = new ModelBook();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return type == ItemRenderType.INVENTORY || type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.EQUIPPED;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        ResourceLocation textureConstruct = (new ResourceLocation(Reference.MODID.toLowerCase(), "textures/blocks/modelConstruct.png"));
        ResourceLocation textureBook = (new ResourceLocation(Reference.MODID.toLowerCase(), "textures/blocks/modelArchiveBook.png"));

        GL11.glPushMatrix();

        float scale = 0;

        if (type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON) || type.equals(ItemRenderType.EQUIPPED)) {
            scale = 1.2F;
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        } else if (type.equals(ItemRenderType.INVENTORY)) {
            scale = 1.0F;
        }

        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(270F, 0.0F, -1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -1.0F, 0.0F);

        Minecraft.getMinecraft().renderEngine.bindTexture(textureConstruct);
        construct.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        Minecraft.getMinecraft().renderEngine.bindTexture(textureBook);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);

        book.bookSpine.offsetX = -0.8F;
        book.coverRight.offsetX = -0.8F;
        book.coverLeft.offsetX = -0.8F;
        book.pagesRight.offsetX = -0.8F;
        book.pagesLeft.offsetX = -0.8F;
        book.flippingPageRight.offsetX = -0.8F;
        book.flippingPageLeft.offsetX = -0.8F;
        book.bookSpine.offsetX = -0.8F;

        book.render((Entity) null, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0625F);

        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPopMatrix();
    }

}
