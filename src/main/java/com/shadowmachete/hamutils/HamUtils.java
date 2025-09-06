package com.shadowmachete.hamutils;

import com.shadowmachete.hamutils.gui.HamTextBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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
    public static final String VERSION = "1.1.0";

    public static final String CATEGORY_HAMUTILS = "key.categories.hamutils";

    @SideOnly(Side.CLIENT)
    private static Minecraft mc;

    private HamChatUtils chatUtils;
    public static final HamLogger logger = new HamLogger();

    private boolean hypixelConnected = false;
    private static boolean inPKD = false;
    private static boolean hidePlayers = false;

    private HamData data;
    private double deltaTime;
    private long previousTime = System.nanoTime();
    private long currentTime = System.nanoTime();
    private boolean timerRunning = false;
    private double winRate = 0;

    public static KeyBinding incrementWinKey;
    public static KeyBinding decrementWinKey;
    public static KeyBinding toggleTimerKey;
    public static KeyBinding resetTimer;
    public static KeyBinding hidePlayersButton;

    private int lastWidth = -1;
    private int lastHeight = -1;
    public static final int borderMargin = 5;
    public final int singleBoxHeightGap = 10;

    public static HamTextBox aveWinTextBox;
    public static HamTextBox numWinTextBox;

    public static Minecraft getMinecraft() {
        return mc;
    }

    public static boolean isInPKD() {
        return inPKD;
    }

    public static boolean hidePlayers() {
        return hidePlayers;
    }

    private void updateAveTimerText() {
        long minutes = (long) winRate / 60;
        long seconds = (long) (winRate - minutes * 60);

        String time = String.format("%d:%02d", minutes, seconds);
        aveWinTextBox.setBody(timerRunning ? time : "Timer paused");
    }

    private void updateNumWinText() {
        numWinTextBox.setBody(String.valueOf(data.numWins));
    }

//    @Mod.EventHandler
//    public void preInit(FMLPreInitializationEvent event) throws Exception {
//        HamSSLHandler.addMojangCert();
//    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        data = HamData.load();

        mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);

        MinecraftForge.EVENT_BUS.register(this);

        chatUtils = new HamChatUtils(mc);
//        pkdSkinFix = new HamPKDSkinFix(mc);
//        MinecraftForge.EVENT_BUS.register(pkdSkinFix);
        MinecraftForge.EVENT_BUS.register(new HamPKDHideFoliage());
        MinecraftForge.EVENT_BUS.register(new HamPKDHidePlayers());

        aveWinTextBox = new HamTextBox(sr.getScaledWidth(), borderMargin, "Ave win time: ");
        numWinTextBox = new HamTextBox(sr.getScaledWidth(), borderMargin + singleBoxHeightGap, "Num wins: ");

        // Initialise key bindings
        incrementWinKey = new KeyBinding("key.hamutils.incrementWin", Keyboard.KEY_EQUALS, CATEGORY_HAMUTILS);
        ClientRegistry.registerKeyBinding(incrementWinKey);

        decrementWinKey = new KeyBinding("key.hamutils.decrementWin", Keyboard.KEY_MINUS, CATEGORY_HAMUTILS);
        ClientRegistry.registerKeyBinding(decrementWinKey);

        toggleTimerKey = new KeyBinding("key.hamutils.toggleTimer", Keyboard.KEY_0, CATEGORY_HAMUTILS);
        ClientRegistry.registerKeyBinding(toggleTimerKey);

        resetTimer = new KeyBinding("key.hamutils.resetTimer", Keyboard.KEY_9, CATEGORY_HAMUTILS);
        ClientRegistry.registerKeyBinding(resetTimer);

        hidePlayersButton = new KeyBinding("key.hamutils.hidePlayers", Keyboard.KEY_Z, CATEGORY_HAMUTILS);
        ClientRegistry.registerKeyBinding(hidePlayersButton);
    }

    // Save data when game stops
    @SubscribeEvent
    public void onClientStop(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        timerRunning = false;
        data.save();
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        int w = event.gui.width;
        int h = event.gui.height;

        if (w != lastWidth || h != lastHeight) {
            lastWidth = w;
            lastHeight = h;
            updateAveTimerText();
            updateNumWinText();
        }
    }

    // Render textbox every frame
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (mc.thePlayer == null) return;

        if (!mc.gameSettings.showDebugInfo) {
            // Average Win rate
            aveWinTextBox.render();

            // Number of wins
            numWinTextBox.render();
        }
    }

    public void updateDeltaTime() {
        previousTime = currentTime;
        currentTime = System.nanoTime();
        deltaTime = (currentTime - previousTime) / 1e9; // in seconds
    }

    // Subscribe to HypixelModApi when log on
    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        String server = event.manager.getRemoteAddress().toString();
        hypixelConnected = server.contains("hypixel.net");
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!hypixelConnected) return;

        if (event.entity == Minecraft.getMinecraft().thePlayer &&
                Minecraft.getMinecraft().getCurrentServerData() != null &&
                Minecraft.getMinecraft().getCurrentServerData().serverIP.contains("hypixel.net")) {

            logger.info("Connected to Hypixel. Subscribing to packets...");
            chatUtils.sendClientMessage("Timer is off. Press " + Keyboard.getKeyName(toggleTimerKey.getKeyCode()) + " to start.");

            HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
            HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, this::handle);

            // store the current skin to use later in PKD
//            HamPKDSkinFix.storeSkin(mc.getSession().getUsername());

            hypixelConnected = false;
        }
    }

    // Handle key press each tick
    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        while (incrementWinKey.isPressed() && timerRunning) {
            data.numWins++;
            logger.info("Increment Win key pressed, numWins = " + data.numWins);
            updateDeltaTime();
            data.timeElapsed += deltaTime;
            winRate = data.timeElapsed / data.numWins;
            updateAveTimerText();
            updateNumWinText();
        }

        while (decrementWinKey.isPressed() && data.numWins > 0 && timerRunning) {
            data.numWins--;
            logger.info("Decrement Win key pressed, numWins = " + data.numWins);
            data.timeElapsed -= deltaTime;
            data.timeElapsed = Math.max(data.timeElapsed, 0);
            winRate = data.numWins != 0 ? data.timeElapsed / data.numWins : 0;
            updateAveTimerText();
            updateNumWinText();
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
            updateAveTimerText();
        }

        while (resetTimer.isPressed()) {
            data = new HamData();
            deltaTime = 0;
            previousTime = System.nanoTime();
            currentTime = System.nanoTime();
            winRate = 0;
            updateAveTimerText();
            updateNumWinText();
        }

        while (hidePlayersButton.isPressed()) {
            hidePlayers = !hidePlayers;
            chatUtils.sendClientMessage("Hide players " + (hidePlayers ? "enabled" : "disabled") + ".");
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

        boolean lastInPKD = inPKD;

        // Preview rooms when joining parkour duels
        if (packet.getMode().isPresent() &&
                packet.getMode().get().equals("DUELS_PARKOUR_EIGHT")) {
            chatUtils.sendCommand("/pr");
            inPKD = true;
        } else {
            inPKD = false;
        }

        if (lastInPKD != inPKD) {
            mc.renderGlobal.loadRenderers();
        }
    }
}
