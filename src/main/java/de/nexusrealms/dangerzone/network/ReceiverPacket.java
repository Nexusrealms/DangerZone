package de.nexusrealms.dangerzone.network;

import net.minecraft.network.packet.CustomPayload;

public interface ReceiverPacket<C> extends CustomPayload {
    void onReceive(C context);
}