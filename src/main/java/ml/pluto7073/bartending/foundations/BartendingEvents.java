package ml.pluto7073.bartending.foundations;

import ml.pluto7073.bartending.content.item.BartendingItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class BartendingEvents {

    public static void init() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (new ResourceLocation("chests/village/village_plains_house").equals(id)) {
                tableBuilder.modifyPools(builder -> builder.with(LootItem.lootTableItem(BartendingItems.GREEN_GRAPE_SEEDS)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3))).build())
                        .with(LootItem.lootTableItem(BartendingItems.RED_GRAPE_SEEDS)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3))).build()));
            }

            if (new ResourceLocation("chests/village/village_savanna_house").equals(id)) {
                tableBuilder.modifyPools(builder -> builder.with(LootItem.lootTableItem(BartendingItems.GREEN_GRAPE_SEEDS)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 5))).build())
                        .with(LootItem.lootTableItem(BartendingItems.RED_GRAPE_SEEDS)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 5))).build()));
            }

            if (new ResourceLocation("gameplay/hero_of_the_village/farmer_gift").equals(id)) {
                tableBuilder.modifyPools(builder -> builder.with(LootItem.lootTableItem(BartendingItems.GREEN_GRAPE_SEEDS).build())); // Only one pool
            }
        });
    }

}
