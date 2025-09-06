package com.shadowmachete.hamutils.gui;

import com.shadowmachete.hamutils.HamUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.awt.Point;

public class HamTextBox {
    private Point position;
    private Point defaultPosition;
    private EnumChatFormatting titleColour = EnumChatFormatting.AQUA;
    private EnumChatFormatting bodyColour = EnumChatFormatting.WHITE;
    private String titleText;
    private String bodyText;
    private int stringWidth = 0;
    private boolean isActive = true;
    public boolean customPosition = false;

    public HamTextBox(int x, int y, String title) {
        this.position = new Point(x, y);
        this.defaultPosition = position;
        this.titleText = title;
    }

    public int getX() {
        return this.position.x;
    }

    public void setX(int newX) {
        this.position.x = newX;
    }

    public int getY() {
        return this.position.y;
    }

    public void setY(int newY) {
        this.position.y = newY;
    }

    public void setTitle(String newTitle) {
        this.titleText = newTitle;
        stringWidth = HamUtils.getMinecraft().fontRendererObj.getStringWidth(this.getFormattedText());
        this.checkPositions();
    }

    public String getTitleText() {
        return this.titleText;
    }

    public void setBody(String newBody) {
        this.bodyText = newBody;
        stringWidth = HamUtils.getMinecraft().fontRendererObj.getStringWidth(this.getFormattedText());
        this.checkPositions();
    }

    public String getBodyText() {
        return this.bodyText;
    }

    public void setTitleColour(EnumChatFormatting colour) {
        this.titleColour = colour;
    }

    public void setBodyColour(EnumChatFormatting colour) {
        this.bodyColour = colour;
    }

    public String getFormattedTitleText() {
        return this.titleColour + this.titleText;
    }

    public String getFormattedBodyText() {
        return this.bodyColour + this.bodyText;
    }

    public String getFormattedText() {
        return this.getFormattedTitleText() + this.getFormattedBodyText();
    }

    public int getStringWidth() {
        return this.stringWidth;
    }

    private void checkPositions() {
        Minecraft mc = HamUtils.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);

        // Checks if the textbox is off the screen to the right
        if (!this.customPosition || this.getX() + this.stringWidth > sr.getScaledWidth() - HamUtils.borderMargin) {
            this.setX(sr.getScaledWidth() - HamUtils.borderMargin - mc.fontRendererObj.getStringWidth(this.getFormattedText()));
        }
    }

    public void render() {
        if (!isActive) return;

        HamUtils.getMinecraft().fontRendererObj.drawString(this.getFormattedText(), this.position.x, this.position.y, 0xFFFFFF); // White text
    }
}
