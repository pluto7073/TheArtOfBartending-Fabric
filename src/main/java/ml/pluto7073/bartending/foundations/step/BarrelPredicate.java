package ml.pluto7073.bartending.foundations.step;

import com.google.common.collect.Lists;
import ml.pluto7073.bartending.content.block.FermentingBarrelBlock;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BarrelPredicate implements Predicate<Block> {

    public static final BarrelPredicate ANY = new BarrelPredicate(
            () -> BartendingBlocks.BARRELS.values().stream().toList()
    );

    private final Supplier<List<FermentingBarrelBlock>> allowed;

    public BarrelPredicate(FermentingBarrelBlock... allowed) {
        this.allowed = () -> Lists.newArrayList(allowed);
    }

    private BarrelPredicate(Supplier<List<FermentingBarrelBlock>> allowed) {
        this.allowed = allowed;
    }

    public FermentingBarrelBlock first() {
        return allowed.get().get(0);
    }

    @Override
    public boolean test(Block block) {
        if (this == ANY) return true;
        if (!(block instanceof FermentingBarrelBlock fermentingBarrelBlock)) return false;
        return allowed.get().contains(fermentingBarrelBlock);
    }

    public Ingredient asIngredient() {
        return Ingredient.of(allowed.get().toArray(ItemLike[]::new));
    }

    public static BarrelPredicate ofWood(WoodType... types) {
        return new BarrelPredicate(Arrays.stream(types).map(BartendingBlocks.BARRELS::get).toArray(FermentingBarrelBlock[]::new));
    }
}
