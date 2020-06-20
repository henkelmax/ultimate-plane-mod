package de.maxhenkel.plane.gui;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.MathTools;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiPlane extends GuiBase<ContainerPlane> {

    private static final ResourceLocation CAR_GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/gui_plane.png");

    private static final ITextComponent TEXT_FUEL = new TranslationTextComponent("gui.plane.fuel");
    private static final ITextComponent TEXT_DAMAGE = new TranslationTextComponent("gui.plane.damage");
    private static final ITextComponent TEXT_ENGINE = new TranslationTextComponent("gui.plane.throttle");

    private PlayerInventory playerInv;
    private EntityPlane plane;

    public GuiPlane(ContainerPlane containerCar, PlayerInventory playerInv, ITextComponent title) {
        super(CAR_GUI_TEXTURE, containerCar, playerInv, title);
        this.playerInv = playerInv;
        this.plane = containerCar.getPlane();

        xSize = 176;
        ySize = 222;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        font.drawString(plane.getName().getFormattedText(), 7, 61, FONT_COLOR);
        font.drawString(playerInv.getDisplayName().getFormattedText(), 8, this.ySize - 96 + 2, FONT_COLOR);

        font.drawString(TEXT_FUEL.getFormattedText(), 7, 9, FONT_COLOR);
        font.drawString(TEXT_DAMAGE.getFormattedText(), 95, 9, FONT_COLOR);
        font.drawString(TEXT_ENGINE.getFormattedText(), 7, 35, FONT_COLOR);

        if (mouseX >= guiLeft + 8 && mouseX < guiLeft + 80 && mouseY >= guiTop + 20 && mouseY < guiTop + 30) {
            renderTooltip(new TranslationTextComponent("tooltip.plane.fuel", plane.getFuel()).getFormattedText(), mouseX - guiLeft, mouseY - guiTop);
        }

        if (mouseX >= guiLeft + 96 && mouseX < guiLeft + 168 && mouseY >= guiTop + 20 && mouseY < guiTop + 30) {
            renderTooltip(new TranslationTextComponent("tooltip.plane.damage", MathTools.round(plane.getPlaneDamage(), 2)).getFormattedText(), mouseX - guiLeft, mouseY - guiTop);
        }

        if (mouseX >= guiLeft + 8 && mouseX < guiLeft + 80 && mouseY >= guiTop + 46 && mouseY < guiTop + 56) {
            renderTooltip(new TranslationTextComponent("tooltip.plane.throttle", Math.round(plane.getEngineSpeed() * 100F)).getFormattedText(), mouseX - guiLeft, mouseY - guiTop);
        }
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        drawFuel((float) plane.getFuel() / (float) EntityPlane.MAX_FUEL);
        drawDamage((100F - Math.min(plane.getPlaneDamage(), 100F)) / 100F);
        drawThrottle(plane.getEngineSpeed());
    }

    public void drawFuel(float percent) {
        int scaled = (int) (72F * percent);
        int i = guiLeft;
        int j = guiTop;
        blit(i + 8, j + 20, 176, 0, scaled, 10);
    }

    public void drawThrottle(float percent) {
        int scaled = (int) (72F * percent);
        int i = guiLeft;
        int j = guiTop;
        blit(i + 8, j + 46, 176, 10, scaled, 10);
    }

    public void drawDamage(float percent) {
        int scaled = (int) (72F * percent);
        int i = guiLeft;
        int j = guiTop;
        blit(i + 96, j + 20, 176, 20, scaled, 10);
    }

}
