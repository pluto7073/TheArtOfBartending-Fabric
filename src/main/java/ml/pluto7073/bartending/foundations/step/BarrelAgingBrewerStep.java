package ml.pluto7073.bartending.foundations.step;

import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BarrelAgingBrewerStep implements BrewerStep {

    public static final String TYPE_ID = "barrel_aging";

    private static final float DEVIATION_CONST = (float) (1 / (1 + Math.exp(-1)));

    public final BarrelPredicate predicate;
    public final float years;
    public final float yearMin;

    public BarrelAgingBrewerStep(BarrelPredicate predicate, float years) {
        this(predicate, years, 0);
    }

    public BarrelAgingBrewerStep(BarrelPredicate predicate, float years, float lower) {
        this.predicate = predicate;
        this.years = years;
        if (lower < 0) {
            lower = 0;
        }
        this.yearMin = years + lower;
    }

    @Override
    public boolean mightMatch(CompoundTag data, Level level) {
        if (!TYPE_ID.equals(data.getString("type"))) return false;
        ResourceLocation barrelId = new ResourceLocation(data.getString("barrel"));
        return predicate.test(BuiltInRegistries.BLOCK.get(barrelId));
    }

    @Override
    public String id() {
        return TYPE_ID;
    }

    @Override
    public boolean matches(CompoundTag data, Level level) {
        if (!TYPE_ID.equals(data.getString("type"))) return false;
        ResourceLocation barrelId = new ResourceLocation(data.getString("barrel"));
        if (!predicate.test(BuiltInRegistries.BLOCK.get(barrelId))) return false;
        int ticks = data.getInt("ticks");
        float years = (float) ticks / BrewingUtil.getConfig(level).yearLengthTicks;
        if (years <= 0) return false;
        return years >= yearMin;
    }

    @Override
    public int getDeviation(CompoundTag data, float standard, Level level) {
        int ticks = data.getInt("ticks");
        float years = ticks / (float) BrewingUtil.getConfig(level).yearLengthTicks;

        return Math.round((1f / (float) (1 + Math.exp(-years / (double) this.years)) - DEVIATION_CONST) * standard);
    }

    @Override
    public void createExactMatchData(CompoundTag tag, Level level) {
        tag.putString("barrel", BuiltInRegistries.BLOCK.getKey(predicate.first()).toString());
        tag.putInt("ticks", (int) (BrewingUtil.getConfig(level).yearLengthTicks * years));
    }

    public static void appendInProgressText(CompoundTag data, List<Component> tooltips, Level level) {
        int ticks = data.getInt("ticks");
        Block barrel = BuiltInRegistries.BLOCK.get(new ResourceLocation(data.getString("barrel")));
        tooltips.add(Component.translatable("tooltip.bartending.fermenting_in").append(Component.translatable(barrel.getDescriptionId()))
                .withStyle(ChatFormatting.GRAY));
        if (level == null) return;
        String time = "" + (ticks / BrewingUtil.getConfig(level).yearLengthTicks);
        tooltips.add(Component.translatable("tooltip.bartending.fermenting_for", time).withStyle(ChatFormatting.GRAY));
    }

}
