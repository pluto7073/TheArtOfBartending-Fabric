package ml.pluto7073.bartending.client.config;

import ml.pluto7073.bartending.TheArtOfBartending;
import ml.pluto7073.bartending.foundations.alcohol.AlcDisplayType;
import ml.pluto7073.plutonium.annotations.EnumOption;
import ml.pluto7073.plutonium.config.ClientConfig;

public class BartendingClientConfig extends ClientConfig {

    public static final BartendingClientConfig INSTANCE = new BartendingClientConfig();

    @EnumOption("PROOF") public AlcDisplayType alcoholDisplayType;

    private BartendingClientConfig() {
        super("bartending", TheArtOfBartending.LOGGER, true);
        load();
    }

}
