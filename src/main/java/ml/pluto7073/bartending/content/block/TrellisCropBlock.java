package ml.pluto7073.bartending.content.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation"})
public class TrellisCropBlock extends CropBlock {

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final VoxelShape SHAPE_BASE = Block.box(1, 0, 1, 15, 16, 15);
    public static final VoxelShape SHAPE_TOP = Block.box(1, 0, 1, 15, 12, 15);

    private final Function<Level, List<ItemStack>> harvestItem;
    private final Item seeds;

    public TrellisCropBlock(Function<Level, List<ItemStack>> harvestItem, Item seeds, Properties properties) {
        super(properties);
        this.harvestItem = harvestItem;
        this.seeds = seeds;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seeds;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, world, pos, newState, moved);
        if (state != newState && !state.is(newState.getBlock())) {
            if (state.getValue(PART) == Part.BOTTOM && world.getBlockState(pos.above()).is(this)) {
                world.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), UPDATE_ALL);
            } else if (state.getValue(PART) == Part.TOP && world.getBlockState(pos.below()).is(this)) {
                world.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), UPDATE_ALL);
            }
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getRawBrightness(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age < this.getMaxAge()) {
                float f = getGrowthSpeed(this, world, pos);
                if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                    if (state.getValue(PART) == Part.BOTTOM) {
                        if (age == getMaxAge() - 1) {
                            BlockState above = world.getBlockState(pos.above());
                            if (!above.is(this)) return;
                            if (above.getValue(AGE) == 0) {
                                world.setBlock(pos.above(), above.setValue(AGE, 3), UPDATE_CLIENTS);
                            }
                        } else if (age < getMaxAge() - 1) {
                            world.setBlock(pos, state.setValue(AGE, age + 1), UPDATE_CLIENTS);
                        }
                    } else if (age >= 3) {
                        BlockState below = world.getBlockState(pos.below());
                        if (!below.is(this)) return;
                        world.setBlock(pos, state.setValue(AGE, age + 1), UPDATE_CLIENTS);
                        if (age + 1 >= getMaxAge()) {
                            world.setBlock(pos.below(), below.setValue(AGE, getMaxAge()), UPDATE_CLIENTS);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void growCrops(Level world, BlockPos pos, BlockState state) {
        BlockState above = world.getBlockState(pos.above());
        if (!above.is(this)) return;
        int newAge = this.getAge(state) + this.getBonemealAgeIncrease(world);
        int maxAge = this.getMaxAge();
        int aboveAge = this.getAge(above);
        if (newAge > maxAge) {
            newAge = maxAge;
        }

        if (aboveAge == 0 && newAge == maxAge) {
            newAge = maxAge - 1;
            aboveAge = 3;
        } else if (newAge == maxAge && aboveAge > 0 && aboveAge < maxAge - 1) {
            newAge = maxAge - 1;
            aboveAge += getBonemealAgeIncrease(world);
            if (aboveAge > maxAge) {
                aboveAge = maxAge;
            }
            if (aboveAge == maxAge) {
                newAge = maxAge;
            }
        } else if (newAge == maxAge && aboveAge == maxAge - 1) {
            aboveAge = maxAge;
        }

        world.setBlock(pos, state.setValue(AGE, newAge), Block.UPDATE_CLIENTS);
        world.setBlock(pos.above(), above.setValue(AGE, aboveAge), Block.UPDATE_CLIENTS);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(AGE);
        if (age != 7) {
            return InteractionResult.PASS;
        }
        List<ItemStack> stacks = harvestItem.apply(world);
        List<ItemStack> otherStacks = harvestItem.apply(world);
        stacks.forEach(stack -> popResource(world, pos, stack));
        otherStacks.forEach(stack -> popResource(world, state.getValue(PART) == Part.BOTTOM ? pos.above() : pos.below(), stack));
        world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4f);
        BlockState base = state.getValue(PART) == Part.BOTTOM ? state : world.getBlockState(pos.below());
        BlockState top = state.getValue(PART) == Part.TOP ? state : world.getBlockState(pos.above());
        world.setBlock(base == state ? pos : pos.below(), base.setValue(AGE, getMaxAge() - 1), UPDATE_CLIENTS);
        world.setBlock(top == state ? pos : pos.above(), top.setValue(AGE, getMaxAge() - 3), UPDATE_CLIENTS);
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == Part.BOTTOM ? SHAPE_BASE : SHAPE_TOP;
    }

    public enum Part implements StringRepresentable {
        TOP("top"), BOTTOM("bottom");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

}
