package ml.pluto7073.bartending.content.item;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.item.AlcoholicShotItem;
import ml.pluto7073.bartending.foundations.item.AlcoholicDrinkItem;
import ml.pluto7073.bartending.foundations.item.PourableBottleItem;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.HashMap;
import java.util.Objects;

import static ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks.registerFinalDrink;
import static net.minecraft.world.item.Items.*;

public class BartendingItems {

    public static final HashMap<AlcoholicDrink, AlcoholicShotItem> SHOTS = new HashMap<>();
    public static final HashMap<AlcoholicDrink, PourableBottleItem> BOTTLES = new HashMap<>();
    public static final HashMap<AlcoholicDrink, AlcoholicDrinkItem> GLASSES = new HashMap<>();
    public static final HashMap<AlcoholicDrink, AlcoholicDrinkItem> SERVING_BOTTLES = new HashMap<>();

    // Items

    public static final ConcoctionItem CONCOCTION =
            new ConcoctionItem(new Item.Properties().stacksTo(1).craftRemainder(GLASS_BOTTLE));
    public static final Item BEER_BOTTLE = new Item(new Item.Properties());
    public static final Item WINE_BOTTLE = new Item(new Item.Properties());
    public static final Item LIQUOR_BOTTLE = new Item(new Item.Properties());
    public static final Item JUG = new Item(new Item.Properties());
    public static final Item SHOT_GLASS = new Item(new Item.Properties());
    public static final Item COCKTAIL_GLASS = new Item(new Item.Properties());
    public static final Item WINE_GLASS = new Item(new Item.Properties());
    public static final Item TALL_GLASS = new Item(new Item.Properties());
    public static final Item SHORT_GLASS = new Item(new Item.Properties());

    public static final Item BOTTLE_OF_BEER = servingBottle(AlcoholicDrinks.BEER);
    public static final Item GLASS_OF_BEER = glass(AlcoholicDrinks.BEER, TALL_GLASS);
    public static final Item JUG_OF_BEER = bottle(AlcoholicDrinks.BEER);

    public static final Item BOTTLE_OF_WHEAT_BEER = servingBottle(AlcoholicDrinks.WHEAT_BEER);
    public static final Item GLASS_OF_WHEAT_BEER = glass(AlcoholicDrinks.WHEAT_BEER, TALL_GLASS);
    public static final Item JUG_OF_WHEAT_BEER = bottle(AlcoholicDrinks.WHEAT_BEER);

    public static final Item BOTTLE_OF_DARK_BEER = servingBottle(AlcoholicDrinks.DARK_BEER);
    public static final Item GLASS_OF_DARK_BEER = glass(AlcoholicDrinks.DARK_BEER, TALL_GLASS);
    public static final Item JUG_OF_DARK_BEER = bottle(AlcoholicDrinks.DARK_BEER);

    public static final Item GLASS_OF_MEAD = glass(AlcoholicDrinks.MEAD, WINE_GLASS);
    public static final Item MEAD = bottle(AlcoholicDrinks.MEAD);

    public static final Item GLASS_OF_APPLE_MEAD = glass(AlcoholicDrinks.APPLE_MEAD, WINE_GLASS);
    public static final Item APPLE_MEAD = bottle(AlcoholicDrinks.APPLE_MEAD);

    public static final Item GLASS_OF_RED_WINE = glass(AlcoholicDrinks.RED_WINE, WINE_GLASS);
    public static final Item RED_WINE = bottle(AlcoholicDrinks.RED_WINE);

    public static final Item GLASS_OF_WHITE_WINE = glass(AlcoholicDrinks.WHITE_WINE, WINE_GLASS);
    public static final Item WHITE_WINE = bottle(AlcoholicDrinks.WHITE_WINE);

    public static final Item GLASS_OF_CHAMPAGNE = glass(AlcoholicDrinks.CHAMPAGNE, WINE_GLASS);
    public static final Item CHAMPAGNE = bottle(AlcoholicDrinks.CHAMPAGNE);

    public static final Item GLASS_OF_SWEET_BERRY_WINE = glass(AlcoholicDrinks.SWEET_BERRY_WINE, WINE_GLASS);
    public static final Item SWEET_BERRY_WINE = bottle(AlcoholicDrinks.SWEET_BERRY_WINE);

    public static final Item GLASS_OF_GLOW_BERRY_WINE = glass(AlcoholicDrinks.GLOW_BERRY_WINE, WINE_GLASS);
    public static final Item GLOW_BERRY_WINE = bottle(AlcoholicDrinks.GLOW_BERRY_WINE);

