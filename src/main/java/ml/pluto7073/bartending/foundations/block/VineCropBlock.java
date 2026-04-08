package ml.pluto7073.bartending.foundations.block;

import com.google.common.collect.ImmutableMap;
import ml.pluto7073.bartending.content.block.EmptyVineFrameBlock;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER;
import static net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation"})
public class VineCropBlock extends CropBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 8);
    public static final VoxelShape SHAPE_BASE_X = Block.box(7, -1, 1, 9, 15, 15);
    public static final VoxelShape SHAPE_TOP_X = Block.box(7, -1, 1, 9, 11, 15);
    public static final VoxelShape SHAPE_BASE_Z = Block.box(1, -1, 7, 15, 15, 9);
    public static final VoxelShape SHAPE_TOP_Z = Block.box(1, -1, 7, 15, 11, 9);
    public static final Map<Direction.Axis, Map<DoubleBlockHalf, VoxelShape>> AXIS_SHAPE_MAP = ImmutableMap.of(
            Direction.Axis.X, ImmutableMap.of(UPPER, SHAPE_TOP_X, LOWER, SHAPE_BASE_X),
            Direction.Axis.Z, ImmutableMap.of(UPPER, SHAPE_TOP_Z, LOWER, SHAPE_BASE_Z)
    );

    private final Supplier<Item> seeds;

    public VineCropBlock(Supplier<Item> seeds, Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(HALF, LOWER).setValue(FACING, Direction.NORTH).setValue(AGE, 0));
        EmptyVineFrameBlock.ITEM_TO_BLOCK_MAP.put(seeds, this);
        this.seeds = seeds;
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            if (player.isCreative()) {
                EmptyVineFrameBlock.preventCreativeDropFromBottomPart(world, pos, state, player);
            } else {
                dropResources(state, world, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.playerDestroy(world, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, stack);
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return super.canSurvive(state, world, pos);
        } else {
            BlockState blockState = world.getBlockState(pos.below());
            return blockState.is(this) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(), defaultBlockState().setValue(HALF, UPPER), UPDATE_ALL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, AGE);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        if (blockPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockPos.above()).canBeReplaced(ctx)) {
            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seeds.get();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        float f = getGrowthSpeed(this, world, state.getValue(HALF) == LOWER ? pos : pos.below());
        if (f <= 1) {
            if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                boolean bottom = state.getValue(HALF) == LOWER;
                BlockState other = world.getBlockState(bottom ? pos.above() : pos.below());
                world.setBlock(pos, state.setValue(AGE, 8), UPDATE_CLIENTS);
                world.setBlock(bottom ? pos.above() : pos.below(), other.setValue(AGE, 8), UPDATE_CLIENTS);
            }
        }

        if (world.getRawBrightness(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age < this.getMaxAge()) {
                if (!world.getBlockState((state.getValue(HALF) == LOWER ? pos : pos.below()).below()).is(BartendingTags.C_FARMLAND)) {
                    return;
                }
                if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                    if (state.getValue(HALF) == LOWER) {
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

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (direction.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.LOWER != (direction == Direction.UP) || neighborState.is(this) && neighborState.getValue(HALF) != doubleBlockHalf) {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public void growCrops(Level world, BlockPos pos, BlockState state) {
        if (state.getValue(AGE) == 8) return;
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
        if (world instanceof ServerLevel level) {
            List<ItemStack> stacks = getDrops(state, level, pos, null);
            stacks.forEach(stack -> {if (!stack.is(BartendingItems.VINE_FRAME)) popResource(world, pos, stack); });
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4f);
            BlockState base = state.getValue(HALF) == LOWER ? state : world.getBlockState(pos.below());
            BlockState top = state.getValue(HALF) == UPPER ? state : world.getBlockState(pos.above());
            world.setBlock(base == state ? pos : pos.below(), base.setValue(AGE, getMaxAge() - 1), UPDATE_CLIENTS);
            world.setBlock(top == state ? pos : pos.above(), top.setValue(AGE, getMaxAge() - 3), UPDATE_CLIENTS);
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return AXIS_SHAPE_MAP.get(state.getValue(FACING).getAxis()).get(state.getValue(HALF));
    }

}
