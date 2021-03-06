package com.ollieread.technomagi.item.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.ollieread.technomagi.common.proxy.CraftingInventory;

public class CraftingManager
{
    private static final CraftingManager instance = new CraftingManager();
    private List<IRecipeTM> recipes = new ArrayList();

    public static final CraftingManager getInstance()
    {
        return instance;
    }

    public ShapedRecipe addRecipe(String knowledge, ItemStack output, Object... items)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (items[i] instanceof String[]) {
            String[] astring = (String[]) ((String[]) items[i++]);

            for (int l = 0; l < astring.length; ++l) {
                String s1 = astring[l];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        } else {
            while (items[i] instanceof String) {
                String s2 = (String) items[i++];
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }

        HashMap hashmap;

        for (hashmap = new HashMap(); i < items.length; i += 2) {
            Character character = (Character) items[i];
            ItemStack itemstack1 = null;

            if (items[i + 1] instanceof Item) {
                itemstack1 = new ItemStack((Item) items[i + 1]);
            } else if (items[i + 1] instanceof Block) {
                itemstack1 = new ItemStack((Block) items[i + 1], 1, 32767);
            } else if (items[i + 1] instanceof ItemStack) {
                itemstack1 = (ItemStack) items[i + 1];
            }

            hashmap.put(character, itemstack1);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1) {
            char c0 = s.charAt(i1);

            if (hashmap.containsKey(Character.valueOf(c0))) {
                aitemstack[i1] = ((ItemStack) hashmap.get(Character.valueOf(c0))).copy();
            } else {
                aitemstack[i1] = null;
            }
        }

        ShapedRecipe shapedrecipes = new ShapedRecipe(j, k, aitemstack, output, knowledge);
        this.recipes.add(shapedrecipes);
        return shapedrecipes;
    }

    public void addShapelessRecipe(String knowledge, ItemStack output, Object... items)
    {
        ArrayList arraylist = new ArrayList();
        Object[] aobject = items;
        int i = items.length;

        for (int j = 0; j < i; ++j) {
            Object object1 = aobject[j];

            if (object1 instanceof ItemStack) {
                arraylist.add(((ItemStack) object1).copy());
            } else if (object1 instanceof Item) {
                arraylist.add(new ItemStack((Item) object1));
            } else {
                if (!(object1 instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipe!");
                }

                arraylist.add(new ItemStack((Block) object1));
            }
        }

        this.recipes.add(new ShapelessRecipe(output, arraylist, knowledge));
    }

    public ItemStack findMatchingRecipe(CraftingInventory crafting, World world, EntityPlayer player)
    {
        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;
        int j;

        for (j = 0; j < 9; ++j) {
            ItemStack itemstack2 = crafting.getStackInSlot(j);

            if (itemstack2 != null) {
                if (i == 0) {
                    itemstack = itemstack2;
                }

                if (i == 1) {
                    itemstack1 = itemstack2;
                }

                ++i;
            }
        }

        if (i == 2 && itemstack.getItem() == itemstack1.getItem() && itemstack.stackSize == 1 && itemstack1.stackSize == 1 && itemstack.getItem().isRepairable()) {
            Item item = itemstack.getItem();
            int j1 = item.getMaxDamage() - itemstack.getItemDamageForDisplay();
            int k = item.getMaxDamage() - itemstack1.getItemDamageForDisplay();
            int l = j1 + k + item.getMaxDamage() * 5 / 100;
            int i1 = item.getMaxDamage() - l;

            if (i1 < 0) {
                i1 = 0;
            }

            return new ItemStack(itemstack.getItem(), 1, i1);
        } else {
            for (j = 0; j < this.recipes.size(); ++j) {
                IRecipeTM recipe = (IRecipeTM) this.recipes.get(j);

                if (recipe.matches(crafting, world)) {

                    if (!recipe.canCraft(player)) {
                        continue;
                    }

                    return recipe.getCraftingResult(crafting);
                }
            }

            return null;
        }
    }

    public List<IRecipeTM> getRecipeList()
    {
        return this.recipes;
    }

}
