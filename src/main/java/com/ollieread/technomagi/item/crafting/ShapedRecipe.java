package com.ollieread.technomagi.item.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import com.ollieread.ennds.extended.ExtendedPlayerKnowledge;

public class ShapedRecipe implements IRecipe, IRecipeKnowledge
{

    public final int width;
    public final int height;
    public final ItemStack[] items;
    private ItemStack output;
    private String knowledge;

    public ShapedRecipe(int width, int height, ItemStack[] items, ItemStack output, String knowledge)
    {
        this.width = width;
        this.height = height;
        this.items = items;
        this.output = output;
        this.knowledge = knowledge;
    }

    @Override
    public boolean matches(InventoryCrafting crafting, World world)
    {
        for (int i = 0; i <= 3 - this.width; ++i) {
            for (int j = 0; j <= 3 - this.height; ++j) {
                if (this.checkMatch(crafting, i, j, true)) {
                    return true;
                }

                if (this.checkMatch(crafting, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting)
    {
        return null;
    }

    @Override
    public int getRecipeSize()
    {
        return this.width * this.height;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }

    private boolean checkMatch(InventoryCrafting crafting, int i, int j, boolean b)
    {
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                int i1 = k - i;
                int j1 = l - j;
                ItemStack itemstack = null;

                if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
                    if (b) {
                        itemstack = this.items[this.width - i1 - 1 + j1 * this.width];
                    } else {
                        itemstack = this.items[i1 + j1 * this.width];
                    }
                }

                ItemStack itemstack1 = crafting.getStackInRowAndColumn(k, l);

                if (itemstack1 != null || itemstack != null) {
                    if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null) {
                        return false;
                    }

                    if (itemstack.getItem() != itemstack1.getItem()) {
                        return false;
                    }

                    if (itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack1.getItemDamage()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean canCraft(EntityPlayer player)
    {
        ExtendedPlayerKnowledge charon = ExtendedPlayerKnowledge.get(player);

        if (charon == null || !charon.hasKnowledge(knowledge)) {
            return false;
        }

        return true;
    }
}