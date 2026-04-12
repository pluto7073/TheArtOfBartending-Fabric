package ml.pluto7073.bartending.compat.fruitfulfun;

import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.step.*;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import snownee.fruits.CoreModule;

import java.util.Optional;

public final class FruityAlcoholicDrinkManager {

    public static AlcoholicDrink createOrangeLiqueur() {
        AlcoholicDrink.Builder builder = AlcoholicDrink.builder().proof(80).ounces(1.5f)
                .name("Orange Liqueur").bottle(BartendingItems.LIQUOR_BOTTLE).color(0xe0d2ba)
                .setVisibleWhen(() -> FabricLoader.getInstance().isModLoaded("fruitfulfun"))
                .addStep(new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(Items.BEETROOT), 10, 3)
                        .setTicks(24000).setLeeway(12000).build())
                .addStep(new DistillingBrewerStep(2, 1));

        BrewerStep adding;

        @SuppressWarnings("Convert2MethodRef")
        Optional<Item> orange = BrewingUtil.supplyIfLoaded("fruitfulfun", () -> () -> CoreModule.ORANGE.getOrCreate());

        if (orange.isPresent()) {
            adding = new AddingItemBrewerStep(() -> Ingredient.of(orange.get()), 10, 3);
        } else adding = new AlternativeBrewerStep();

        return builder.addStep(adding).addStep(new DistillingBrewerStep(3, 2)).build();
    }

}
