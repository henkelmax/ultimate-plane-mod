package de.maxhenkel.plane.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ContainerPlane extends ContainerBase {

    protected EntityPlaneSoundBase plane;

    public ContainerPlane(int id, EntityPlaneSoundBase plane, Inventory playerInv) {
        super(Main.PLANE_CONTAINER_TYPE.get(), id, playerInv, plane.getInventory());
        this.plane = plane;

        int numRows = plane.getInventory().getContainerSize() / 9;

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
