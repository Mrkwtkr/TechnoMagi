package com.ollieread.technomagi.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import com.ollieread.ennds.ability.AbilityRegistry;
import com.ollieread.ennds.ability.IAbilityActive;
import com.ollieread.ennds.extended.ExtendedPlayerKnowledge;
import com.ollieread.technomagi.common.Reference;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiTMOverlay extends Gui
{

    private Minecraft mc;
    private int width;
    private int height;
    private int xSize = 22;
    private int ySize = 102;
    private int xOffset = 0;
    private int yOffset = 0;
    private int aOffset = 0;
    private static final ResourceLocation texture = new ResourceLocation(Reference.MODID.toLowerCase(), "textures/gui/overlay.png");
    public static int highlightTicks;
    public static boolean shouldDisplay = false;
    private FontRenderer fontrenderer;

    public GuiTMOverlay(Minecraft mc)
    {
        super();
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderExperienceBar(RenderGameOverlayEvent event)
    {
        if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
            return;
        }

        ExtendedPlayerKnowledge charon = ExtendedPlayerKnowledge.get(this.mc.thePlayer);

        if (charon == null || charon.canSpecialise()) {
            return;
        }

        this.fontrenderer = Minecraft.getMinecraft().fontRenderer;

        ScaledResolution scaled = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.width = scaled.getScaledWidth();
        this.height = scaled.getScaledHeight();
        this.xOffset = 2;
        this.yOffset = (this.height - this.ySize) / 2;

        int nanites = charon.nanites.getNanites();
        int maxNanites = charon.nanites.getMaxNanites();
        int researchNanites = charon.nanites.getData();
        float nanite = 102 / 100;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        this.mc.getTextureManager().bindTexture(texture);
        this.drawTexturedModalRect(this.xOffset, this.yOffset, 0, 0, this.xSize, this.ySize);

        this.drawTexturedModalRect(this.xOffset + 22, this.yOffset, 22, 0, 5, this.ySize);

        this.drawTexturedModalRect(this.xOffset + 28, this.yOffset, 22, 0, 5, this.ySize);

        float ro = ((100 - researchNanites) * nanite);
        float yo = ((100 - nanites) * nanite);

        if (maxNanites > 0) {
            this.drawTexturedModalRect(this.xOffset + 22, (int) (this.yOffset + yo), 27, (int) yo, 5, (int) (this.ySize - yo));
        }

        if (researchNanites > 0) {
            this.drawTexturedModalRect(this.xOffset + 28, (int) (this.yOffset + ro), 32, (int) ro, 5, (int) (this.ySize - ro));
        }

        List<String> abilities = charon.abilities.getActiveAbilities();

        if (abilities.size() > 0) {
            int currentAbility = charon.abilities.getCurrentAbility();
            int end = (aOffset + 4) >= abilities.size() ? abilities.size() : aOffset + 4;
            int s = -1;

            if (currentAbility >= 0) {
                if (currentAbility < aOffset) {
                    aOffset = currentAbility;
                } else if (currentAbility > end) {
                    aOffset = currentAbility - 4;
                }

                if (end >= abilities.size()) {
                    end = abilities.size();
                }

                s = (currentAbility - aOffset) * 20;
            }

            if (s > -1) {
                this.drawTexturedModalRect(this.xOffset - 1, (this.yOffset - 1) + s, 37, 0, 24, 24);
            }

            if (aOffset > 0) {
                this.drawTexturedModalRect(this.xOffset + 5, this.yOffset - 8, 37, 36, 11, 7);
            }

            if (abilities.size() > (end + 1)) {
                this.drawTexturedModalRect(this.xOffset + 5, this.yOffset + 103, 37, 24, 11, 7);
            }

            for (int i = 0; i < 5; i++) {

                if (i > end || (aOffset + i) >= abilities.size())
                    break;

                IAbilityActive ability = AbilityRegistry.getActiveAbility(abilities.get(aOffset + i));

                this.mc.getTextureManager().bindTexture(ability.getIcon());
                this.func_146110_a(5, yOffset + (3 + (20 * i)), 0, 0, 16, 16, 16, 16);
            }

            if (currentAbility > -1 && shouldDisplay && highlightTicks > 0) {
                String display = AbilityRegistry.getActiveAbility(abilities.get(currentAbility)).getLocalisedName();
                int k1 = (width - fontrenderer.getStringWidth(display)) / 2;
                int l1 = height - 72;

                if (!this.mc.playerController.shouldDrawHUD()) {
                    l1 += 14;
                }

                int i2 = (int) ((float) highlightTicks * 256.0F / 10.0F);

                if (i2 > 255) {
                    i2 = 255;
                }

                if (i2 > 0) {
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    fontrenderer.drawStringWithShadow(display, k1, l1, 10339063 + (i2 << 24));
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                    --highlightTicks;
                }
            } else {
                shouldDisplay = false;
                highlightTicks = 0;
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

}
