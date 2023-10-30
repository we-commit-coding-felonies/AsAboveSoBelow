package com.quartzshard.aasb.api.alchemy.aspects.stack.chamber;

import com.quartzshard.aasb.api.alchemy.IAlchemicalFlow;
import com.quartzshard.aasb.api.alchemy.aspects.stack.AspectStack;

/**
 * Defines a contract for things that store AspectStacks, but is by no means required <br>
 * For this to expose its contents to the outside world, you will probably want an AspectHandler as well
 * <p>
 * If this doesnt suit your needs, feel free to do your own thing <br>
 * As a result, it should also not be expected for everything to universally use this
 * 
 * @param <A> The type of aspect
 * @param <S> The corresponding stack class
 */
public interface IAspectChamber<A extends IAlchemicalFlow<A>, S extends AspectStack<A>> {
	public enum AspectAction {
		EXECUTE, SIMULATE;

		public boolean execute() {
			return this == EXECUTE;
		}

		public boolean simulate() {
			return this == SIMULATE;
		}
	}
	
	/**
	 * Gets the stack of this chambers contents
	 */
	S getContents();
	
	/**
	 * Gets the current aspect amount stored by this chamber (Stack.getAmount())
	 * @return
	 */
	default int getAmount() {
		return getContents().getAmount();
	}
	
	/**
	 * Gets the maximum capacity of this chamber
	 * @return
	 */
	int getCapacity();
	
	/**
	 * Gets the remaining number of stacks this chamber can hold
	 * @return
	 */
	int spaceLeft();

	/**
	 * Checks if this chamber is EVER allowed to hold the given aspect
	 * @param aspect
	 * @return
	 */
	boolean isValid(A aspect);
	
	/**
	 * Checks if the given stack is EVER to be allowed in the chamber <br>
	 * Shorthand for isValid(stack.getAspect()) <br>
	 * Has nothing to do with capacity or stack size, see canFit(stack) instead
	 * @param stack
	 * @return
	 */
	default boolean isValid(S stack) {
		return isValid(stack.getAspect());
	}
	
	/**
	 * Checks if the given stack is able to be put into the chamber right now <br>
	 * Actual check may vary, but is usually based on stack size.
	 * Can be used to arbitrarily lock out stacks that dont meet some criteria
	 * <p>
	 * Unlike isValid(), the return value of this function may change based on state
	 * @param stack
	 * @return
	 */
	boolean canFit(S stack);
	
	/**
	 * Attempts to insert the given stack into the chamber
	 * @param stack
	 * @param action if SIMULATE, will not actually insert anything
	 * @return Remainder amount of what couldnt be inserted. Subtract this from your total
	 */
	int insert(S stack, AspectAction action);
	
	/**
	 * Attempts to extract the given stack from the chamber <br>
	 * @apiNote Remember that the aspect system is PUSH ONLY! Please be respectful of that when using this function!
	 * @param stack a requested stack. will fail if aspects dont match
	 * @param action if SIMULATE, will not actually extract anything
	 * @return The actual extracted stack, or EMPTY if nothing
	 */
	S extract(S stack, AspectAction action);
	
	/**
	 * Attempts to extract the given stack from the chamber <br>
	 * @apiNote Remember that the aspect system is PUSH ONLY! Please be respectful of that when using this function!
	 * @param amount requested amount, aspect agnostic
	 * @param action if SIMULATE, will not actually extract anything
	 * @return The actual extracted stack, or EMPTY if nothing
	 */
	S extract(int amount, AspectAction action);
	
	/**
	 * Checks whether this chamber is empty
	 * @return
	 */
	boolean isEmpty();
	
	/**
	 * Requests that the chamber void its contents <br>
	 * The chamber does not have to obey this, and will return false if it refuses <br>
	 * Will always return TRUE if the chamber is already empty, so this can be used as an "empty this if its not already empty" request
	 * @return
	 */
	boolean clear();
	
	/**
	 * Forces the chamber to void its contents <br>
	 * 
	 * @apiNote Generally, you should try to avoid using this function,
	 * its only here in case it is actually necessary for something.
	 * Please use the normal clear() function where possible
	 */
	void forceClear();
}
