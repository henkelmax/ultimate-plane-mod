package de.maxhenkel.plane.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
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

        xSize = 176;
        ySize = 222;
    }

    @Override
    protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.func_230451_b_(matrixStack, mouseX, mouseY);

        field_230712_o_.func_238422_b_(matrixStack, plane.getName(), 7, 61, FONT_COLOR);
        field_230712_o_.func_238422_b_(matrixStack, playerInv.getDisplayName(), 8, this.ySize - 96 + 2, FONT_COLOR);

        field_230712_o_.func_238422_b_(matrixStack, TEXT_FUEL, 7, 9, FONT_COLOR);
        field_230712_o_.func_238422_b_(matrixStack, TEXT_DAMAGE, 95, 9, FONT_COLOR);
        field_230712_o_.func_238422_b_(matrixStack, TEXT_ENGINE, 7, 35, FONT_COLOR);

        if (mouseX >= guiLeft + 8 && mouseX < guiLeft + 80 && mouseY >= guiTop + 20 && mouseY < guiTop + 30) {
            List<IFormattableTextComponent> list = new ArrayList<>();
            list.add(new TranslationTextComponent("tooltip.plane.fuel", plane.getFuel()));
            func_238654_b_(matrixStack, list, mouseX - guiLeft, mouseY - guiTop);
        }

        if (mouseX >= guiLeft + 96 && mouseX < guiLeft + 168 && mouseY >= guiTop + 20 && mouseY < guiTop + 30) {
            List<IFormattableTextComponent> list = new ArrayList<>();
            list.add(new TranslationTextComponent("tooltip.plane.damage", MathUtils.round(plane.getPlaneDamage(), 2)));
            func_238654_b_(matrixStack, list, mouseX - guiLeft, mouseY - guiTop);
        }

        if (mouseX >= guiLeft + 8 && mouseX < guiLeft + 80 && mouseY >= guiTop + 46 && mouseY < guiTop + 56) {
            List<IFormattableTextComponent> list = new ArrayList<>();
            list.add(new TranslationTextComponent("tooltip.plane.throttle", Math.round(plane.getEngineSpeed() * 100F)));
            func_238654_b_(matrixStack, list, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.func_230450_a_(matrixStack, partialTicks, mouseX, mouseY);

        drawFuel(matrixStack, (float) plane.getFuel() / (float) plane.getMaxFuel());
        drawDamage(matrixStack, (100F - Math.min(plane.getPlaneDamage(), 100F)) / 100F);
        drawThrottle(matrixStack, plane.getEngineSpeed());
    }

    public void drawFuel(MatrixStack matrixStack, float percent) {
        int scaled = (int) (72F * percent);
        int i = guiLeft;
        int j = guiTop;
        func_238474_b_(matrixStack, i + 8, j + 20, 176, 0, scaled, 10);
    }

    public void drawThrottle(MatrixStack matrixStack, float percent) {
        int scaled = (int) (72F * percent);
        int i = guiLeft;
        int j = guiTop;
        func_238474_b_(matrixStack, i + 8, j + 46, 176, 10, scaled, 10);
    }

    public void drawDamage(MatrixStack matrixStack, float percent) {
        int scaled = (int) (72F * percent);
        int i = guiLeft;
        int j = guiTop;
        func_238474_b_(matrixStack, i + 96, j + 20, 176, 20, scaled, 10);
    }

}
