package net.tommy.somerandomstuff.entitys;

import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.tommy.somerandomstuff.SomeRandomStuff;
import net.tommy.somerandomstuff.client.SomeRandomStuffClient;;

public class PetSlimeEntityRenderer extends MobEntityRenderer<PetSlimeEntity, PetSlimeEntityModel> {
    public PetSlimeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PetSlimeEntityModel(context.getPart(SomeRandomStuffClient.MODEL_PET_SLIME_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(PetSlimeEntity entity) {
        return new Identifier(SomeRandomStuff.MOD_ID, "textures/entity/pet_slime/pet_slime.png");
    }
}
