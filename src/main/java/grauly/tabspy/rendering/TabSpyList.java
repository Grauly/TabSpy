package grauly.tabspy.rendering;

import grauly.tabspy.mixin.PlayerListHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

public class TabSpyList extends PlayerListHud {

    public TabSpyList(MinecraftClient client, InGameHud inGameHud) {
        super(client, inGameHud);
    }

    public List<PlayerListEntry> collectPlayers() {
        return MinecraftClient.getInstance().player.networkHandler.getListedPlayerListEntries().stream().sorted(PlayerListHudAccessor.ordering()).limit(80L).toList();
    }


}
