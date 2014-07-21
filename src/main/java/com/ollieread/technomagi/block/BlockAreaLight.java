package com.ollieread.technomagi.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.ollieread.technomagi.TechnoMagi;
import com.ollieread.technomagi.common.Reference;
import com.ollieread.technomagi.item.ItemDigitalTool;
import com.ollieread.technomagi.tileentity.TileEntityAreaLight;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAreaLight extends BlockContainer implements IDigitalToolable
{

    public BlockAreaLight(String name)
    {
        super(Material.iron);

        setBlockName(name);
        setBlockTextureName(name);
        setCreativeTab(TechnoMagi.tabTM);
        setLightLevel(0F);
        setLightOpacity(0);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntityAreaLight();
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return !world.canBlockSeeTheSky(x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        if (!world.isRemote) {

        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float playerX, float playerY, float playerZ)
    {
        if (!world.isRemote) {
            ItemStack stack = player.getHeldItem();

            if (stack != null) {
                ItemDigitalTool tool = (ItemDigitalTool) stack.getItem();

                if (tool != null) {
                    TileEntityAreaLight light = (TileEntityAreaLight) world.getTileEntity(x, y, z);

                    if (light != null) {
                        light.toggleStatus();

                        player.addChatComponentMessage(new ChatComponentText("Area light " + (light.isOn() ? "enabled" : "disabled")));

                        return true;
                    }
                }
            }
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        blockIcon = register.registerIcon(Reference.MODID.toLowerCase() + ":" + getTextureName());
    }

}
