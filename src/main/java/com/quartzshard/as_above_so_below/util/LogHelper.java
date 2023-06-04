package com.quartzshard.as_above_so_below.util;

import java.util.Map;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

/**
 * Has some nice functions to make outputting stuff to log a tad easier. <br>
 * Meant to be used to help standardize the format of logs
 * 
 * @author solunareclipse1
 */
public class LogHelper {
    public static final Logger LOGGER = LogUtils.getLogger();
    
    /**
	 * Prints a standardized, short DEBUG to the logs
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 */
	public static void debug(String name, String label) {
		LOGGER.info("|"+ label +"| - Debug from ["+ name +"]");
	}
    /**
	 * Prints a standardized DEBUG to the logs
	 * containing a simple 1 line message
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 */
	public static void debug(String name, String label, String msg) {
		LOGGER.info("|"+ label +"| - Debug from ["+ name +"]:");
		LOGGER.info(msg);
	}
    /**
	 * Prints a standardized DEBUG to the logs
	 * with some extra info contained in a HashMap
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 * @param data A Hashtable<String,String>, each key/value pair will be printed on a different line
	 */
	public static void debug(String name, String label, String msg, Map<String,String> data) {
		LOGGER.info("|"+ label +"| - Debug from ["+ name +"]:");
		LOGGER.info(msg);
		for (Map.Entry<String,String> entry : data.entrySet()) {
			LOGGER.info(entry.getKey() +" : "+ entry.getValue());
		}
	}

    /**
	 * Prints a standardized INFO to the logs
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 */
	public static void info(String name, String label, String msg) {
		LOGGER.info("|"+ label +"| - Message from ["+ name +"]:");
		LOGGER.info(msg);
	}
    /**
	 * Prints a standardized INFO to the logs
	 * with some extra info contained in a HashMap
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 * @param data A Map<String,String>, each entry will be printed on a new line
	 */
	public static void info(String name, String label, String msg, Map<String,String> data) {
		LOGGER.info("|"+ label +"| - Message from ["+ name +"]:");
		LOGGER.info(msg);
		for (Map.Entry<String,String> entry : data.entrySet()) {
			LOGGER.info(entry.getKey() +" : "+ entry.getValue());
		}
	}
	
    /**
	 * Prints a standardized WARN to the logs
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 */
	public static void warn(String name, String label, String msg) {
		LOGGER.warn("|"+ label +"| - Warning from ["+ name +"]:");
		LOGGER.warn(msg);
	}
    /**
	 * Prints a standardized WARN to the logs
	 * with some extra info contained in a HashMap
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 * @param data A Hashtable<String,String>, each entry will be printed on a new line
	 */
	public static void warn(String name, String label, String msg, Map<String,String> data) {
		LOGGER.info("|"+ label +"| - Warning from ["+ name +"]:");
		LOGGER.info(msg);
		for (Map.Entry<String,String> entry : data.entrySet()) {
			LOGGER.info(entry.getKey() +" : "+ entry.getValue());
		}
	}
	
    /**
	 * Prints a standardized ERROR to the logs
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing the error
	 */
	public static void error(String name, String label, String msg) {
		LOGGER.error("|"+ label +"| - Error from ["+ name +"]:");
		LOGGER.error(msg);
	}
    /**
	 * Prints a standardized ERROR to the logs
	 * with some extra info contained in a HashMap
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 * @param data A Hashtable<String,String>, each entry will be printed on a new line
	 */
	public static void error(String name, String label, String msg, Map<String,String> data) {
		LOGGER.info("|"+ label +"| - Error from ["+ name +"]:");
		LOGGER.info(msg);
		for (Map.Entry<String,String> entry : data.entrySet()) {
			LOGGER.info(entry.getKey() +" : "+ entry.getValue());
		}
	}
}