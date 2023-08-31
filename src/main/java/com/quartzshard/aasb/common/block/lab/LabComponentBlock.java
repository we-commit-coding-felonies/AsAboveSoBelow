package com.quartzshard.aasb.common.block.lab;

import net.minecraft.world.level.block.Block;

/**
 * this class is used for blocks that, together, form a completed lab
 * includes both tables and labwares
 */
public class LabComponentBlock extends Block {
	
	/**
	 * Labwares define recipe, tables define I/O
	 */
	public enum LabComponentType {
		LABWARE,
		TABLE
	}
	
	public final LabComponentType LAB_TYPE;

	public LabComponentBlock(LabComponentType type, Properties props) {
		super(props);
		LAB_TYPE = type;
	}

}
