package grauly.tabspy.config;

import grauly.tabspy.TabSpy;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "tabspy")
public class TabSpyConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public String regex = "\\[.*(Helper|Mod|Admin|Owner)\\].*";
    public boolean enabledByDefault = false;

    @Override
    public void validatePostLoad() throws ValidationException {
        TabSpy.isShowing = enabledByDefault;
    }
}
