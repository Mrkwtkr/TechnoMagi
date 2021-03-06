package com.ollieread.technomagi.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import com.ollieread.technomagi.tileentity.TileEntityAnalysis;

public class SlotAnalysis extends Slot
{

    protected boolean empty = false;
    protected TileEntityAnalysis analysis;

    public SlotAnalysis(TileEntityAnalysis analysis, int i, int x, int y)
    {
        super((IInventory) analysis, i, x, y);
        this.analysis = analysis;
    }

    public boolean canTakeStack(EntityPlayer player)
    {
        return !analysis.inProgress();
    }

}
