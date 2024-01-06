package net.tommy.somerandomstuff.entitys;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

public class PetSlimeEntityModel extends EntityModel<PetSlimeEntity> {
    private final ModelPart base;
    private final ModelPart inside;
    private final ModelPart leftEye;
    private final ModelPart rightEye;

    public PetSlimeEntityModel(ModelPart modelPart) {
        //RenderLayer.getEntityTranslucent(new Identifier(SomeRandomStuff.MOD_ID, "textures/entity/pet_slime/pet_slime.png"))
        super(RenderLayer::getEntityTranslucent);
        this.base = modelPart.getChild(EntityModelPartNames.BODY);
        this.inside = modelPart.getChild(EntityModelPartNames.CUBE);
        this.leftEye = modelPart.getChild(EntityModelPartNames.LEFT_EYE);
        this.rightEye = modelPart.getChild(EntityModelPartNames.RIGHT_EYE);
    }

    @Override
    public void setAngles(PetSlimeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        ImmutableList.of(this.base, this.inside, this.leftEye, this.rightEye)
                .forEach((modelRenderer) -> modelRenderer.render(matrices, vertices, light, overlay, red, green, blue, alpha));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-6F, 12F, -6F, 12F, 12F, 12F), ModelTransform.pivot(0F, 0F, 0F));
        modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(27,38).cuboid(-5f, 13.5f, -5f, 9f, 9f, 9f), ModelTransform.pivot(0f,0f,0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_EYE, ModelPartBuilder.create().uv(0,39).cuboid(-3f, 16f, -7f, 2f, 3f, 2f), ModelTransform.pivot(0f,0f,0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_EYE, ModelPartBuilder.create().uv(0,39).cuboid(1f, 16f, -7f, 2f, 3f, 2f), ModelTransform.pivot(0f,0f,0f));
        return TexturedModelData.of(modelData,64,64);
    }
}
