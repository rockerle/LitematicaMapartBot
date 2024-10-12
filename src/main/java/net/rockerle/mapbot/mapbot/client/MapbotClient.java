package net.rockerle.mapbot.mapbot.client;

import fi.dy.masa.litematica.data.DataManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.rockerle.mapbot.mapbot.client.playeractions.InventoryActionManager;
import net.rockerle.mapbot.mapbot.client.playeractions.PlacementVerifierWrapper;
import net.rockerle.mapbot.mapbot.client.playeractions.PlayerWalker;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class MapbotClient implements ClientModInitializer {
    KeyBinding test, setStorageKey;
    public static MinecraftClient mc;
    private PlayerWalker walker;
    private PlacementVerifierWrapper verWrapper;
    private static InventoryActionManager invManager;
    public static boolean runningBot;

    public static BlockPos lastInteracted = null;

    @Override
    public void onInitializeClient() {
        mc = MinecraftClient.getInstance();
        test = KeyBindingHelper.registerKeyBinding(new KeyBinding("start_map_building", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "MapartBot"));
        setStorageKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("storage toggle", InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_O, "MapartBot"));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> this.runningBot = false);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            walker = new PlayerWalker(mc.player);
            verWrapper = new PlacementVerifierWrapper(walker);
            invManager = new InventoryActionManager(mc);
            runningBot = false;
        });

        ClientTickEvents.START_CLIENT_TICK.register(ctx -> {
            if (runningBot) {
                if(!verWrapper.schematicFinished()) {
                    this.runningBot = false;
                    try {
                        verWrapper.verify(DataManager.getSchematicPlacementManager().getSelectedSchematicPlacement());
                    } catch (Exception e) {
                        mc.player.sendMessage(Text.of("§4No Placement Selected!"), true);
                    }
                }

            }
            if (test.wasPressed() && mc.player != null) {
                this.runningBot = !this.runningBot;
            }
            if(setStorageKey.wasPressed()){
                mc.player.sendMessage(Text.of(invManager.cachToString()));
            }
            if (walker != null && walker.isActive())
                walker.tick();
        });
    }

    public static InventoryActionManager getInvManager(){
        return invManager;
    }
}