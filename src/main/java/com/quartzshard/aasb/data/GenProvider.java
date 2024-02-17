package com.quartzshard.aasb.data;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.init.object.EntityInit;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

/**
 * i honestly have no idea what im doing here and am just plagiarizing Create
 * https://github.com/Creators-of-Create/Create/blob/9a70cfff41bd5e0f3eb0bbd397ac3e53038b5ff6/src/main/java/com/simibubi/create/foundation/data/GeneratedEntriesProvider.java
 */
public class GenProvider extends DatapackBuiltinEntriesProvider {
	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.DAMAGE_TYPE, EntityInit::bootstrapDmg);

	public GenProvider(PackOutput packOut, CompletableFuture<Provider> regs) {
		super(packOut, regs, BUILDER, Set.of(AASB.MODID));
	}
	
	@Override
	public String getName() {
		return AASB.MODID.toUpperCase() + " | 'Generated Registry Entries'? idk lol";
	}
}
