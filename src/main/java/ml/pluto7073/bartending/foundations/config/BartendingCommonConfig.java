package ml.pluto7073.bartending.foundations.config;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.plutonium.annotations.BooleanOption;
import ml.pluto7073.plutonium.annotations.DoubleOption;
import ml.pluto7073.plutonium.annotations.IntOption;
import ml.pluto7073.plutonium.config.ServerConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import org.apache.logging.log4j.Logger;

public class BartendingCommonConfig extends ServerConfig {

    public static final BartendingCommonConfig INSTANCE = new BartendingCommonConfig(TheArtOfBartending.CONFIG_TYPE, false);

    @BooleanOption(defaultVal = true, hasTooltip = false) public boolean doBlackout;
    @IntOption(defaultVal = 72000) public int yearLengthTicks;
    @DoubleOption(defaultVal = 1.0) public double fermentationTimeScale;

    public BartendingCommonConfig(ServerConfigType<?> type, boolean copy) {
        super("bartending", TheArtOfBartending.LOGGER, type, copy);
        load();
    }

}
