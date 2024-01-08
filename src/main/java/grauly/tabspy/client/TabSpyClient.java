package grauly.tabspy.client;

import grauly.tabspy.TabSpy;
import grauly.tabspy.config.TabSpyConfig;
import grauly.tabspy.rendering.TabSpyHudRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.math.Color;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.lwjgl.glfw.GLFW;

public class TabSpyClient implements ClientModInitializer {

    KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.tabspy.activate", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_N, // The keycode of the key
            "category.tabspy.name" // The translation key of the keybinding's category.
    ));

    @Override
    public void onInitializeClient() {
        AutoConfig.register(TabSpyConfig.class, GsonConfigSerializer::new);
        HudRenderCallback.EVENT.register(new TabSpyHudRenderer());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                TabSpy.isShowing = !TabSpy.isShowing;
                MutableText messageText = Text.translatable("text.tabspy.status");
                MutableText statusText = Text.empty();
                if (TabSpy.isShowing) {
                    statusText = Text.translatable("text.tabspy.status.active");
                    statusText.setStyle(Style.EMPTY.withColor(Color.ofRGB(0f,1f,0f).getColor()));
                } else {
                    statusText = Text.translatable("text.tabspy.status.inactive");
                    statusText.setStyle(Style.EMPTY.withColor(Color.ofRGB(1f, 0f, 0f).getColor()));
                }
                messageText.append(statusText);
                client.player.sendMessage(messageText,true);
            }
        });
    }
}
