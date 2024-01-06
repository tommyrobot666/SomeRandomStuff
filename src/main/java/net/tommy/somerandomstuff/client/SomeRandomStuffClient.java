package net.tommy.somerandomstuff.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.particle.SuspendParticle;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.tommy.somerandomstuff.SomeRandomStuff;
import net.tommy.somerandomstuff.entitys.PetSlimeEntityModel;
import net.tommy.somerandomstuff.entitys.PetSlimeEntityRenderer;

@Environment(EnvType.CLIENT)
public class SomeRandomStuffClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_PET_SLIME_LAYER = new EntityModelLayer(new Identifier(SomeRandomStuff.MOD_ID, "pet_slime"), "main");
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(SomeRandomStuff.BABY_PET_SLIME, SuspendParticle.HappyVillagerFactory::new);

        EntityRendererRegistry.register(SomeRandomStuff.PET_SLIME, PetSlimeEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_PET_SLIME_LAYER, PetSlimeEntityModel::getTexturedModelData);
    }
}
