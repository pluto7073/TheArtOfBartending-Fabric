package ml.pluto7073.bartending.foundations.item;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.content.fluid.BartendingFluids;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.fluid.FluidHolder;
import ml.pluto7073.pdapi.block.PDBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.WoodType;

public class BartendingCreativeTabs {

    public static final ResourceKey<CreativeModeTab> MAIN_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, TheArtOfBartending.asId("main"));

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MAIN_TAB,
                FabricItemGroup.builder().title(Component.translatable("itemGroup.bartending.main"))
                        .icon(() -> new ItemStack(BartendingItems.RED_WINE)).build());
        ItemGroupEvents.modifyEntriesEvent(MAIN_TAB).register(stacks -> {

            // Blocks
            stacks.accept(BartendingItems.BOILER);
            stacks.accept(BartendingItems.DISTILLERY);
            stacks.accept(BartendingItems.BOTTLER);
            stacks.accept(PDBlocks.DRINK_WORKSTATION);
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.OAK));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.SPRUCE));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.BIRCH));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.JUNGLE));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.ACACIA));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.DARK_OAK));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.MANGROVE));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.CHERRY));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.BAMBOO));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.CRIMSON));
            stacks.accept(BartendingBlocks.BARRELS.get(WoodType.WARPED));

            // Bottles
            stacks.accept(Items.GLASS_BOTTLE);
            stacks.accept(BartendingItems.WINE_BOTTLE);
            stacks.accept(BartendingItems.BEER_BOTTLE);
            stacks.accept(BartendingItems.LIQUOR_BOTTLE);

            // Drinks
            stacks.accept(BartendingItems.RED_WINE);
            stacks.accept(BartendingItems.WHITE_WINE);
            stacks.accept(BartendingItems.SWEET_VERMOUTH);
            stacks.accept(BartendingItems.DRY_VERMOUTH);
            stacks.accept(BartendingItems.VODKA);
            stacks.accept(BartendingItems.APPLE_LIQUEUR);
            stacks.accept(BartendingItems.RUM);
            if (AlcoholicDrinks.COFFEE_LIQUEUR.isVisible()) stacks.accept(BartendingItems.COFFEE_LIQUEUR);
            stacks.accept(BartendingItems.GIN);
            stacks.accept(BartendingItems.TEQUILA);

            // Servings
            stacks.accept(BartendingItems.BEER);
            stacks.accept(BartendingItems.GLASS_OF_RED_WINE);
            stacks.accept(BartendingItems.GLASS_OF_WHITE_WINE);
            stacks.accept(BartendingItems.SHOT_OF_SWEET_VERMOUTH);
            stacks.accept(BartendingItems.SHOT_OF_DRY_VERMOUTH);
            stacks.accept(BartendingItems.SHOT_OF_VODKA);
            stacks.accept(BartendingItems.SHOT_OF_APPLE_LIQUEUR);
            stacks.accept(BartendingItems.SHOT_OF_RUM);
            if (AlcoholicDrinks.COFFEE_LIQUEUR.isVisible()) stacks.accept(BartendingItems.SHOT_OF_COFFEE_LIQUEUR);
            stacks.accept(BartendingItems.SHOT_OF_GIN);
            stacks.accept(BartendingItems.SHOT_OF_TEQUILA);

            // Buckets
            stacks.accept(BartendingFluids.BEER.bucket());
            stacks.accept(BartendingFluids.RED_WINE.bucket());
            stacks.accept(BartendingFluids.WHITE_WINE.bucket());
            stacks.accept(BartendingFluids.SWEET_VERMOUTH.bucket());
            stacks.accept(BartendingFluids.DRY_VERMOUTH.bucket());
            stacks.accept(BartendingFluids.VODKA.bucket());
            stacks.accept(BartendingFluids.APPLE_LIQUEUR.bucket());
            stacks.accept(BartendingFluids.RUM.bucket());
            if (AlcoholicDrinks.COFFEE_LIQUEUR.isVisible()) stacks.accept(BartendingFluids.APPLE_LIQUEUR.bucket());
            stacks.accept(BartendingFluids.GIN.bucket());
            stacks.accept(BartendingFluids.TEQUILA.bucket());
        });
    }

}