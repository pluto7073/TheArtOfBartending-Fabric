package ml.pluto7073.bartending.foundations.item;

import ml.pluto7073.bartending.foundations.BartendingStats;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholHandler;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholicDrink;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@MethodsReturnNonnullByDefault
public class AlcoholicDrinkItem extends AbstractCustomizableDrinkItem {

    public final int alcohol;
    public final AlcoholicDrink source;
    public final Item bottle;

    public AlcoholicDrinkItem(AlcoholicDrink source, Item bottle, Properties settings) {
        super(bottle, source.standardOunces(), settings);
        this.source = source;
        this.bottle = bottle;
        alcohol = BrewingUtil.getStandardAlcohol(source);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof Player player) {
            player.awardStat(BartendingStats.CONSUME_ALCOHOL.get(),
                    (int) Math.ceil(getConsumedChemicalContent(AlcoholHandler.INSTANCE.getId(), stack, world)));
        }
        return super.finishUsingItem(stack, world, user);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 16;
    }

    @Override
    public float getChemicalContent(ResourceLocation id, ItemStack stack, Level level) {
        return super.getChemicalContent(id, stack, level) + ("bartending:alcohol".equals(id.toString()) ? alcohol +
                (stack.getOrCreateTagElement(DRINK_DATA_NBT_KEY).contains("Deviation") ?
                        stack.getOrCreateTagElement(DRINK_DATA_NBT_KEY).getInt("Deviation") : 0) : 0);
    }

}
