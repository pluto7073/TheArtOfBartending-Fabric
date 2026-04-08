package ml.pluto7073.bartending.compat.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import ml.pluto7073.bartending.compat.rei.TheArtOfREI;
import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.step.FermentingBrewerStep;
import ml.pluto7073.bartending.foundations.step.BrewerStep;
import ml.pluto7073.bartending.foundations.step.BarrelAgingBrewerStep;

import java.util.ArrayList;
import java.util.List;

public class AlcoholBrewingDisplay extends BasicDisplay {

    public final AlcoholicDrink drink;

    public AlcoholBrewingDisplay(AlcoholicDrink drink) {
        super(getAllIngredientsForDrink(drink), List.of(EntryIngredients.of(AlcoholicDrinks.getFinalDrink(drink))), AlcoholicDrinks.getOptionalKey(drink));
        this.drink = drink;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return TheArtOfREI.ALCOHOL_BREWING;
    }

    private static List<EntryIngredient> getAllIngredientsForDrink(AlcoholicDrink drink) {
        ArrayList<EntryIngredient> list = new ArrayList<>();
        for (BrewerStep step : drink.steps()) {
            if (step instanceof FermentingBrewerStep boiling) {
                list.addAll(boiling.ingredients.keySet().stream().map(EntryIngredients::ofIngredient).toList());
            } else if (step instanceof BarrelAgingBrewerStep fermenting) {
                list.addAll(BartendingBlocks.BARRELS.values().stream().filter(fermenting.predicate).map(EntryIngredients::of).toList());
            }
        }
        return list;
    }

}
