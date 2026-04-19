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
import ml.pluto7073.bartending.foundations.alcohol.SecondaryAlcoholicDrink;
import ml.pluto7073.bartending.foundations.step.*;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;
import java.util.function.*;

public class AlcoholBrewingCategory implements DisplayCategory<AlcoholBrewingDisplay> {

    @Override
    public CategoryIdentifier<? extends AlcoholBrewingDisplay> getCategoryIdentifier() {
        return TheArtOfREI.ALCOHOL_BREWING;
    }

    @Override
    public List<Widget> setupDisplay(AlcoholBrewingDisplay display, Rectangle bounds) {
        ArrayList<Widget> widgets = new ArrayList<>();
        int y = bounds.y + 8;

        widgets.add(Widgets.createRecipeBase(bounds));

        if (display.drink instanceof SecondaryAlcoholicDrink secondary) {
            y += setupSecondaryBaseDisplay(widgets, y, secondary, bounds);
        }

        Stack<DisplayBuilderFunction<?>> setupStack = new Stack<>();
        for (BrewerStep step : display.drink.steps()) {
            DisplayBuilderFunction<?> newFunc = null;
            if (step instanceof FermentingBrewerStep boiling) {
                newFunc = new FermentingBuilderFunction(boiling);
            } else if (step instanceof BarrelAgingBrewerStep fermenting) {
                newFunc = new AgingBuilderFunction(fermenting);
            } else if (step instanceof DistillingBrewerStep distilling) {
                newFunc = new DistillingBuilderFunction(distilling.runs());
            } else if (step instanceof AddingItemBrewerStep adding) {
                newFunc = new AddItemBuilderFunction(adding);
            }
            DisplayBuilderFunction<?> firstFunc;
            if (!setupStack.isEmpty() && (firstFunc = setupStack.pop()) != null) {
                if (newFunc != null && firstFunc.canCombine(newFunc)) {
                    newFunc = firstFunc.apply(newFunc);
                } else {
                    y += firstFunc.create(widgets, y, bounds);
                }
            }
            setupStack.push(newFunc);
        }

        if (!setupStack.isEmpty()) {
            for (DisplayBuilderFunction<?> func : setupStack) {
                y += func.create(widgets, y, bounds);
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

    private static int setupSecondaryBaseDisplay(ArrayList<Widget> widgets, int baseY, SecondaryAlcoholicDrink drink, Rectangle bounds) {
        widgets.add(new TextWidget(Component.translatable("tooltip.bartending.base"), ChatFormatting.WHITE, new Point(bounds.x + 8, baseY + 2)));
        widgets.add(Widgets.createSlot(new Point(Minecraft.getInstance().font.width(Component.translatable("tooltip.bartending.base")) + bounds.x + 18, baseY))
                .entries(EntryIngredients.ofIngredient(Ingredient.of(drink.getBases().stream().map(AlcoholicDrinks::getFinalDrink).map(ItemStack::new)))).markInput());
        return 20;
    }

    private interface DisplayBuilderFunction<Self extends DisplayBuilderFunction<?>> extends Function<DisplayBuilderFunction<?>, Self> {

        /**
         * Creates the new widget
         * @return the change in Y starting position for the next widget
         */
        int create(ArrayList<Widget> widgets, int baseY, Rectangle bounds);

        default boolean canCombine(DisplayBuilderFunction<?> other) {
            return getClass() == other.getClass();
        }

        /**
         * Combines another function with this one
         * @return Will always return a new instance
         */
        @Override
        Self apply(DisplayBuilderFunction<?> other);
    }

    private record FermentingBuilderFunction(FermentingBrewerStep step) implements DisplayBuilderFunction<FermentingBuilderFunction> {

        @Override
        public int create(ArrayList<Widget> widgets, int baseY, Rectangle bounds) {
            int tx = bounds.x + 8;
            for (Map.Entry<Ingredient, Pair<Integer, Integer>> e : step.ingredients.entrySet()) {
                List<ItemStack> stacks = new ArrayList<>();

                for (ItemStack itemStack : e.getKey().getItems()) {
                    ItemStack stack = itemStack.copy();
                    stack.setCount(e.getValue().getFirst());
                    stacks.add(stack);
                }

                widgets.add(Widgets.createSlot(new Point(tx, baseY)).entries(EntryIngredients.ofItemStacks(stacks)).markInput());

                tx += 18;
            }

            widgets.add(Widgets.createTexturedWidget(BoilerScreen.TEXTURE, new Rectangle(bounds.x + 8, baseY + 20, 17, 11), 176, 0));
            widgets.add(new TextWidget(Component.translatable("tooltip.bartending.boiling_in_progress", BrewingUtil.getTimeString(step.wantedTicks / 20)),
                    ChatFormatting.WHITE, new Point(bounds.x + 28, baseY + 24)));
            return 36;
        }

        @Override
        public boolean canCombine(DisplayBuilderFunction<?> other) {
            return false;
        }

        @Override
        public FermentingBuilderFunction apply(DisplayBuilderFunction<?> other) {
            return this;
        }
    }

    private record AgingBuilderFunction(BarrelAgingBrewerStep step) implements DisplayBuilderFunction<AgingBuilderFunction> {

        @Override
        public AgingBuilderFunction apply(DisplayBuilderFunction<?> agingBuilderFunction) {
            return this;
        }

        @Override
        public int create(ArrayList<Widget> widgets, int baseY, Rectangle bounds) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 8, baseY))
                    .entries(EntryIngredients.ofIngredient(step.predicate.asIngredient())).markInput());
            widgets.add(new TextWidget(Component.translatable("tooltip.bartending.fermenting_for", step.years),
                    ChatFormatting.WHITE, new Point(bounds.x + 28, baseY + 2)));
            return 20;
        }

