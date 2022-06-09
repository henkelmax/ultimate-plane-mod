package de.maxhenkel.plane.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class PlaneScreen extends ScreenBase<ContainerPlane> {

    private static final ResourceLocation CAR_GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/gui_plane.png");

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
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);

        font.draw(matrixStack, plane.getName().getVisualOrderText(), 7, 61, FONT_COLOR);
        font.draw(matrixStack, playerInv.getDisplayName().getVisualOrderText(), 8, this.imageHeight - 96 + 2, FONT_COLOR);

        font.draw(matrixStack, TEXT_FUEL.getVisualOrderText(), 7, 9, FONT_COLOR);
        font.draw(matrixStack, TEXT_DAMAGE.getVisualOrderText(), 95, 9, FONT_COLOR);
        font.draw(matrixStack, TEXT_ENGINE.getVisualOrderText(), 7, 35, FONT_COLOR);

        if (mouseX >= leftPos + 8 && mouseX < leftPos + 80 && mouseY >= topPos + 20 && mouseY < topPos + 30) {
            List<FormattedCharSequence> list = new ArrayList<>();
            list.add(Component.translatable("tooltip.plane.fuel", String.valueOf(plane.getFuel())).getVisualOrderText());
            renderTooltip(matrixStack, list, mouseX - leftPos, mouseY - topPos);
        }

        if (mouseX >= leftPos + 96 && mouseX < leftPos + 168 && mouseY >= topPos + 20 && mouseY < topPos + 30) {
            List<FormattedCharSequence> list = new ArrayList<>();
            list.add(Component.translatable("tooltip.plane.damage", String.valueOf(MathUtils.round(plane.getPlaneDamage(), 2))).getVisualOrderText());
            renderTooltip(matrixStack, list, mouseX - leftPos, mouseY - topPos);
        }

        if (mouseX >= leftPos + 8 && mouseX < leftPos + 80 && mouseY >= topPos + 46 && mouseY < topPos + 56) {
            List<FormattedCharSequence> list = new ArrayList<>();
            list.add(Component.translatable("tooltip.plane.throttle", String.valueOf(Math.round(plane.getEngineSpeed() * 100F))).getVisualOrderText());
            renderTooltip(matrixStack, list, mouseX - leftPos, mouseY - topPos);
        }
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);

        drawFuel(matrixStack, (float) plane.getFuel() / (float) plane.getMaxFuel());
        drawDamage(matrixStack, (100F - Math.min(plane.getPlaneDamage(), 100F)) / 100F);
        drawThrottle(matrixStack, plane.getEngineSpeed());
    }

    public void drawFuel(PoseStack matrixStack, float percent) {
        int scaled = (int) (72F * percent);
        int i = leftPos;
        int j = topPos;
        blit(matrixStack, i + 8, j + 20, 176, 0, scaled, 10);
    }

    public void drawThrottle(PoseStack matrixStack, float percent) {
        int scaled = (int) (72F * percent);
        int i = leftPos;
        int j = topPos;
        blit(matrixStack, i + 8, j + 46, 176, 10, scaled, 10);
    }

    public void drawDamage(PoseStack matrixStack, float percent) {
        int scaled = (int) (72F * percent);
        int i = leftPos;
        int j = topPos;
        blit(matrixStack, i + 96, j + 20, 176, 20, scaled, 10);
    }

}
