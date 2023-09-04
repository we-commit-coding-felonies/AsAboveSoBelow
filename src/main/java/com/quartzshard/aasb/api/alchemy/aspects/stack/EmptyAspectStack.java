package com.quartzshard.aasb.api.alchemy.aspects.stack;

import com.quartzshard.aasb.api.alchemy.aspects.AspectEmpty;

public class EmptyAspectStack extends AspectStack<AspectEmpty> {
	public static final String TYPE_KEY = "empty";
	public EmptyAspectStack() {
		super(TYPE_KEY, new AspectEmpty(), 0);
	}
	
	@Override
	public boolean setAspect(AspectEmpty aspect) {return false;}
}
