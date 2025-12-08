package de.maxhenkel.plane.gui;

import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.PlaneMod;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class PlaneScreen extends ScreenBase<ContainerPlane> {

    private static final Identifier CAR_GUI_TEXTURE = Identifier.fromNamespaceAndPath(PlaneMod.MODID, "textures/gui/gui_plane.png");

    private static final Component TEXT_FUEL = Component.translatable("gui.plane.fuel");
    private static final Component TEXT_DAMAGE = Component.translatable("gui.plane.damage");
    private static final Component TEXT_ENGINE = Component.translatable("gui.plane.throttle");

    private Inventory playerInv;
    private EntityPlaneSoundBase plane;

    public PlaneScreen(ContainerPlane containerCar, Inventory playerInv, Component title) {
        super(CAR_GUI_TEXTURE, containerCar, playerInv, title);
        this.playerInv = playerInv;
        this.plane = containerCar.getPlane();

        imageWidth = 176;
        imageHeight = 222;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        guiGraphics.drawString(font, plane.getName().getVisualOrderText(), 7, 61, FONT_COLOR, false);
        guiGraphics.drawString(font, playerInv.getDisplayName().getVisualOrderText(), 8, this.imageHeight - 96 + 2, FONT_COLOR, false);

        guiGraphics.drawString(font, TEXT_FUEL.getVisualOrderText(), 7, 9, FONT_COLOR, false);
        guiGraphics.drawString(font, TEXT_DAMAGE.getVisualOrderText(), 95, 9, FONT_COLOR, false);
        guiGraphics.drawString(font, TEXT_ENGINE.getVisualOrderText(), 7, 35, FONT_COLOR, false);

        if (mouseX >= leftPos + 8 && mouseX < leftPos + 80 && mouseY >= topPos + 20 && mouseY < topPos + 30) {
            List<FormattedCharSequence> list = new ArrayList<>();
            list.add(Component.translatable("tooltip.plane.fuel", String.valueOf(plane.getFuel())).getVisualOrderText());
            guiGraphics.setTooltipForNextFrame(font, list, mouseX, mouseY);
        }

        if (mouseX >= leftPos + 96 && mouseX < leftPos + 168 && mouseY >= topPos + 20 && mouseY < topPos + 30) {
            List<FormattedCharSequence> list = new ArrayList<>();
            list.add(Component.translatable("tooltip.plane.damage", String.valueOf(MathUtils.round(plane.getPlaneDamage(), 2))).getVisualOrderText());
            guiGraphics.setTooltipForNextFrame(font, list, mouseX, mouseY);
        }

        if (mouseX >= leftPos + 8 && mouseX < leftPos + 80 && mouseY >= topPos + 46 && mouseY < topPos + 56) {
            List<FormattedCharSequence> list = new ArrayList<>();
            list.add(Component.translatable("tooltip.plane.throttle", String.valueOf(Math.round(plane.getEngineSpeed() * 100F))).getVisualOrderText());
            guiGraphics.setTooltipForNextFrame(font, list, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

        drawFuel(guiGraphics, (float) plane.getFuel() / (float) plane.getFuelCapacity());
        drawDamage(guiGraphics, (100F - Math.min(plane.getPlaneDamage(), 100F)) / 100F);
        drawThrottle(guiGraphics, plane.getEngineSpeed());
    }

    public void drawFuel(GuiGraphics guiGraphics, float percent) {
        int scaled = (int) (72F * percent);
        int i = leftPos;
        int j = topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, i + 8, j + 20, 176, 0, scaled, 10, 256, 256);
    }

    public void drawThrottle(GuiGraphics guiGraphics, float percent) {
        int scaled = (int) (72F * percent);
        int i = leftPos;
        int j = topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, i + 8, j + 46, 176, 10, scaled, 10, 256, 256);
    }

    public void drawDamage(GuiGraphics guiGraphics, float percent) {
        int scaled = (int) (72F * percent);
        int i = leftPos;
        int j = topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, i + 96, j + 20, 176, 20, scaled, 10, 256, 256);
    }

}
