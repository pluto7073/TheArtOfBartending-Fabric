package ml.pluto7073.bartending.content.fluid;

import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.fluid.AlcFlowableFluid;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class WhiteFineFluid extends AlcFlowableFluid {
    @Override
    public Block getLegacyBlock() {
        return BartendingBlocks.WHITE_WINE;
    }

    @Override
    public BartendingFluids.FluidHolder getHolder() {
        return BartendingFluids.WHITE_WINE;
    }

    @Override
    public Item getBucket() {
        return BartendingItems.WHITE_WINE_BUCKET;
    }

    public static class Still extends WhiteFineFluid {

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
    }

    public static class Flowing extends WhiteFineFluid {

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
    }
}
