package com.quartzshard.aasb.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mojang.logging.LogUtils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * because outputting text is very challenging
 */
public class Logger {
	public static final org.slf4j.Logger LOG = LogUtils.getLogger();
    /**
	 * Prints a standardized, short DEBUG to the logs
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 */
	public static void debug(String name, String label) {
		LOG.info("|"+ label +"| - Debug from ["+ name +"]");
	}
    /**
	 * Prints a standardized DEBUG to the logs
	 * containing an extra simple 1 line message
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 */
	public static void debug(String name, String label, String msg) {
		LOG.debug("|"+ label +"| - Debug from ["+ name +"]:");
		LOG.debug(msg);
	}
    /**
	 * Prints a standardized DEBUG to the logs
	 * with some organized extra info contained in a Map
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 * @param data A Hashtable<String,String>, each key/value pair will be printed on a different line
	 */
	public static void debug(String name, String label, String msg, Map<String,String> data) {
		LOG.debug("|"+ label +"| - Debug from ["+ name +"]:");
		LOG.debug(msg);
		for (Map.Entry<String,String> entry : data.entrySet()) {
			LOG.debug(entry.getKey() +" : "+ entry.getValue());
		}
	}
	/**
	 * Prints a standardized DEBUG to the logs
	 * alongside some extra info
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 * @param data Strings of information that should also be printed, each on its own line
	 */
	public static void debug(String name, String label, String msg, String... data) {
		Map<String,String> dm = new LinkedHashMap<>();
		for (int i = 0; i < data.length; i++) {
			dm.put("("+i+")", data[i]);
		}
		debug(name, label, msg, dm);
	}

    /**
	 * Prints a standardized INFO to the logs
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 */
	public static void info(String name, String label, String msg) {
		LOG.info("|"+ label +"| - Message from ["+ name +"]:");
		LOG.info(msg);
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
		LOG.info("|"+ label +"| - Message from ["+ name +"]:");
		LOG.info(msg);
		for (Map.Entry<String,String> entry : data.entrySet()) {
			LOG.info(entry.getKey() +" : "+ entry.getValue());
		}
	}
	/**
	 * Prints a standardized INFO to the logs
	 * alongside some extra info
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 * @param data Strings of information that should also be printed, each on its own line
	 */
	public static void info(String name, String label, String msg, String... data) {
		Map<String,String> dm = new LinkedHashMap<>();
		for (int i = 0; i < data.length; i++) {
			dm.put("("+i+")", data[i]);
		}
		info(name, label, msg, dm);
	}
	
    /**
	 * Prints a standardized WARN to the logs
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 */
	public static void warn(String name, String label, String msg) {
		LOG.warn("|"+ label +"| - Warning from ["+ name +"]:");
		LOG.warn(msg);
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
		LOG.warn("|"+ label +"| - Warning from ["+ name +"]:");
		LOG.warn(msg);
		for (Map.Entry<String,String> entry : data.entrySet()) {
			LOG.warn(entry.getKey() +" : "+ entry.getValue());
		}
	}
	/**
	 * Prints a standardized WARN to the logs
	 * alongside some extra info
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 * @param data Strings of information that should also be printed, each on its own line
	 */
	public static void warn(String name, String label, String msg, String... data) {
		Map<String,String> dm = new LinkedHashMap<>();
		for (int i = 0; i < data.length; i++) {
			dm.put("("+i+")", data[i]);
		}
		warn(name, label, msg, dm);
	}
	
    /**
	 * Prints a standardized ERROR to the logs
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing the error
	 */
	public static void error(String name, String label, String msg) {
		LOG.error("|"+ label +"| - Error from ["+ name +"]:");
		LOG.error(msg);
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
		LOG.error("|"+ label +"| - Error from ["+ name +"]:");
		LOG.error(msg);
		for (Map.Entry<String,String> entry : data.entrySet()) {
			LOG.error(entry.getKey() +" : "+ entry.getValue());
		}
	}
	/**
	 * Prints a standardized ERROR to the logs
	 * alongside some extra info
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing things
	 * @param data Strings of information that should also be printed, each on its own line
	 */
	public static void error(String name, String label, String msg, String... data) {
		Map<String,String> dm = new LinkedHashMap<>();
		for (int i = 0; i < data.length; i++) {
			dm.put("("+i+")", data[i]);
		}
		error(name, label, msg, dm);
	}
	
	/**
	 * Prints a standardized message to a player's chat
	 * 
	 * @param name A name for what is printing this
	 * @param label A label for this, meant for searching
	 * @param msg A short message describing the error
	 * @param playet The player to send the message to
	 */
	public static void chat(String name, String label, String msg, Player player) {
		player.sendSystemMessage(Component.literal("|"+ label +"| - Message from ["+ name +"]:"));
		player.sendSystemMessage(Component.literal(msg));
	}
}
