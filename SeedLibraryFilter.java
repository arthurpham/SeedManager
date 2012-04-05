import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ic2.api.CropCard;
import net.minecraft.src.ic2.api.Items;
import net.minecraft.src.ic2.common.ItemCropSeed;
import java.util.Collection;
import java.util.Vector;

public class SeedLibraryFilter {
    public boolean allow_unknown_type = true;
    public boolean allow_unknown_ggr = true;
    public int seed_type = -1;
    public int min_growth = 0;
    public int min_gain = 0;
    public int min_resistance = 0;
    public int max_growth = 31;
    public int max_gain = 31;
    public int max_resistance = 31;
    public int min_total = 0;
    public int max_total = 93;
    public SeedLibrarySort sort = SeedLibrarySort.TOTAL_DESC;

    public static final int CACHE_SIZE = 10;
    public Vector<ItemStack> cache = new Vector<ItemStack>(CACHE_SIZE+1);
    public boolean cached_nothing = false;

    public void copyFrom(SeedLibraryFilter source) {
        allow_unknown_type = source.allow_unknown_type;
        allow_unknown_ggr = source.allow_unknown_ggr;
        seed_type = source.seed_type;
        min_growth = source.min_growth;
        min_gain = source.min_gain;
        min_resistance = source.min_resistance;
        max_growth = source.max_growth;
        max_gain = source.max_gain;
        max_resistance = source.max_resistance;
        min_total = source.min_total;
        max_total = source.max_total;
        sort = source.sort;

        settingsChanged();
    }

    public ItemStack getSeed(Collection<ItemStack> seeds) {
        if (cached_nothing) {
            return null;
        }

        if (cache.size() == 0) {
            fillCache(seeds);
        }

        if (cache.size() == 0) {
            cached_nothing = true;
            return null;
        }

        return cache.get(0);
    }

    public int getCount(Collection<ItemStack> seeds) {
        int count = 0;
        for (ItemStack seed : seeds) {
            if (isMatch(seed)) {
                count += seed.stackSize;
            }
        }

        return count;
    }

    public void newSeed(ItemStack seed) {
        if (isMatch(seed)) {
            addToCache(seed);
        }
    }

    public void lostSeed(ItemStack seed) {
        removeFromCache(seed);
    }

    public void settingsChanged() {
        cache.clear();
        cached_nothing = false;
    }

    public boolean isMatch(ItemStack seed) {
        short id = ItemCropSeed.getIdFromStack(seed);
        byte scan = ItemCropSeed.getScannedFromStack(seed);

        if (scan == 0) {
            return allow_unknown_type && allow_unknown_ggr;
        }

        if (seed_type != -1 && seed_type != id) {
            return false;
        }

        if (scan < 4) {
            return allow_unknown_ggr;
        }

        byte growth = ItemCropSeed.getGrowthFromStack(seed);
        byte gain = ItemCropSeed.getGainFromStack(seed);
        byte resistance = ItemCropSeed.getResistanceFromStack(seed);

        if (growth < min_growth || growth > max_growth) {
            return false;
        }

        if (gain < min_gain || gain > max_gain) {
            return false;
        }

        if (resistance < min_resistance || resistance > max_resistance) {
            return false;
        }

        int total = growth + gain + resistance;
        if (total < min_total || total > max_total) {
            return false;
        }

        return true;
    }

    public String getCropName() {
        int crop_id = seed_type;
        if (crop_id == -1) {
            return "Any";
        } else if (!CropCard.idExists(crop_id)) {
            return "(Invalid)";
        } else {
            return CropCard.getCrop(crop_id).name();
        }
    }

    public void setCropFromSeed(ItemStack seed) {
        if (seed == null) {
            seed_type = -1;
        } else if (seed.itemID != Items.getItem("cropSeed").itemID) {
            seed_type = -1;
        } else if (ItemCropSeed.getScannedFromStack(seed) == 0) {
            seed_type = -1;
        } else {
            seed_type = ItemCropSeed.getIdFromStack(seed);
        }
        settingsChanged();
    }

    protected void fillCache(Collection<ItemStack> seeds) {
        cache.clear();

        for (ItemStack seed : seeds) {
            newSeed(seed);
        }
    }

    protected void addToCache(ItemStack seed) {
        cached_nothing = false;

        int pos = 0;
        for (int i=cache.size()-1; i>=0; i--) {
            int cmp = sort.compare(seed, cache.get(i));
            if (cmp <= 0) {
                pos = i + 1;
                break;
            }
        }

        if (pos >= CACHE_SIZE) {
            return;
        }

        cache.add(pos, seed);

        while (cache.size() > CACHE_SIZE) {
            cache.remove(cache.size() - 1);
        }

        return;
    }

    protected void removeFromCache(ItemStack seed) {
        cache.remove(seed);
    }


    // Save/load
    public void loadFromNBT(NBTTagCompound input) {
        allow_unknown_type = input.getBoolean("allow_unknown_type");
        allow_unknown_ggr = input.getBoolean("allow_unknown_ggr");
        seed_type = input.getInteger("seed_type");
        min_growth = input.getInteger("min_growth");
        min_gain = input.getInteger("min_gain");
        min_resistance = input.getInteger("min_resistance");
        max_growth = input.getInteger("max_growth");
        max_gain = input.getInteger("max_gain");
        max_resistance = input.getInteger("max_resistance");
        min_total = input.getInteger("min_total");
        max_total = input.getInteger("max_total");

        int sort_type = input.getInteger("sort_type");
        boolean sort_desc = input.getBoolean("sort_desc");
        sort = SeedLibrarySort.getSort(sort_type, sort_desc);

        settingsChanged();
    }

    public void writeToNBT(NBTTagCompound output) {
        output.setBoolean("allow_unknown_type", allow_unknown_type);
        output.setBoolean("allow_unknown_ggr", allow_unknown_ggr);
        output.setInteger("seed_type", seed_type);
        output.setInteger("min_growth", min_growth);
        output.setInteger("min_gain", min_gain);
        output.setInteger("min_resistance", min_resistance);
        output.setInteger("max_growth", max_growth);
        output.setInteger("max_gain", max_gain);
        output.setInteger("max_resistance", max_resistance);
        output.setInteger("min_total", min_total);
        output.setInteger("max_total", max_total);

        output.setInteger("sort_type", sort.sort_type);
        output.setBoolean("sort_desc", sort.descending);
    }

}