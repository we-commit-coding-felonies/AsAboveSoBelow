package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;

/**
 * This should really never be used directly, use one of its derivatives instead <br>
 * For items that can handle one of the item keybinds
 */
public interface IHandleKeybind {
	
	/**
	 * Called serverside by KeyPressPacket to ask an item to handle the given PressContext
	 * @return if the keybind was handled
	 */
	boolean handle(PressContext ctx);
}
