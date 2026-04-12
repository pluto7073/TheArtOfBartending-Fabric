package ml.pluto7073.bartending.compat.rei.category;

import com.mojang.datafixers.util.Pair;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import ml.pluto7073.bartending.client.gui.BoilerScreen;
import ml.pluto7073.bartending.compat.rei.TextWidget;
import ml.pluto7073.bartending.compat.rei.TheArtOfREI;
import ml.pluto7073.bartending.compat.rei.display.AlcoholBrewingDisplay;
import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.alcohol.SecondaryAlcoholicDrink;
import ml.pluto7073.bartending.foundations.step.*;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class AlcoholBrewingCategory implements DisplayCategory<AlcoholBrewingDisplay> {
    @Override
    public CategoryIdentifier<? extends AlcoholBrewingDisplay> getCategoryIdentifier() {
        return TheArtOfREI.ALCOHOL_BREWING;
    }

    @Override
    public List<Widget> setupDisplay(AlcoholBrewingDisplay display, Rectangle bounds) {
        ArrayList<Widget> widgets = new ArrayList<>();
        int y = bounds.y - 32;

        widgets.add(Widgets.createRecipeBase(bounds));

        if (display.drink instanceof SecondaryAlcoholicDrink secondary) {
            setupSecondaryBaseDisplay(widgets, y += 52, secondary, bounds);
        }

        for (BrewerStep step : display.drink.steps()) {
            if (step instanceof FermentingBrewerStep boiling) {
                setupBoilingDisplay(widgets, y += 52, boiling, bounds);
            } else if (step instanceof BarrelAgingBrewerStep fermenting) {
                setupFermentingDisplay(widgets, y += 52, fermenting, bounds);
            } else if (step instanceof DistillingBrewerStep distilling) {
                setupDistillingDisplay(widgets, y += 40, distilling, bounds);
            } else if (step instanceof AddingItemBrewerStep adding) {
                setupAddItemDisplay(widgets, y += 52, adding, bounds);
            }
        }

        widgets.add(Widgets.createSlot(new Point(bounds.x + bounds.getWidth() - 64, bounds.getMaxY() - 32))
                .markInput().entry(EntryStacks.of(display.drink.bottle())));
        widgets.add(Widgets.createResultSlotBackground(new Point(bounds.x + bounds.getWidth() - 32, bounds.getMaxY() - 32)));
        widgets.add(Widgets.createSlot(new Point(bounds.x + bounds.getWidth() - 32, bounds.getMaxY() - 32))
                .markOutput().disableBackground().entries(display.getOutputEntries().get(0)));

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return DisplayCategory.super.getDisplayHeight() * 2 + 64;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.bartending.brewing");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(BartendingItems.RED_WINE);
    }

    private static void setupFermentingDisplay(ArrayList<Widget> widgets, int baseY, BarrelAgingBrewerStep step, Rectangle bounds) {
        widgets.add(Widgets.createRecipeBase(new Rectangle(bounds.x, baseY - 12, bounds.width, 52)));
        widgets.add(Widgets.createSlot(new Point(bounds.x + 8, baseY + 6))
                .entries(EntryIngredients.ofIngredient(step.predicate.asIngredient())).markInput());
        widgets.add(new TextWidget(Component.translatable("tooltip.bartending.fermenting_for", step.years),
                ChatFormatting.WHITE, new Point(bounds.x + 28, baseY + 8)));
    }

    private static void setupDistillingDisplay(ArrayList<Widget> widgets, int baseY, DistillingBrewerStep step, Rectangle bounds) {
        Component text = Component.translatable("tooltip.bartending.distilled_times", step.runs);
        widgets.add(new TextWidget(text, ChatFormatting.WHITE, new Point(bounds.x + 8, baseY + 8)));
    }

    private static void setupBoilingDisplay(ArrayList<Widget> widgets, int baseY, FermentingBrewerStep step, Rectangle bounds) {
        widgets.add(Widgets.createRecipeBase(new Rectangle(bounds.x, baseY - 12, bounds.width, 52)));
        int tx = bounds.x + 8;
        for (Map.Entry<Ingredient, Pair<Integer, Integer>> e : step.ingredients.entrySet()) {
            List<ItemStack> stacks = new ArrayList<>();

            for (ItemStack itemStack : e.getKey().getItems()) {
                ItemStack stack = itemStack.copy();
                stack.setCount(e.getValue().getFirst());
                stacks.add(stack);
            }

            widgets.add(Widgets.createSlot(new Point(tx, baseY)).entries(EntryIngredients.ofItemStacks(stacks)).markInput());

            tx += 20;
        }

        widgets.add(Widgets.createTexturedWidget(BoilerScreen.TEXTURE, new Rectangle(bounds.x + 8, baseY + 20, 17, 11), 176, 0));
        widgets.add(new TextWidget(Component.translatable("tooltip.bartending.boiling_in_progress", BrewingUtil.getTimeString(step.wantedTicks / 20)),
                ChatFormatting.WHITE, new Point(bounds.x + 28, baseY + 24)));
    }

    private static void setupSecondaryBaseDisplay(ArrayList<Widget> widgets, int baseY, SecondaryAlcoholicDrink drink, Rectangle bounds) {
        widgets.add(Widgets.createRecipeBase(new Rectangle(bounds.x, baseY - 12, bounds.width, 42)));
        widgets.add(new TextWidget(Component.translatable("tooltip.bartending.base"), ChatFormatting.WHITE, new Point(bounds.x + 8, baseY + 6)));
        widgets.add(Widgets.createSlot(new Point(Minecraft.getInstance().font.width(Component.translatable("tooltip.bartending.base")) + bounds.x + 18, baseY + 8))
                .entries(EntryIngredients.ofIngredient(Ingredient.of(drink.getBases().stream().map(AlcoholicDrinks::getFinalDrink).map(ItemStack::new)))).markInput());
    }

    private static void setupAddItemDisplay(ArrayList<Widget> widgets, int baseY, AddingItemBrewerStep step, Rectangle bounds) {
        widgets.add(Widgets.createRecipeBase(new Rectangle(bounds.x, baseY - 12, bounds.width, 42)));
        widgets.add(new TextWidget(Component.translatable("tooltip.bartending.added"), ChatFormatting.WHITE, new Point(bounds.x + 8, baseY + 6)));
        widgets.add(Widgets.createSlot(new Point(Minecraft.getInstance().font.width(Component.translatable("tooltip.bartending.added")) + bounds.x + 18, baseY + 8))
                .entries(EntryIngredients.ofIngredient(Ingredient.of(Arrays.stream(step.ingredient().get().getItems()).peek(stack -> stack.setCount(step.amount()))))));
    }

}
