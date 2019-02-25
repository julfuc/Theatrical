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

package com.georlegacy.general.theatrical.packets.handlers;

import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.packets.UpdateLightPacket;
import com.georlegacy.general.theatrical.tiles.fixtures.TileFresnel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class UpdateLightPacketHandler implements IMessageHandler<UpdateLightPacket, IMessage> {

    @Override
    public IMessage onMessage(UpdateLightPacket message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                BlockPos blockPos = message.getPos();
                TileFresnel tileFresnel = (TileFresnel) Minecraft
                    .getMinecraft().world.getTileEntity(blockPos);
                tileFresnel.setTilt(message.getTilt());
                tileFresnel.setPan(message.getPan());
                Minecraft.getMinecraft().world.markChunkDirty(blockPos, tileFresnel);
            });
        } else {
            ctx.getServerHandler().player.server.addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.world;
                BlockPos blockPos = message.getPos();
                TileFresnel tileFresnel = (TileFresnel) world
                    .getTileEntity(blockPos);
                tileFresnel.setTilt(message.getTilt());
                tileFresnel.setPan(message.getPan());
                world.markChunkDirty(blockPos, tileFresnel);
                TheatricalPacketHandler.INSTANCE.sendToAll(
                    new UpdateLightPacket(tileFresnel.getTilt(), tileFresnel.getPan(),
                        tileFresnel.getPower(), tileFresnel.getPos()));
            });
        }
        return null;
    }

}
