package ml.pluto7073.bartending.foundations.step;

import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class BottleFermentingBrewerStep implements BrewerStep {

    public static final String TYPE_ID = "extra_fermenting";

    private final int minYears;
    private final BarrelPredicate barrel;
    private final Supplier<Item> baseItem;

    public BottleFermentingBrewerStep(int minYears, BarrelPredicate barrel, Supplier<Item> baseItem) {
        this.minYears = minYears;
        this.barrel = barrel;
        this.baseItem = baseItem;
    }

    @Override
    public boolean matches(CompoundTag data, Level level) {
        ResourceLocation barrelId = new ResourceLocation(data.getString("barrel"));
        if (!barrel.test(BuiltInRegistries.BLOCK.get(barrelId))) return false;
        int ticks = data.getInt("ticks");
        int years = ticks / BrewingUtil.getConfig(level).yearLengthTicks;
        return years >= minYears;
    }

    @Override
    public int getDeviation(CompoundTag data, float standard, Level level) {
        int ticks = data.getInt("ticks");
        int years = ticks / BrewingUtil.getConfig(level).yearLengthTicks;
        return (int) ((1 - Math.exp(-(years - minYears) / (8f * minYears))) * standard);
    }

    @Override
    public void createExactMatchData(CompoundTag tag, Level level) {}

    public boolean testItem(ItemStack stack) {
        return stack.is(baseItem.get());
    }

    @Override
    public String id() {
        return TYPE_ID;
    }
}
