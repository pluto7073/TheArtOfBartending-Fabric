package ml.pluto7073.bartending.foundations.mixin;

import com.mojang.authlib.GameProfile;
import ml.pluto7073.bartending.foundations.alcohol.AbsorbedAlcoholHandler;
import ml.pluto7073.bartending.foundations.config.BartendingCommonConfig;
import ml.pluto7073.bartending.foundations.util.BrewingUtil;
import ml.pluto7073.bartending.foundations.alcohol.blackout.BlackoutData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Unique private short bartending$BlackoutAttemptCooldown = 600;
    @Unique private BlackoutData bartending$BlackoutData = null;

    private ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void bartending$TickBlackouts(CallbackInfo ci) {
        if (!BartendingCommonConfig.INSTANCE.doBlackout) return;
        float alc = BrewingUtil.calculateBAC(AbsorbedAlcoholHandler.INSTANCE.get(this));
        if (alc < 0.11f) {
            bartending$BlackoutData = null;
            bartending$BlackoutAttemptCooldown = 600;
            return;
        }

        if (bartending$BlackoutData == null) {
            --bartending$BlackoutAttemptCooldown;
            if (bartending$BlackoutAttemptCooldown <= 0) {
                bartending$BlackoutAttemptCooldown = 600;
                int chance = random.nextInt(0, alc > 0.20f ? 1 : 7);
                if (chance == 0) bartending$BlackoutData = new BlackoutData((ServerPlayer) (Object) this, false);
            }
        } else {
            bartending$BlackoutData.tick();
        }
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    public void bartending$ReadBlackoutData(CompoundTag compound, CallbackInfo ci) {
        if (!compound.contains("BlackoutData") || !BartendingCommonConfig.INSTANCE.doBlackout) {
            bartending$BlackoutAttemptCooldown = 600;
            bartending$BlackoutData = null;
            return;
        }
        CompoundTag data = compound.getCompound("BlackoutData");
        bartending$BlackoutAttemptCooldown = data.getShort("AttemptCooldown");
        if (data.getBoolean("WillBlackout")) {
            bartending$BlackoutData = new BlackoutData((ServerPlayer) (Object) this, true);
            bartending$BlackoutData.loadData(data);
        } else {
            bartending$BlackoutData = null;
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    public void bartending$SaveBlackoutData(CompoundTag compound, CallbackInfo ci) {
        CompoundTag data = new CompoundTag();
        data.putShort("AttemptCooldown", bartending$BlackoutAttemptCooldown);
        boolean willBlackout = bartending$BlackoutData != null;
        data.putBoolean("WillBlackout", willBlackout);
        if (willBlackout) {
            bartending$BlackoutData.saveData(data);
        }

        compound.put("BlackoutData", data);
    }

}
