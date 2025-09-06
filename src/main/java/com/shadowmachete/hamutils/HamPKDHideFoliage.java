package com.shadowmachete.hamutils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HamPKDHideFoliage {
    // All foliage blocks to replace
    private static final ModelResourceLocation[] FOLIAGE_BLOCKS = {
        new ModelResourceLocation("minecraft:tall_grass"),
        new ModelResourceLocation("minecraft:fern"),
        new ModelResourceLocation("minecraft:double_grass", "half=upper"),
        new ModelResourceLocation("minecraft:double_grass", "half=lower"),
        new ModelResourceLocation("minecraft:double_fern", "half=upper"),
        new ModelResourceLocation("minecraft:double_fern", "half=lower"),
    };

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        TextureMap blockAtlas = Minecraft.getMinecraft().getTextureMapBlocks();
        HamUtils.logger.info("Replacing foliage models with DynamicTransparentBlockModel for PKD");

//        Block block = Blocks.double_plant; // Or any block
//        for (IBlockState state : block.getBlockState().getValidStates()) {
//            ModelResourceLocation mrl = Minecraft.getMinecraft()
//                    .getBlockRendererDispatcher()
//                    .getBlockModelShapes()
//                    .getBlockStateMapper()
//                    .putAllStateModelLocations()
//                    .get(state);
//            HamUtils.logger.info("Double plant state: %s -> model: %s", state, mrl);
//        }

        for (ModelResourceLocation mrl: FOLIAGE_BLOCKS) {
            try {
                IBakedModel oldModel = event.modelRegistry.getObject(mrl);

                if (oldModel != null) {
                    HamUtils.logger.info("Wrapping model for block: %s", mrl);
                    // Wrap with dynamic model that hides when in PKD
                    IBakedModel dynamicModel = new DynamicTransparentBlockModel(oldModel);
                    event.modelRegistry.putObject(mrl, dynamicModel);
                } else {
                    HamUtils.logger.warn("No model found for block: %s", mrl);
                }
            } catch (Exception e) {
                HamUtils.logger.error("Error processing block: " + mrl, e);
            }
        }
    }
}