        @Override
        public boolean canCombine(DisplayBuilderFunction<?> other) {
            return false;
        }
    }

    private record DistillingBuilderFunction(int runs) implements DisplayBuilderFunction<DistillingBuilderFunction> {

        @Override
        public int create(ArrayList<Widget> widgets, int baseY, Rectangle bounds) {
            Component text = Component.translatable("tooltip.bartending.distilled_times", runs);
            widgets.add(new TextWidget(text, ChatFormatting.WHITE, new Point(bounds.x + 8, baseY)));
            return 12;
        }

        @Override
        public DistillingBuilderFunction apply(DisplayBuilderFunction<?> other) {
            if (!(other instanceof DistillingBuilderFunction distilling)) return new DistillingBuilderFunction(runs);
            return new DistillingBuilderFunction(runs + distilling.runs());
        }
    }

    private record AddItemBuilderFunction(List<Pair<Integer, Ingredient>> ingredients) implements DisplayBuilderFunction<AddItemBuilderFunction> {

        private AddItemBuilderFunction(AddingItemBrewerStep step) {
            this(List.of(new Pair<>(step.amount(), step.ingredient().get())));
        }

        @Override
        public int create(ArrayList<Widget> widgets, int baseY, Rectangle bounds) {
            widgets.add(new TextWidget(Component.translatable("tooltip.bartending.added"), ChatFormatting.WHITE, new Point(bounds.x + 8, baseY)));
            for (int i = 0; i < ingredients.size(); i++) {
                Pair<Integer, Ingredient> ingredient = ingredients.get(i);
                widgets.add(Widgets.createSlot(new Point(Minecraft.getInstance().font.width(Component.translatable("tooltip.bartending.added")) + bounds.x + 18 * (i % 6) + 18, baseY + 18 * (i / 6)))
                        .entries(EntryIngredients.ofIngredient(Ingredient.of(Arrays.stream(ingredient.getSecond().getItems()).map(stack -> stack.copyWithCount(ingredient.getFirst()))))).markInput());
            }
            return 20 + 18 * (ingredients.size() / 6);
        }

        @Override
        public AddItemBuilderFunction apply(DisplayBuilderFunction<?> other) {
            if (!(other instanceof AddItemBuilderFunction addItem)) return new AddItemBuilderFunction(ingredients);
            return new AddItemBuilderFunction(createNewIngredient(addItem));
        }

        private List<Pair<Integer, Ingredient>> createNewIngredient(AddItemBuilderFunction other) {
            List<Pair<Integer, Ingredient>> newList = new ArrayList<>(ingredients);
            newList.addAll(other.ingredients);
            return newList;
        }
    }

}
