package net.tommy.somerandomstuff.mixin;

import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Direction.class)
public interface DirectionAccessor {
    @Accessor("ALL")
    static Direction[] getAllDirections(){
        throw new AssertionError();
    }// how was i supposted to know about .values() ?
}
