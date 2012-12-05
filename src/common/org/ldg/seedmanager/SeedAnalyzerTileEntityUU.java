package org.ldg.seedmanager;

import cpw.mods.fml.common.Side;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet54PlayNoteBlock;
import net.minecraft.src.TileEntity;
import ic2.common.TileEntityElectricMachine;
import ic2.common.ItemCropSeed;
import ic2.api.IWrenchable;
import ic2.api.Items;
import ro.narc.liquiduu.IAcceleratorFriend;
import ro.narc.liquiduu.InstantRecipe;

public class SeedAnalyzerTileEntityUU extends SeedAnalyzerTileEntity implements IAcceleratorFriend {
    //public interface IAcceleratorFriend {
    // Is this machine ready to use this input?
    @Override
    public boolean instantReady(ItemStack input) {
        return true;
    }

    // What's the recipe for this input?
    @Override
    public InstantRecipe getInstantRecipe(ItemStack input) {
        if (!isSeed(input)) {
            return null;
        }

        byte scan = ItemCropSeed.getScannedFromStack(input);
        if (scan >= 4) {
            return new InstantRecipe(input, input, 0);
        }

        short id = ItemCropSeed.getIdFromStack(input);
        byte growth = ItemCropSeed.getGrowthFromStack(input);
        byte gain = ItemCropSeed.getGainFromStack(input);
        byte resistance = ItemCropSeed.getResistanceFromStack(input);
        ItemStack output = ItemCropSeed.generateItemStackFromValues(id, growth, gain, resistance, (byte)4);

        int cost = 0;
        for (int level=scan; level<4; level++) {
            cost += cost_to_upgrade[level];
        }

        return new InstantRecipe(input, output, cost / 20);
    }

    // How many of these batches can the machine handle right now?
    // NOTE: Only called in advanced mode (recipe.machine == null)
    @Override
    public int instantCapacity(InstantRecipe recipe, int batches) {
        return batches;
    }

    // We have made some batches of the recipe.
    // NOTE: Only called in advanced mode (recipe.machine == null)
    @Override
    public void instantProcess(InstantRecipe recipe, int batches) { }
    //}
}
