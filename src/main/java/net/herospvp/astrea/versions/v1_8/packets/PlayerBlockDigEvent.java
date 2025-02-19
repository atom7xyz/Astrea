package net.herospvp.astrea.versions.v1_8.packets;

import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class PlayerBlockDigEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final PacketEvent packetEvent;
    private final long instant;

    private final Location location;
    private final EnumWrappers.PlayerDigType playerDigType;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
