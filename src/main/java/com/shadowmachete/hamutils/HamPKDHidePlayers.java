package com.shadowmachete.hamutils;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HamPKDHidePlayers {
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (HamUtils.hidePlayers()
                && !event.entityPlayer.getUniqueID().equals(HamUtils.getMinecraft().getSession().getProfile().getId())) {
            event.setCanceled(true);
        }
    }
}
