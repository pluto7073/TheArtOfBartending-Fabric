package ml.pluto7073.bartending.content.block.entity;

import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.content.gui.BottlerMenu;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.step.BrewerStep;
import ml.pluto7073.bartending.foundations.step.BottleFermentingBrewerStep;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.item.PourableBottleItem;
import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("UnstableApiUsage")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BottlerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    public static final int CONCOCTION_INPUT_SLOT = 0,
            RESULT_DISPLAY_SLOT = 1,
            BOTTLE_INSERT_SLOT = 2,
            RESULT_SLOT = 3,
            CONTAINER_SIZE = 4;

    public static final int[] TOP_SLOTS = { CONCOCTION_INPUT_SLOT },
            SIDE_SLOTS = { BOTTLE_INSERT_SLOT, CONCOCTION_INPUT_SLOT, RESULT_SLOT },
            BOTTOM_SLOTS = { RESULT_SLOT };

    public static final int BOTTLE_TIME_DATA = 0;
    public static final int DATA_COUNT = 1;

    public static final int MAX_BOTTLE_TIME = 200;

    public final ContainerData data;

    public NonNullList<ItemStack> container;
    public int bottleTick;

    private ItemStack currentResult;

    public BottlerBlockEntity(BlockPos pos, BlockState blockState) {
        super(BartendingBlockEntities.BOTTLER_BLOCK_ENTITY_TYPE, pos, blockState);
        container = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        currentResult = ItemStack.EMPTY;
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return index == BOTTLE_TIME_DATA ? bottleTick : 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == BOTTLE_TIME_DATA) bottleTick = value;
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(super.getUpdateTag());
        return tag;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BottlerBlockEntity entity) {

        ItemStack concoction = entity.getItem(CONCOCTION_INPUT_SLOT);
        ItemStack display = entity.getItem(RESULT_DISPLAY_SLOT);
        ItemStack bottle = entity.getItem(BOTTLE_INSERT_SLOT);
        ItemStack output = entity.getItem(RESULT_SLOT);

        // Testing for start of bottling
        if (concoction.is(BartendingItems.CONCOCTION) && display.isEmpty() && concoction.getOrCreateTag().contains("BrewingSteps") && entity.bottleTick == 0) {
            ListTag steps = concoction.getOrCreateTag().getList("BrewingSteps", CompoundTag.TAG_COMPOUND);
            AlcoholicDrink match = null;
            for (AlcoholicDrink drink : AlcoholicDrinks.values()) {
                if (drink.matches(steps, level)) match = drink;
            }
            if (match == null) {
                display = BrewingUtil.constructFailedConcoction(concoction);
                concoction = new ItemStack(Items.GLASS_BOTTLE);
            } else {
                entity.currentResult = new ItemStack(AlcoholicDrinks.getFinalDrink(match));
                int deviation = match.getTotalDeviation(steps, level);
                BrewingUtil.setAlcoholDeviation(entity.currentResult, deviation);
                entity.bottleTick++;
            }
            setChanged(level, pos, state);
        } else if (concoction.getItem() instanceof PourableBottleItem && display.isEmpty() && concoction.getOrCreateTag().contains("ExtraFermentingData") && entity.bottleTick == 0) {
            AlcoholicDrink match = null;
            drinkLoop: for (AlcoholicDrink drink : AlcoholicDrinks.values()) {
                if (drink.steps().length != 1) continue;
                for (BrewerStep step : drink.steps()) {
                    if (!(step instanceof BottleFermentingBrewerStep extra)) continue drinkLoop;
                    if (!extra.matches(concoction.getOrCreateTagElement("ExtraFermentingData"), level))
                        continue drinkLoop;
                    if (!extra.testItem(concoction)) continue drinkLoop;
                    match = drink;
                }
            }
            if (match != null) {
                entity.currentResult = new ItemStack(AlcoholicDrinks.getFinalDrink(match));
                ListTag stepList = new ListTag();
                stepList.add(concoction.getOrCreateTagElement("ExtraFermentingData"));
                int deviation = match.getTotalDeviation(stepList, level);
                deviation += BrewingUtil.getAlcoholDeviation(concoction);
                BrewingUtil.setAlcoholDeviation(entity.currentResult, deviation);
                entity.bottleTick++;
            }
        } else if (!display.isEmpty() || concoction.isEmpty() || concoction.is(Items.GLASS_BOTTLE) || !(concoction.getOrCreateTag().contains("BrewingSteps") || concoction.getOrCreateTag().contains("ExtraFermentingData"))) {
            entity.bottleTick = 0;
            entity.currentResult = ItemStack.EMPTY;
            setChanged(level, pos, state);
        }

        if (!entity.currentResult.isEmpty()) {
            entity.bottleTick++;
        }

        if (display.isEmpty()) {
            setChanged(level, pos, state);
        }

        // Test for finish bottling
        if (entity.bottleTick >= MAX_BOTTLE_TIME && !entity.currentResult.isEmpty()) {
            display = entity.currentResult;
            Item bottleItem = Items.GLASS_BOTTLE;
            if (concoction.getItem() instanceof PourableBottleItem pourable) {
                bottleItem = pourable.emptyBottleItem;
            }
            concoction = new ItemStack(bottleItem);
            entity.bottleTick = 0;
            entity.currentResult = ItemStack.EMPTY;
            setChanged(level, pos, state);
        }

        // Test for bottle
        if (!display.isEmpty()) {
            AlcoholicDrink drink = display.getItem() instanceof PourableBottleItem pour
                    ? pour.drink : null;
            if (drink != null && !bottle.isEmpty() && output.isEmpty()) {
                if (bottle.is(drink.bottle())) {
                    bottle.shrink(1);
                    if (bottle.isEmpty()) bottle = ItemStack.EMPTY;
                    output = display;
                    display = ItemStack.EMPTY;
                    setChanged(level, pos, state);
                }
            }
        }

        if (display.is(BartendingItems.CONCOCTION) && bottle.is(Items.GLASS_BOTTLE) && output.isEmpty()) {
            output = display;
            bottle.shrink(1);
            display = ItemStack.EMPTY;
            setChanged(level, pos, state);
        }

        entity.setItem(CONCOCTION_INPUT_SLOT, concoction);
        entity.setItem(RESULT_DISPLAY_SLOT, display);
        entity.setItem(BOTTLE_INSERT_SLOT, bottle);
        entity.setItem(RESULT_SLOT, output);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        bottleTick = tag.getInt("BottleTick");
        currentResult = ItemStack.of(tag.getCompound("CurrentResult"));
        ContainerHelper.loadAllItems(tag, container);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BottleTick", bottleTick);
        tag.put("CurrentResult", currentResult.save(new CompoundTag()));
        ContainerHelper.saveAllItems(tag, container);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.bottler");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new BottlerMenu(containerId, inventory, this, data);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return switch (side) {
            case UP -> TOP_SLOTS;
            case DOWN -> BOTTOM_SLOTS;
            default -> SIDE_SLOTS;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return isValid(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (direction != Direction.DOWN && direction != Direction.UP && index == CONCOCTION_INPUT_SLOT) {
            return stack.is(Items.GLASS_BOTTLE);
        }
        return true;
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return BrewingUtil.isEmpty(container);
    }

    @Override
    public ItemStack getItem(int slot) {
        return container.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == RESULT_DISPLAY_SLOT) return ItemStack.EMPTY;
        return ContainerHelper.removeItem(container, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(container, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack s = container.get(slot);
        container.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null) return false;
        if (level.getBlockEntity(worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    public void clearContent() {
        container.clear();
    }

    public boolean isValid(int slot, ItemStack stack) {
        return switch (slot) {
            case CONCOCTION_INPUT_SLOT -> stack.is(BartendingItems.CONCOCTION);
            case BOTTLE_INSERT_SLOT -> stack.is(BartendingTags.EMPTY_GLASS_BOTTLES);
            default -> false;
        };
    }

}
