package net.tommy.somerandomstuff.mixin;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Registries.class)
public interface RegistriesAccessor {
    @Accessor("DEFAULT_ENTRIES")
    static Map<Identifier, Supplier<?>> getDefaultEntries(){
        throw new AssertionError();
    }
    @Accessor("DEFAULT_ENTRIES")
    static void setDefaultEntries(Map<Identifier, Supplier<?>> entries){
        throw new AssertionError();
    }
}
