package ml.pluto7073.bartending.foundations.alcohol;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import ml.pluto7073.bartending.foundations.BartendingRegistries;
import ml.pluto7073.bartending.foundations.alcohol.SecondaryAlcoholicDrink.Criteria;
import ml.pluto7073.bartending.foundations.item.PourableBottleItem;
import ml.pluto7073.bartending.foundations.step.AlternativeBrewerStep;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.bartending.foundations.step.BrewerStep;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AlcoholicDrink {

    private final BrewerStep[] steps;
    private final int standardProof;
    private final float standardOunces;
    private final int color;
    private final Item bottle;
    private final Supplier<Boolean> isVisible;
    private final String englishName;
    private final HashMap<Item, Integer> itemToAmountMap;
    private final List<Pair<Optional<Criteria<AlcoholicDrink>>, BrewerStep[]>> otherMethods;

    protected AlcoholicDrink(BrewerStep[] steps, List<Pair<Optional<Criteria<AlcoholicDrink>>, BrewerStep[]>> otherMethods, int standardProof, float standardOunces, int color, Item bottle, Supplier<Boolean> isVisible, String englishName) {
        this.steps = steps;
        this.standardProof = standardProof;
        this.standardOunces = standardOunces;
        this.color = color;
        this.bottle = bottle;
        this.isVisible = isVisible;
        this.englishName = englishName;
        itemToAmountMap = new HashMap<>();
        this.otherMethods = ImmutableList.copyOf(otherMethods);
    }

    public void addItem(Item item, int mb) {
        itemToAmountMap.put(item, mb);
    }

    public void forEach(BiConsumer<Item, Integer> consumer) {
        itemToAmountMap.forEach(consumer);
    }

    public BrewerStep[] steps() {
        return steps;
    }

    public int standardProof() {
        return standardProof;
    }

    public float standardOunces() {
        return standardOunces;
    }

    public int color() {
        return color;
    }

    public Item bottle() {
        return bottle;
    }

    public boolean isVisible() {
        return isVisible.get();
    }

    public String englishName() {
        return englishName;
    }

    public boolean matches(ItemStack stack, Level level) {
        ListTag steps = stack.getOrCreateTag().getList("BrewingSteps", CompoundTag.TAG_COMPOUND);

        if (stack.getItem() instanceof PourableBottleItem) {
            outer:
            for (Pair<Optional<Criteria<AlcoholicDrink>>, BrewerStep[]> pair : otherMethods) {
                if (pair.getFirst().isPresent()) {
                    if (!pair.getFirst().get().test(((PourableBottleItem) stack.getItem()).drink)) continue;
                }
                if (steps.size() != pair.getSecond().length) continue;
                for (int i = 0; i < steps.size(); i++) {
                    if (!pair.getSecond()[i].matches(steps.getCompound(i), level)) continue outer;
                }
                return true;
            }
        }

        if (steps.size() != this.steps.length) return false;

        for (int i = 0; i < steps.size(); i++) {
            BrewerStep step = this.steps[i];
            CompoundTag data = steps.getCompound(i);
            if (!step.matches(data, level)) return false;
        }

        return true;
    }

    /**
     * Determines whether a list of steps <em>might</em> match this alcoholic drink
     * @param stack The item stack to test against
     * @param level The current level
     * @return <code>true</code> if the steps present are in the correct order and match even if a few steps at the end are missing<br>
     * <code>false</code> if a step provided doesn't match an existing step or there are more steps than required for this drink
     */
    public boolean mightMatch(ItemStack stack, Level level) {
        ListTag steps = stack.getOrCreateTag().getList("BrewingSteps", CompoundTag.TAG_COMPOUND);
        if (stack.getItem() instanceof PourableBottleItem) {
            outer:
            for (Pair<Optional<Criteria<AlcoholicDrink>>, BrewerStep[]> pair : otherMethods) {
                if (pair.getFirst().isPresent()) {
                    if (!pair.getFirst().get().test(((PourableBottleItem) stack.getItem()).drink)) continue;
                }
                if (steps.size() != pair.getSecond().length) continue;
                for (int i = 0; i < steps.size(); i++) {
                    BrewerStep step = pair.getSecond()[i];
                    CompoundTag data = steps.getCompound(i);
                    if (!step.mightMatch(data, level)) continue outer;
                }
                return true;
            }
        }
        if (steps.size() > this.steps.length) return false;
        for (int i = 0; i < steps.size(); i++) {
            BrewerStep step = this.steps[i];
            CompoundTag data = steps.getCompound(i);
            if (!step.mightMatch(data, level)) return false;
        }

        return true;
    }

    /**
     * Returns the total deviation in grams of alcohol from the standard recipe.
     * This should only be performed on a list of steps that is known to match this Alcoholic drink, as this method does not check whether the list of steps is correct or not
     * @param stack The <code>ItemStack</code> of the concoction
     * @param level The current Level
     * @return The amount of alcohol in grams to be added or removed from the standard amount
     */
    public int getTotalDeviation(ItemStack stack, Level level) {
        ListTag steps = stack.getOrCreateTag().getList("BrewingSteps", CompoundTag.TAG_COMPOUND);
        int deviation = 0;
        float standard = BrewingUtil.getStandardAlcohol(this);
        outer: for (Pair<Optional<Criteria<AlcoholicDrink>>, BrewerStep[]> pair : otherMethods) {
            deviation = 0;
            if (pair.getFirst().isPresent()) {
                if (!pair.getFirst().get().test(((PourableBottleItem) stack.getItem()).drink)) continue;
            }
            if (steps.size() != pair.getSecond().length) continue;
            for (int i = 0; i < steps.size(); i++) {
                BrewerStep step = pair.getSecond()[i];
                CompoundTag data = steps.getCompound(i);
                if (!step.mightMatch(data, level)) continue outer;
                deviation += step.getDeviation(data, standard, level);
            }
            if (BrewingUtil.getProof(this, standard + deviation) > 190) {
                return (int) (BrewingUtil.getAlcohol(this, standardOunces) - standard);
            }
            return deviation;
        }
        for (int i = 0; i < steps.size(); i++) {
            BrewerStep step = this.steps[i];
            CompoundTag data = steps.getCompound(i);
            deviation += step.getDeviation(data, standard, level);
        }
        if (BrewingUtil.getProof(this, standard + deviation) > 190) {
            return (int) (BrewingUtil.getAlcohol(this, standardOunces) - standard);
        }
        return deviation;
    }

    public String getLanguageKey() {
        return Objects.requireNonNull(BartendingRegistries.ALCOHOLIC_DRINK.getKey(this)).toLanguageKey("alcohol");
    }

    public static SecondaryBuilder secondaryBuilder(Criteria<AlcoholicDrink> base) {
        return new SecondaryBuilder(base);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class SecondaryBuilder extends Builder {

        private final Criteria<AlcoholicDrink> baseDrink;

        private SecondaryBuilder(Criteria<AlcoholicDrink> baseDrink) {
            this.baseDrink = baseDrink;
        }

        @Override
        public AlcoholicDrink build() {
            return new SecondaryAlcoholicDrink(baseDrink, alternatives, steps.toArray(BrewerStep[]::new), standardProof, standardOunces, color, bottle, isVisible, name);
        }
    }

    public static class Builder {

        final List<BrewerStep> steps = new ArrayList<>();
        final List<Pair<Optional<Criteria<AlcoholicDrink>>, BrewerStep[]>> alternatives = new ArrayList<>();
        int standardProof = 0, color = 0xFFFFFF;
        float standardOunces = 0f;
        Item bottle = Items.GLASS_BOTTLE;
        Supplier<Boolean> isVisible = () -> true;
        String name = "[UNNAMED ALCOHOLIC DRINK]";

        private Builder() {}

        public Builder addAlternativeSteps(Optional<Criteria<AlcoholicDrink>> base, BrewerStep... steps) {
            alternatives.add(new Pair<>(base, steps));
            return this;
        }

        public Builder addStep(BrewerStep step) {
            steps.add(step);
            return this;
        }

        public Builder proof(int proof) {
            standardProof = proof;
            return this;
        }

        public Builder ounces(float ounces) {
            standardOunces = ounces;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder bottle(Item bottle) {
            this.bottle = bottle;
            return this;
        }

        public Builder setVisibleWhen(Supplier<Boolean> isVisible) {
            this.isVisible = isVisible;
            return this;
        }

        public Builder name(String englishName) {
            this.name = englishName;
            return this;
        }

        public AlcoholicDrink build() {
            return new AlcoholicDrink(steps.toArray(BrewerStep[]::new), alternatives, standardProof, standardOunces, color, bottle, isVisible, name);
        }

    }

}
