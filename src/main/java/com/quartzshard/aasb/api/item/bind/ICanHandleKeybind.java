package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;

public interface ICanHandleKeybind {
	
	/**
	 * Called serverside by KeyPressPacket to ask an item to handle the given PressContext
	 */
	public boolean handle(PressContext ctx);
}
