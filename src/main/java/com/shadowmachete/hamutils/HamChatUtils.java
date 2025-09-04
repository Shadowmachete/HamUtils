package com.shadowmachete.hamutils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class HamChatUtils {

    private final Minecraft mc;

    public HamChatUtils(Minecraft mc) {
        this.mc = mc;
    }

    /**
     * Sends a message to the client chat without sending it to the server.
     */
    public void sendClientMessage(String message) {
        if (mc.thePlayer != null && mc.ingameGUI != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public void sendCommand(String command) {
        if (command != null && !command.isEmpty()) {
            if (!command.startsWith("/")) {
                command = "/" + command;
            }
            mc.thePlayer.sendChatMessage(command);
        }
    }
}