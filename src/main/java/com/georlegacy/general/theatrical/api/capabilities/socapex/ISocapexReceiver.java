package com.georlegacy.general.theatrical.api.capabilities.socapex;

import java.util.List;
import net.minecraft.util.math.BlockPos;

public interface ISocapexReceiver {

    int[] receiveSocapex(int[] channels, boolean simulate);

    int[] extractSocapex(int[] channels, boolean simulate);

    int getEnergyStored(int channel);

    int getMaxEnergyStored(int channel);

    boolean canExtract(int channel);

    boolean canReceive(int channel);

    String getIdentifier();

    void assignIdentifier(String identifier);

    BlockPos getPos();

    List<BlockPos> getDevices();
}
