import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICrafting;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import java.util.List;

public class SeedLibraryContainer extends Container
{
    public SeedLibraryTileEntity seedlibrary;

    public SeedLibraryContainer(IInventory iinventory, SeedLibraryTileEntity seedmanager)
    {
        seedlibrary = seedmanager;

        if (seedlibrary.listeners != null) {
            crafters = seedlibrary.listeners;
        } else {
            seedlibrary.listeners = crafters;
        }

        for (int i = 0; i < 9; i++)
        {
            addSlot(new Slot(seedlibrary, i, 8 + i * 18, 108));
        }

        addSlot(new Slot(seedlibrary, -1, 38, 16));

        int i = 2*18;

        for (int k = 0; k < 3; k++)
        {
            for (int j1 = 0; j1 < 9; j1++)
            {
                addSlot(new Slot(iinventory, j1 + k * 9 + 9, 8 + j1 * 18, 104 + k * 18 + i));
            }
        }

        for (int l = 0; l < 9; l++)
        {
            addSlot(new Slot(iinventory, l, 8 + l * 18, 162 + i));
        }
    }

    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return seedlibrary.isUseableByPlayer(entityplayer);
    }

    public ItemStack slotClick(int i, int j, boolean flag, EntityPlayer entityplayer) {
        if (i == 9) {
            // Clicked the "take a seed's type" slot.
            ItemStack seed = entityplayer.inventory.getItemStack();
            seedlibrary.getGUIFilter().setCropFromSeed(seed);
            return null;
        }
        return super.slotClick(i, j, flag, entityplayer);
    }

    public ItemStack transferStackInSlot(int i)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i < 9)
            {
                if (!mergeItemStack(itemstack1, 9, 45, true))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(itemstack1, 0, 9, false))
            {
                return null;
            }
            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize != itemstack.stackSize)
            {
                slot.onPickupFromSlot(itemstack1);
            }
            else
            {
                return null;
            }
        }
        return itemstack;
    }

    public void onCraftGuiOpened(ICrafting crafter) {
        super.onCraftGuiOpened(crafter);

        seedlibrary.updateSeedCount();
        seedlibrary.updateGUIFilter();
    }

    public void onCraftGuiClosed(EntityPlayer player) {
        super.onCraftGuiClosed(player);
        crafters.remove(player);
    }
}