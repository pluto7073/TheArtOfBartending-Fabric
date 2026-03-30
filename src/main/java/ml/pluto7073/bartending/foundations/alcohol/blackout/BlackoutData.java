package ml.pluto7073.bartending.foundations.alcohol.blackout;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.foundations.alcohol.AlcoholHandler;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

import java.time.Instant;
import java.util.List;

public class BlackoutData {

    public static final float BAC_MULTIPLIER_AFTER_BLACKOUT = (float) Math.pow(0.5, 6000.0 / AlcoholHandler.ALCOHOL_HALF_LIFE_TICKS);

    private final ServerPlayer player;
    private short blackoutIn;
    private long blackoutEndsIn;
    private AfterWakeup afterWakeup;

    public BlackoutData(ServerPlayer player, boolean fromLoad) {
        this.player = player;
        RandomSource random = player.getRandom();
        blackoutIn = 600;
        blackoutEndsIn = 0;
        if (!fromLoad) this.player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 700, 2, true, false, false));
        // Prepare what happens after
        int chance = random.nextInt(1, 100);
        if (chance < 5) {
            afterWakeup = AfterWakeup.BLACKOUT_LOCATION;
        } else if (chance < 30) {
            afterWakeup = AfterWakeup.BED;
        } else if (chance < 55) {
            afterWakeup = AfterWakeup.WORLD_SPAWN;
        } else {
            afterWakeup = AfterWakeup.RANDOM_RADIUS;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public void tick() {
        long now = Instant.now().toEpochMilli();
        if (blackoutEndsIn > now) {
            long remaining = blackoutEndsIn - now;
            player.connection.disconnect(Component.translatable("disconnect.bartending.still_blacked_out", BrewingUtil.getTimeString((int) (remaining / 1000))));
            return;
        }
        if (blackoutIn <= -100) {
            float alc = AlcoholHandler.INSTANCE.get(player);
            AlcoholHandler.INSTANCE.set(player, alc * BAC_MULTIPLIER_AFTER_BLACKOUT);
            switch (afterWakeup) {
                case BED -> {
                    BlockPos respawnPos = player.getRespawnPosition();
                    ResourceKey<Level> respawnDim = player.getRespawnDimension();
                    if (respawnPos == null) {
                        respawnPos = player.level().getSharedSpawnPos();
                        respawnDim = player.level().dimension();
                    }
                    player.teleportTo(player.getServer().getLevel(respawnDim), respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), 0, 0);
                }
                case WORLD_SPAWN -> {
                    BlockPos worldSpawn = player.level().getSharedSpawnPos();
                    player.teleportTo(worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ());
                }
                case RANDOM_RADIUS -> {
                    CommandSourceStack stack = player.getServer().createCommandSourceStack().withSuppressedOutput();
                    Vec2 center = new Vec2((float) player.position().x, (float) player.position().z);
                    String name = player.getScoreboardName();
                    try {
                        SpreadPlayersCommand.spreadPlayers(stack, center, 250, 750, player.level().getMaxBuildHeight(), false, List.of(player));
                    } catch (CommandSyntaxException e) {
                        TheArtOfBartending.LOGGER.error("Could not spread player", e);
                    }
                    //player.getServer().getCommands().performPrefixedCommand(stack, "execute as " + name + " at @s run spreadplayers ~ ~ 500 1000 false @s");
                }
            }
            return;
        }
        --blackoutIn;
        if (blackoutIn <= 0) {
            blackoutIn = -100;
            blackoutEndsIn = now + 5 * 60 * 1000;
            player.connection.disconnect(Component.translatable("disconnect.bartending.blacked_out"));
            // Do blackout
        }
    }

    public void loadData(CompoundTag tag) {
        blackoutIn = tag.getShort("BlackoutIn");
        afterWakeup = AfterWakeup.valueOf(tag.getString("AfterWakeup"));
        if (player.hasEffect(MobEffects.DARKNESS) && blackoutIn > 1) {
            player.removeEffect(MobEffects.DARKNESS);
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, blackoutIn + 100, 2, true, false, false));
        }
        if (tag.contains("BlackoutEndsIn")) {
            blackoutEndsIn = tag.getLong("BlackoutEndsIn");
        }
    }

    public void saveData(CompoundTag tag) {
        tag.putShort("BlackoutIn", blackoutIn);
        tag.putString("AfterWakeup", afterWakeup.name());
        if (blackoutEndsIn != 0) {
            tag.putLong("BlackoutEndsIn", blackoutEndsIn);
        }
    }

    public enum AfterWakeup {
        BED, WORLD_SPAWN, BLACKOUT_LOCATION, RANDOM_RADIUS
    }

}
