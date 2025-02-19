package net.herospvp.astrea.common.detections.misc;

import net.herospvp.astrea.common.core.CheckBuilder;
import net.herospvp.astrea.common.utils.CompactUtils;
import net.herospvp.astrea.versions.v1_8.lists.Doors;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class BugAbuseCheck extends CheckBuilder {

    public BugAbuseCheck(
            @NotNull CompactUtils compactUtils,
            @NotNull String checkName,
            boolean registerEvents,
            boolean decreasing,
            boolean needMaps
    ) {
        super(compactUtils, checkName, registerEvents, decreasing, needMaps);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) {
            return;
        }

        Player player = ((Player) attacker).getPlayer();
        if (getUsableLinkedHashMap().get(attacker) == null) {
            return;
        }

        long attackerTime = (long) getUsableLinkedHashMap().get(attacker) + 5;
        long playerPing = getCompactUtils().getNmsUtils().getLatency(player);

        if (System.currentTimeMillis() >= attackerTime + playerPing) {
            return;
        }

        addVl(player, playerPing < 200 ? 1 : 0, "Glitch", "Door");
        event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void on(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Material material = event.getClickedBlock().getType();
        if (material == null) {
            return;
        }

        Optional<Doors> values =
                Arrays.stream(Doors.values())
                        .filter(
                                doors -> doors.getMaterial() == material
                        ).findFirst();

        if (!values.isPresent()) {
            return;
        }
        getUsableLinkedHashMap().replace(event.getPlayer(), System.currentTimeMillis());
    }

}
