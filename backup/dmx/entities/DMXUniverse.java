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

package com.georlegacy.general.theatrical.logic.transport.dmx.entities;

import com.georlegacy.general.theatrical.core.exceptions.dmx.DMXValueOutOfBoundsException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DMXUniverse contains 512 channels and is the main transport core for transportation
 *
 * @author James Conway
 */
public class DMXUniverse {

    private final UUID uuid;

    private DMXChannel[] channels;

    private final Set<DMXNetworkPlane> networkPlanes;
    private final Set<DMXThruPlane> thruPlanes;

    public DMXUniverse() {
        this.uuid = UUID.randomUUID();

        this.channels = new DMXChannel[512];
        this.networkPlanes = new HashSet<>();
        this.thruPlanes = new HashSet<>();
    }

    public DMXChannel[] getChannels() {
        return channels;
    }

    public DMXChannel getChannel(int index) {
        if (index > 511 || index < 0) {
            throw new DMXValueOutOfBoundsException("There are only 512 channels in this universe");
        }
        return channels[index];
    }

}
