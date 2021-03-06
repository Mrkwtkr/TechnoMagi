package com.ollieread.technomagi.item;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.ollieread.ennds.research.ResearchRegistry;
import com.ollieread.technomagi.common.Reference;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSampleVile extends ItemTM
{

    @SideOnly(Side.CLIENT)
    protected IIcon itemIconFull;

    public ItemSampleVile(String name)
    {
        super(name);

        setMaxStackSize(64);
        setHasSubtypes(true);
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {

    }

    public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
        stack.stackTagCompound = new NBTTagCompound();
    }

    public static String getEntity(ItemStack stack)
    {
        NBTTagCompound compound = stack.stackTagCompound;

        if (compound.hasKey("Entity")) {
            return compound.getString("Entity");
        }

        return null;
    }

    public static void setEntity(ItemStack stack, Class entityClass)
    {
        NBTTagCompound compound = stack.stackTagCompound;

        if (compound == null) {
            stack.stackTagCompound = new NBTTagCompound();
            compound = stack.stackTagCompound;
        }
        String entityName;

        if (!entityClass.equals(EntityPlayer.class)) {
            entityName = (String) EntityList.classToStringMapping.get(entityClass);
        } else {
            entityName = "player";
        }

        compound.setString("Entity", entityName);
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + (getEntity(stack) != null ? ".full.name" : ".name"));
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        String info = "";

        String entityName = getEntity(stack);
        Class entityClass = (Class) EntityList.stringToClassMapping.get(entityName);

        if (entityName != null) {
            if (entityClass != null) {
                info = StatCollector.translateToLocal("entity." + entityName + ".name");
            } else if (entityName.equals("player")) {
                info = EnumChatFormatting.DARK_PURPLE + "Player";
            }

            list.add(info);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(Reference.MODID.toLowerCase() + ":" + getIconString());
        itemIconFull = register.registerIcon(Reference.MODID.toLowerCase() + ":" + getIconString() + "Full");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack)
    {
        String entityName = this.getEntity(stack);

        if (entityName != null) {
            return itemIconFull;
        }

        return itemIcon;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        ItemStack initial = new ItemStack(item, 1, 0);
        initial.stackTagCompound = new NBTTagCompound();
        this.setEntity(initial, EntityPlayer.class);
        list.add(initial);

        Iterator iterator = ResearchRegistry.getMonitorableEntities().iterator();

        while (iterator.hasNext()) {
            Class entityClass = (Class) iterator.next();

            ItemStack stack = new ItemStack(item, 1, 1);
            initial.stackTagCompound = new NBTTagCompound();
            this.setEntity(stack, entityClass);

            list.add(stack);
        }
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10)
    {
        System.out.println("In use");
        stack.stackSize--;

        if (stack.stackSize == 0) {
            stack = null;
        }

        return true;
    }

}
