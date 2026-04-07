package ml.pluto7073.bartending.compat.create;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import ml.pluto7073.bartending.content.block.entity.BoilerBlockEntity;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class GoggleInfoBoilerBlockEntity extends BoilerBlockEntity implements IHaveGoggleInformation {

    public GoggleInfoBoilerBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    ").append(Component.translatable("tooltip.bartending.boiler_info")));
        if (BrewingUtil.isEmpty(boilingStacks)) {
            tooltip.add(Component.literal("     ").append(Component.translatable("tooltip.bartending.empty")).withStyle(ChatFormatting.GRAY));
            return true;
        }

        tooltip.add(Component.literal(" "));

        tooltip.add(Component.literal("    ").append(Component.translatable("tooltip.bartending.currently_boiling").withStyle(ChatFormatting.GRAY)));

        for (ItemStack s : boilingStacks) {
            if (s.isEmpty()) continue;
            tooltip.add(Component.literal("     ")
                    .append(Component.translatable(s.getDescriptionId()).append(" x").append(s.getCount() + "").withStyle(ChatFormatting.AQUA)));
        }

        tooltip.add(Component.literal(" "));
        tooltip.add(Component.literal("    ").append(
                Component.translatable("tooltip.bartending.boiling_in_progress", BrewingUtil.getTimeString(boilTicks / 20)).withStyle(ChatFormatting.GRAY)));
        return true;
    }
}
