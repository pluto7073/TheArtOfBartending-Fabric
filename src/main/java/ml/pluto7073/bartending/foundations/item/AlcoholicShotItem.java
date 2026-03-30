package ml.pluto7073.bartending.foundations.item;

import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.BartendingStats;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholHandler;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.chemicals.handlers.ConsumedInstance.AbsorptionType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlcoholicShotItem extends Item {

    public final int alcohol;
    public final AlcoholicDrink source;

    public AlcoholicShotItem(AlcoholicDrink source, Properties properties) {
        super(properties);
        this.source = source;
        alcohol = BrewingUtil.getAlcohol(source, 1.5f);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 16;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        Player player = user instanceof Player ? (Player) user : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        }

        if (!level.isClientSide) {
            if (player != null) {
                player.addChemical(AlcoholHandler.INSTANCE, AbsorptionType.DRINK, alcohol);
            }
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            player.awardStat(BartendingStats.CONSUME_ALCOHOL.get(), alcohol);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        if (player == null || !player.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return new ItemStack(BartendingItems.SHOT_GLASS);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(BartendingItems.SHOT_GLASS));
            }
        }
        user.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        if (isAdvanced.isCreative() || isAdvanced.isAdvanced()) AlcoholHandler.INSTANCE.appendTooltip(tooltipComponents, alcohol, stack);
    }
}
