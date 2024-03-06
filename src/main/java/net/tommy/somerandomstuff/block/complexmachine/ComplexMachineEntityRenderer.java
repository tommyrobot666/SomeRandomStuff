package net.tommy.somerandomstuff.block.complexmachine;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class ComplexMachineEntityRenderer implements BlockEntityRenderer<ComplexMachineEntity> {
    public ComplexMachineEntityRenderer(BlockEntityRendererFactory.Context ctx){
    }

    @Override
    public void render(ComplexMachineEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        //matrices.translate(0.5, 0.5, 0.5);
        int lightInside = Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up()),
                Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().down()),
                        Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().east()),
                                Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().west()),
                                        Math.max(WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().north()),
                                                WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().south()))))));
        for (MachinePart part : entity.parts){
            BlockPos to_translate = listIndexToPos(entity.parts.indexOf(part),ComplexMachineEntity.PARTS_IN_EACH_BLOCK,ComplexMachineEntity.PARTS_IN_EACH_BLOCK);
            matrices.translate(to_translate.getX()/ComplexMachineEntity.PARTS_IN_EACH_BLOCK,to_translate.getY()/ComplexMachineEntity.PARTS_IN_EACH_BLOCK,to_translate.getZ()/ComplexMachineEntity.PARTS_IN_EACH_BLOCK);
            MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(part.item_that_looks_similar),ModelTransformationMode.GROUND, lightInside, OverlayTexture.DEFAULT_UV, matrices,vertexConsumers,entity.getWorld(),0);
        }
        matrices.pop();
    }

    private static BlockPos listIndexToPos(int idx, int width, int height){
        int a = width * height;
        int b = idx - a * (idx/a);
        return new BlockPos(idx/a,b/width,b%width);
    }
}
