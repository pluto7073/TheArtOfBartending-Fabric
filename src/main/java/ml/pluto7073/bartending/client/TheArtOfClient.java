package ml.pluto7073.bartending.client;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.client.block.renderer.BottlerBlockEntityRenderer;
import ml.pluto7073.bartending.client.gui.BoilerScreen;
import ml.pluto7073.bartending.client.gui.BottlerScreen;
import ml.pluto7073.bartending.client.gui.DistilleryScreen;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.content.block.entity.BartendingBlockEntities;
import ml.pluto7073.bartending.content.block.entity.BoilerBlockEntity;
import ml.pluto7073.bartending.content.fluid.BartendingFluids;
import ml.pluto7073.bartending.content.gui.BartendingMenuTypes;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.content.item.ConcoctionItem;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.bartending.client.config.BartendingClientConfig;
import ml.pluto7073.bartending.foundations.util.ColorUtil;
import ml.pluto7073.pdapi.config.PDClientConfig;
import ml.pluto7073.pdapi.util.DrinkUtil;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
@Environment(EnvType.CLIENT)
public class TheArtOfClient implements ClientModInitializer {

    private static final List<Item> GLASSES = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        PDClientConfig.INSTANCE.addManagedConfig(BartendingClientConfig.INSTANCE);
        initColors();
        registerScreens();
        initRendering();
        registerItemProperties();
    }

    private static void initColors() {
        ColorProviderRegistry.BLOCK.register((state, blockTintGetter, pos, index) -> {
            if (blockTintGetter == null) return -1;
            if (pos == null) return -1;
            if (!(blockTintGetter.getBlockEntity(pos) instanceof BoilerBlockEntity boiler)) return -1;
            if (boiler.data.get(BoilerBlockEntity.BOIL_TIME_DATA) <= 0) return 4210943;
            return BrewingUtil.getColorForConcoction(boiler.getItem(BoilerBlockEntity.DISPLAY_RESULT_ITEM_SLOT_INDEX));
        }, BartendingBlocks.BOILER);

        ColorProviderRegistry.ITEM.register((stack, i) -> i > 0 ? -1 : 4210943, BartendingItems.BOILER);

        ColorProviderRegistry.ITEM.register((stack, index) -> index > 0 ? -1 : ConcoctionItem.isFailed(stack) ? 0x545252 : BrewingUtil.getColorForConcoction(stack), BartendingItems.CONCOCTION);

        ColorProviderRegistry.ITEM.register((stack, i) -> {
            DrinkAddition[] array = Arrays.stream(DrinkUtil.getAdditionsFromStack(stack))
                    .filter(DrinkAddition::changesColor).toList().toArray(new DrinkAddition[0]);
            if (array.length == 0) return i > 0 ? -1 : 4210943;
            return i > 0 ? -1 : BrewingUtil.getColorForDrinkWithDefault(stack, array[0].getColor());
        }, BartendingItems.MIXED_DRINK);

        BartendingBlocks.BARRELS.forEach((type, barrel) -> {
            String id = BuiltInRegistries.BLOCK.getKey(barrel).toString();
            int color = ColorUtil.COLORS_REGISTRY.containsKey(id) ? ColorUtil.get(BuiltInRegistries.BLOCK.getKey(barrel).toString()) : barrel.defaultMapColor().col;
            ColorProviderRegistry.BLOCK.register((state, getter, pos, index) -> color, barrel);
            ColorProviderRegistry.ITEM.register((stack, i) -> color, barrel);
        });

        BartendingItems.SHOTS.forEach((drink, item) ->
                ColorProviderRegistry.ITEM.register((stack, i) -> i > 0 ? -1 : drink.color(), item));
        BartendingItems.BOTTLES.forEach((drink, item) ->
                ColorProviderRegistry.ITEM.register((stack, i) -> i > 0 ? -1 : drink.color(), item));
        BartendingItems.GLASSES.forEach((drink, item) ->
                ColorProviderRegistry.ITEM.register((stack, i) -> i > 0 ? -1 : BrewingUtil.getColorForDrinkWithDefault(stack, drink.color()), item));
        BartendingItems.SERVING_BOTTLES.forEach((drink, item) ->
                ColorProviderRegistry.ITEM.register((stack, i) -> i > 0 ? -1 : BrewingUtil.getColorForDrinkWithDefault(stack, drink.color()), item));
    }

    private static void initRendering() {
        BlockRenderLayerMap.INSTANCE.putBlock(BartendingBlocks.DISTILLERY, RenderType.translucent());
        BlockEntityRenderers.register(BartendingBlockEntities.BOTTLER_BLOCK_ENTITY_TYPE, BottlerBlockEntityRenderer::new);

        BartendingFluids.initRendering();
    }

    private static void registerScreens() {
        MenuScreens.register(BartendingMenuTypes.BOILER_MENU_TYPE, BoilerScreen::new);
        MenuScreens.register(BartendingMenuTypes.BOTTLER_MENU_TYPE, BottlerScreen::new);
        MenuScreens.register(BartendingMenuTypes.DISTILLERY_MENU_TYPE, DistilleryScreen::new);
    }

    private static void registerItemProperties() {
        GLASSES.add(BartendingItems.COCKTAIL_GLASS);
        GLASSES.add(Items.GLASS_BOTTLE);
        GLASSES.add(BartendingItems.WINE_GLASS);
        GLASSES.add(BartendingItems.TALL_GLASS);
        GLASSES.add(BartendingItems.SHORT_GLASS);

        ItemProperties.register(BartendingItems.MIXED_DRINK, TheArtOfBartending.asId("glass"),
                new ClampedItemPropertyFunction() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public float call(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i) {
                        return unclampedCall(itemStack, clientLevel, livingEntity, i);
                    }

                    @Override
                    public float unclampedCall(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i) {
                        if (!stack.getOrCreateTag().contains("FromItem")) return 0;
                        Item glass = BuiltInRegistries.ITEM.get(
                                new ResourceLocation(stack.getOrCreateTag().getString("FromItem")));
                        if (glass == Items.AIR) return 0;
                        return GLASSES.indexOf(glass);
                    }
                });

        ItemProperties.register(BartendingItems.CONCOCTION, TheArtOfBartending.asId("just_liquid"), (itemStack, clientLevel, livingEntity, i) -> {
            if (!itemStack.getOrCreateTag().contains("JustLiquid")) return 0;
            boolean b = itemStack.getOrCreateTag().getBoolean("JustLiquid");
            return b ? 1 : 0;
        });
    }

}
