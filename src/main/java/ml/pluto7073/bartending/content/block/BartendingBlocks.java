package ml.pluto7073.bartending.content.block;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.content.item.BartendingItems;
import ml.pluto7073.bartending.foundations.block.VineCropBlock;
import ml.pluto7073.pdapi.block.PDBlocks;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BartendingBlocks {

    public static final List<Block> DROPS_SELF_LIST = new ArrayList<>();
    public static final HashMap<WoodType, FermentingBarrelBlock> BARRELS = Util.make(() -> {
        HashMap<WoodType, FermentingBarrelBlock> blocks = new HashMap<>();
        for (WoodType type : WoodType.values().toList()) {
            if (!new ResourceLocation(type.name()).getNamespace().equals("minecraft")) continue;
            Block planks = BuiltInRegistries.BLOCK.get(new ResourceLocation(type.name() + "_planks"));
            blocks.put(type, new FermentingBarrelBlock(type, BlockBehaviour.Properties.copy(Blocks.BARREL).mapColor(planks.defaultMapColor())));
        }
        return blocks;
    });

    public static final Block BOILER = new BoilerBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON).pushReaction(PushReaction.BLOCK));
    public static final Block BOTTLER = new BottlerBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND));
    public static final Block DISTILLERY = new DistilleryBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND));
    public static final Block COUNTER_TOP = new CountertopBlock(BlockBehaviour.Properties.copy(PDBlocks.DRINK_WORKSTATION));
    public static final Block VINE_FRAME = new EmptyVineFrameBlock(BlockBehaviour.Properties.of()
            .mapColor(Blocks.OAK_PLANKS.defaultMapColor())
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F, 3.0F)
            .sound(SoundType.WOOD)
            .forceSolidOff()
            .ignitedByLava());
    public static final Block RED_GRAPE_PLANT = new VineCropBlock(() -> BartendingItems.RED_GRAPE_SEEDS, BlockBehaviour.Properties.copy(VINE_FRAME));
    public static final Block GREEN_GRAPE_PLANT = new VineCropBlock(() -> BartendingItems.GREEN_GRAPE_SEEDS, BlockBehaviour.Properties.copy(VINE_FRAME));

    private static void register(String id, Block block) {
        register(id, block, false);
    }

    private static void register(String id, Block block, boolean dropsSelf) {
        Registry.register(BuiltInRegistries.BLOCK, TheArtOfBartending.asId(id), block);
        if (dropsSelf) DROPS_SELF_LIST.add(block);
    }

    public static void init() {
        register("boiler", BOILER, true);
        register("bottler", BOTTLER, true);
        register("distillery", DISTILLERY, true);
        register("countertop", COUNTER_TOP, true);
        register("vine_frame", VINE_FRAME, true);
        register("red_grape_plant", RED_GRAPE_PLANT);
        register("green_grape_plant", GREEN_GRAPE_PLANT);

        BARRELS.forEach((type, block) -> register(type.name() + "_fermenting_barrel", block, true));
    }

}