    public static final Item GLASS_OF_CRIMSON_WINE = glass(AlcoholicDrinks.CRIMSON_WINE, COCKTAIL_GLASS);
    public static final Item CRIMSON_WINE = bottle(AlcoholicDrinks.CRIMSON_WINE);

    public static final Item GLASS_OF_WARPED_WINE = glass(AlcoholicDrinks.WARPED_WINE, COCKTAIL_GLASS);
    public static final Item WARPED_WINE = bottle(AlcoholicDrinks.WARPED_WINE);

    public static final Item SHOT_OF_DRY_VERMOUTH = shot(AlcoholicDrinks.DRY_VERMOUTH);
    public static final Item GLASS_OF_DRY_VERMOUTH = glass(AlcoholicDrinks.DRY_VERMOUTH, WINE_GLASS);
    public static final Item DRY_VERMOUTH = bottle(AlcoholicDrinks.DRY_VERMOUTH);

    public static final Item SHOT_OF_SWEET_VERMOUTH = shot(AlcoholicDrinks.SWEET_VERMOUTH);
    public static final Item GLASS_OF_SWEET_VERMOUTH = glass(AlcoholicDrinks.SWEET_VERMOUTH, WINE_GLASS);
    public static final Item SWEET_VERMOUTH = bottle(AlcoholicDrinks.SWEET_VERMOUTH);

    public static final Item GLASS_OF_ABSINTHE = glass(AlcoholicDrinks.ABSINTHE, COCKTAIL_GLASS);
    public static final Item SHOT_OF_ABSINTHE = shot(AlcoholicDrinks.ABSINTHE);
    public static final Item ABSINTHE = bottle(AlcoholicDrinks.ABSINTHE);

    public static final Item SHOT_OF_VODKA = shot(AlcoholicDrinks.VODKA);
    public static final Item VODKA = bottle(AlcoholicDrinks.VODKA);

    public static final Item SHOT_OF_APPLE_LIQUEUR = shot(AlcoholicDrinks.APPLE_LIQUEUR);
    public static final Item APPLE_LIQUEUR = bottle(AlcoholicDrinks.APPLE_LIQUEUR);

    public static final Item SHOT_OF_RUM = shot(AlcoholicDrinks.RUM);
    public static final Item RUM = bottle(AlcoholicDrinks.RUM);

    public static final Item SHOT_OF_COFFEE_LIQUEUR = shot(AlcoholicDrinks.COFFEE_LIQUEUR);
    public static final Item COFFEE_LIQUEUR = bottle(AlcoholicDrinks.COFFEE_LIQUEUR);

    public static final Item SHOT_OF_GIN = shot(AlcoholicDrinks.GIN);
    public static final Item GIN = bottle(AlcoholicDrinks.GIN);

    public static final Item SHOT_OF_TEQUILA = shot(AlcoholicDrinks.TEQUILA);
    public static final Item TEQUILA = bottle(AlcoholicDrinks.TEQUILA);

    public static final Item SHOT_OF_ORANGE_LIQUEUR = shot(AlcoholicDrinks.ORANGE_LIQUEUR);
    public static final Item ORANGE_LIQUEUR = bottle(AlcoholicDrinks.ORANGE_LIQUEUR);

    public static final Item SHOT_OF_WHISKEY = shot(AlcoholicDrinks.WHISKEY);
    public static final Item WHISKEY = bottle(AlcoholicDrinks.WHISKEY);

    public static final Item SHOT_OF_EVERCLEAR = shot(AlcoholicDrinks.EVERCLEAR);
    public static final Item EVERCLEAR = bottle(AlcoholicDrinks.EVERCLEAR);

    public static final Item MIXED_DRINK = new MixedDrinkItem(new Item.Properties().stacksTo(1));

    public static final Item RED_GRAPE_SEEDS = new Item(new Item.Properties());
    public static final Item GREEN_GRAPE_SEEDS = new Item(new Item.Properties());

    public static final Item RED_GRAPE = new Item(new Item.Properties().food(new FoodProperties.Builder().fast().nutrition(1).saturationMod(0.25f).build()));
    public static final Item GREEN_GRAPE = new Item(new Item.Properties().food(Objects.requireNonNull(RED_GRAPE.getFoodProperties())));
    public static final Item SKINNED_GRAPE = new Item(new Item.Properties().food(Objects.requireNonNull(RED_GRAPE.getFoodProperties())));

    // Block Items

