package ml.pluto7073.bartending.foundations.step;

import com.mojang.datafixers.util.Pair;
import ml.pluto7073.bartending.foundations.config.BartendingCommonConfig;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.plutonium.PlutoniumConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.*;

public class FermentingBrewerStep implements BrewerStep {

    public static final String TYPE_ID = "initial_fermenting";

    public final HashMap<Ingredient, Pair<Integer, Integer>> ingredients;
    public final int wantedTicks;
    private final int tickLeeway;

    private FermentingBrewerStep(HashMap<Ingredient, Pair<Integer, Integer>> ingredients, int ticks, int leeway) {
        this.ingredients = ingredients;
        this.wantedTicks = ticks;
        this.tickLeeway = leeway;
    }

    @Override
    public boolean mightMatch(CompoundTag data, Level level) {
        if (!TYPE_ID.equals(data.getString("type"))) return false;
        int ticks = data.getInt("ticks");
        if (ticks > wantedTicks + tickLeeway) return false;
        return testInventory(data);
    }

    @Override
    public String id() {
        return TYPE_ID;
    }

    @SuppressWarnings("unchecked")
    private boolean testInventory(CompoundTag data) {
        if (data.contains("item", CompoundTag.TAG_STRING)) {
            if (ingredients.size() > 1) return false;
            Ingredient base = List.copyOf(ingredients.keySet()).get(0);
            int wantedCount = ingredients.get(base).getFirst();
            int countLeeway = ingredients.get(base).getSecond();
            ResourceLocation itemId = new ResourceLocation(data.getString("item"));
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (!base.test(new ItemStack(item))) return false;
            int count = data.getInt("count");
            return count >= wantedCount - countLeeway && count <= wantedCount + countLeeway;
        } else if (data.contains("Items", Tag.TAG_LIST)) {
            ListTag items = data.getList("Items", Tag.TAG_COMPOUND);
            if (items.size() != ingredients.size()) return false;
            HashMap<Ingredient, Pair<Integer, Integer>> map =
                    (HashMap<Ingredient, Pair<Integer, Integer>>) ingredients.clone();
            for (Tag t : items) {
                if (!(t instanceof CompoundTag item)) continue;
                ItemStack stack = BrewingUtil.stackFromTag(item);
                Optional<Ingredient> match = findMatching(stack, List.copyOf(map.keySet()));
                if (match.isEmpty()) return false;
                Ingredient base = match.get();
                int count = map.get(base).getFirst();
                int leeway = map.get(base).getSecond();
                if (stack.getCount() >= count - leeway &&
                        stack.getCount() <= count + leeway) {
                    map.remove(base);
                } else return false;
            }
            return map.isEmpty();
        } else return false;
    }


    @Override
    public boolean matches(CompoundTag data, Level level) {
        if (!TYPE_ID.equals(data.getString("type"))) return false;
        int ticks = data.getInt("ticks");
        if (Math.abs(wantedTicks - ticks) > tickLeeway) return false;
        return testInventory(data);
    }

    @Override
    public int getDeviation(CompoundTag data, float standard, Level level) {
        int fromCount = 0;
        if (data.contains("item", Tag.TAG_STRING)) {
            int count = data.getInt("count");
            int wantedCount = ingredients.get(new ArrayList<>(ingredients.keySet()).get(0)).getFirst();
            fromCount = Math.round(Mth.clampedMap(count, 0, wantedCount, -standard, 0));
            if (count > wantedCount) fromCount = 0;
        } else if (data.contains("Items", Tag.TAG_LIST)) {
            ListTag list = data.getList("Items", Tag.TAG_COMPOUND);
            for (Tag t : list) {
                if (!(t instanceof CompoundTag c)) continue;
                ItemStack stack = BrewingUtil.stackFromTag(c);
                Ingredient base = findMatching(stack, List.copyOf(this.ingredients.keySet()))
                        .orElseThrow(IllegalStateException::new);
                int count = ingredients.get(base).getFirst();
                int tempCount = Math.round(Mth.clampedMap(stack.getCount(), 0, count, -standard, 0));
                if (stack.getCount() > count) tempCount = 0;
                if (-tempCount > -fromCount) fromCount = tempCount;
            }
        }

        int ticks = data.getInt("ticks");
        int fromTicks = Math.round(Mth.clampedMap(ticks, wantedTicks - tickLeeway,
                wantedTicks + tickLeeway, -0.05f, 0.05f) * standard);
        if (Math.abs(fromTicks) > Math.abs(fromCount)) return fromTicks;
        else if (Math.abs(fromCount) > Math.abs(fromTicks)) return fromCount;
        else return Math.min(fromTicks, fromCount);
    }

