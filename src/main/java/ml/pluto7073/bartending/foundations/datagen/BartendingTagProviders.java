package ml.pluto7073.bartending.foundations.datagen;

import com.simibubi.create.AllTags;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.content.block.FermentingBarrelBlock;
import ml.pluto7073.bartending.content.fluid.BartendingFluids;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.item.AlcoholicDrinkItem;
import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.pdapi.tag.PDTags;
import ml.pluto7073.pdapi.util.DrinkUtil;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class BartendingTagProviders {

    public static class FluidTagProvider extends FabricTagProvider.FluidTagProvider {

        public FluidTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            BartendingFluids.FLUID_TAGS.forEach((fluid, tags) ->
                    tags.stream().map(this::getOrCreateTagBuilder).forEach(builder -> builder.add(fluid)));
        }

    }

    public static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {

        public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            FabricTagBuilder pickaxe = getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE);
            pickaxe.add(BartendingBlocks.BOILER, BartendingBlocks.BOTTLER, BartendingBlocks.DISTILLERY);

            FabricTagBuilder axe = getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE);
            BartendingBlocks.BARRELS.values().stream()
                    .sorted(DrinkUtil.alphabetizer(FermentingBarrelBlock::getDescriptionId))
                    .forEach(axe::add);
            getOrCreateTagBuilder(BartendingTags.C_FARMLAND).add(Blocks.FARMLAND);
            getOrCreateTagBuilder(BartendingTags.C_OAK_LOGS).add(Blocks.OAK_LOG, Blocks.DARK_OAK_LOG)
                    .addOptional(new ResourceLocation("palegardenbackport:pale_oak_log"));
        }

    }

    public static class ItemTagProvider extends FabricTagProvider.ItemTagProvider {


        public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider arg) {
            getOrCreateTagBuilder(BartendingTags.GLASSES).add(Items.GLASS_BOTTLE, BartendingItems.COCKTAIL_GLASS,
                    BartendingItems.WINE_GLASS, BartendingItems.TALL_GLASS, BartendingItems.SHORT_GLASS);
            FabricTagBuilder workstationDrinks = getOrCreateTagBuilder(PDTags.WORKSTATION_DRINKS);
            workstationDrinks.add(BartendingItems.MIXED_DRINK).addTag(BartendingTags.GLASSES);
            BartendingItems.GLASSES.values().stream()
                    .sorted(DrinkUtil.alphabetizer(AlcoholicDrinkItem::getDescriptionId))
                    .forEach(workstationDrinks::add);
            FabricTagBuilder uprightOnBelt = getOrCreateTagBuilder(AllTags.AllItemTags.UPRIGHT_ON_BELT.tag);
            uprightOnBelt.add(BartendingItems.SHOT_GLASS, BartendingItems.CONCOCTION, BartendingItems.BEER_BOTTLE,
                    BartendingItems.WINE_BOTTLE, BartendingItems.LIQUOR_BOTTLE, BartendingItems.JUG,
                    BartendingItems.MIXED_DRINK).addTag(BartendingTags.GLASSES);

            BrewingUtil.collectionsToStream(BartendingItems.SHOTS.values(), BartendingItems.BOTTLES.values(),
                    BartendingItems.GLASSES.values(), BartendingItems.SERVING_BOTTLES.values())
                            .sorted(DrinkUtil.alphabetizer(Item::getDescriptionId))
                                    .forEach(uprightOnBelt::add);

            getOrCreateTagBuilder(BartendingTags.C_FRUITS)
                    .add(Items.APPLE, Items.MELON_SLICE, Items.SWEET_BERRIES, Items.CHORUS_FRUIT, Items.GLOW_BERRIES, BartendingItems.RED_GRAPE, BartendingItems.GREEN_GRAPE, BartendingItems.SKINNED_GRAPE);
            getOrCreateTagBuilder(BartendingTags.UNSKINNED_GRAPES)
                    .add(BartendingItems.RED_GRAPE, BartendingItems.GREEN_GRAPE);
            getOrCreateTagBuilder(BartendingTags.C_APPLES)
                    .add(Items.APPLE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE)
                    .addOptional(new ResourceLocation("create:honeyed_apple"));
        }
    }

}
