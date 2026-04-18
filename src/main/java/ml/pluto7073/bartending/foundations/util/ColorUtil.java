package ml.pluto7073.bartending.foundations.util;

import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;

public class ColorUtil {

    public static final HashMap<String, Integer> COLORS_REGISTRY = new HashMap<>();

    public static int get(String s) {

        if (BuiltInRegistries.ITEM.containsKey(new ResourceLocation(s))) {
            ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(s)));
            if (stack.is(Items.CRIMSON_FUNGUS) || stack.is(BartendingTags.CRIMSON_BOTANICALS))
                return COLORS_REGISTRY.get("bartending:crimson");
            if (stack.is(BartendingTags.BOTANICAL_ELEMENTS))
                return COLORS_REGISTRY.get("bartending:botanicals");
            if (stack.is(Items.WARPED_FUNGUS) || stack.is(BartendingTags.WARPED_BOTANICALS))
                return COLORS_REGISTRY.get("bartending:warped");
        }
        if (!COLORS_REGISTRY.containsKey(s)) return -1;
        return COLORS_REGISTRY.get(s);
    }

    static {
        COLORS_REGISTRY.put("minecraft:wheat", 9402184);
        COLORS_REGISTRY.put("minecraft:sweet_berries", 9321518);
        COLORS_REGISTRY.put("minecraft:glow_berries", 12223780);
        COLORS_REGISTRY.put("minecraft:potato", 0xf2e6ab);
        COLORS_REGISTRY.put("minecraft:apple", 0xbc8a49);
        COLORS_REGISTRY.put("minecraft:sugar_cane", 0x825424);
        COLORS_REGISTRY.put("minecraft:cactus", 0x77ad8e);
        COLORS_REGISTRY.put("minecraft:grass", 0xcdeaaf);
        COLORS_REGISTRY.put("minecraft:tall_grass", 0xcdeaaf);
        COLORS_REGISTRY.put("minecraft:fern", 0xcdeaaf);
        COLORS_REGISTRY.put("minecraft:large_fern", 0xcdeaaf);
        COLORS_REGISTRY.put("minecraft:honey_bottle", 0xdfe5a0);
        COLORS_REGISTRY.put("bartending:botanicals", 0x7f5c36);
        COLORS_REGISTRY.put("bartending:warped", 0x54bfbf);
        COLORS_REGISTRY.put("bartending:crimson", 0xbf5454);
        COLORS_REGISTRY.put("fruitfulfun:orange", 0xf2b91d);
        COLORS_REGISTRY.put("bartending:red_grape", 9321518);
        COLORS_REGISTRY.put("bartending:skinned_grape", 12223780);

        COLORS_REGISTRY.put("bartending:oak_fermenting_barrel", 0xc29d62);
        COLORS_REGISTRY.put("bartending:cherry_fermenting_barrel", 0xefd0ef);
        COLORS_REGISTRY.put("bartending:bamboo_fermenting_barrel", 0xe3cc6a);
    }

}
