package ml.pluto7073.bartending.content.item;

import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.step.FermentingBrewerStep;
import ml.pluto7073.bartending.foundations.step.DistillingBrewerStep;
import ml.pluto7073.bartending.foundations.step.BarrelAgingBrewerStep;
import ml.pluto7073.bartending.foundations.tags.BartendingTags;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
public class ConcoctionItem extends Item {

    public ConcoctionItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        Player playerEntity = user instanceof Player ? (Player) user : null;
        if (playerEntity instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)playerEntity, stack);
        }

        if (!level.isClientSide) {

        }

        if (playerEntity != null) {
            playerEntity.awardStat(Stats.ITEM_USED.get(this));
            if (!playerEntity.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        if (playerEntity == null || !playerEntity.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (playerEntity != null) {
                playerEntity.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        user.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        if (!stack.getOrCreateTag().contains("BrewingSteps")) {
            tooltip.add(Component.translatable("tooltip.bartending.risky_drink").withStyle(ChatFormatting.GRAY));
            return;
        }
        ListTag steps = stack.getOrCreateTag().getList("BrewingSteps", ListTag.TAG_COMPOUND);

        if (level != null) {
            List<AlcoholicDrink> matches = AlcoholicDrinks.values().stream().filter(drink -> drink.mightMatch(steps, level)).toList();

            if (!matches.isEmpty()) {
                int index = matches.size();

                if (stack.getOrCreateTag().contains("suggestIndex")) {
                    index = stack.getOrCreateTag().getInt("suggestIndex");
                }

                if (index >= matches.size()) {
                    index = level.random.nextInt(matches.size());
                }

                stack.getOrCreateTag().putInt("suggestIndex", index);

                AlcoholicDrink match = matches.get(index);
                ResourceLocation id = AlcoholicDrinks.getId(match);

                tooltip.add(Component.translatable("tooltip.bartending.might_create").withStyle(ChatFormatting.GRAY)
                        .append(Component.translatable(id.toLanguageKey("alcohol")).withStyle(ChatFormatting.AQUA)));
            } else {
                tooltip.add(Component.translatable("tooltip.bartending.might_create").withStyle(ChatFormatting.GRAY)
                        .append(Component.translatable("item.bartending.concoction")).withStyle(ChatFormatting.AQUA));
            }
        }

        for (Tag tag : steps) {
            if (!(tag instanceof CompoundTag data)) continue;
            String type = data.getString("type");
            switch (type) {
                case FermentingBrewerStep.TYPE_ID -> FermentingBrewerStep.appendInProgressText(data, tooltip);
                case BarrelAgingBrewerStep.TYPE_ID -> BarrelAgingBrewerStep.appendInProgressText(data, tooltip, level);
                case DistillingBrewerStep.TYPE_ID -> DistillingBrewerStep.appendInProgressText(data, tooltip);
            }
        }
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (stack.getOrCreateTag().contains("BrewingSteps")) {
            ListTag list = stack.getOrCreateTag().getList("BrewingSteps", Tag.TAG_COMPOUND);
            if (!list.isEmpty()) {
                CompoundTag data = list.getCompound(0);
                if (data.contains("item", Tag.TAG_STRING)) {
                    ResourceLocation id = new ResourceLocation(data.getString("item"));
                    ItemStack item = new ItemStack(BuiltInRegistries.ITEM.get(id));
                    if (item.is(BartendingTags.BOTANICAL_ELEMENTS)) {
                        return "concoction.bartending.botanical";
                    } else if (item.is(BartendingTags.CRIMSON_BOTANICALS) || item.is(Items.CRIMSON_FUNGUS)) {
                        return "concoction.bartending.crimson";
                    } else if (item.is(BartendingTags.WARPED_BOTANICALS) || item.is(Items.WARPED_FUNGUS)) {
                        return "concoction.bartending.warped";
                    }
                    return id.toLanguageKey("concoction");
                } else if (data.contains("Items")) {
                    ItemStack s = BrewingUtil.stackFromTag(data.getList("Items", Tag.TAG_COMPOUND).getCompound(0));
                    if (s.is(BartendingTags.BOTANICAL_ELEMENTS)) {
                        return "concoction.bartending.botanical";
                    }
                    ResourceLocation key = BuiltInRegistries.ITEM.getKey(s.getItem());
                    return key.toLanguageKey("concoction");
                }
            }
        }
        return super.getDescriptionId(stack);
    }

    public static boolean isFailed(ItemStack stack) {
        return stack.getOrCreateTag().contains("FailedConcoctionData");
    }

}
