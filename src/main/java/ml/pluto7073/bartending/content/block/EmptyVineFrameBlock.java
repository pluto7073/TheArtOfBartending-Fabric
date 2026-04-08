package ml.pluto7073.bartending.content.block;

import com.google.common.collect.ImmutableMap;
import ml.pluto7073.bartending.foundations.block.VineCropBlock;
import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER;
import static net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EmptyVineFrameBlock extends Block {

    public static final Map<Supplier<Item>, VineCropBlock> ITEM_TO_BLOCK_MAP = new HashMap<>();
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private Map<Item, VineCropBlock> itemToBlockMap;

    public EmptyVineFrameBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(HALF, LOWER).setValue(FACING, Direction.NORTH));
    }

    public Map<Item, VineCropBlock> getItemToBlockMap() {
        if (itemToBlockMap == null) {
            itemToBlockMap = ITEM_TO_BLOCK_MAP.entrySet().stream().map(e -> new AbstractMap.SimpleEntry<>(e.getKey().get(), e.getValue()))
                    .collect(ImmutableMap.toImmutableMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        }
        return itemToBlockMap;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(HALF) == UPPER) return InteractionResult.PASS;
        Map<Item, VineCropBlock> map = getItemToBlockMap();
        ItemStack seeds = player.getItemInHand(hand);
        if (!map.containsKey(seeds.getItem())) return InteractionResult.PASS;
        if (!world.isClientSide) {
            VineCropBlock block = map.get(seeds.getItem());
            world.setBlock(pos, block.defaultBlockState(), UPDATE_ALL);
            if (!player.getAbilities().instabuild) {
                seeds.shrink(1);
                if (seeds.isEmpty()) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.below());
        return super.canSurvive(state, world, pos) && (state.getValue(HALF) == LOWER ? blockState.isFaceSturdy(world, pos.below(), Direction.UP) || blockState.is(BartendingTags.C_FARMLAND) : blockState.is(this));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(), defaultBlockState().setValue(HALF, UPPER), UPDATE_ALL);
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

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.is(this) && neighborState.getValue(HALF) != doubleBlockHalf ? state.setValue(FACING, neighborState.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return VineCropBlock.AXIS_SHAPE_MAP.get(state.getValue(FACING).getAxis()).get(state.getValue(HALF));
    }
}
