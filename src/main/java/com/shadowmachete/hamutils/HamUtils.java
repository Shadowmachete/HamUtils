package com.shadowmachete.hamutils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

@Mod(modid = HamUtils.MODID,
        version = HamUtils.VERSION,
        name = HamUtils.NAME,
        useMetadata = true,
        dependencies = "required-after:hypixel_mod_api")
public class HamUtils {
    public static final String MODID = "hamutils";
    public static final String NAME = "HamUtils";
    public static final String VERSION = "1.0.0";

    public static final String CATEGORY_HAMUTILS = "key.categories.hamutils";

    @SideOnly(Side.CLIENT)
    private Minecraft mc;
    private HamChatUtils chatUtils;

    public static final HamLogger logger = new HamLogger();

    private HamData data;
    private double deltaTime;
    private long previousTime = System.nanoTime();
    private long currentTime = System.nanoTime();
    private boolean timerRunning = false;
    private double winRate = 0;

    public static KeyBinding incrementWinKey;
    public static KeyBinding decrementWinKey;
    public static KeyBinding toggleTimerKey;

//    public Minecraft getMinecraft() {
//        return mc;
//    }
//
//    public GuiTextField getWinRateTextField() {
//        return winRateTextField;
//    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        data = HamData.load();

        mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(this);

        chatUtils = new HamChatUtils(mc);

        // Initialise key bindings
        incrementWinKey = new KeyBinding("key.hamutils.incrementWin", Keyboard.KEY_EQUALS, CATEGORY_HAMUTILS);
        ClientRegistry.registerKeyBinding(incrementWinKey);

        decrementWinKey = new KeyBinding("key.hamutils.decrementWin", Keyboard.KEY_MINUS, CATEGORY_HAMUTILS);
        ClientRegistry.registerKeyBinding(decrementWinKey);

        toggleTimerKey = new KeyBinding("key.hamutils.toggleTimer", Keyboard.KEY_0, CATEGORY_HAMUTILS);
        ClientRegistry.registerKeyBinding(toggleTimerKey);
    }

    // Save data when game stops
    @SubscribeEvent
    public void onClientStop(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        timerRunning = false;
        data.save();
    }

    // Render textbox every frame
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null) return;

        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            long minutes = (long) winRate / 60;
            long seconds = (long) (winRate - minutes * 60);

            int x = 10;
            int y = 10;
            mc.fontRendererObj.drawString("[" + minutes + ":" + seconds + " mins per win]", x, y, 0xFFFFFF); // White text
        }
    }

    public void updateDeltaTime() {
        previousTime = currentTime;
        currentTime = System.nanoTime();
        deltaTime = (currentTime - previousTime) / 1e9; // in seconds
    }

    // Subscribe to HypixelModApi when log on
    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        String server = event.manager.getRemoteAddress().toString();
        if (server.contains("hypixel.net")) {
            logger.info("Connected to Hypixel. Subscribing to packets...");
            chatUtils.sendClientMessage("Timer is off. Press " + toggleTimerKey.getKeyCode() + " to start.");

            HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);

            HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, this::handle);
        }
    }

    // Handle key press each tick
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        while (incrementWinKey.isPressed()) {
            if (!timerRunning) continue;

            data.numWins++;
            logger.info("Increment Win key pressed, numWins = " + data.numWins);
            updateDeltaTime();
            data.timeElapsed += deltaTime;
            winRate = data.timeElapsed / data.numWins;
        }

        while (decrementWinKey.isPressed()) {
            if (data.numWins <= 0) continue;
            if (!timerRunning) continue;

            data.numWins--;
            logger.info("Decrement Win key pressed, numWins = " + data.numWins);
            data.timeElapsed -= deltaTime;
            winRate = data.timeElapsed / data.numWins;
        }

        while (toggleTimerKey.isPressed()) {
            if (!timerRunning) {
                previousTime = System.nanoTime();
                currentTime = System.nanoTime();
                chatUtils.sendClientMessage("Timer started.");
                logger.info("Starting timer");
            } else {
                chatUtils.sendClientMessage("Timer paused.");
                logger.info("Pausing timer");
            }
            timerRunning = !timerRunning;
        }
    }

    private void handle(ClientboundLocationPacket packet) {
        // Log the new lobby joined
        String newLobby = "{";

        newLobby += "\"server\":\"" + packet.getServerName() + "\",";
        if (packet.getServerType().isPresent()) newLobby += "\"gameType\":\"" + packet.getServerType().get() + "\",";
        if (packet.getLobbyName().isPresent()) newLobby += "\"lobbyName\":\"" + packet.getLobbyName().get() + "\",";
        if (packet.getMode().isPresent()) newLobby += "\"mode\":\"" + packet.getMode().get() + "\",";
        if (packet.getMap().isPresent()) newLobby += "\"map\":\"" + packet.getMap().get() + "\",";
        newLobby = StringUtils.removeEnd(newLobby, ",") + "}";

        logger.info(newLobby);

        // Preview rooms when joining parkour duels
        if (packet.getMode().isPresent() &&
                packet.getMode().get().equals("DUELS_PARKOUR_EIGHT")) {
            chatUtils.sendCommand("/pr");
        }
    }
}
