package ml.pluto7073.bartending.foundations.datagen;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.pdapi.PDAPI;
import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.addition.DrinkAddition.Builder;
import ml.pluto7073.pdapi.addition.action.RestoreHungerAction;
import ml.pluto7073.pdapi.datagen.provider.DrinkAdditionProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ml.pluto7073.bartending.TheArtOfBartending.asId;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BartendingAdditionProvider extends DrinkAdditionProvider {

    private static final Builder APPLE = builder()
            .changesColor(true).color(12356169)
            .addAction(new RestoreHungerAction(6, 4));
    private static final Builder COCOA_BEAN = builder()
            .addAction(new RestoreHungerAction(1, 0));
    private static final Builder COFFEE_BEAN = builder()
            .chemical(PDAPI.asId("caffeine"), 5)
            .addAction(new RestoreHungerAction(1, 0));
    private static final Builder LIME = builder()
            .changesColor(true).color(14149836).volume(0.5)
            .addAction(new RestoreHungerAction(3, 2));
    private static final Builder SWEET_BERRIES = builder()
            .changesColor(true).color(0x93203b).volume(0.25)
            .addAction(new RestoreHungerAction(1, 1));
    private static final Builder ORANGE = builder()
            .changesColor(true).color(0xe8b025).volume(3)
            .addAction(new RestoreHungerAction(3, 1));

    public BartendingAdditionProvider(FabricDataOutput out) {
        super(out);
    }

    @Override
    public void buildAdditions(BiConsumer<ResourceLocation, DrinkAddition> consumer) {
        BiConsumer<ResourceLocation, DrinkAddition> fruitfulfun = withConditions(consumer, DefaultResourceConditions.allModsLoaded("fruitfulfun"));
        BartendingItems.SHOTS.forEach((alc, shot) -> {
            int amount = BrewingUtil.getAlcohol(alc, 1.5f);
            builder().changesColor(alc.color() != 0xFFFFFF)
                    .color(alc.color())
                    .chemical(asId("alcohol"), amount)
                    .volume(1.5)
                    .save(BuiltInRegistries.ITEM.getKey(shot), consumer);
        });
        APPLE.save(asId("apple"), consumer);
        COCOA_BEAN.save(asId("cocoa_beans"), consumer);
        COFFEE_BEAN.save(asId("compat/plutoscoffee/coffee_bean"),
                withConditions(consumer, DefaultResourceConditions.allModsLoaded("plutoscoffee")));
        LIME.save(asId("compat/fruitfulfun/lime"), fruitfulfun);
        SWEET_BERRIES.save(asId("sweet_berries"), consumer);
        ORANGE.save(asId("compat/fruitfulfun/orange"), fruitfulfun);
    }

}
