package com.quartzshard.aasb.api.alchemy.aspects;

import com.quartzshard.aasb.api.alchemy.IAlchemicalFlow;

public class AspectWay implements IAlchemicalFlow<AspectWay> {
	private final long value;
	
	public AspectWay(long value) {
		this.value = value;
	}
	
	/**
	 * gets the value of this way
	 * @return long
	 */
	public long getValue() {
		return this.value;
	}

	@Override
	public boolean flows(AspectWay to) {
		long v = to.getValue();
		return value/2 <= v && v <= value*2;
	}

	@Override
	public boolean perpendicular(AspectWay to) {
		// TODO Probably nothing? should return false always probably?
		// sol: maybe should return true if to.value == (this.value/2 || this.value*2)? but probably not
		return false;
	}

	@Override
	public boolean violates(AspectWay to) {
		return !this.flows(to);
	}

}
