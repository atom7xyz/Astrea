package net.herospvp.astrea.common.detections.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;
import net.herospvp.astrea.Astrea;
import net.herospvp.astrea.common.core.Bank;
import net.herospvp.astrea.common.core.CheckBuilder;
import net.herospvp.astrea.common.core.Profile;
import net.herospvp.astrea.common.core.threads.AutoClickerAnalyzerThread;
import net.herospvp.astrea.common.packets.CPacketHandler;
import net.herospvp.astrea.versions.v1_8.packets.PlayerBlockDigEvent;
import net.herospvp.astrea.versions.v1_8.packets.PlayerBlockPlaceEvent;
import net.herospvp.astrea.versions.v1_8.packets.PlayerSwingEvent;
import net.herospvp.astrea.common.utils.CompactUtils;
import net.herospvp.astrea.common.utils.EventUtils;
import net.herospvp.astrea.versions.v1_8.lists.OneHitMaterials;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

@Getter
public class AutoClickerCheck extends CheckBuilder {

    private final CPacketHandler cPacketHandler;
    private final Bank bank;
    private final PacketListener swingListener, digListener, placeListener;

    public AutoClickerCheck(
            @NotNull CompactUtils compactUtils,
            @NotNull String checkName,
            boolean registerEvents,
            boolean decreasing,
            boolean needMaps
    ) {
        super(compactUtils, checkName, registerEvents, decreasing, needMaps);

        EventUtils eventUtils = compactUtils.getEventUtils();
        cPacketHandler = compactUtils.getCPacketHandler();
        Astrea astrea = compactUtils.getAstrea();

        swingListener = new PacketAdapter(astrea, PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                eventUtils.callEvent(
                        new PlayerSwingEvent(
                                event.getPlayer(),
                                event,
                                System.currentTimeMillis()
                        ));
            }
        };
        cPacketHandler.addPacket(swingListener);

        digListener = new PacketAdapter(astrea, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packetContainer = event.getPacket();

                BlockPosition blockPosition = packetContainer.getBlockPositionModifier().read(0);
                EnumWrappers.PlayerDigType digType = packetContainer.getPlayerDigTypes().read(0);

                eventUtils.callEvent(
                        new PlayerBlockDigEvent(
                                player,
                                event,
                                System.currentTimeMillis(),
                                new Location(player.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()),
                                digType
                        ));
            }
        };
        cPacketHandler.addPacket(digListener);

        placeListener = new PacketAdapter(astrea, PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                eventUtils.callEvent(
                        new PlayerBlockPlaceEvent(
                                event.getPlayer(),
                                event,
                                System.currentTimeMillis()
                        ));
            }
        };
        cPacketHandler.addPacket(placeListener);

        bank = compactUtils.getBank();
        new AutoClickerAnalyzerThread(compactUtils, this);
    }

    @EventHandler (ignoreCancelled = true)
    public void on(PlayerSwingEvent event) {
        Profile profile = bank.getProfile(event.getPlayer());
        long instant = event.getInstant();

        if (profile.isDigging() || profile.isImmune(instant)) {
            return;
        }

        profile.addClick(instant);
    }

    @EventHandler (ignoreCancelled = true)
    public void on(PlayerBlockDigEvent event) {
        Profile profile = bank.getProfile(event.getPlayer());

        switch (event.getPlayerDigType()) {
            case START_DESTROY_BLOCK: {
                Optional<OneHitMaterials> optional = Arrays.stream(OneHitMaterials.values())
                        .filter(
                                m -> m.getMaterial() == event.getLocation().getBlock().getType()
                        ).findFirst();

                if (optional.isPresent()) {
                    profile.setImmuneTime(event.getInstant() + 300);
                    break;
                }
                profile.setDigging(true);
                break;
            }
            case STOP_DESTROY_BLOCK: {
                profile.setDigging(false);
                profile.setImmuneTime(event.getInstant() + 300);
                break;
            }
            case ABORT_DESTROY_BLOCK: {
                profile.setDigging(false);
                break;
            }
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void on(PlayerBlockPlaceEvent event) {
        Profile profile = bank.getProfile(event.getPlayer());
        profile.setImmuneTime(event.getInstant());
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent event) {
        bank.addProfile(event.getPlayer());
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent event) {
        bank.removeProfile(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerGameModeChangeEvent event) {
        Profile profile = bank.getProfile(event.getPlayer());

        if (event.getNewGameMode().equals(GameMode.CREATIVE)) {
            profile.setDigging(true);
            return;
        }

        bank.getProfile(event.getPlayer()).setDigging(false);
    }

    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);
        if (value) {
            return;
        }
        cPacketHandler.removePacket(placeListener);
        cPacketHandler.removePacket(digListener);
        cPacketHandler.removePacket(swingListener);
    }

}
