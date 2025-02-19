package net.herospvp.astrea.common.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketListener;
import org.jetbrains.annotations.NotNull;

public class CPacketHandler {

    private final ProtocolManager manager;

    public CPacketHandler() {
        manager = ProtocolLibrary.getProtocolManager();
    }

    public void addPacket(
            @NotNull PacketListener packetListener
    ) {
        manager.addPacketListener(packetListener);
    }

    public void removePacket(
            @NotNull PacketListener packetListener
    ) {
        manager.removePacketListener(packetListener);
    }

}
