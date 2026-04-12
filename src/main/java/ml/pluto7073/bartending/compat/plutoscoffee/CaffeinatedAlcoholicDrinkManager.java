package ml.pluto7073.bartending.compat.plutoscoffee;

import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.step.AddingItemBrewerStep;
import ml.pluto7073.bartending.foundations.step.AlternativeBrewerStep;
import ml.pluto7073.bartending.foundations.step.BarrelAgingBrewerStep;
import ml.pluto7073.bartending.foundations.step.BarrelPredicate;
import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public final class CaffeinatedAlcoholicDrinkManager {

    public static AlcoholicDrink createCoffeeLiqueur() {
        AlcoholicDrink.Builder builder = AlcoholicDrink.secondaryBuilder(() -> List.of(AlcoholicDrinks.VODKA, AlcoholicDrinks.RUM), true).name("Coffee Liqueur")
                .addStep(new AddingItemBrewerStep(() -> Ingredient.of(Items.SUGAR), 3, 0))
                .setVisibleWhen(() -> FabricLoader.getInstance().isModLoaded("plutoscoffee"))
                .color(0x211304).ounces(1.5f).proof(70).bottle(BartendingItems.LIQUOR_BOTTLE);

        if (FabricLoader.getInstance().isModLoaded("plutoscoffee")) {
            builder.addStep(new AddingItemBrewerStep(() -> Ingredient.of(BartendingTags.PLUTOSCOFFEE_ROASTED_COFFEE_BEANS), 16, 0));
        } else {
            builder.addStep(new AlternativeBrewerStep());
        }

        return builder.addStep(new BarrelAgingBrewerStep(BarrelPredicate.ANY, 0.1f)).build();
    }

}
