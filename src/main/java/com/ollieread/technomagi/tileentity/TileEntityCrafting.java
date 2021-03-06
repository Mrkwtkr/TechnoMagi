package com.ollieread.technomagi.tileentity;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyStorage;
import cofh.lib.util.helpers.EnergyHelper;

import com.ollieread.technomagi.common.proxy.BasicEnergy;
import com.ollieread.technomagi.common.proxy.CraftingInventory;
import com.ollieread.technomagi.common.proxy.PlayerLocked;
import com.ollieread.technomagi.item.crafting.CraftingManager;
import com.ollieread.technomagi.network.message.MessageSyncTileEntityTM;
import com.ollieread.technomagi.util.PacketHelper;

import cpw.mods.fml.common.FMLCommonHandler;

public class TileEntityCrafting extends TileEntityTM implements IPlayerLocked, IInventory, IEnergyStorage, IEnergyConnection
{

    protected PlayerLocked locked = null;
    protected CraftingInventory inventory = null;
    protected BasicEnergy storage = null;

    protected int progress = 0;
    protected int ticks;
    protected int waiting;
    protected boolean isCrafting = false;
    public CraftingManager crafting = CraftingManager.getInstance();

    public int field_145926_a;
    public float field_145933_i;
    public float field_145931_j;
    public float field_145932_k;
    public float field_145929_l;
    public float field_145930_m;
    public float field_145927_n;
    public float field_145928_o;
    public float field_145925_p;
    public float field_145924_q;
    private static Random field_145923_r = new Random();

    protected int powerUsage = 2;

    public TileEntityCrafting()
    {
        locked = new PlayerLocked();
        inventory = new CraftingInventory(10);
        storage = new BasicEnergy(3200, 2, 0);
    }

    public int getProgress()
    {
        return progress;
    }

    public void setProgress(int i)
    {
        progress = i;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        ticks = compound.getInteger("Ticks");
        progress = compound.getInteger("Progress");
        isCrafting = compound.getBoolean("IsCrafting");

        locked.readFromNBT(compound);
        inventory.readFromNBT(compound);
        storage.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        compound.setInteger("Ticks", ticks);
        compound.setInteger("Progress", progress);
        compound.setBoolean("IsCrafting", isCrafting);

        locked.writeToNBT(compound);
        inventory.writeToNBT(compound);
        storage.writeToNBT(compound);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        this.field_145927_n = this.field_145930_m;
        this.field_145925_p = this.field_145928_o;
        this.field_145924_q += 0.02F;
        this.field_145930_m -= 0.1F;

        while (this.field_145928_o >= (float) Math.PI) {
            this.field_145928_o -= ((float) Math.PI * 2F);
        }

        while (this.field_145928_o < -(float) Math.PI) {
            this.field_145928_o += ((float) Math.PI * 2F);
        }

        while (this.field_145924_q >= (float) Math.PI) {
            this.field_145924_q -= ((float) Math.PI * 2F);
        }

        while (this.field_145924_q < -(float) Math.PI) {
            this.field_145924_q += ((float) Math.PI * 2F);
        }

        float f2;

        for (f2 = this.field_145924_q - this.field_145928_o; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
            ;
        }

        while (f2 < -(float) Math.PI) {
            f2 += ((float) Math.PI * 2F);
        }

        this.field_145928_o += f2 * 0.4F;

        if (this.field_145930_m < 0.0F) {
            this.field_145930_m = 0.0F;
        }

        if (this.field_145930_m > 1.0F) {
            this.field_145930_m = 1.0F;
        }

        ++this.field_145926_a;
        this.field_145931_j = this.field_145933_i;
        float f = (this.field_145932_k - this.field_145933_i) * 0.4F;
        float f3 = 0.2F;

        if (f < -f3) {
            f = -f3;
        }

        if (f > f3) {
            f = f3;
        }

        this.field_145929_l += (f - this.field_145929_l) * 0.9F;
        this.field_145933_i += this.field_145929_l;

        if (!worldObj.isRemote) {
            if (canCraft() && isCrafting()) {
                craft();
            }

            if (EnergyHelper.isAdjacentEnergyHandlerFromSide(this, ForgeDirection.DOWN.ordinal())) {
                int input = storage.getMaxReceive();
                int receive = storage.receiveEnergy(input, true);
                int extract = EnergyHelper.extractEnergyFromAdjacentEnergyHandler(this, ForgeDirection.DOWN.ordinal(), receive, true);

                if (receive > 0 && extract > 0) {

                    if (receive == extract) {
                        extract = EnergyHelper.extractEnergyFromAdjacentEnergyHandler(this, ForgeDirection.DOWN.ordinal(), receive, false);
                    } else if (receive > extract) {
                        extract = EnergyHelper.extractEnergyFromAdjacentEnergyHandler(this, ForgeDirection.DOWN.ordinal(), extract, false);
                    } else if (receive < extract) {
                        extract = EnergyHelper.extractEnergyFromAdjacentEnergyHandler(this, ForgeDirection.DOWN.ordinal(), receive, false);
                    }
                    receiveEnergy(extract, false);

                    sync();
                }
            }
        }
    }

