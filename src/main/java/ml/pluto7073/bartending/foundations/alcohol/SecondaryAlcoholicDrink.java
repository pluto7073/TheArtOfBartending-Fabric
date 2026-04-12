package ml.pluto7073.bartending.foundations.alcohol;

import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.foundations.item.PourableBottleItem;
import ml.pluto7073.bartending.foundations.step.BrewerStep;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SecondaryAlcoholicDrink extends AlcoholicDrink {

    private final Criteria<AlcoholicDrink> base;

    protected SecondaryAlcoholicDrink(Criteria<AlcoholicDrink> base, BrewerStep[] steps, int standardProof, float standardOunces, int color, Item bottle, Supplier<Boolean> isVisible, String englishName) {
        super(steps, standardProof, standardOunces, color, bottle, isVisible, englishName);
        for (AlcoholicDrink drink : base.get()) {
            if (!AlcoholicDrinks.BASES.contains(drink)) {
                AlcoholicDrinks.BASES.add(drink);
            }
        }
        this.base = base;
    }

    @Override
    public boolean matches(ItemStack stack, Level level) {
        if (!(stack.getItem() instanceof PourableBottleItem pourable)) {
            return false;
        }
        if (!base.test(pourable.drink)) {
            return false;
        }
        return super.matches(stack, level);
    }

    @Override
    public boolean mightMatch(ItemStack stack, Level level) {
        if (!(stack.getItem() instanceof PourableBottleItem pourable)) {
            return false;
        }
        if (!base.test(pourable.drink)) {
            return false;
        }
        return super.mightMatch(stack, level);
    }

    @Override
    public int getTotalDeviation(ItemStack stack, Level level) {
        return super.getTotalDeviation(stack, level) + BrewingUtil.getAlcoholDeviation(stack);
    }

    @FunctionalInterface
    public interface Criteria<T> extends Predicate<T>, Supplier<List<T>> {

        @Override
        default boolean test(T t) {
            for (T t1 : get()) {
                if (t1 == t || t1.equals(t)) {
                    return true;
                }
            }
            return false;
        }
    }

}
