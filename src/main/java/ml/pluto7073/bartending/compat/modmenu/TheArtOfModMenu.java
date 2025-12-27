package ml.pluto7073.bartending.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import ml.pluto7073.bartending.client.TheArtOfClient;
import ml.pluto7073.bartending.client.config.BartendingClientConfig;
import ml.pluto7073.bartending.foundations.alcohol.AlcDisplayType;
import ml.pluto7073.pdapi.client.gui.PDConfigScreen;
import net.minecraft.network.chat.Component;

public class TheArtOfModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PDConfigScreen.INSTANCE::apply;
    }

}
