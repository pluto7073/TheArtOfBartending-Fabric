package ml.pluto7073.bartending.content.block.entity;

import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.content.gui.DistilleryMenu;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.alcohol.SecondaryAlcoholicDrink;
import ml.pluto7073.bartending.foundations.step.DistillingBrewerStep;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
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
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DistilleryBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    public static final int INPUT_SLOT_INDEX = 0,
    DISPLAY_SLOT_INDEX = 1,
    RESULT_SLOT_INDEX = 2,
    INVENTORY_SIZE = 3;

    public static final int DATA_DISTILL_TIME = 0,
    DATA_COUNT = 1;

    public static final int[] TOP_SLOTS = { INPUT_SLOT_INDEX, RESULT_SLOT_INDEX },
    SIDE_SLOTS = { INPUT_SLOT_INDEX },
    BOTTOM_SLOTS = { INPUT_SLOT_INDEX, RESULT_SLOT_INDEX };

    public static final int MAX_DISTILL_TIME = 300;

    protected NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == DATA_DISTILL_TIME ? distillTime : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == DATA_DISTILL_TIME) {
                distillTime = value;
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public int distillTime;

    public DistilleryBlockEntity(BlockPos pos, BlockState state) {
        super(BartendingBlockEntities.DISTILLERY_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        distillTime = tag.getShort("DistillTime");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putShort("DistillTime", (short) distillTime);
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.distillery");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new DistilleryMenu(containerId, inventory, this, data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DistilleryBlockEntity entity) {
        int heat = BrewingUtil.getHeatedData(entity);
        boolean lit = heat > 0;
        boolean changed = false;
        ItemStack input = entity.getItem(INPUT_SLOT_INDEX);
        ItemStack progress = entity.getItem(DISPLAY_SLOT_INDEX);
        ItemStack output = entity.getItem(RESULT_SLOT_INDEX);
        if (output.isEmpty() && (input.is(BartendingItems.CONCOCTION) || progress.is(BartendingItems.CONCOCTION) || AlcoholicDrinks.BASE_ITEMS.contains(input.getItem()) || AlcoholicDrinks.BASE_ITEMS.contains(progress.getItem())) && lit) {
            if (entity.distillTime == 0 && !input.isEmpty() && progress.isEmpty()) {
                progress = input.copy();
                entity.setItem(INPUT_SLOT_INDEX, ItemStack.EMPTY);
                progress.getOrCreateTag().putBoolean("JustLiquid", true);
                entity.setItem(DISPLAY_SLOT_INDEX, progress);
            }

            entity.distillTime += heat;
            if (entity.distillTime >= MAX_DISTILL_TIME) {
                changed = true;
                entity.distillTime = 0;
                ItemStack newOutput = progress.copy();
                newOutput.removeTagKey("JustLiquid");
                ListTag steps = newOutput.getOrCreateTag().getList("BrewingSteps", ListTag.TAG_COMPOUND);
                CompoundTag last = steps.getCompound(steps.size() - 1);
                if (DistillingBrewerStep.TYPE_ID.equals(last.getString("type"))) {
                    last.putInt("runs", last.getInt("runs") + 1);
                } else {
                    CompoundTag distilled = new CompoundTag();
                    distilled.putString("type", DistillingBrewerStep.TYPE_ID);
                    distilled.putInt("runs", 1);
                    steps.add(distilled);
                }
                entity.setItem(DISPLAY_SLOT_INDEX, ItemStack.EMPTY);
                entity.setItem(RESULT_SLOT_INDEX, newOutput);
            }
        } else {
            entity.distillTime = 0;
        }

        if (changed) setChanged(level, pos, state);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index != DISPLAY_SLOT_INDEX;
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.items) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack itemStack = items.get(slot);
        boolean bl = !stack.isEmpty() && ItemStack.isSameItemSameTags(itemStack, stack);
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        if (slot == INPUT_SLOT_INDEX && !bl) {
            distillTime = 0;
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == RESULT_SLOT_INDEX) {
            return stack.is(Items.GLASS_BOTTLE);
        }
        if (index == DISPLAY_SLOT_INDEX) {
            return AbstractFurnaceBlockEntity.isFuel(stack);
        }
        return stack.is(BartendingItems.CONCOCTION);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

}
