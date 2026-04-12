package ml.pluto7073.bartending.foundations.mixin;

import ml.pluto7073.bartending.content.alcohol.AlcoholicDrinks;
import ml.pluto7073.bartending.content.block.BartendingBlocks;
import ml.pluto7073.bartending.foundations.step.AddingItemBrewerStep;
import ml.pluto7073.pdapi.client.gui.DrinkWorkstationMenu;
import ml.pluto7073.pdapi.recipes.DrinkWorkstationRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DrinkWorkstationMenu.class)
public abstract class DrinkWorkstationMenuMixin extends ItemCombinerMenu {

    @Shadow
    protected abstract void decrementStack(int slot, Player player);

    @Shadow
    public abstract void createResult();

    private DrinkWorkstationMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Inject(at = @At("RETURN"), method = "mayPickup", cancellable = true)
    private void bartending$MayPickupBottleOverride(Player player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) return;
        if (AlcoholicDrinks.BASE_ITEMS.contains(resultSlots.getItem(0).getItem())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("RETURN"), method = "isValidBlock", cancellable = true)
    private void bartending$TestValidCounter(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) cir.setReturnValue(state.is(BartendingBlocks.COUNTER_TOP));
    }

    @Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lml/pluto7073/pdapi/client/gui/DrinkWorkstationMenu;decrementStack(ILnet/minecraft/world/entity/player/Player;)V", ordinal = 1), cancellable = true)
    private void bartending$InjectItemAddFullStackFunctionality(Player player, ItemStack stack, CallbackInfo ci) {
        if (!AlcoholicDrinks.BASE_ITEMS.contains(stack.getItem())) {
            return;
        }
        inputSlots.setItem(1, ItemStack.EMPTY);
        access.execute((world, pos) -> {
            world.levelEvent(10000, pos, 0);
        });
        ci.cancel();
    }

    @Inject(at = @At("RETURN"), method = "lambda$createInputSlotDefinitions$4", cancellable = true)
    private void bartending$AllowBottlesAsInput(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) return;
        if (AlcoholicDrinks.BASE_ITEMS.contains(stack.getItem())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("RETURN"), method = "lambda$createInputSlotDefinitions$6", cancellable = true)
    private void bartending$AllowAnyAddition(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(at = @At("HEAD"), method = "createResult", cancellable = true)
    private void bartending$TestSecondaryAlcoholicDrinkRecipe(CallbackInfo ci) {
        if (!AlcoholicDrinks.BASE_ITEMS.contains(inputSlots.getItem(0).getItem())) return;
        if (inputSlots.getItem(0).isDamaged()) {
            resultSlots.setItem(0, ItemStack.EMPTY);
            ci.cancel();
            return;
        }
        ItemStack addition = inputSlots.getItem(1);
        if (addition.isEmpty()) {
            resultSlots.setItem(0, ItemStack.EMPTY);
            ci.cancel();
            return;
        }
        ItemStack bottle = inputSlots.getItem(0).copy();
        ListTag steps = bottle.getOrCreateTag().getList("BrewingSteps", Tag.TAG_COMPOUND);
        if (!steps.isEmpty()) {
            CompoundTag step = steps.getCompound(steps.size() - 1);
            if (step.getString("type").equals(AddingItemBrewerStep.TYPE_ID)) {
                if (step.getString("item").equals(BuiltInRegistries.ITEM.getKey(addition.getItem()).toString())) {
                    step.putInt("count", step.getInt("count") + addition.getCount());
                    resultSlots.setItem(0, bottle);
                    ci.cancel();
                    return;
                }
            }
        }
        CompoundTag step = new CompoundTag();
        step.putString("type", AddingItemBrewerStep.TYPE_ID);
        step.putString("item", BuiltInRegistries.ITEM.getKey(addition.getItem()).toString());
        step.putInt("count", addition.getCount());
        steps.add(step);
        bottle.getOrCreateTag().put("BrewingSteps", steps);
        resultSlots.setItem(0, bottle);
        ci.cancel();
    }

}
