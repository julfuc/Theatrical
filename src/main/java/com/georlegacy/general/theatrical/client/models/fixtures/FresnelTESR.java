/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical.client.models.fixtures;

import static net.minecraft.util.EnumFacing.Axis.X;
import static net.minecraft.util.EnumFacing.Axis.Z;

import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.init.TheatricalModels;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FresnelTESR extends TileEntitySpecialRenderer<TileEntityFresnel> {

    private static float lastBrightnessX, lastBrightnessY;


    @Override
    public void render(TileEntityFresnel te, double x, double y, double z, float partialTicks,
        int destroyStage, float a) {
        EnumFacing direction = te.getWorld().getBlockState(te.getPos())
            .getValue(BlockDirectional.FACING);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0F, 1F, 0F);
        GlStateManager.translate(0F, 1F, 1F);
        GlStateManager.disableLighting();
        renderLight(te, direction, partialTicks);
        double distance = te.getDistance();
        GlStateManager.translate(0F, -1.5F, -1F);
        GlStateManager.translate(0.5F, .5F, .5F);
        GlStateManager.translate(-.5F, -.5F, -.5F);
        if (te.getPower() > 0) {
            renderLightBeam(te, partialTicks, ((te.getPower() / 255) * 0.4f), 0.25, distance,
                te.getGelType().getHex());
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityFresnel te) {
        return true;
    }


    public void renderLight(TileEntityFresnel te, EnumFacing direction, float partialTicks) {
        GlStateManager.translate(0.5F, 0, -.5F);
        GlStateManager.rotate(direction.getHorizontalAngle() , 0, 1, 0);
        GlStateManager.translate(-.5F, 0, 0.5F);
        renderHookBar(te);
        GlStateManager.translate(0.5F, 0, -.6F);
        GlStateManager.rotate(te.prevPan + (te.getPan() - te.prevPan) * partialTicks, 0, 1, 0);
        GlStateManager.translate(-.5F, 0, 0.6F);
        renderLightHandle(te);
        GlStateManager.translate(0.7F, -.75F, -.64F);
        GlStateManager.rotate(te.prevTilt + (te.getTilt() - te.prevTilt) * partialTicks,
            1, 0, 0);
        GlStateManager.translate(-.7F, .75F, 0.64F);
        renderLightBody(te);
    }

    public void renderModel(IBakedModel model, TileEntityFresnel te){
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft()
            .getBlockRendererDispatcher();
        BlockPos pos = te.getPos();
        IBlockState state = getWorld().getBlockState(pos);
        state = state.getBlock().getActualState(state, getWorld(), pos);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        bufferbuilder.setTranslation(-pos.getX(), -1 - pos.getY(), -1 - pos.getZ());
        bufferbuilder.color(255, 255, 255, 255);
        blockrendererdispatcher.getBlockModelRenderer()
            .renderModel(getWorld(), model, state, pos, bufferbuilder, false);
        bufferbuilder.setTranslation(0, 0, 0);
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public void renderHookBar(TileEntityFresnel te) {
        IBlockState state = getWorld().getBlockState(te.getPos());
        state = state.getBlock().getActualState(state, getWorld(), te.getPos());
        renderModel(state.getValue(BlockFresnel.ON_BAR) ? TheatricalModels.FRESNEL_HOOK_BAR : TheatricalModels.FRESNEL_HOOK, te);
    }

    public void renderLightHandle(TileEntityFresnel te) {
       renderModel(TheatricalModels.FRESNEL_HANDLE, te);
    }

    public void renderLightBody(TileEntityFresnel te) {
        renderModel(TheatricalModels.FRESNEL_BODY, te);
    }

    public static void pushBrightness(int u, int t) {
        lastBrightnessX = OpenGlHelper.lastBrightnessX;
        lastBrightnessY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, u, t);
    }

    public static void pushMaxBrightness() {
        pushBrightness(240, 240);
    }

    public static void popBrightness() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX,
            lastBrightnessY);
    }

    public void renderLightBeam(TileEntityFresnel tileEntityFresnel, float partialTicks,
        float alpha, double beamSize, double length, int color) {
        //TODO: Fix me so the lamps don't disappear when you look through me.
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder render = tessellator.getBuffer();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (alpha * 255);
        pushMaxBrightness();

        //Open GL Stuff
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.8, 0);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int func = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
        float ref = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
        GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableTexture2D();

        render.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        double width = beamSize;
        double endMultiplier = tileEntityFresnel.getFocus();


        //Do the actual beam vertexes
        render.pos(width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        render.pos(width, width, 0).color(r, g, b, a).endVertex();
        render.pos(width, -width, 0).color(r, g, b, a).endVertex();
        render.pos(width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();

        render.pos(-width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        render.pos(-width, -width, 0).color(r, g, b, a).endVertex();
        render.pos(-width, width, 0).color(r, g, b, a).endVertex();
        render.pos(-width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();

        render.pos(-width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        render.pos(-width, width, 0).color(r, g, b, a).endVertex();
        render.pos(width, width, 0).color(r, g, b, a).endVertex();
        render.pos(width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();

        render.pos(width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        render.pos(width, -width, 0).color(r, g, b, a).endVertex();
        render.pos(-width, -width, 0).color(r, g, b, a).endVertex();
        render.pos(-width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0)
            .endVertex();
        tessellator.draw();

        //OpenGL Stuff
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();
        GlStateManager.alphaFunc(func, ref);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        popBrightness();
    }
}
