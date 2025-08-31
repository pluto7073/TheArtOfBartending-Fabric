package ml.pluto7073.bartending.content.alcohol;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.compat.fruitfulfun.FruityAlcoholicDrinkManager;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.content.fluid.BartendingFluids;
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

    public static final AlcoholicDrink BEER = register("beer", new AlcoholicDrink.Builder().proof(10).ounces(12)
            .addStep(new BoilingBrewerStep.Builder().addIngredient(Ingredient.of(Items.WHEAT), 24, 4)
                    .setTicks(9600).setLeeway(600).build())
            .addStep(new FermentingBrewerStep(BarrelPredicate.ANY, 3, 1))
            .bottle(BartendingItems.JUG).name("Beer")
            .color(0x7a5814).build());
    public static final AlcoholicDrink WHEAT_BEER = register("wheat_beer", new AlcoholicDrink.Builder().proof(8).ounces(12)
            .addStep(new BoilingBrewerStep.Builder().addIngredient(Ingredient.of(Items.WHEAT), 12, 2)
                    .setTicks(9600).setLeeway(600).build())
            .addStep(new FermentingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.BIRCH)), 2, 1))
            .bottle(BartendingItems.JUG).name("Wheat Beer")
            .color(0xd1a347).build());
    public static final AlcoholicDrink DARK_BEER = register("dark_beer", new AlcoholicDrink.Builder().proof(16).ounces(12)
            .addStep(new BoilingBrewerStep.Builder().addIngredient(Ingredient.of(Items.WHEAT), 24, 4)
                    .setTicks(9600).setLeeway(600).build())
            .addStep(new FermentingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.DARK_OAK)), 9, 2))
            .bottle(BartendingItems.JUG).name("Dark Beer")
            .color(0x211408).build());
    public static final AlcoholicDrink MEAD = register("mead", new AlcoholicDrink.Builder().proof(20).ounces(5)
            .addStep(new BoilingBrewerStep.Builder().addIngredient(Ingredient.of(Items.HONEY_BOTTLE), 4, 2)
                    .setTicks(3600).setLeeway(600).build())
            .addStep(new FermentingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.OAK)), 4, 2))
            .bottle(BartendingItems.WINE_BOTTLE).name("Mead")
            .color(0xedeba1).build());
    public static final AlcoholicDrink APPLE_MEAD = register("apple_mead", new AlcoholicDrink.Builder().proof(30).ounces(5)
            .addStep(new BoilingBrewerStep.Builder().addIngredient(Ingredient.of(Items.HONEY_BOTTLE), 4, 2)
                    .addIngredient(Ingredient.of(Items.APPLE), 16, 4).setTicks(4800).setLeeway(600).build())
            .addStep(new FermentingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.OAK)), 4, 2))
            .bottle(BartendingItems.WINE_BOTTLE).name("Apple Mead")
            .color(0x683222).build());
    public static final AlcoholicDrink RED_WINE = register("red_wine", new AlcoholicDrink.Builder().proof(24).ounces(5)
            .addStep(new BoilingBrewerStep.Builder().addIngredient(Ingredient.of(Items.SWEET_BERRIES), 96, 10)
                    .setTicks(6000).setLeeway(600).build())
            .addStep(new FermentingBrewerStep(BarrelPredicate.ANY, 1, 0))
            .bottle(BartendingItems.WINE_BOTTLE).name("Red Wine")
            .color(0x2b0010).build());
    public static final AlcoholicDrink WHITE_WINE = register("white_wine", new AlcoholicDrink.Builder().proof(24).ounces(5)
            .addStep(new BoilingBrewerStep.Builder().addIngredient(Ingredient.of(Items.GLOW_BERRIES), 96, 10)
                    .setTicks(6000).setLeeway(600).build())
            .addStep(new FermentingBrewerStep(BarrelPredicate.ANY, 1, 0))
            .bottle(BartendingItems.WINE_BOTTLE).name("White Wine")
            .color(0xe2c36c).build());
    public static final AlcoholicDrink CHAMPAGNE = register("champagne", new AlcoholicDrink.Builder().proof(24).ounces(5)
            .addStep(new ExtraFermentingBrewerStep(
                    1,
                    BarrelPredicate.ANY,
                    () -> BartendingItems.WHITE_WINE
            )).name("Champagne").color(0xe2c36c)
            .bottle(BartendingItems.WINE_BOTTLE).build());
    public static final AlcoholicDrink CRIMSON_WINE = register("crimson_wine", new AlcoholicDrink.Builder().proof(52).ounces(5)
            .addStep(new BoilingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(BartendingTags.CRIMSON_BOTANICALS), 32, 8)
                    .addIngredient(Ingredient.of(Items.CRIMSON_FUNGUS), 96, 16)
                    .setTicks(9000).setLeeway(900).build())
            .addStep(new FermentingBrewerStep(BarrelPredicate.ofWood(WoodType.CRIMSON), 2, 1))
            .bottle(BartendingItems.WINE_BOTTLE).name("Crimson Wine")
            .color(0xa02020).build());
    public static final AlcoholicDrink WARPED_WINE = register("warped_wine", new AlcoholicDrink.Builder().proof(56).ounces(5)
            .addStep(new BoilingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(BartendingTags.WARPED_BOTANICALS), 32, 8)
                    .addIngredient(Ingredient.of(Items.WARPED_FUNGUS), 96, 16)
                    .setTicks(9000).setLeeway(900).build())
            .addStep(new FermentingBrewerStep(BarrelPredicate.ofWood(WoodType.WARPED), 2, 1))
            .bottle(BartendingItems.WINE_BOTTLE).name("Warped Wine")
            .color(0x1af2f2).build());
    public static final AlcoholicDrink APPLE_LIQUEUR = register("apple_liqueur", new AlcoholicDrink.Builder()
            .proof(60).ounces(1.5f).addStep(new BoilingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(Items.APPLE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE), 64)
                    .setTicks(24000).setLeeway(6000).build())
            .addStep(new FermentingBrewerStep(new BarrelPredicate(BartendingBlocks.BARRELS.get(WoodType.CHERRY),
                    BartendingBlocks.BARRELS.get(WoodType.ACACIA)), 6, 2))
            .addStep(new DistillingBrewerStep(3, 1)).bottle(BartendingItems.LIQUOR_BOTTLE)
            .color(0xbc8a49).name("Apple Liqueur").build());
    public static final AlcoholicDrink VODKA = register("vodka", new AlcoholicDrink.Builder().proof(80).ounces(1.5f)
            .addStep(new BoilingBrewerStep.Builder().addIngredient(Ingredient.of(Items.POTATO, Items.POISONOUS_POTATO), 10)
                    .setTicks(24000).build())
            .addStep(new DistillingBrewerStep(3, 1)).bottle(BartendingItems.LIQUOR_BOTTLE)
            .name("Vodka").build());
    public static final AlcoholicDrink RUM = register("rum", new AlcoholicDrink.Builder().proof(80).ounces(1.5f)
            .addStep(new BoilingBrewerStep.Builder().addIngredient(Ingredient.of(Items.SUGAR_CANE), 50)
                    .setTicks(2400).setLeeway(600).build())
            .addStep(new FermentingBrewerStep(BarrelPredicate.ofWood(WoodType.OAK, WoodType.DARK_OAK),
                    8, 2))
            .addStep(new DistillingBrewerStep(2)).bottle(BartendingItems.LIQUOR_BOTTLE)
            .color(0x825424).name("Rum").build());
    public static final AlcoholicDrink COFFEE_LIQUEUR = register("coffee_liqueur", new AlcoholicDrink.Builder()
            .addStep(new AlternativeBrewerStep()).name("Coffee Liqueur")
            .setVisibleWhen(() -> FabricLoader.getInstance().isModLoaded("plutoscoffee"))
            .color(0x211304).ounces(1.5f).proof(80).bottle(BartendingItems.LIQUOR_BOTTLE).build());
    public static final AlcoholicDrink GIN = register("gin", new AlcoholicDrink.Builder().proof(90).ounces(1.5f)
            .addStep(new BoilingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(Items.BLUE_ORCHID, Items.LILY_OF_THE_VALLEY,
                            Items.CORNFLOWER, Items.ALLIUM), 10, 2)
                    .addIngredient(Ingredient.of(Items.WHEAT), 10, 3)
                    .addIngredient(Ingredient.of(Items.SWEET_BERRIES), 20)
                    .setTicks(6000).setLeeway(600).build())
            .addStep(new DistillingBrewerStep(2, 1)).name("Gin")
            .bottle(BartendingItems.LIQUOR_BOTTLE).build());
    public static final AlcoholicDrink TEQUILA = register("tequila", new AlcoholicDrink.Builder().proof(80)
            .ounces(1.5f).addStep(new BoilingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(Items.CACTUS), 20, 5)
                    .setTicks(18000).setLeeway(3600).build())
            .addStep(new FermentingBrewerStep(BarrelPredicate.ofWood(WoodType.BIRCH),
                    10, 2))
            .addStep(new DistillingBrewerStep(2, 1))
            .color(0xEFEFEF).name("Tequila").bottle(BartendingItems.LIQUOR_BOTTLE).build());
    public static final AlcoholicDrink ORANGE_LIQUEUR = register("orange_liqueur", FruityAlcoholicDrinkManager.createOrangeLiqueur());
    public static final AlcoholicDrink DRY_VERMOUTH = register("dry_vermouth", new AlcoholicDrink.Builder().proof(33)
            .ounces(5).addStep(new AlternativeBrewerStep())
            .color(0xfcf3ba).name("Dry Vermouth").bottle(BartendingItems.WINE_BOTTLE).build());
    public static final AlcoholicDrink SWEET_VERMOUTH = register("sweet_vermouth", new AlcoholicDrink.Builder()
            .proof(33).ounces(5).addStep(new AlternativeBrewerStep())
            .color(0x51190d).name("Sweet Vermouth").bottle(BartendingItems.WINE_BOTTLE).build());
    public static final AlcoholicDrink WHISKEY = register("whiskey", new AlcoholicDrink.Builder().proof(80).ounces(1.5f)
            .addStep(new BoilingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(Items.WHEAT), 15, 3)
                    .setTicks(18000).setLeeway(3600).build())
            .addStep(new FermentingBrewerStep(BarrelPredicate.ofWood(WoodType.OAK), 10, 2))
            .addStep(new DistillingBrewerStep(2))
            .name("Whiskey").color(0x442612).bottle(BartendingItems.LIQUOR_BOTTLE).build());
    public static final AlcoholicDrink ABSINTHE = register("absinthe", new AlcoholicDrink.Builder().proof(120).ounces(5)
            .addStep(new BoilingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(Items.GRASS, Items.TALL_GRASS, Items.FERN, Items.LARGE_FERN), 128, 24)
                    .addIngredient(Ingredient.of(Items.WHEAT_SEEDS), 32, 10)
                    .setTicks(3600).setLeeway(600).build())
            .addStep(new DistillingBrewerStep(6, 3))
            .color(0x679b33).bottle(BartendingItems.WINE_BOTTLE).name("Absinthe").build());
    public static final AlcoholicDrink EVERCLEAR = register("everclear", new AlcoholicDrink.Builder().proof(190).ounces(1.5f)
            .addStep(new BoilingBrewerStep.Builder()
                    .addIngredient(Ingredient.of(Items.WHEAT), 128)
                    .setTicks(3600).setLeeway(600).build())
            .addStep(new DistillingBrewerStep(6, 3))
            .bottle(BartendingItems.LIQUOR_BOTTLE).name("Everclear").build());

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
    }

    public static Item getFinalDrink(AlcoholicDrink drink) {
        return DRINK_TO_ITEM.get(drink);
    }

}
