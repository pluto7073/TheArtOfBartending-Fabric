package ml.pluto7073.bartending.foundations.datagen;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.content.fluid.BartendingFluids;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.item.AlcoholicDrinkItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

public class BartendingModelsProvider extends FabricModelProvider {

    public BartendingModelsProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        for (Block block : BartendingBlocks.BARRELS.values()) {
            generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                    .with(generators.createColumnWithFacing())
                    .with(PropertyDispatch.property(BlockStateProperties.OPEN)
                            .select(false, Variant.variant().with(VariantProperties.MODEL,
                                    TheArtOfBartending.asId("block/fermenting_barrel")))
                            .select(true, Variant.variant().with(VariantProperties.MODEL,
                                    TheArtOfBartending.asId("block/fermenting_barrel_open")))));
            generators.delegateItemModel(block, TheArtOfBartending.asId("block/fermenting_barrel"));
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        for (Item shot : BartendingItems.SHOTS.values()) {
            generators.generateFlatItem(shot, new ModelTemplate(
                    Optional.of(TheArtOfBartending.asId("item/liquor_shot")),
                    Optional.empty()
            ));
        }
        for (Item liquor : BartendingItems.BOTTLES.values()
                .stream().filter(item -> item.emptyBottleItem == BartendingItems.LIQUOR_BOTTLE).toList()) {
            generators.generateFlatItem(liquor, new ModelTemplate(
                    Optional.of(TheArtOfBartending.asId("item/liquor")),
                    Optional.empty()
            ));
        }
        for (Item wine : BartendingItems.BOTTLES.values()
                .stream().filter(item -> item.emptyBottleItem == BartendingItems.WINE_BOTTLE).toList()) {
            generators.generateFlatItem(wine, new ModelTemplate(
                    Optional.of(TheArtOfBartending.asId("item/sealed_wine_bottle")),
                    Optional.empty()
            ));
        }
        for (Item jug : BartendingItems.BOTTLES.values()
                .stream().filter(item -> item.emptyBottleItem == BartendingItems.JUG).toList()) {
            generators.generateFlatItem(jug, new ModelTemplate(
                    Optional.of(TheArtOfBartending.asId("item/full_jug")),
                    Optional.empty()
            ));
        }
        for (AlcoholicDrinkItem bottle : BartendingItems.SERVING_BOTTLES.values()) {
            if (bottle == BartendingItems.BOTTLE_OF_BEER) continue;
            generators.generateFlatItem(bottle, new ModelTemplate(
                    Optional.of(TheArtOfBartending.asId("item/bottle_of_beer")),
                    Optional.empty()
            ));
        }
        for (AlcoholicDrinkItem glass : BartendingItems.GLASSES.values()) {
            if (glass.source.standardProof() < 20) {
                generators.generateFlatItem(glass, new ModelTemplate(
                        Optional.of(TheArtOfBartending.asId("item/simple_glass")),
                        Optional.empty()
                ));
            } else if (glass.source.standardProof() >= 50) {
                generators.generateFlatItem(glass, new ModelTemplate(
                        Optional.of(TheArtOfBartending.asId("item/mixed_drink")),
                        Optional.empty()
                ));
            } else {
                generators.generateFlatItem(glass, new ModelTemplate(
                        Optional.of(TheArtOfBartending.asId("item/glass_of_alc")),
                        Optional.empty()
                ));
            }
        }

        generators.generateFlatItem(BartendingItems.RED_GRAPE_SEEDS, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(BartendingItems.RED_GRAPE, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(BartendingItems.GREEN_GRAPE_SEEDS, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(BartendingItems.GREEN_GRAPE, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(BartendingItems.SKINNED_GRAPE, ModelTemplates.FLAT_ITEM);
    }

}
