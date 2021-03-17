package de.maxhenkel.plane.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class PlaneScreen extends ScreenBase<ContainerPlane> {

    private static final ResourceLocation CAR_GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/gui_plane.png");

    private static final ITextComponent TEXT_FUEL = new TranslationTextComponent("gui.plane.fuel");
    private static final ITextComponent TEXT_DAMAGE = new TranslationTextComponent("gui.plane.damage");
    private static final ITextComponent TEXT_ENGINE = new TranslationTextComponent("gui.plane.throttle");

    private PlayerInventory playerInv;
    private EntityPlaneSoundBase plane;

    public PlaneScreen(ContainerPlane containerCar, PlayerInventory playerInv, ITextComponent title) {
        super(CAR_GUI_TEXTURE, containerCar, playerInv, title);
        this.playerInv = playerInv;
        this.plane = containerCar.getPlane();

        imageWidth = 176;
        imageHeight = 222;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);

        font.draw(matrixStack, plane.getName().getVisualOrderText(), 7, 61, FONT_COLOR);
        font.draw(matrixStack, playerInv.getDisplayName().getVisualOrderText(), 8, this.imageHeight - 96 + 2, FONT_COLOR);

        font.draw(matrixStack, TEXT_FUEL.getVisualOrderText(), 7, 9, FONT_COLOR);
        font.draw(matrixStack, TEXT_DAMAGE.getVisualOrderText(), 95, 9, FONT_COLOR);
        font.draw(matrixStack, TEXT_ENGINE.getVisualOrderText(), 7, 35, FONT_COLOR);

        if (mouseX >= leftPos + 8 && mouseX < leftPos + 80 && mouseY >= topPos + 20 && mouseY < topPos + 30) {
            List<IReorderingProcessor> list = new ArrayList<>();
            list.add(new TranslationTextComponent("tooltip.plane.fuel", String.valueOf(plane.getFuel())).getVisualOrderText());
            renderTooltip(matrixStack, list, mouseX - leftPos, mouseY - topPos);
        }

        if (mouseX >= leftPos + 96 && mouseX < leftPos + 168 && mouseY >= topPos + 20 && mouseY < topPos + 30) {
            List<IReorderingProcessor> list = new ArrayList<>();
            list.add(new TranslationTextComponent("tooltip.plane.damage", String.valueOf(MathUtils.round(plane.getPlaneDamage(), 2))).getVisualOrderText());
            renderTooltip(matrixStack, list, mouseX - leftPos, mouseY - topPos);
        }

        if (mouseX >= leftPos + 8 && mouseX < leftPos + 80 && mouseY >= topPos + 46 && mouseY < topPos + 56) {
            List<IReorderingProcessor> list = new ArrayList<>();
            list.add(new TranslationTextComponent("tooltip.plane.throttle", String.valueOf(Math.round(plane.getEngineSpeed() * 100F))).getVisualOrderText());
            renderTooltip(matrixStack, list, mouseX - leftPos, mouseY - topPos);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);

        drawFuel(matrixStack, (float) plane.getFuel() / (float) plane.getMaxFuel());
        drawDamage(matrixStack, (100F - Math.min(plane.getPlaneDamage(), 100F)) / 100F);
        drawThrottle(matrixStack, plane.getEngineSpeed());
    }

    public void drawFuel(MatrixStack matrixStack, float percent) {
        int scaled = (int) (72F * percent);
        int i = leftPos;
        int j = topPos;
        blit(matrixStack, i + 8, j + 20, 176, 0, scaled, 10);
    }

    public void drawThrottle(MatrixStack matrixStack, float percent) {
        int scaled = (int) (72F * percent);
        int i = leftPos;
        int j = topPos;
        blit(matrixStack, i + 8, j + 46, 176, 10, scaled, 10);
    }

    public void drawDamage(MatrixStack matrixStack, float percent) {
        int scaled = (int) (72F * percent);
        int i = leftPos;
        int j = topPos;
        blit(matrixStack, i + 96, j + 20, 176, 20, scaled, 10);
    }

}
