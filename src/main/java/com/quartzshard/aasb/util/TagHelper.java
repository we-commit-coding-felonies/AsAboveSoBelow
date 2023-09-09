package com.quartzshard.aasb.util;

import net.minecraft.tags.TagKey;

import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;

/**
 * functions for working with tags <br>
 * also has Mekanism's lazy tag lookup stuff <br>
 * https://github.com/mekanism/Mekanism/tree/1.18.x/src/main/java/mekanism/common/tags
 */
public class TagHelper {
	public static <T extends IForgeRegistryEntry<T>> ITagManager<T> manager(IForgeRegistry<T> registry) {
		ITagManager<T> tags = registry.tags();
		if (tags == null) {
			throw new IllegalStateException("Expected " + registry.getRegistryName() + " to have tags.");
		}
		return tags;
	}
	
	public record LazyTagLookup<T extends IForgeRegistryEntry<T>>(TagKey<T> key, Lazy<ITag<T>> lazyTag) {

		@SuppressWarnings("null")
		public static <T extends IForgeRegistryEntry<T>> LazyTagLookup<T> create(IForgeRegistry<T> registry, TagKey<T> key) {
			return new LazyTagLookup<>(key, Lazy.of(() -> manager(registry).getTag(key)));
		}

		public ITag<T> tag() {
			return lazyTag.get();
		}

		public boolean contains(T element) {
			return tag().contains(element);
		}

		public boolean isEmpty() {
			return tag().isEmpty();
		}
	}
}
