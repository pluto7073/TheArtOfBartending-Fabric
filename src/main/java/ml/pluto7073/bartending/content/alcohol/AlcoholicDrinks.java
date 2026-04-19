package ml.pluto7073.bartending.content.alcohol;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.compat.fruitfulfun.FruityAlcoholicDrinkManager;
import ml.pluto7073.bartending.compat.plutoscoffee.CaffeinatedAlcoholicDrinkManager;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.BartendingRegistries;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.step.*;
import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.*;

@MethodsReturnNonnullByDefault
public final class AlcoholicDrinks {

    private static final HashMap<AlcoholicDrink, Item> DRINK_TO_ITEM = new HashMap<>();
    public static final List<AlcoholicDrink> BASES = new ArrayList<>();
    public static final List<Item> BASE_ITEMS = new ArrayList<>();

    public static final AlcoholicDrink BEER = register("beer", AlcoholicDrink.builder().proof(10).ounces(12)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.WHEAT), 24, 4)
                    .setTicks(48000).setLeeway(24000).build())
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ANY, 0.5f, -0.25f))
            .bottle(BartendingItems.JUG).name("Beer")
            .color(0x7a5814).build());
    public static final AlcoholicDrink WHEAT_BEER = register("wheat_beer", AlcoholicDrink.builder().proof(8).ounces(12)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.WHEAT), 12, 2)
                    .setTicks(36000).setLeeway(12000).build())
            .addStep(new BarrelAgingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.BIRCH)), 0.3333f, -0.1667f))
            .bottle(BartendingItems.JUG).name("Wheat Beer")
            .color(0xd1a347).build());
    public static final AlcoholicDrink DARK_BEER = register("dark_beer", AlcoholicDrink.builder().proof(16).ounces(12)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.WHEAT), 32, 6)
                    .setTicks(72000).setLeeway(24000).build())
            .addStep(new BarrelAgingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.DARK_OAK)), 0.6667f, -0.3333f))
            .bottle(BartendingItems.JUG).name("Dark Beer")
            .color(0x211408).build());
    public static final AlcoholicDrink MEAD = register("mead", AlcoholicDrink.builder().proof(20).ounces(5)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.HONEY_BOTTLE), 8, 2)
                    .setTicks(18000).setLeeway(12000).build())
            .addStep(new BarrelAgingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.OAK)), 4, 2))
            .bottle(BartendingItems.WINE_BOTTLE).name("Mead")
            .color(0xedeba1).build());
    public static final AlcoholicDrink APPLE_MEAD = register("apple_mead", AlcoholicDrink.builder().proof(30).ounces(5)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.HONEY_BOTTLE), 8, 2)
                    .addIngredient(Ingredient.of(Items.APPLE), 16, 4).setTicks(18000).setLeeway(12000).build())
            .addStep(new BarrelAgingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.OAK)), 4, 2))
            .bottle(BartendingItems.WINE_BOTTLE).name("Apple Mead")
            .color(0x683222).build());
    public static final AlcoholicDrink RED_WINE = register("red_wine", AlcoholicDrink.builder().proof(24).ounces(5)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(BartendingItems.RED_GRAPE), 192, 32)
                    .setTicks(24000).setLeeway(12000).build())
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ANY, 2, -1.5f))
            .bottle(BartendingItems.WINE_BOTTLE).name("Red Wine")
            .color(0x2b0010).build());
    public static final AlcoholicDrink WHITE_WINE = register("white_wine", AlcoholicDrink.builder().proof(24).ounces(5)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(BartendingItems.SKINNED_GRAPE), 192, 32)
                    .setTicks(24000).setLeeway(12000).build())
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ANY, 2, -1.5f))
            .bottle(BartendingItems.WINE_BOTTLE).name("White Wine")
            .color(0xe2c36c).build());
    public static final AlcoholicDrink SWEET_BERRY_WINE = register("sweet_berry_wine", AlcoholicDrink.builder().proof(24).ounces(5)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.SWEET_BERRIES), 192, 32)
                    .setTicks(24000).setLeeway(12000).build())
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ANY, 1, -1.5f))
            .bottle(BartendingItems.WINE_BOTTLE).name("Sweet Berry Wine")
            .color(0x2b0010).build());
    public static final AlcoholicDrink GLOW_BERRY_WINE = register("glow_berry_wine", AlcoholicDrink.builder().proof(24).ounces(5)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.GLOW_BERRIES), 192, 32)
                    .setLeeway(24000).setLeeway(12000).build())
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ANY, 1, -1.5f))
            .bottle(BartendingItems.WINE_BOTTLE).name("Glow Berry Wine")
            .color(0xe2c36c).build());
    public static final AlcoholicDrink CHAMPAGNE = register("champagne", AlcoholicDrink.secondaryBuilder(() -> List.of(WHITE_WINE, GLOW_BERRY_WINE)).proof(24).ounces(5)
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ANY, 1))
            .name("Champagne").color(0xe2c36c)
            .bottle(BartendingItems.WINE_BOTTLE).build());
    public static final AlcoholicDrink CRIMSON_WINE = register("crimson_wine", AlcoholicDrink.builder().proof(52).ounces(5)
            .addStep(new FermentingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(BartendingTags.CRIMSON_BOTANICALS), 128, 16)
                    .addIngredient(Ingredient.of(Items.CRIMSON_FUNGUS), 96, 16)
                    .setTicks(48000).setLeeway(24000).build())
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ofWood(WoodType.CRIMSON), 2, -1))
            .bottle(BartendingItems.WINE_BOTTLE).name("Crimson Wine")
            .color(0xa02020).build());
    public static final AlcoholicDrink WARPED_WINE = register("warped_wine", AlcoholicDrink.builder().proof(56).ounces(5)
            .addStep(new FermentingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(BartendingTags.WARPED_BOTANICALS), 128, 16)
                    .addIngredient(Ingredient.of(Items.WARPED_FUNGUS), 96, 16)
                    .setTicks(48000).setLeeway(24000).build())
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ofWood(WoodType.WARPED), 2, -1))
            .bottle(BartendingItems.WINE_BOTTLE).name("Warped Wine")
            .color(0x1af2f2).build());
    public static final AlcoholicDrink GRAIN_ALCOHOL = register("grain_alcohol", AlcoholicDrink.builder().proof(190).ounces(1.5f)
            .addStep(new FermentingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(Items.WHEAT, Items.POTATO, BartendingItems.SKINNED_GRAPE, Items.POISONOUS_POTATO), 128)
                    .setTicks(36000).setLeeway(12000).build())
            .addStep(new DistillingBrewerStep(6, 1))
            .bottle(BartendingItems.LIQUOR_BOTTLE).name("Grain Alcohol").build());
    public static final AlcoholicDrink VODKA = register("vodka", AlcoholicDrink.builder().proof(80).ounces(1.5f)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.POTATO, Items.POISONOUS_POTATO, BartendingItems.SKINNED_GRAPE, Items.WHEAT), 10)
                    .setTicks(24000).setLeeway(12000).build())
            .addStep(new DistillingBrewerStep(3, 1)).bottle(BartendingItems.LIQUOR_BOTTLE)
            .addAlternativeSteps(Optional.of(() -> List.of(GRAIN_ALCOHOL)), new AddingItemBrewerStep(() -> Ingredient.of(Items.POTION), 2, 0))
            .name("Vodka").build());
    public static final AlcoholicDrink RUM = register("rum", AlcoholicDrink.builder().proof(80).ounces(1.5f)
            .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.SUGAR_CANE), 50)
                    .setTicks(4500).setLeeway(1500).build())
            .addStep(new DistillingBrewerStep(2, 1))
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ofWood(WoodType.OAK, WoodType.DARK_OAK),
                    1, -0.5f))
            .bottle(BartendingItems.LIQUOR_BOTTLE)
            .color(0x825424).name("Rum").build());
    public static final AlcoholicDrink APPLE_LIQUEUR = register("apple_liqueur", AlcoholicDrink.secondaryBuilder(() -> List.of(VODKA))
            .proof(60).ounces(1.5f).addStep(new AddingItemBrewerStep(() -> Ingredient.of(BartendingTags.C_APPLES), 64, 0))
            .addStep(new DistillingBrewerStep(3, 1)).bottle(BartendingItems.LIQUOR_BOTTLE)
            .addStep(new BarrelAgingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.CHERRY),
                    BartendingBlocks.BARRELS.get(WoodType.ACACIA)), 6, -2))
            .color(0xbc8a49).name("Apple Liqueur").build());
    public static final AlcoholicDrink COFFEE_LIQUEUR = register("coffee_liqueur", CaffeinatedAlcoholicDrinkManager.createCoffeeLiqueur());
    public static final AlcoholicDrink GIN = register("gin", AlcoholicDrink.secondaryBuilder(() -> List.of(VODKA)).proof(90).ounces(1.5f)
            .addStep(new AddingItemBrewerStep(() -> Ingredient.of(Items.BLUE_ORCHID, Items.LILY_OF_THE_VALLEY, Items.CORNFLOWER, Items.ALLIUM), 10, 2))
            .addStep(new AddingItemBrewerStep(() -> Ingredient.of(Items.SWEET_BERRIES), 20, 0))
            .addStep(new AddingItemBrewerStep(() -> Ingredient.of(BartendingTags.BOTANICAL_ELEMENTS), 5, 2))
            .addStep(new DistillingBrewerStep(2, 1)).name("Gin")
            .bottle(BartendingItems.LIQUOR_BOTTLE).build());
    public static final AlcoholicDrink TEQUILA = register("tequila", AlcoholicDrink.builder().proof(80)
            .ounces(1.5f).addStep(new FermentingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(Items.CACTUS), 20, 5)
                    .setTicks(24000).setLeeway(12000).build())
            .addStep(new DistillingBrewerStep(2, 1))
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ofWood(WoodType.BIRCH),
                    1, -0.6667f))
            .addStep(new DistillingBrewerStep(2, 1))
            .color(0xEFEFEF).name("Tequila").bottle(BartendingItems.LIQUOR_BOTTLE).build());
    public static final AlcoholicDrink ORANGE_LIQUEUR = register("orange_liqueur", FruityAlcoholicDrinkManager.createOrangeLiqueur());
    public static final AlcoholicDrink DRY_VERMOUTH = register("dry_vermouth", AlcoholicDrink.secondaryBuilder(() -> List.of(WHITE_WINE, GLOW_BERRY_WINE))
            .proof(33).ounces(5).addStep(new AddingItemBrewerStep(() -> Ingredient.of(BartendingItems.SHOT_OF_VODKA), 5, 1))
            .addStep(new AddingItemBrewerStep(() -> Ingredient.of(BartendingTags.BOTANICAL_ELEMENTS), 3, 0))
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ANY, 0.5f))
            .color(0xfcf3ba).name("Dry Vermouth").bottle(BartendingItems.WINE_BOTTLE).build());
    public static final AlcoholicDrink SWEET_VERMOUTH = register("sweet_vermouth", AlcoholicDrink.secondaryBuilder(() -> List.of(RED_WINE, SWEET_BERRY_WINE))
            .proof(33).ounces(5).addStep(new AddingItemBrewerStep(() -> Ingredient.of(BartendingItems.SHOT_OF_VODKA), 5, 1))
            .addStep(new AddingItemBrewerStep(() -> Ingredient.of(Items.SUGAR, Items.HONEY_BOTTLE), 3, 0))
            .addStep(new AddingItemBrewerStep(() -> Ingredient.of(BartendingTags.BOTANICAL_ELEMENTS), 3, 0))
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ANY, 1, -0.5f))
            .color(0x51190d).name("Sweet Vermouth").bottle(BartendingItems.WINE_BOTTLE).build());
    public static final AlcoholicDrink WHISKEY = register("whiskey", AlcoholicDrink.builder().proof(80).ounces(1.5f)
            .addStep(new FermentingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(Items.WHEAT), 64, 3)
                    .setTicks(12000).setLeeway(6000).build())
            .addStep(new DistillingBrewerStep(2, 1))
            .addStep(new BarrelAgingBrewerStep(BarrelPredicate.ofWood(WoodType.OAK), 10, -5))
            .name("Whiskey").color(0x442612).bottle(BartendingItems.LIQUOR_BOTTLE).build());
    public static final AlcoholicDrink ABSINTHE = register("absinthe", AlcoholicDrink.secondaryBuilder(() -> List.of(GRAIN_ALCOHOL)).proof(120).ounces(5)
            .addStep(new AddingItemBrewerStep(() -> Ingredient.of(Items.GRASS, Items.TALL_GRASS, Items.FERN, Items.LARGE_FERN), 128, 24))
            .addStep(new AddingItemBrewerStep(() -> Ingredient.of(Items.WHEAT_SEEDS), 32, 10))
            .addStep(new DistillingBrewerStep(6, 3))
            .color(0x679b33).bottle(BartendingItems.WINE_BOTTLE).name("Absinthe").build());

    private static AlcoholicDrink register(String id, AlcoholicDrink drink) {
        return Registry.register(BartendingRegistries.ALCOHOLIC_DRINK, TheArtOfBartending.asId(id), drink);
    }

    public static AlcoholicDrink get(ResourceLocation id) {
        return getOptional(id).orElseThrow(() -> new IllegalArgumentException("No registered Drink with id " + id));
    }

    public static Optional<AlcoholicDrink> getOptional(ResourceLocation id) {
        return BartendingRegistries.ALCOHOLIC_DRINK.getOptional(id);
    }

    public static Optional<ResourceLocation> getOptionalKey(AlcoholicDrink drink) {
        return Optional.ofNullable(BartendingRegistries.ALCOHOLIC_DRINK.getKey(drink));
    }

    public static ResourceLocation getId(AlcoholicDrink drink) {
        return getOptionalKey(drink).orElseThrow(() -> new IllegalArgumentException("Unregistered value: " + drink));
    }

    public static Collection<AlcoholicDrink> values() {
        return BartendingRegistries.ALCOHOLIC_DRINK.stream().toList();
    }

    public static void registerFinalDrink(AlcoholicDrink drink, Item item) {
        DRINK_TO_ITEM.put(drink, item);
        if (BASES.contains(drink)) {
            BASE_ITEMS.add(item);
        }
    }

    public static Item getFinalDrink(AlcoholicDrink drink) {
        return DRINK_TO_ITEM.get(drink);
    }

}
