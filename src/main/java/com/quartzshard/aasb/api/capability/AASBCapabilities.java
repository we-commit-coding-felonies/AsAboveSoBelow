package com.quartzshard.aasb.api.capability;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.api.capability.aspect.way.IHandleWay;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID)
public class AASBCapabilities {
	public static Capability<IHandleWay> WAY_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
	public static Capability<IHandleShape> SHAPE_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
	public static Capability<IHandleForm> FORM_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
	
	@SubscribeEvent
	public void registerCapabilities(RegisterCapabilitiesEvent evt) {
		evt.register(IHandleWay.class);
		evt.register(IHandleShape.class);
		evt.register(IHandleForm.class);
	}
}
