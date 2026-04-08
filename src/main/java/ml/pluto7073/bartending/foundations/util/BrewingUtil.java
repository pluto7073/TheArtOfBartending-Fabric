package ml.pluto7073.bartending.foundations.util;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.compat.create.TheArtOfCreate;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.BartendingRegistries;
import ml.pluto7073.bartending.foundations.alcohol.AlcDisplayType;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.config.BartendingCommonConfig;
import ml.pluto7073.bartending.foundations.step.*;
import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BrewingUtil {

    @SafeVarargs
    public static <T> Stream<T> collectionsToStream(Collection<? extends T>... lists) {
        return Arrays.stream(lists).flatMap(Collection::stream);
    }

    public static BartendingCommonConfig getConfig(Level level) {
        if (level.isClientSide) {
            return TheArtOfBartending.CONFIG_TYPE.getCopy();
        } else {
            return BartendingCommonConfig.INSTANCE;
        }
    }

    public static ItemStack createConcoctionFromBaseTicks(NonNullList<ItemStack> base, int ticks) {
        CompoundTag data = new CompoundTag();
        data.putString("type", FermentingBrewerStep.TYPE_ID);
        data.putInt("ticks", ticks);
        if (isInventorySingleItem(base)) {
            data.putString("item", BuiltInRegistries.ITEM.getKey(base.get(0).getItem()).toString());
            data.putInt("count", base.get(0).getCount());
        } else {
            BrewingUtil.saveAllItems(data, base, false);
        }
        ItemStack stack = new ItemStack(BartendingItems.CONCOCTION, 1);
        ListTag list = new ListTag();
        list.add(data);
        stack.getOrCreateTag().put("BrewingSteps", list);
        return stack;
    }

    public static int getHeatedData(BlockEntity entity) {
        Level level = entity.getLevel();
        if (level == null) return 0;
        BlockState below = level.getBlockState(entity.getBlockPos().below());
        boolean heated = below.is(BlockTags.CAMPFIRES) ||
                below.is(Blocks.FIRE) ||
                below.is(Blocks.LAVA);
        boolean superheated = below.is(BartendingTags.SUPERHEATING_BLOCKS);
        if (FabricLoader.getInstance().isModLoaded("create")) {
            int heat = TheArtOfCreate.getHeatFromBlazeBurner(below);
            if (heat != -1) return heat;
        }
        return superheated ? 2 : heated ? 1 : 0;
    }

    public static <T> T either(Supplier<T> one, Supplier<T> two, Supplier<Boolean> condition) {
        if (condition.get()) {
            return two.get();
        }
        return one.get();
    }

    public static ItemStack stackFromTag(CompoundTag tag) {
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getString("id")));
        int count = tag.getInt("Count");
        CompoundTag data = null;
        if (tag.contains("tag", Tag.TAG_COMPOUND)) {
            data = tag.getCompound("tag");
            item.verifyTagAfterLoad(data);
        }
        ItemStack stack = new ItemStack(item, count);
        if (data != null) stack.setTag(data);
        return stack;
    }

    private static void saveItem(ItemStack stack, CompoundTag compoundTag) {
        ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(stack.getItem());
        compoundTag.putString("id", resourceLocation.toString());
        compoundTag.putInt("Count", stack.getCount());
        compoundTag.put("tag", stack.getOrCreateTag().copy());
    }

    public static void saveAllItems(CompoundTag tag, NonNullList<ItemStack> list, boolean saveEmpty) {
        ListTag listTag = new ListTag();

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemStack = list.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                saveItem(itemStack, compoundTag);
                listTag.add(compoundTag);
            }
        }

        if (!listTag.isEmpty() || saveEmpty) {
            tag.put("Items", listTag);
        }

    }

    public static void loadAllItems(CompoundTag tag, NonNullList<ItemStack> list) {
        ListTag listTag = tag.getList("Items", 10);

        for(int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            if (j < list.size()) {
                list.set(j, stackFromTag(compoundTag));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Tag> T compute(CompoundTag tag, String key, BiFunction<String, T, T> remapper) {
        Tag oldValue = tag.get(key);
        T newVal = remapper.apply(key, oldValue == null ? null : (T) oldValue);
        tag.put(key, newVal);
        return newVal;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Tag> T computeIfAbsent(CompoundTag tag, String key, Function<String, T> computer) {
        if (tag.contains(key))
            return (T) tag.get(key);
        T val = computer.apply(key);
        tag.put(key, val);
        return val;
    }

    public static void computeAllIfAbsent(CompoundTag tag, Collection<String> keys, Function<String, Tag> computer) {
        for (String key : keys) {
            computeIfAbsent(tag, key, computer);
        }
    }

    public static boolean isInventorySingleItem(NonNullList<ItemStack> inventory) {
        return inventory.stream().filter(s -> !s.isEmpty()).toArray().length == 1;
    }

    public static float mbFromOunces(float ounces) {
        return ounces * 250 / 12;
    }

    public static ResourceLocation[] getBaseItemFromConcoction(ItemStack concoction) {
        if (!concoction.getOrCreateTag().contains("BrewingSteps"))
            return new ResourceLocation[] {new ResourceLocation("minecraft:air")};
        ListTag list = concoction.getOrCreateTag().getList("BrewingSteps", Tag.TAG_COMPOUND);
        CompoundTag data = list.getCompound(0);
        if (data.contains("item", Tag.TAG_STRING)) {
            return new ResourceLocation[] {
                    new ResourceLocation(data.getString("item"))
            };
        } else if (data.contains("Items", Tag.TAG_LIST)) {
            NonNullList<ItemStack> stacks = NonNullList.withSize(4, ItemStack.EMPTY);
            loadAllItems(data, stacks);
            return stacks.stream().filter(Predicate.not(ItemStack::isEmpty)).map(ItemStack::getItem)
                    .map(BuiltInRegistries.ITEM::getKey).toList().toArray(new ResourceLocation[0]);
        }
        return new ResourceLocation[0];
    }

    public static int getColorForConcoction(ItemStack concoction) {
        ResourceLocation[] items = getBaseItemFromConcoction(concoction);
        if (items.length == 1) {
            return ColorUtil.get(items[0].toString());
        }
        List<Integer> colors = Arrays.stream(items).map(ResourceLocation::toString)
                .map(ColorUtil::get).filter(i -> i != 0).toList();
        return averageColors(colors);
    }

    public static int getColorForDrinkWithDefault(ItemStack drink, int normal, Level level) {
        DrinkAddition[] additions = DrinkUtil.getAdditionsFromStack(drink, level);
        List<Integer> colors = Arrays.stream(additions).filter(DrinkAddition::changesColor)
                .map(DrinkAddition::getColor).collect(Collectors.toCollection(ArrayList::new));
        colors.add(0, normal);
        return averageColors(colors);
    }

    public static boolean isEmpty(NonNullList<ItemStack> items) {
        for (ItemStack s : items) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    public static int averageColors(Collection<Integer> colors) {
        if (colors.isEmpty()) return 0xFFFFFF;
        int r = 0;
        int g = 0;
        int b = 0;
        for (int color : colors) {
            r += (color >> 16 & 255);
            g += (color >> 8 & 255);
            b += (color & 255);
        }
        r /= colors.size();
        g /= colors.size();
        b /= colors.size();
        return r << 16 | g << 8 | b;
    }

    public static String getTimeString(int seconds) {
        float minutes = seconds / 60f;
        float hours = minutes / 60;
        int hoursDisplay = (int) hours;
        int rawIntMinutes = (int) minutes;
        int minutesDisplay = rawIntMinutes - hoursDisplay * 60;
        int secondsDisplay = seconds - rawIntMinutes * 60;
        String paddedMinutes = ((minutesDisplay < 10 && hoursDisplay > 0) ? "0" : "") + minutesDisplay;
        String paddedSeconds = (secondsDisplay < 10 ? "0" : "") + secondsDisplay;

        return hoursDisplay == 0 ? "%s:%s".formatted(paddedMinutes, paddedSeconds) : "%s:%s:%s".formatted(hoursDisplay, paddedMinutes, paddedSeconds);
    }

    public static String getMinecraftDays(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        return "" + (minutes / 20);
    }

    public static int getAlcoholDeviation(ItemStack stack) {
        if (!stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY).contains("Deviation")) return 0;
        return stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY).getInt("Deviation");
    }

    public static ItemStack setAlcoholDeviation(ItemStack stack, int deviation) {
        stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY).putInt("Deviation", deviation);
        return stack;
    }

    public static int getOuncesFromBottle(Item bottle) {
        if (bottle == BartendingItems.LIQUOR_BOTTLE) return 30;
        if (bottle == BartendingItems.WINE_BOTTLE) return 25;
        if (bottle == BartendingItems.JUG) return 40;
        return 12;
    }

    public static void runOn(EnvType type, Supplier<Supplier<Runnable>> run) {
        if (FabricLoader.getInstance().getEnvironmentType() == type) {
            run.get().get().run();
        }
    }

    public static AlcoholicDrink getDrink(CompoundTag tag) {
        if (tag.contains("Alcohol")) {
            AlcoholicDrink drink = BartendingRegistries.ALCOHOLIC_DRINK.get(new ResourceLocation(tag.getString("Alcohol")));
            if (drink == null) throw new IllegalArgumentException("Unregistered value: '" + tag.getString("Alcohol") + "'");
            return drink;
        }
        throw new IllegalArgumentException("Tag doesn't contain tag 'Alcohol'");
    }

    /**
     * @param drink The drink to get the alcohol from
     * @return The amount of alcohol in grams in a standard size of the specified drink
     */
    public static int getStandardAlcohol(AlcoholicDrink drink) {
        float ounces = drink.standardOunces();
        return getAlcohol(drink, ounces);
    }

    public static int getAlcohol(AlcoholicDrink drink, float ounces) {
        int proof = drink.standardProof();
        float percent = proof / 200f;
        float ouncesAlc = ounces * percent;
        return Math.round(convertType(ouncesAlc, AlcDisplayType.OUNCES, AlcDisplayType.GRAMS));
    }

    public static int getProof(AlcoholicDrink drink, float newAmount) {
        float ounces = drink.standardOunces();
        float ounceAlc = convertType(newAmount, AlcDisplayType.GRAMS, AlcDisplayType.OUNCES);
        return Math.round(200 * (ounceAlc / ounces));
    }

    public static float calculateBAC(float grams) {
        return (grams / (184.35f * 454 * 0.615f)) * 100;
    }

    public static float gramsFromBAC(float bac) {
        return (184.35f * 454 * 0.615f) * (bac / 100);
    }

    public static float convertType(float amount, AlcDisplayType from, AlcDisplayType to) {
        float grams = amount / from.multiplier;
        return grams * to.multiplier;
    }

    public static ItemStack constructFailedConcoction(ItemStack og) {
        ItemStack failed = new ItemStack(BartendingItems.CONCOCTION);
        int ticksBoiled = 0, ticksFermented = 0, distilled = 0;
        ListTag steps = og.getOrCreateTag().getList("BrewingSteps", Tag.TAG_COMPOUND);
        for (Tag tag : steps) {
            if (!(tag instanceof CompoundTag data)) continue;
            switch (data.getString("type")) {
                case FermentingBrewerStep.TYPE_ID -> ticksBoiled += data.getInt("ticks");
                case BarrelAgingBrewerStep.TYPE_ID -> ticksFermented += data.getInt("ticks");
                case DistillingBrewerStep.TYPE_ID -> distilled += data.getInt("runs");
            }
        }
        CompoundTag data = new CompoundTag();
        data.putInt("ticksBoiled", ticksBoiled);
        data.putInt("ticksFermented", ticksFermented);
        data.putInt("distilled", distilled);
        failed.getOrCreateTag().put("FailedConcoctionData", data);
        return failed;
    }

    public static void runIfLoaded(String modid, Supplier<Runnable> r) {
        if (FabricLoader.getInstance().isModLoaded(modid)) {
            r.get().run();
        }
    }

    public static <T> Optional<T> supplyIfLoaded(String modid, Supplier<Supplier<T>> t) {
        if (FabricLoader.getInstance().isModLoaded(modid)) {
            return Optional.of(t.get().get());
        } else return Optional.empty();
    }

}
