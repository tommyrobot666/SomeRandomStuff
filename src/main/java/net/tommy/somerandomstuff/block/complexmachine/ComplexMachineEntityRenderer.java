package net.tommy.somerandomstuff.block.complexmachine;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ComplexMachineEntityRenderer implements BlockEntityRenderer<ComplexMachineEntity> {
    public ComplexMachineEntityRenderer(BlockEntityRendererFactory.Context ctx){
    }

    @Override
    public void render(ComplexMachineEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        int lightInside = Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up()),
                Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().down()),
                        Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().east()),
                                Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().west()),
                                        Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().north()),
                                                WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().south()))))));
        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(entity.parts.get(0).item_that_looks_similar),ModelTransformationMode.GROUND, lightInside, OverlayTexture.DEFAULT_UV, matrices,vertexConsumers,entity.getWorld(),0);
        matrices.pop();
    }
}
