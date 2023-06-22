package com.quartzshard.aasb.api.alchemy;

public interface IAlchemicalFlow<T> {
	/**
	 * Checks if the caller flows into the arg. Order matters!
	 * @param to The Shape we're checking flow to.
	 * @return 
	 */
	public boolean flows(T to);
	
	/**
	 * Checks if the caller is perpendicular to the arg. Order matters!
	 * @param to The Shape we're checking against.
	 * @return
	 */
	public boolean perpendicular(T to);
	
	/**
	 * Checks if flow is violated when travelling from caller to arg. Order matters!
	 * @param to The Shape we're checking against.
	 * @return
	 */
	public boolean violates(T to);
}
