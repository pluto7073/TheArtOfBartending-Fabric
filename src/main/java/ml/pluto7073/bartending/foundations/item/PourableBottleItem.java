package ml.pluto7073.bartending.foundations.item;

import ml.pluto7073.bartending.content.sound.BartendingSounds;
import ml.pluto7073.bartending.foundations.BartendingStats;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholHandler;
import ml.pluto7073.bartending.foundations.recipe.BartendingRecipes;
import ml.pluto7073.bartending.foundations.recipe.PouringRecipe;
import ml.pluto7073.bartending.foundations.step.FermentingBrewerStep;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.bartending.foundations.alcohol.AlcDisplayType;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.chemicals.handlers.ConsumedInstance;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PourableBottleItem extends Item {

    public final Item emptyBottleItem;
    public final AlcoholicDrink drink;

    public PourableBottleItem(Item emptyBottleItem, AlcoholicDrink drink, Properties properties) {
        super(properties);
        this.emptyBottleItem = emptyBottleItem;
        this.drink = drink;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 24;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return BartendingSounds.LIQUID_POUR;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player)) return super.finishUsingItem(stack, level, livingEntity);
        RecipeManager recipes = level.getRecipeManager();
        List<PouringRecipe> matches = recipes.getAllRecipesFor(BartendingRecipes.POURING.<PouringRecipe>type()).stream()
                .filter(r -> r.matches(player.getInventory(), level)).toList();
        player.awardStat(Stats.ITEM_USED.get(this));
        if (matches.isEmpty()) {
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
            }
            int alc = BrewingUtil.getAlcohol(drink, 1);
            if (!level.isClientSide) {
                player.addChemical(AlcoholHandler.INSTANCE, ConsumedInstance.AbsorptionType.DRINK, alc);
            }
            player.awardStat(BartendingStats.CONSUME_ALCOHOL.get(), alc);
            stack.hurtAndBreak(2, player, user -> user.awardStat(BartendingStats.ALCOHOL_BOTTLES_DRANK.get()));
        } else {
            PouringRecipe recipe = matches.get(0);
            ItemStack result = recipe.assemble(player.getInventory(), level.registryAccess());
            ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
            if (offhand.getCount() == 1) {
                player.setItemInHand(InteractionHand.OFF_HAND, result);
            } else {
                offhand.shrink(1);
                player.getInventory().placeItemBackInInventory(result);
            }
            stack.hurtAndBreak((int) (recipe.ounces() * 2), player, user -> user.awardStat(BartendingStats.ALCOHOL_BOTTLES_DRANK.get()));
            player.gameEvent(GameEvent.FLUID_PLACE);
        }
        if (stack.isEmpty() && !player.getAbilities().instabuild) {
            return new ItemStack(emptyBottleItem, 1);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        int amount = BrewingUtil.getStandardAlcohol(drink);
        amount += BrewingUtil.getAlcoholDeviation(stack);
        if (isAdvanced.isAdvanced() || isAdvanced.isCreative()) AlcoholHandler.INSTANCE.appendTooltip(tooltip, amount, stack, AlcDisplayType.PROOF);
        if (stack.getOrCreateTag().contains("ExtraFermentingData")) {
            FermentingBrewerStep.appendInProgressText(stack.getOrCreateTagElement("ExtraFermentingData"), tooltip, level);
        }
        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }
}
