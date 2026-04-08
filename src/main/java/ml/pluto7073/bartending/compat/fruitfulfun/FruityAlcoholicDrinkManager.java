package ml.pluto7073.bartending.compat.fruitfulfun;

import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.step.AlternativeBrewerStep;
import ml.pluto7073.bartending.foundations.step.FermentingBrewerStep;
import ml.pluto7073.bartending.foundations.step.BrewerStep;
import ml.pluto7073.bartending.foundations.step.DistillingBrewerStep;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import snownee.fruits.CoreModule;

import java.util.Optional;

public final class FruityAlcoholicDrinkManager {

    public static AlcoholicDrink createOrangeLiqueur() {
        AlcoholicDrink.Builder builder = new AlcoholicDrink.Builder().proof(80).ounces(1.5f)
                .name("Orange Liqueur").bottle(BartendingItems.LIQUOR_BOTTLE).color(0xe0d2ba)
                .setVisibleWhen(() -> FabricLoader.getInstance().isModLoaded("fruitfulfun"));

        BrewerStep boiling;

        @SuppressWarnings("Convert2MethodRef")
        Optional<Item> orange = BrewingUtil.supplyIfLoaded("fruitfulfun", () -> () -> CoreModule.ORANGE.getOrCreate());

        if (orange.isPresent()) {
            boiling = new FermentingBrewerStep.Builder().addIngredient(Ingredient.of(orange.get()), 40, 10)
                    .setTicks(24000).setLeeway(6000).build();
        } else boiling = new AlternativeBrewerStep();

        return builder.addStep(boiling).addStep(new DistillingBrewerStep(3, 2)).build();
    }

}
