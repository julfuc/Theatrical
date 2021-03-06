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

package com.georlegacy.general.theatrical.blocks.fixtures;

import com.georlegacy.general.theatrical.TheatricalMain;
import com.georlegacy.general.theatrical.api.ISupport;
import com.georlegacy.general.theatrical.blocks.fixtures.base.BlockHangable;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.guis.handlers.enumeration.GUIID;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.fixtures.TileFresnel;
import com.georlegacy.general.theatrical.tiles.fixtures.TileMovingHead;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMovingHead extends BlockHangable implements ITileEntityProvider, IHasTileEntity {

    public static final PropertyBool FLIPPED = PropertyBool.create("flipped");

    public BlockMovingHead() {
        super("moving_head", new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP});
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
        EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
        float hitZ) {
        if (!worldIn.isRemote) {
            if (!playerIn.isSneaking()) {
                playerIn.openGui(TheatricalMain.instance, GUIID.FIXTURE_MOVING_HEAD.getNid(), worldIn,
                    pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return super
            .onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }


    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        BlockPos up = pos.offset(EnumFacing.UP);
        if (worldIn.getBlockState(up).getBlock() != Blocks.AIR && worldIn.getBlockState(up).getBlock() instanceof ISupport) {
            ISupport support = (ISupport) worldIn.getBlockState(up).getBlock();
            for (EnumFacing facing : allowedPlaces) {
                if (support.getBlockPlacementDirection().equals(facing)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
        float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FLIPPED,
            facing.getOpposite() == EnumFacing.UP);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileMovingHead();
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileMovingHead.class;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos,
        EnumFacing side) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(0, 0, 0, 1, 1.1, 1);
        return axisAlignedBB;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileMovingHead tileFresnel = (TileMovingHead) worldIn.getTileEntity(pos);
        if (tileFresnel != null && tileFresnel.getLightBlock() != null) {
            worldIn.setBlockToAir(tileFresnel.getLightBlock());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (world.getTileEntity(pos) instanceof TileFresnel) {
            TileMovingHead tileFresnel = (TileMovingHead) world.getTileEntity(pos);
            if (tileFresnel != null) {
                return (int) (tileFresnel.getIntensity() * 4 / 255);
            }
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta)).withProperty(FLIPPED, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | (state.getValue(FACING)).getHorizontalIndex();

        if (state.getValue(FLIPPED))
        {
            i |= 4;
        }
        return i;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (state.getValue(FLIPPED)) {
            super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, FLIPPED);
    }
}
