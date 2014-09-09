package com.ollieread.technomagi.event.handler;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;

import com.ollieread.ennds.ability.AbilityRegistry;
import com.ollieread.ennds.extended.ExtendedPlayerAbilities;
import com.ollieread.ennds.extended.ExtendedPlayerKnowledge;
import com.ollieread.ennds.research.ResearchRegistry;
import com.ollieread.technomagi.block.IDigitalToolable;
import com.ollieread.technomagi.common.init.Blocks;
import com.ollieread.technomagi.common.init.Items;
import com.ollieread.technomagi.common.init.Potions;
import com.ollieread.technomagi.network.PacketHandler;
import com.ollieread.technomagi.network.message.MessageEntityInteractEvent;
import com.ollieread.technomagi.network.message.MessagePlayerInteractEvent;
import com.ollieread.technomagi.tileentity.TileEntityTeleporter;
import com.ollieread.technomagi.util.PlayerHelper;
import com.ollieread.technomagi.util.TeleportHelper;
import com.ollieread.technomagi.util.VersionChecker;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class PlayerEventHandler
{

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            new Thread(new VersionChecker((EntityPlayer) event.entity)).start();

            World world = event.entity.worldObj;

            if (world.provider.dimensionId != 0) {
                if (world.provider.dimensionId == -1) {
                    ResearchRegistry.researchEvent("toNether", event, ExtendedPlayerKnowledge.get((EntityPlayer) event.entity));
                } else if (world.provider.dimensionId == 1) {
                    ResearchRegistry.researchEvent("toEnd", event, ExtendedPlayerKnowledge.get((EntityPlayer) event.entity));
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        ExtendedPlayerKnowledge playerKnowledge = ExtendedPlayerKnowledge.get(event.entityPlayer);

        if (playerKnowledge != null && !playerKnowledge.canSpecialise()) {
            if (event.entityPlayer.getHeldItem() == null) {
                ExtendedPlayerAbilities abilities = playerKnowledge.abilities;

                if (abilities.useAbility(event)) {
                    if (event.entityPlayer.worldObj.isRemote && (event.isCanceled() || event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR))) {
                        PacketHandler.INSTANCE.sendToServer(new MessagePlayerInteractEvent(event));
                    }
                }
            } else if (event.entityPlayer.getHeldItem().isItemEqual(new ItemStack(Items.itemDigitalTool))) {
                if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
                    Block block = event.world.getBlock(event.x, event.y, event.z);

                    if (block instanceof IDigitalToolable) {
                        if (((IDigitalToolable) block).onTooled(event.entityPlayer, event.world, event.x, event.y, event.z, event.entityPlayer.getHeldItem())) {
                            event.setCanceled(true);
                            event.entityPlayer.swingItem();
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event)
    {
        ExtendedPlayerKnowledge playerKnowledge = ExtendedPlayerKnowledge.get(event.entityPlayer);

        if (playerKnowledge != null && !playerKnowledge.canSpecialise()) {
            if (event.entityPlayer.getHeldItem() == null) {
                ExtendedPlayerAbilities abilities = playerKnowledge.abilities;

                if (abilities.useAbility(event)) {
                    if (event.entityPlayer.worldObj.isRemote && event.isCanceled()) {
                        PacketHandler.INSTANCE.sendToServer(new MessageEntityInteractEvent(event));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event)
    {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;

            if (event.source instanceof EntityDamageSource || event.source.equals(DamageSource.generic) || event.source.equals(DamageSource.anvil) || event.source.equals(DamageSource.cactus) || event.source.equals(DamageSource.fall) || event.source.equals(DamageSource.fallingBlock)) {
                if (player.isPotionActive(Potions.potionHardness)) {
                    event.ammount = event.ammount / 2;
                }
            }

            if (event.source.equals(DamageSource.inFire)) {
                AbilityRegistry.passiveAbilityEvent("inFire", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("inFire", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.onFire)) {
                AbilityRegistry.passiveAbilityEvent("onFire", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("onFire", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.lava)) {
                AbilityRegistry.passiveAbilityEvent("inLava", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("inLava", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.inWall)) {
                AbilityRegistry.passiveAbilityEvent("inWall", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("inWall", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.starve)) {
                AbilityRegistry.passiveAbilityEvent("starve", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("starve", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.cactus)) {
                AbilityRegistry.passiveAbilityEvent("cactus", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("cactus", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.outOfWorld)) {
                AbilityRegistry.passiveAbilityEvent("void", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("void", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.magic)) {
                AbilityRegistry.passiveAbilityEvent("magic", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("magic", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.wither)) {
                AbilityRegistry.passiveAbilityEvent("wither", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("wither", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.anvil)) {
                AbilityRegistry.passiveAbilityEvent("anvil", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("anvil", event, ExtendedPlayerKnowledge.get(player));
            } else if (event.source.equals(DamageSource.fallingBlock)) {
                AbilityRegistry.passiveAbilityEvent("fallingBlock", event, ExtendedPlayerKnowledge.get(player));
                ResearchRegistry.researchEvent("fallingBlock", event, ExtendedPlayerKnowledge.get(player));
            }
        }
    }

    @SubscribeEvent
    public void onFallEvent(LivingFallEvent event)
    {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            AbilityRegistry.passiveAbilityEvent("fall", event, ExtendedPlayerKnowledge.get((EntityPlayer) event.entity));
            ResearchRegistry.researchEvent("fall", event, ExtendedPlayerKnowledge.get((EntityPlayer) event.entity));
        }
    }

    @SubscribeEvent
    public void onStartTrackingEvent(StartTracking event)
    {
        if (!event.entityPlayer.worldObj.isRemote && event.target instanceof EntityPlayer) {
            ExtendedPlayerKnowledge charon = ExtendedPlayerKnowledge.get((EntityPlayer) event.target);

            charon.syncTo(event.entityPlayer);
        }
    }

    @SubscribeEvent
    public void onLivingJumpEvent(LivingJumpEvent event)
    {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (PlayerHelper.isStoodOnMeta(player, Blocks.blockTeleporter, 0)) {
                TileEntityTeleporter teleporter = (TileEntityTeleporter) PlayerHelper.getTileEntityStoodOn(player);

                if (teleporter != null && teleporter.canUse()) {
                    TileEntityTeleporter destination = TeleportHelper.findTeleporterAbove(teleporter);

                    if (destination != null) {
                        TeleportHelper.teleportPlayerToTeleporter(player, teleporter, destination);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerCrafting(ItemCraftedEvent event)
    {
        if (!event.player.worldObj.isRemote) {
            ResearchRegistry.researchCrafting(event, ExtendedPlayerKnowledge.get(event.player));
        }
    }

    @SubscribeEvent
    public void onPlayerHarvest(HarvestDropsEvent event)
    {
        if (!event.world.isRemote && event.harvester != null) {
            ResearchRegistry.researchMining(event, ExtendedPlayerKnowledge.get(event.harvester));
        }
    }

    @SubscribeEvent
    public void onEnderTeleport(EnderTeleportEvent event)
    {
        if (!event.entityLiving.worldObj.isRemote) {
            if (event.entityLiving instanceof EntityPlayer) {
                ResearchRegistry.researchEvent("enderTeleport", event, ExtendedPlayerKnowledge.get((EntityPlayer) event.entityLiving));
            }
        }

        World world = event.entityLiving.worldObj;

        int startX = (int) (event.targetX - 7);
        int endX = (int) (event.targetX + 7);
        int startY = (int) (event.targetY - 7);
        int endY = (int) (event.targetY + 7);
        int startZ = (int) (event.targetZ - 7);
        int endZ = (int) (event.targetZ + 7);

        for (int i = startX; i <= endX; i++) {
            for (int j = startZ; j <= endZ; j++) {
                for (int k = startY; k <= endY; k++) {
                    if (world.getBlock(i, k, j).equals(Blocks.blockTeleporter) && world.getBlockMetadata(i, k, j) == 2) {
                        event.targetX = i;
                        event.targetY = k + 1;
                        event.targetZ = j;

                        return;
                    } else if (world.getBlock(i, k, j).equals(Blocks.blockTeleporter) && world.getBlockMetadata(i, k, j) == 3) {
                        event.setCanceled(true);

                        return;
                    }
                }
            }
        }

        int startX1 = (int) (event.entityLiving.posX - 7);
        int endX1 = (int) (event.entityLiving.posX + 7);
        int startY1 = (int) (event.entityLiving.posY - 7);
        int endY1 = (int) (event.entityLiving.posY + 7);
        int startZ1 = (int) (event.entityLiving.posZ - 7);
        int endZ1 = (int) (event.entityLiving.posZ + 7);

        for (int i = startX1; i <= endX1; i++) {
            for (int j = startZ1; j <= endZ1; j++) {
                for (int k = startY1; k <= endY1; k++) {
                    if (world.getBlock(i, k, j).equals(Blocks.blockTeleporter) && world.getBlockMetadata(i, k, j) == 3) {
                        event.setCanceled(true);

                        return;
                    }
                }
            }
        }
    }

}
