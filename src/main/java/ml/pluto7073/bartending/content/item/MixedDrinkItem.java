package ml.pluto7073.bartending.content.item;

import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@MethodsReturnNonnullByDefault
public class MixedDrinkItem extends AbstractCustomizableDrinkItem {

    public MixedDrinkItem(Properties settings) {
        super(BartendingItems.COCKTAIL_GLASS, 0, settings);
    }

    @Override
    protected Item baseItem(ItemStack stack) {
        ResourceLocation id = new ResourceLocation(stack.getOrCreateTag().getString("FromItem"));
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) return baseItem;
        return item;
    }
}
