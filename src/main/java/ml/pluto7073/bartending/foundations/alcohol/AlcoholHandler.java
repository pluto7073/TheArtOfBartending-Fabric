package ml.pluto7073.bartending.foundations.alcohol;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ml.pluto7073.bartending.client.TheArtOfClient;
import ml.pluto7073.bartending.foundations.command.TAOBCommands;
import ml.pluto7073.bartending.foundations.item.AlcoholicDrinkItem;
import ml.pluto7073.bartending.foundations.BrewingUtil;
import ml.pluto7073.bartending.foundations.item.PourableBottleItem;
import ml.pluto7073.pdapi.DrinkUtil;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalHandler;
import ml.pluto7073.pdapi.addition.chemicals.ConsumableChemicalRegistry;
import ml.pluto7073.pdapi.item.AbstractCustomizableDrinkItem;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AlcoholHandler implements ConsumableChemicalHandler {

    public static final AlcoholHandler INSTANCE = (AlcoholHandler) ConsumableChemicalRegistry.register(new AlcoholHandler());
    public static final int ALCOHOL_HALF_LIFE_TICKS = 4500;
    public static final float ALCOHOL_TICK_MULTIPLIER = (float) Math.pow(0.5, 1.0 / ALCOHOL_HALF_LIFE_TICKS);

    @Override
    public void tickPlayer(Player player) {
        float alcohol = get(player);
        alcohol *= ALCOHOL_TICK_MULTIPLIER;
        set(player, alcohol);
    }

    @Override
    public float get(Player player) {
        return player.getEntityData().get(AlcoholData.PLAYER_ALCOHOL_CONTENT);
    }

    @Override
    public void add(Player player, float amount) {
        float current = player.getEntityData().get(AlcoholData.PLAYER_ALCOHOL_CONTENT);
        set(player, current + amount);
    }

    @Override
    public void set(Player player, float amount) {
        player.getEntityData().set(AlcoholData.PLAYER_ALCOHOL_CONTENT, amount);
    }

    @Override
    public Collection<MobEffectInstance> getEffectsForAmount(float amount, Player player) {
        float bac = BrewingUtil.calculateBAC(amount);
        List<MobEffectInstance> list = new ArrayList<>();
        if (bac >= 0.01f) {
            list.add(new MobEffectInstance(MobEffects.WEAKNESS, 30 * 20, 0, true, true));
        }
        if (bac > 0.05f) {
            list.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30 * 20, 0, true, true));
        }
        if (bac > 0.11f) { // Blackouts here
            list.add(new MobEffectInstance(MobEffects.CONFUSION, 30 * 20, 0, true, true));
            list.add(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30 * 20, 0, true, true));
            list.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30 * 20, 1, true, true));
            list.add(new MobEffectInstance(MobEffects.DARKNESS, 30 * 20, 1, true, true));
        }
        if (bac > 0.21f) {
            list.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30 * 20, 2, true, true));
            list.add(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30 * 20, 1, true, true));
        }
        if (bac > 0.35f) {
            list.add(new MobEffectInstance(MobEffects.POISON, 30 * 20, 1, true, true));
        }
        return list;
    }

    @Override
    public String getName() {
        return "alcohol";
    }

    @Override
    public void saveToTag(SynchedEntityData data, CompoundTag tag) {
        tag.putFloat("Alcohol", data.get(AlcoholData.PLAYER_ALCOHOL_CONTENT));
    }

    @Override
    public void loadFromTag(SynchedEntityData data, CompoundTag tag) {
        data.set(AlcoholData.PLAYER_ALCOHOL_CONTENT, tag.getFloat("Alcohol"));
    }

    @Override
    public void defineDataForPlayer(SynchedEntityData data) {
        data.define(AlcoholData.PLAYER_ALCOHOL_CONTENT, 0.0f);
    }

    @Override
    public void appendTooltip(List<Component> tooltip, float amount, ItemStack stack) {
        appendTooltip(tooltip, amount, stack, TheArtOfClient.config().getAlcoholDisplayType());
    }

    public void appendTooltip(List<Component> tooltip, float amount, ItemStack stack, AlcDisplayType display) {
        if (amount <= 0) return;
        if (DrinkUtil.getAdditionsFromStack(stack).length > 0) {
            tooltip.add(Component.translatable("tooltip.bartending.alcohol_content", amount + "g").withStyle(ChatFormatting.GOLD));
            return;
        }
        String content = switch (display) {
            case GRAMS -> amount + "g";
            case ML -> BrewingUtil.convertType(amount, AlcDisplayType.GRAMS, AlcDisplayType.ML) + "mL";
            case OUNCES -> BrewingUtil.convertType(amount, AlcDisplayType.GRAMS, AlcDisplayType.OUNCES) + "oz";
            case PROOF -> {
                if (!stack.getOrCreateTagElement(AbstractCustomizableDrinkItem.DRINK_DATA_NBT_KEY).contains("Deviation")) {
                    yield stack.getItem() instanceof AlcoholicDrinkItem drink ? drink.source.standardProof() + "" :
                            stack.getItem() instanceof PourableBottleItem pourable ? pourable.drink.standardProof() + "" : "";
                }
                amount += BrewingUtil.getAlcoholDeviation(stack);
                if (stack.getItem() instanceof PourableBottleItem pourable) {
                    AlcoholicDrink drink = pourable.drink;
                    yield BrewingUtil.getProof(drink, amount) + "";
                } else if (stack.getItem() instanceof AlcoholicDrinkItem drink) {
                    AlcoholicDrink alcDrink = drink.source;
                    yield BrewingUtil.getProof(alcDrink, amount) + "";
                }
                yield "";
            }
        };
        if (display == AlcDisplayType.PROOF) {
            tooltip.add(Component.translatable("tooltip.bartending.proof", content).withStyle(ChatFormatting.GOLD));
            return;
        }
        tooltip.add(Component.translatable("tooltip.bartending.alcohol_content", content).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public @Nullable LiteralArgumentBuilder<CommandSourceStack> getDrinkSubcommand() {
        return TAOBCommands.alcohol();
    }

    public static void init() {
        INSTANCE.appendTooltip(new ArrayList<>(), 0, ItemStack.EMPTY);
    }

}