    public void setCrafting(boolean crafting)
    {
        isCrafting = crafting;
        PacketHelper.syncTile(new MessageSyncTileEntityTM(this));
    }

    public boolean canCraft()
    {
        if (getPlayer() != null) {
            EntityPlayer player = worldObj.getPlayerEntityByName(getPlayer());

            if (player != null) {
                ItemStack recipe = crafting.findMatchingRecipe(inventory, worldObj, player);

                if (recipe != null) {
                    ItemStack result = inventory.getStackInSlot(9);

                    if (result == null) {
                        return getEnergyStored() > powerUsage;
                    }

                    if (result.isItemEqual(recipe) && (result.stackSize + recipe.stackSize) <= result.getMaxStackSize()) {
                        return getEnergyStored() > powerUsage;
                    }
                }
            }
        }

        return false;
    }

    public boolean isCrafting()
    {
        return isCrafting;
    }

    public void craft()
    {
        if (storage.modifyEnergyStored(powerUsage)) {
            ticks++;

            if (ticks >= 20) {
                ticks = 0;
                progress++;
            }

            if (progress >= 20) {
                if (getPlayer() != null) {
                    EntityPlayer player = worldObj.getPlayerEntityByName(getPlayer());

                    if (player != null) {
                        ItemStack recipe = crafting.findMatchingRecipe(inventory, worldObj, player);

                        removeFromGrid(player, recipe);

                        if (inventory.getStackInSlot(9) == null) {
                            inventory.setInventorySlotContents(9, recipe.copy());
                        } else if (inventory.getStackInSlot(9).isItemEqual(recipe)) {
                            inventory.getStackInSlot(9).stackSize += recipe.stackSize;
                        }
                    }
                }
                progress = 0;
                ticks = 0;
                setCrafting(false);
            }
        }
    }

    private void removeFromGrid(EntityPlayer player, ItemStack stack)
    {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, inventory);

        for (int i = 0; i < 9; ++i) {
            ItemStack itemstack1 = inventory.getStackInSlot(i);

            if (itemstack1 != null) {
                inventory.decrStackSize(i, 1);

                if (itemstack1.getItem().hasContainerItem(itemstack1)) {
                    ItemStack itemstack2 = itemstack1.getItem().getContainerItem(itemstack1);

                    if (itemstack2 != null && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage()) {
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, itemstack2));
                        continue;
                    }

                    if (!itemstack1.getItem().doesContainerItemLeaveCraftingGrid(itemstack1) || !player.inventory.addItemStackToInventory(itemstack2)) {
                        if (inventory.getStackInSlot(i) == null) {
                            inventory.setInventorySlotContents(i, itemstack2);
                        } else {
                            player.dropPlayerItemWithRandomChoice(itemstack2, false);
                        }
                    }
                }
            }
        }
    }

    /* Everything below is just a proxy for the interfaces */

    /* INVENTORY */

    @Override
    public int getSizeInventory()
    {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int i, int q)
    {
        return inventory.decrStackSize(i, q);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return inventory.getStackInSlotOnClosing(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack)
    {
        inventory.setInventorySlotContents(i, stack);
    }

    public ItemStack[] getInventory()
    {
        return inventory.getInventory();
    }

    @Override
    public String getInventoryName()
    {
        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return inventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack)
    {
        return inventory.isItemValidForSlot(i, stack);
    }

    public ItemStack getStackInRowAndColumn(int row, int col)
    {
        return inventory.getStackInRowAndColumn(row, col);
    }

    /* ENERGY */

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return from.equals(ForgeDirection.DOWN);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored()
    {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored()
    {
        return storage.getMaxEnergyStored();
    }

    /* LOCKED */

    @Override
    public boolean hasPlayer()
    {
        return locked.hasPlayer();
    }

    @Override
    public void setPlayer(String name)
    {
        locked.setPlayer(name);
    }

    @Override
    public String getPlayer()
    {
        return locked.getPlayer();
    }

    @Override
    public boolean isPlayer(String name)
    {
        return locked.isPlayer(name);
    }

    public boolean isPlayer(EntityPlayer player)
    {
        return isPlayer(player.getCommandSenderName());
    }

}
