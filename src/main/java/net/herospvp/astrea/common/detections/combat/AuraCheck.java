package net.herospvp.astrea.common.detections.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import net.herospvp.astrea.common.core.CheckBuilder;
import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class AuraCheck extends CheckBuilder {

    /*
    OBBIETTIVO:
    Detectare strane rotazioni

    ESEMPIO:
    PlayerA si avvicina a PlayerB
    PlayerB ruota colpendo PlayerA
    PlayerB quando PlayerA esce dal range, ruota alla posizione originaria <- CHEAT

    COME:
    Si campionano le rotazioni e si confrontano
    */

    private final PacketListener lookListener, positionLookListener;
    private final LinkedHashMap<Player, Object> yawMap, pitchMap;

    public AuraCheck(
            @NotNull CompactUtils compactUtils,
            @NotNull String checkName,
            boolean registerEvents,
            boolean decreasing,
            boolean needMaps
    ) {
        super(compactUtils, checkName, registerEvents, decreasing, needMaps);

        pitchMap = getUsableLinkedHashMap();
        yawMap = getOtherUsableLinkedHashMap();

        lookListener = new PacketAdapter(compactUtils.getAstrea(), ListenerPriority.HIGH, PacketType.Play.Client.LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                // packet.getFloat().getValues().get(0)
            }
        };
        compactUtils.getCPacketHandler().addPacket(lookListener);

        positionLookListener  = new PacketAdapter(compactUtils.getAstrea(), ListenerPriority.HIGH, PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) {
                    return;
                }
                PacketContainer packet = event.getPacket();
            }
        };
        compactUtils.getCPacketHandler().addPacket(positionLookListener);
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (!(damager instanceof Player)) {
            return;
        }
        Player player = (Player) damager;
        long time = System.currentTimeMillis();

        if (getUsableLinkedHashMap().get(player) == null) {
            getUsableLinkedHashMap().put(player, time);
            return;
        }
        long actualTiming = (long) getUsableLinkedHashMap().get(player);



        getUsableLinkedHashMap().replace(player, time);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        getUsableLinkedHashMap().replace(player, new LinkedList<Long>());
        getOtherUsableLinkedHashMap().replace(player, new LinkedList<Long>());
    }

    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);
        if (value) return;
        getCompactUtils().getCPacketHandler().removePacket(lookListener);
        getCompactUtils().getCPacketHandler().removePacket(positionLookListener);
    }

}
