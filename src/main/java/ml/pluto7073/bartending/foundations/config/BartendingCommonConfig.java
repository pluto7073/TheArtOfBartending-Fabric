package ml.pluto7073.bartending.foundations.config;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.plutonium.annotations.BooleanOption;
import ml.pluto7073.plutonium.annotations.IntOption;
import ml.pluto7073.plutonium.config.ServerConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import org.apache.logging.log4j.Logger;

public class BartendingCommonConfig extends ServerConfig {

    public static final BartendingCommonConfig INSTANCE = new BartendingCommonConfig(TheArtOfBartending.CONFIG_TYPE, false);

    @BooleanOption(defaultVal = true, hasTooltip = false) public boolean doBlackout;
    @IntOption(defaultVal = 24000) public int yearLengthTicks;

    public BartendingCommonConfig(ServerConfigType<?> type, boolean copy) {
        super("bartending", TheArtOfBartending.LOGGER, type, copy);
        load();
    }

}
