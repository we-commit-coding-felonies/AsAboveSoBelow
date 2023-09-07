package com.quartzshard.aasb.api.capability.aspect;

import com.quartzshard.aasb.api.alchemy.IAlchemicalFlow;
import com.quartzshard.aasb.api.alchemy.aspects.stack.AspectStack;

import net.minecraft.core.Direction;

/**
 * Abstract Capability Interface (dont use this directly!)<p>
 * Defines the base contract for all forms of AspectStack communication and transfer <br>
 * If you want to interact with lab blocks, you're gonna want this
 * This system is push-only, meaning something should *never* pull AspectStacks out of something else
 * 
 * @param <A> The type of aspect
 * @param <S> The corresponding stack class
 */
public interface IAspectHandler<A extends IAlchemicalFlow<A>, S extends AspectStack<A>> {
	public enum AspectType {
		WAY, SHAPE, FORM, EMPTY;

		public static <A extends IAlchemicalFlow<A>, S extends AspectStack<A>> AspectType of(S query) {
			return query.getType();
		}
		public static <A extends IAlchemicalFlow<A>> AspectType of(A query) {
			return query.type();
		}
	}
	
	/**
	 * Gets the number of Chambers this has avaliable <br>
	 * Chambers are fairly similar in concept to fluid tanks,
	 * but with a name change to help tell the two apart 
	 * @return
	 */
	int getChamberCount();
	
	/**
	 * Queries the contents of a Chamber <br>
	 * This will return a COPY of the contents, meaning any changes made to it will not be reflected at the source <br>
	 * Obviously, this also means you should discard it once you are done with it, else face possible duplication bugs
	 * 
	 * @param idx the index of the Chamber
	 * @return A copy of the Chamber's contents
	 */
	S getChamberContents(int idx);
	
	/**
	 * Gets the maximum amount of an aspect that a Chamber can store
	 * @param idx the index of the Chamber
	 * @return
	 */
	int getChamberCapacity(int idx);
	
	

	/**
	 * Defines if this can currently receive Aspects at all
	 * @return
	 */
	boolean canAccept();
	
	/**
	 * Defines if this can accept Aspects from a given side
	 * @return
	 */
	boolean canAccept(Direction side);
	
	/**
	 * Defines if this will EVER actively try to push aspects out of itself
	 * @return
	 */
	boolean canPush();
	
	/**
	 * Queries whether this will EVER attempt to push to the given side
	 * @param side
	 * @return
	 */
	boolean canPushTo(Direction side);
	
	/**
	 * Queries whether this is currently attempting to push aspects
	 * @return
	 */
	boolean isPushing();
	
	/**
	 * Causes this to perform a push attempt <br>
	 * This should pretty much never be called externally
	 * @apiNote DO NOT USE THIS TO PULL ITEMS! Aspects are PUSH ONLY! Attempting to pull items using this function may cause unexpected behavior!
	 * @return If the push was actually attempted. False if it was denied.
	 */
	boolean attemptPush();
	
	/**
	 * Defines whether this acts like a pipe <br>
	 * Does not actually make it act like a pipe, this is purely for informational purposes <br>
	 * Some things may not wish to interact with pipes, so please respect this option!
	 * @return
	 */
	boolean isPipe();
	
	
	int receiveFrom(S stack, Direction side);
}
