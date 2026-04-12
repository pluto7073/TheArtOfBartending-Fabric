package ml.pluto7073.bartending.foundations.step;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.function.Supplier;

public class AddingItemBrewerStep implements BrewerStep {

    public static final String TYPE_ID = "add_item";

    public final Supplier<Ingredient> ingredient;
    public final int amount;
    private final int leeway;

    public AddingItemBrewerStep(Supplier<Ingredient> ingredient, int amount, int leeway) {
        this.ingredient = ingredient;
        this.amount = amount;
        this.leeway = leeway;
    }

    @Override
    public boolean mightMatch(CompoundTag data, Level level) {
        if (!TYPE_ID.equals(data.getString("type"))) return false;
        return ingredient.get().test(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(data.getString("item")))));
    }

    @Override
    public boolean matches(CompoundTag data, Level level) {
        if (!TYPE_ID.equals(data.getString("type"))) return false;
        int count = data.getInt("count");
        if (count < amount - leeway || count > amount + leeway) {
            return false;
        }
        return ingredient.get().test(new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(data.getString("item")))));
    }

    @Override
    public int getDeviation(CompoundTag data, float standard, Level level) {
        float diff = data.getInt("count") - amount;
        float avgDiff = diff / leeway;
        return Math.round((0.0625f) * avgDiff);
    }

    @Override
    public void createExactMatchData(CompoundTag tag, Level level) {
        ItemStack[] items = ingredient.get().getItems();
        if (items.length == 0) return;
        tag.putString("type", TYPE_ID);
        tag.putInt("count", leeway);
        tag.putString("item", BuiltInRegistries.ITEM.getKey(ingredient.get().getItems()[0].getItem()).toString());
    }

    @Override
    public String id() {
        return TYPE_ID;
    }

    public static void appendInProgressText(CompoundTag data, List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.bartending.added").append(BuiltInRegistries.ITEM.get(new ResourceLocation(data.getString("item"))).getDescription())
                .append(Component.literal(" "))
                .append(Component.literal("x" + data.getInt("count"))).withStyle(ChatFormatting.GRAY));
    }

}
