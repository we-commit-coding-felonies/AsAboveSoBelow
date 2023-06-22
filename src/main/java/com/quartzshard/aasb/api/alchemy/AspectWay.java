package com.quartzshard.aasb.api.alchemy;

public class AspectWay implements IAlchemicalFlow<AspectWay>{
	private long value;
	
	public AspectWay(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return this.value;
	}

	@Override
	public boolean flows(AspectWay to) {
		// TODO Check if the way is within double or half.
		return false;
	}

	@Override
	public boolean perpendicular(AspectWay to) {
		// TODO Probably nothing? should return false always probably?
		return false;
	}

	@Override
	public boolean violates(AspectWay to) {
		return !this.flows(to);
	}

}
