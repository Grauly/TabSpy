package grauly.tabspy.rendering;

import grauly.tabspy.TabSpy;
import grauly.tabspy.config.TabSpyConfig;
import grauly.tabspy.mixin.PlayerListHudAccessor;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.regex.Pattern;

import static grauly.tabspy.TabSpy.regex;

public class TabSpyHudRenderer implements HudRenderCallback {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final int nameSpacing = 3;

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        context.getMatrices().push();
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        if (mc.player == null || mc.player.networkHandler == null || !TabSpy.isShowing) {
            return;
        }

        recompilePattern();
        if (TabSpy.regex == null) {
            Text warningText = Text.translatable("text.tabspy.regexerror");
            context.drawTextWithShadow(mc.textRenderer, warningText, screenWidth - 5 - mc.textRenderer.getWidth(warningText), screenHeight / 2 - mc.textRenderer.fontHeight / 2, -1);
            return;
        }

        var relevantPlayerEntries = getRelevantPlayerEntries();
        int maxNameWidth = 0;
        for (PlayerListEntry playerListEntry : relevantPlayerEntries) {
            maxNameWidth = Math.max(mc.textRenderer.getWidth(mc.inGameHud.getPlayerListHud().getPlayerName(playerListEntry)), maxNameWidth);
        }
        int x = screenWidth - maxNameWidth - 5;
        int y = screenHeight / 2 - (relevantPlayerEntries.size() * (mc.textRenderer.fontHeight)) - (relevantPlayerEntries.size() * nameSpacing);
        for (PlayerListEntry playerListEntry : relevantPlayerEntries) {
            context.drawTextWithShadow(
                    mc.textRenderer,
                    mc.inGameHud.getPlayerListHud().getPlayerName(playerListEntry),
                    x,
                    y,
                    playerListEntry.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1
            );
            y += mc.textRenderer.fontHeight + nameSpacing;
        }
        context.getMatrices().pop();
    }

    private void recompilePattern() {
        TabSpyConfig config = AutoConfig.getConfigHolder(TabSpyConfig.class).getConfig();
        if (regex == null || !config.regex.equals(regex.pattern())) {
            try {
                regex = Pattern.compile(config.regex);
            } catch (Exception e) {
                regex = null;
            }
        }
    }

    private List<PlayerListEntry> getRelevantPlayerEntries() {
        return mc.player.networkHandler.getListedPlayerListEntries()
                .stream()
                .sorted(PlayerListHudAccessor.ordering())
                .limit(80L)
                .filter(p -> mc.inGameHud.getPlayerListHud().getPlayerName(p).getString().matches(regex.pattern()))
                .toList();
    }
}
