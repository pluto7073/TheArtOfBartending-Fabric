package ml.pluto7073.bartending.foundations.mixin;

import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.foundations.step.AddingItemBrewerStep;
import ml.pluto7073.pdapi.client.gui.DrinkWorkstationMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DrinkWorkstationMenu.class)
public abstract class DrinkWorkstationMenuMixin extends ItemCombinerMenu {

    private DrinkWorkstationMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Inject(at = @At("RETURN"), method = "isValidBlock", cancellable = true)
    private void bartending$TestValidCounter(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) cir.setReturnValue(state.is(BartendingBlocks.COUNTER_TOP));
    }

    @Inject(at = @At("HEAD"), method = "createResult", cancellable = true)
    private void bartending$TestSecondaryAlcoholicDrinkRecipe(CallbackInfo ci) {
        if (!AlcoholicDrinks.BASE_ITEMS.contains(inputSlots.getItem(0).getItem())) return;
        if (inputSlots.getItem(0).isDamaged()) {
            ci.cancel();
            return;
        }
        ItemStack addition = inputSlots.getItem(1);
        ItemStack bottle = inputSlots.getItem(0).copy();
        ListTag steps = bottle.getOrCreateTag().getList("BrewingSteps", Tag.TAG_COMPOUND);
        if (!steps.isEmpty()) {
            CompoundTag step = steps.getCompound(steps.size() - 1);
            if (step.getString("type").equals(AddingItemBrewerStep.TYPE_ID)) {
                if (step.getString("item").equals(BuiltInRegistries.ITEM.getKey(addition.getItem()).toString())) {
                    step.putInt("count", step.getInt("count") + 1);
                    resultSlots.setItem(0, bottle);
                    ci.cancel();
                    return;
                }
            }
        }
        CompoundTag step = new CompoundTag();
        step.putString("type", AddingItemBrewerStep.TYPE_ID);
        step.putString("item", BuiltInRegistries.ITEM.getKey(addition.getItem()).toString());
        step.putInt("count", 1);
        steps.add(step);
        bottle.getOrCreateTag().put("BrewingSteps", steps);
        resultSlots.setItem(0, bottle);
        ci.cancel();
    }

}
