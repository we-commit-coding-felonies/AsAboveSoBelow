package com.quartzshard.aasb.api.tag;

import net.minecraft.tags.TagKey;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;

/**
 * stolen from mekanism
 * https://github.com/mekanism/Mekanism/blob/b029d0fad666aae3119ed3272ebfee491510f536/src/main/java/mekanism/common/tags/LazyTagLookup.java
 */
public record LazyTagLookup<T>(TagKey<T> key, Lazy<ITag<T>> lazyTag) {

	public static <TYPE> LazyTagLookup<TYPE> create(IForgeRegistry<TYPE> registry, TagKey<TYPE> key) {
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

	public static <TYPE> ITagManager<TYPE> manager(IForgeRegistry<TYPE> registry) {
		ITagManager<TYPE> tags = registry.tags();
		if (tags == null) {
			throw new IllegalStateException("Expected " + registry.getRegistryName() + " to have tags.");
		}
		return tags;
	}
}