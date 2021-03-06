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

package com.georlegacy.general.theatrical.packets;

import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.tiles.TileFixture;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateLightPacket implements IMessage {

    public UpdateLightPacket() {
    }


    public UpdateLightPacket(int tilt, int pan, float power, BlockPos blockPos) {
        this.tilt = tilt;
        this.pan = pan;
        this.pos = blockPos;
        this.power = power;
    }

    private BlockPos pos;
    private int tilt;
    private int pan;
    private float power;

    public BlockPos getPos() {
        return pos;
    }

    public int getTilt() {
        return tilt;
    }

    public int getPan() {
        return pan;
    }

    public float getPower() {
        return power;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tilt = buf.readInt();
        pan = buf.readInt();
        power = buf.readFloat();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new BlockPos(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(tilt);
        buf.writeInt(pan);
        buf.writeFloat(power);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static class ServerHandler implements IMessageHandler<UpdateLightPacket, IMessage>{

        @Override
        public IMessage onMessage(UpdateLightPacket message, MessageContext ctx) {
            doTheFuckingThing(message, ctx);
            return null;
        }

        private void doTheFuckingThing(UpdateLightPacket message, MessageContext ctx){
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.world;
                BlockPos blockPos = message.getPos();
                TileFixture tileFresnel = (TileFixture) world
                    .getTileEntity(blockPos);
                tileFresnel.setTilt(message.getTilt());
                tileFresnel.setPan(message.getPan());
                world.markChunkDirty(blockPos, tileFresnel);
                TheatricalPacketHandler.INSTANCE.sendToAll(
                    new UpdateLightPacket(tileFresnel.getTilt(), tileFresnel.getPan(),
                        tileFresnel.getIntensity(), tileFresnel.getPos()));
            });
        }
    }

    public static class ClientHandler implements
        IMessageHandler<UpdateLightPacket, IMessage> {

        @Override
        public IMessage onMessage(UpdateLightPacket message, MessageContext ctx) {
            doTheFuckingThing(message, ctx);
            return null;
        }

        private void doTheFuckingThing(UpdateLightPacket message, MessageContext ctx){
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                BlockPos blockPos = message.getPos();
                TileFixture tileFresnel = (TileFixture) Minecraft
                    .getMinecraft().world.getTileEntity(blockPos);
                tileFresnel.setTilt(message.getTilt());
                tileFresnel.setPan(message.getPan());
                Minecraft.getMinecraft().world.markChunkDirty(blockPos, tileFresnel);
            });
        }
    }

}
