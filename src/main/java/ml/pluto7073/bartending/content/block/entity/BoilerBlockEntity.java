package ml.pluto7073.bartending.content.block.entity;

import ml.pluto7073.bartending.compat.create.TheArtOfCreate;
import ml.pluto7073.bartending.content.gui.BoilerMenu;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.util.ColorUtil;
import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.bartending.foundations.water.ValidWaterSources;
import ml.pluto7073.pdapi.util.BasicSingleStorage;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BoilerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    public static final FluidVariant WATER = FluidVariant.of(Fluids.WATER);

    public static final int WATER_INPUT_SLOT_INDEX = 0;
    public static final int ITEM_INPUT_SLOT_INDEX = 1;
    public static final int DISPLAY_RESULT_ITEM_SLOT_INDEX = 2;
    public static final int GLASS_BOTTLE_INSERT_SLOT_INDEX = 3;
    public static final int RESULT_SLOT_INDEX = 4;
    public static final int INVENTORY_SIZE = 5;

    public static final int WATER_AMOUNT_DATA = 0;
    public static final int BOIL_TIME_DATA = 1;
    public static final int HEATED_DATA = 2;
    public static final int COLOR_DATA = 3;
    public static final int DATA_COUNT = 4;

    public static final int[] TOP_SLOTS = { 1, 3 };
    public static final int[] SIDE_SLOTS = { 0, 1, 4 };
    public static final int[] BOTTOM_SLOTS = { 0, 3, 4 };

    public final ContainerData data;

    private static final VoxelShape INSIDE = Block.box(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape ABOVE = Block.box(0.0, 16.0, 0.0, 16.0, 20.0, 16.0);
    private static final VoxelShape SUCK = Shapes.or(INSIDE, ABOVE);

    public NonNullList<ItemStack> inventory;
    public final BasicSingleStorage water;
    public final Storage<FluidVariant> exposed;
    public int boilTicks;

    protected final NonNullList<ItemStack> boilingStacks;

    public BoilerBlockEntity(BlockPos pos, BlockState blockState) {
        super(BartendingBlockEntities.BOILER_BLOCK_ENTITY_TYPE, pos, blockState);
        this.inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.water = new BasicSingleStorage(WATER, 81000, this::setChanged);
        this.exposed = FilteringStorage.insertOnlyOf(water);
        boilingStacks = NonNullList.withSize(4, ItemStack.EMPTY);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case WATER_AMOUNT_DATA -> (int) water.amount;
                    case BOIL_TIME_DATA -> boilTicks;
                    case HEATED_DATA -> BrewingUtil.getHeatedData(BoilerBlockEntity.this);
                    case COLOR_DATA -> BrewingUtil.isEmpty(boilingStacks) ? 4210943 :
                        BrewingUtil.averageColors(boilingStacks.stream().map(ItemStack::getItem)
                                .map(BuiltInRegistries.ITEM::getKey).map(ResourceLocation::toString)
                                .filter(ColorUtil.COLORS_REGISTRY::containsKey).map(ColorUtil::get).toList());
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case WATER_AMOUNT_DATA -> water.amount = value;
                    case BOIL_TIME_DATA -> boilTicks = value;
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }



    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        boilTicks = tag.getInt("BoilTicks");
        water.amount = tag.getInt("WaterAmount");
        water.variant = FluidVariant.fromNbt(tag.getCompound("FluidVariant"));
        ContainerHelper.loadAllItems(tag, inventory);
        boilingStacks.clear();
        if (tag.contains("BoilingStacks")) {
            ListTag listTag = tag.getList("BoilingStacks", Tag.TAG_COMPOUND);

            for(int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag = listTag.getCompound(i);
                int j = compoundTag.getByte("Slot") & 255;
                if (j < boilingStacks.size()) {
                    boilingStacks.set(j, ItemStack.of(compoundTag));
                }
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BoilTicks", boilTicks);
        tag.putInt("WaterAmount", (int) water.amount);
        tag.put("FluidVariant", water.variant.toNbt());
        tag.put("BoilingStacks", ContainerHelper.saveAllItems(new CompoundTag(), boilingStacks).get("Items"));
        ContainerHelper.saveAllItems(tag, inventory);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    private boolean tryBoilItem(ItemStack stack) {
        if (!stack.is(BartendingTags.BOILABLES)) return false;
        for (int i = 0; i < boilingStacks.size(); i++) {
            ItemStack current = boilingStacks.get(i);
            if (current.isEmpty()) {
                boilingStacks.set(i, stack);
                boilTicks = 0;
                return true;
            }
            if (!current.is(stack.getItem())) continue;
            if (current.getCount() >= 255) return false;
            boilTicks = 0;
            if (current.getCount() <= 255 - stack.getCount()) {
                current.grow(stack.getCount());
                return true;
            }
            int diff = 255 - current.getCount();
            current.setCount(255);
            stack.shrink(diff);
            return false;
        }
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BoilerBlockEntity entity) {
        // Ticking Water Refill
        ItemStack waterStack = entity.getItem(WATER_INPUT_SLOT_INDEX);
        int water;
        if ((water = ValidWaterSources.getAmountFromItem(waterStack)) > 0) {
            waterAdd: try (Transaction transaction = Transaction.openOuter()) {
                long amount = entity.water.insert(WATER, water, transaction);
                if (water - amount > 2025) {
                    transaction.abort();
                    break waterAdd;
                }
                Item remaining = waterStack.getItem().getCraftingRemainingItem();
                if (waterStack.is(ConventionalItemTags.POTIONS)) remaining = Items.GLASS_BOTTLE;
                entity.setItem(WATER_INPUT_SLOT_INDEX, remaining == null ? ItemStack.EMPTY : new ItemStack(remaining, 1));
                transaction.commit();
            }
        }
        int maxConcoctions = (int) entity.water.amount / 20250;

        ItemStack input = entity.getItem(ITEM_INPUT_SLOT_INDEX);

        if (!input.isEmpty() && entity.water.amount >= 20250) {
            if (entity.tryBoilItem(input)) {
                if (input.getItem().getCraftingRemainingItem() != null) {
                    entity.setItem(ITEM_INPUT_SLOT_INDEX, new ItemStack(input.getItem().getCraftingRemainingItem(), input.getCount()));
                } else entity.setItem(ITEM_INPUT_SLOT_INDEX, ItemStack.EMPTY);
            }
        }

        if (entity.isBoiling()) {
            entity.boilTicks += (int) Math.pow(2, BrewingUtil.getHeatedData(entity) - 1);
        }

        if (BrewingUtil.isEmpty(entity.boilingStacks)) {
            entity.setItem(DISPLAY_RESULT_ITEM_SLOT_INDEX, ItemStack.EMPTY);
            entity.boilTicks = 0;
        } else {
            ItemStack display = BrewingUtil.createConcoctionFromBaseTicks(entity.boilingStacks, entity.boilTicks);
            ItemStack currentDisplay = entity.getItem(DISPLAY_RESULT_ITEM_SLOT_INDEX);
            if (!currentDisplay.is(BartendingItems.CONCOCTION) || currentDisplay.getCount() > maxConcoctions) {
                display.setCount(maxConcoctions);
            } else display.setCount(currentDisplay.getCount());
            display.getOrCreateTag().putBoolean("JustLiquid", true);
            entity.setItem(DISPLAY_RESULT_ITEM_SLOT_INDEX, display);
        }

        ItemStack bottle = entity.getItem(GLASS_BOTTLE_INSERT_SLOT_INDEX);
        ItemStack display = entity.getItem(DISPLAY_RESULT_ITEM_SLOT_INDEX);

        if (bottle.is(Items.GLASS_BOTTLE) && !display.isEmpty() && entity.getItem(RESULT_SLOT_INDEX).isEmpty()) {
            entity.water.amount -= 20250;
            bottle.shrink(1);
            if (bottle.isEmpty()) entity.setItem(GLASS_BOTTLE_INSERT_SLOT_INDEX, ItemStack.EMPTY);
            ItemStack res = display.copy();
            res.setCount(1);
            res.removeTagKey("JustLiquid");
            entity.setItem(RESULT_SLOT_INDEX, res);
            display.shrink(1);
            if (display.isEmpty()) {
                entity.setItem(DISPLAY_RESULT_ITEM_SLOT_INDEX, ItemStack.EMPTY);
                entity.boilingStacks.clear();
                entity.boilTicks = 0;
            }
        }

        setChanged(level, pos, state);
    }

    public static List<ItemEntity> getItemsAtAndAbove(Level level, BoilerBlockEntity entity) {
        return SUCK.toAabbs().stream().flatMap((box) ->
                level.getEntitiesOfClass(ItemEntity.class, box.move(entity.worldPosition.getX(), entity.worldPosition.getY(), entity.worldPosition.getZ()),
                EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    public boolean isBoiling() {
        // If water
        return water.amount > 0 && BrewingUtil.getHeatedData(this) != 0;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.boiler");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new BoilerMenu(containerId, inventory, this, this.data);
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return BrewingUtil.isEmpty(inventory);
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == DISPLAY_RESULT_ITEM_SLOT_INDEX) return ItemStack.EMPTY;
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack s = this.inventory.get(slot);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        assert level != null;
        if (level.getBlockEntity(worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return switch (side) {
            case DOWN -> BOTTOM_SLOTS;
            case UP -> TOP_SLOTS;
            default -> SIDE_SLOTS;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return isValid(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        if (direction == Direction.DOWN && slot == WATER_INPUT_SLOT_INDEX) {
            return stack.is(Items.GLASS_BOTTLE) || stack.is(Items.BUCKET);
        } else return direction != Direction.DOWN || slot != GLASS_BOTTLE_INSERT_SLOT_INDEX;
    }

    public boolean isValid(int slot, ItemStack stack) {
        return switch (slot) {
            case WATER_INPUT_SLOT_INDEX -> ValidWaterSources.getAmountFromItem(stack) > 0;
            case ITEM_INPUT_SLOT_INDEX -> stack.is(BartendingTags.BOILABLES);
            case GLASS_BOTTLE_INSERT_SLOT_INDEX -> stack.is(Items.GLASS_BOTTLE);
            default -> false;
        };
    }

}
