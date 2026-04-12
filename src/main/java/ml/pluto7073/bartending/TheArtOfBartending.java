package ml.pluto7073.bartending;

import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.content.block.entity.BartendingBlockEntities;
import ml.pluto7073.bartending.content.enchantment.BartendingEnchantments;
import ml.pluto7073.bartending.content.entity.effect.BartendingMobEffects;
import ml.pluto7073.bartending.content.fluid.BartendingFluids;
import ml.pluto7073.bartending.content.gui.BartendingMenuTypes;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.content.sound.BartendingSounds;
import ml.pluto7073.bartending.foundations.BartendingStats;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholHandler;
import ml.pluto7073.bartending.foundations.alcohol.BartendingEntityData;
import ml.pluto7073.bartending.foundations.command.BartendingCommands;
import ml.pluto7073.bartending.foundations.config.BartendingCommonConfig;
import ml.pluto7073.bartending.foundations.item.BartendingCreativeTabs;
import ml.pluto7073.bartending.foundations.recipe.BartendingRecipes;
import ml.pluto7073.bartending.foundations.specialty.GlassDrinkBaseSerializer;
import ml.pluto7073.pdapi.config.PDCommonConfig;
import ml.pluto7073.pdapi.util.PseudoDataFixerRegistry;
import ml.pluto7073.plutonium.PlutoniumConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class TheArtOfBartending implements ModInitializer {

    public static final String MOD_ID = "bartending";
    public static final Logger LOGGER = LogManager.getLogger("TheArtOfBartending");
    public static final ServerConfigType<BartendingCommonConfig> CONFIG_TYPE =
            Registry.register(PlutoniumConfig.SERVER_CONFIG_TYPES, asId("common"), new ServerConfigType<>(BartendingCommonConfig.INSTANCE, BartendingCommonConfig::new, true));

    @Override
    public void onInitialize() {
        PDCommonConfig.INSTANCE.addManagedConfig(CONFIG_TYPE);
        PseudoDataFixerRegistry.register(asId("everclear"), asId("grain_alcohol"));
        PseudoDataFixerRegistry.register(asId("shot_of_everclear"), asId("shot_of_grain_alcohol"));
        GlassDrinkBaseSerializer.init();
        BartendingEntityData.init();
        AlcoholHandler.init();
        Arrays.stream(BartendingRecipes.values()).forEach(BartendingRecipes::register);
        Arrays.stream(BartendingStats.values()).forEach(BartendingStats::register);
        BartendingSounds.init();
        BartendingFluids.init();
        BartendingBlocks.init();
        BartendingBlockEntities.init();
        BartendingMenuTypes.init();
        BartendingItems.init();
        BartendingEnchantments.init();
        BartendingMobEffects.init();
        BartendingCreativeTabs.init();
        BartendingCommands.init();
        LOGGER.info("Oh no its alcohol time...");
    }

    public static ResourceLocation asId(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

}
