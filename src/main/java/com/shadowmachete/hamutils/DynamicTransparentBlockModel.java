package com.shadowmachete.hamutils;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import java.util.Collections;
import java.util.List;

public class DynamicTransparentBlockModel implements IBakedModel {
    private final IBakedModel original;

    public DynamicTransparentBlockModel(IBakedModel original) {
        this.original = original;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing face) {
        if (HamUtils.isInPKD()) return Collections.emptyList();
        return original.getFaceQuads(face);
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        if (HamUtils.isInPKD()) return Collections.emptyList();
        return original.getGeneralQuads();
    }

    // delegate other IBakedModel methods
    @Override public boolean isAmbientOcclusion() { return original.isAmbientOcclusion(); }
    @Override public boolean isGui3d() { return original.isGui3d(); }
    @Override public boolean isBuiltInRenderer() { return original.isBuiltInRenderer(); }
    @Override public TextureAtlasSprite getParticleTexture() { return original.getParticleTexture(); }
    @Override @Deprecated public ItemCameraTransforms getItemCameraTransforms() { return original.getItemCameraTransforms(); }
}
