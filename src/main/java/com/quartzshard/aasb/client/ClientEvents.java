package com.quartzshard.aasb.client;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.client.sound.SentientWhispersAmbient;
import com.quartzshard.aasb.common.entity.projectile.SentientArrowEntity;
import com.quartzshard.aasb.util.ClientUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AASB.MODID, value = Dist.CLIENT)
public class ClientEvents {
	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof SentientArrowEntity projectile && ClientUtil.mc().mouseHandler.isMouseGrabbed()) {
			ClientUtil.mc().getSoundManager().play(new SentientWhispersAmbient(projectile, 392));
		}
	}
}