    @Override
    public void createExactMatchData(CompoundTag tag, Level level) {
        tag.putInt("ticks", (int) (wantedTicks * BartendingCommonConfig.INSTANCE.fermentationTimeScale));
        if (ingredients.size() <= 1) {
            Ingredient i = ingredients.keySet().iterator().next();
            tag.putString("item", BuiltInRegistries.ITEM.getKey(i.getItems()[0].getItem()).toString());
            tag.putInt("count", ingredients.get(i).getFirst());
        } else {
            NonNullList<ItemStack> inv = NonNullList.withSize(4, ItemStack.EMPTY);
            int index = 0;
            for (Ingredient i : ingredients.keySet()) {
                int count = ingredients.get(i).getFirst();
                ItemStack stack = i.getItems()[0].copy();
                stack.setCount(count);
                inv.set(index, stack);
                index++;
            }
            BrewingUtil.saveAllItems(tag, inv, false);
        }
    }

    public static void appendInProgressText(CompoundTag data, List<Component> tooltips) {
        int ticks = data.getInt("ticks");
        if (data.contains("item", Tag.TAG_STRING)) {
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(data.getString("item")));
            int count = data.getInt("count");
            appendItem(item, count, tooltips);
        }
        if (data.contains("Items", Tag.TAG_LIST)) {
            for (Tag t : data.getList("Items", Tag.TAG_COMPOUND)) {
                if (!(t instanceof CompoundTag item)) continue;
                ItemStack stack = BrewingUtil.stackFromTag(item);
                appendItem(stack.getItem(), stack.getCount(), tooltips);
            }
        }
        String time = BrewingUtil.getTimeString(ticks / 20);
        tooltips.add(Component.translatable("tooltip.bartending.boiling_in_progress", time).withStyle(ChatFormatting.GRAY));
    }

    private static void appendItem(Item item, int count, List<Component> tooltips) {
        tooltips.add(Component.literal(count + "x ").append(Component.translatable(item.getDescriptionId()))
                .withStyle(ChatFormatting.GRAY));
    }

    private static Optional<Ingredient> findMatching(ItemStack stack, List<Ingredient> list) {
        for (Ingredient item : list) {
            if (item.test(stack)) return Optional.of(item);
        }
        return Optional.empty();
    }

    public static class Builder {

        private final HashMap<Ingredient, Pair<Integer, Integer>> ingredients;
        private int wantedTicks, tickLeeway;

        public Builder() {
            ingredients = new HashMap<>();
            wantedTicks = 6000;
            tickLeeway = 1200;
        }

        public Builder setTicks(int ticks) {
            wantedTicks = ticks;
            return this;
        }

        public Builder setLeeway(int leeway) {
            tickLeeway = leeway;
            return this;
        }

        public Builder addIngredient(Ingredient ingredient, int count, int leeway) {
            ingredients.put(ingredient, new Pair<>(count, leeway));
            return this;
        }

        public Builder addIngredient(Ingredient ingredient, int count) {
            return addIngredient(ingredient, count, 5);
        }

        public FermentingBrewerStep build() {
            return new FermentingBrewerStep(ingredients, wantedTicks, tickLeeway);
        }

    }

}
