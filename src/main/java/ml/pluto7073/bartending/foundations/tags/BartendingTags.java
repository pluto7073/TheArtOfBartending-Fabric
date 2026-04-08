package ml.pluto7073.bartending.foundations.tags;

import ml.pluto7073.bartending.TheArtOfBartending;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BartendingTags {

    public static final TagKey<Block> EXTRA_BOILER_HEATERS = block("extra_boiler_heaters");
    public static final TagKey<Block> SUPERHEATING_BLOCKS = block("superheating_blocks");
    public static final TagKey<Block> C_FARMLAND = block("c:farmland");

    public static final TagKey<Item> EMPTY_GLASS_BOTTLES = item("empty_glass_bottles");
    public static final TagKey<Item> BOILABLES = item("boilables");
    public static final TagKey<Item> BOTANICAL_ELEMENTS = item("botanical_elements");
    public static final TagKey<Item> C_FRUITS = item("c:fruits");
    public static final TagKey<Item> WINE_FRUITS = item("bartending:wine_fruits");
    public static final TagKey<Item> NETHER_WINE_FRUITS = item("bartending:nether_wine_fruits");
    public static final TagKey<Item> WARPED_BOTANICALS = item("warped_botanicals");
    public static final TagKey<Item> CRIMSON_BOTANICALS = item("crimson_botanicals");
    public static final TagKey<Item> GLASSES = item("glasses");
    public static final TagKey<Item> UNSKINNED_GRAPES = item("unskinned_grapes");

    private static TagKey<Item> item(String name) {
        if (name.contains(":")) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(name));
        }
        return TagKey.create(Registries.ITEM, TheArtOfBartending.asId(name));
    }

    private static TagKey<Block> block(String name) {
        if (name.contains(":")) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(name));
        }
        return TagKey.create(Registries.BLOCK, TheArtOfBartending.asId(name));
    }

}
