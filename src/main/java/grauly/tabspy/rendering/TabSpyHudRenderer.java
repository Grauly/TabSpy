package grauly.tabspy.rendering;

import grauly.tabspy.TabSpy;
import grauly.tabspy.config.TabSpyConfig;
import grauly.tabspy.mixin.PlayerListHudAccessor;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.regex.Pattern;

import static grauly.tabspy.TabSpy.MODID;
import static grauly.tabspy.TabSpy.regex;

public class TabSpyHudRenderer implements HudRenderCallback {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier warningImage = new Identifier(MODID, "textures/icon/icon_regex_warning.png");
    private static final int warningResolution = 16;
    private static final int nameSpacing = 3;

    @Override
    public void onHudRender(MatrixStack matrices, float tickDelta) {
        matrices.push();
        int width = mc.getWindow().getScaledWidth();
        int height = mc.getWindow().getScaledHeight();

        if (mc.player == null || mc.player.networkHandler == null || !TabSpy.isShowing) {
            return;
        }

        recompilePattern();
        if (TabSpy.regex == null) {
            Text warningText = Text.translatable("text.tabspy.regexerror");
            DrawableHelper.drawTextWithShadow(matrices,mc.textRenderer,warningText,width - 5 - mc.textRenderer.getWidth(warningText), height/2 - mc.textRenderer.fontHeight/2,-1);
            return;
        }

        var relevantPlayerEntries = getRelevantPlayerEntries();
        int maxNameWidth = 0;
        for (PlayerListEntry playerListEntry : relevantPlayerEntries) {
            maxNameWidth = Math.max(mc.textRenderer.getWidth(mc.inGameHud.getPlayerListHud().getPlayerName(playerListEntry)), maxNameWidth);
        }
        int x = width - maxNameWidth - 5;
        int y = height / 2 - (relevantPlayerEntries.size() * (mc.textRenderer.fontHeight)) - (relevantPlayerEntries.size() * nameSpacing);
        for (PlayerListEntry playerListEntry : relevantPlayerEntries) {
            mc.textRenderer.drawWithShadow(
                    matrices,
                    mc.inGameHud.getPlayerListHud().getPlayerName(playerListEntry),
                    x,
                    y,
                    playerListEntry.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1
            );
            y += mc.textRenderer.fontHeight + nameSpacing;
        }
        matrices.pop();
    }

    private void recompilePattern() {
        TabSpyConfig config = AutoConfig.getConfigHolder(TabSpyConfig.class).getConfig();
        if(regex == null || !config.regex.equals(regex.pattern())) {
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
