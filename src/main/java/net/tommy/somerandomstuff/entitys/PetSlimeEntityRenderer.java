package net.tommy.somerandomstuff.entitys;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.tommy.somerandomstuff.SomeRandomStuff;
import net.tommy.somerandomstuff.client.SomeRandomStuffClient;

public class PetSlimeEntityRenderer extends MobEntityRenderer<PetSlimeEntity, PetSlimeEntityModel> {
    public PetSlimeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PetSlimeEntityModel(context.getPart(SomeRandomStuffClient.MODEL_PET_SLIME_LAYER)), 0.5f);
    }
    @Override
    public Identifier getTexture(PetSlimeEntity entity) {
        return new Identifier(SomeRandomStuff.MOD_ID, "textures/entity/pet_slime/pet_slime.png");
    }

    @Override
    public void render(PetSlimeEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        super.render(entity, entityYaw, partialTicks, matrixStack, vertexConsumerProvider, light);
    }
}