    public static final Item BOILER = new BlockItem(BartendingBlocks.BOILER, new Item.Properties());
    public static final Item BOTTLER = new BlockItem(BartendingBlocks.BOTTLER, new Item.Properties());
    public static final Item DISTILLERY = new BlockItem(BartendingBlocks.DISTILLERY, new Item.Properties());
    public static final Item COUNTER_TOP = new BlockItem(BartendingBlocks.COUNTER_TOP, new Item.Properties());
    public static final Item VINE_FRAME = new BlockItem(BartendingBlocks.VINE_FRAME, new Item.Properties());
    public static final Item RED_GRAPE_PLANT = new BlockItem(BartendingBlocks.RED_GRAPE_PLANT, new Item.Properties());
    public static final Item GREEN_GRAPE_PLANT = new BlockItem(BartendingBlocks.GREEN_GRAPE_PLANT, new Item.Properties());

    public static final HashMap<WoodType, BlockItem> FERMENTING_BARRELS = Util.make(() -> {
        HashMap<WoodType, BlockItem> map = new HashMap<>();
        BartendingBlocks.BARRELS.forEach((type, block) -> map.put(type, new BlockItem(block, new Item.Properties())));
        return map;
    });

    private static AlcoholicDrinkItem glass(AlcoholicDrink drink, Item glass) {
        Item.Properties props = new Item.Properties()
                .rarity(Rarity.UNCOMMON);
        AlcoholicDrinkItem item = new AlcoholicDrinkItem(drink, glass, props);
        GLASSES.put(drink, item);
        return item;
    }

    private static PourableBottleItem bottle(AlcoholicDrink drink) {
        int ouncesTotal = BrewingUtil.getOuncesFromBottle(drink.bottle());
        int servings = ouncesTotal * 2;
        Item.Properties properties = new Item.Properties()
                .rarity(Rarity.UNCOMMON);
        PourableBottleItem item = new PourableBottleItem(drink.bottle(), drink, properties);
        registerFinalDrink(drink, item);
        BOTTLES.put(drink, item);
        return item;
    }

    private static AlcoholicDrinkItem servingBottle(AlcoholicDrink drink) {
        Item.Properties props = new Item.Properties()
                .rarity(Rarity.UNCOMMON);
        AlcoholicDrinkItem item = new AlcoholicDrinkItem(drink, BartendingItems.BEER_BOTTLE, props);
        SERVING_BOTTLES.put(drink, item);
        return item;
    }

    private static AlcoholicShotItem shot(AlcoholicDrink drink) {
        AlcoholicShotItem shot = new AlcoholicShotItem(
                drink, new Item.Properties()
                .rarity(Rarity.UNCOMMON)
                .craftRemainder(SHOT_GLASS)
                .stacksTo(16)
        );
        SHOTS.put(drink, shot);
        return shot;
    }

    private static void register(String id, Item item) {
        Items.registerItem(TheArtOfBartending.asId(id), item);
    }

    public static void init() {
        BOTTLES.forEach((alc, bottle) -> {
            String path = AlcoholicDrinks.getId(alc).getPath();
            register(path, bottle);
        });
        SHOTS.forEach((alc, shot) -> {
            String path = AlcoholicDrinks.getId(alc).withPrefix("shot_of_").getPath();
            register(path, shot);
        });
        GLASSES.forEach((alc, glass) -> {
            String path = AlcoholicDrinks.getId(alc).withPrefix("glass_of_").getPath();
            register(path, glass);
        });
        SERVING_BOTTLES.forEach((alc, bottle) -> {
            String path = AlcoholicDrinks.getId(alc).withPrefix("bottle_of_").getPath();
            register(path, bottle);
        });
        register("concoction", CONCOCTION);
        register("wine_bottle", WINE_BOTTLE);
        register("beer_bottle", BEER_BOTTLE);
        register("liquor_bottle", LIQUOR_BOTTLE);
        register("jug", JUG);
        register("shot_glass", SHOT_GLASS);
        register("cocktail_glass", COCKTAIL_GLASS);
        register("wine_glass", WINE_GLASS);
        register("tall_glass", TALL_GLASS);
        register("short_glass", SHORT_GLASS);
        register("mixed_drink", MIXED_DRINK);

        register("red_grape_seeds", RED_GRAPE_SEEDS);
        register("green_grape_seeds", GREEN_GRAPE_SEEDS);

        register("red_grape", RED_GRAPE);
        register("green_grape", GREEN_GRAPE);
        register("skinned_grape", SKINNED_GRAPE);

        register("boiler", BOILER);
        register("bottler", BOTTLER);
        register("distillery", DISTILLERY);
        register("countertop", COUNTER_TOP);
        register("vine_frame", VINE_FRAME);
        register("red_grape_plant", RED_GRAPE_PLANT);
        register("green_grape_plant", GREEN_GRAPE_PLANT);
        FERMENTING_BARRELS.forEach((type, item) -> register(type.name() + "_fermenting_barrel", item));
    }

}
