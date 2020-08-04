package de.maxhenkel.plane.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;

public class ContainerPlane extends ContainerBase {

    protected EntityPlaneSoundBase plane;

    public ContainerPlane(int id, EntityPlaneSoundBase plane, PlayerInventory playerInv) {
        super(Main.PLANE_CONTAINER_TYPE, id, plane.getInventory(), playerInv);
        this.plane = plane;

        int numRows = plane.getInventory().getSizeInventory() / 9;

        for (int j = 0; j < numRows; j++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(plane.getInventory(), k + j * 9, 8 + k * 18, 72 + j * 18));
            }
        }

        addPlayerInventorySlots();
    }

    public EntityPlaneSoundBase getPlane() {
        return plane;
    }

    @Override
    public int getInvOffset() {
        return 56;
    }

}
