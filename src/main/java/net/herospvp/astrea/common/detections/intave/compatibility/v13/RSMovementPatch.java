package net.herospvp.astrea.common.detections.intave.compatibility.v13;

import de.jpx3.intave.api.external.linked.event.AsyncIntaveViolationEvent;
import net.herospvp.astrea.common.core.Patch;
import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.LinkedHashMap;

public class RSMovementPatch extends Patch {

    private final LinkedHashMap<Player, Long> knockbackMap;

    public RSMovementPatch(CompactUtils compactUtils) {
        super(compactUtils);
        this.knockbackMap = new LinkedHashMap<>();
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void on(AsyncIntaveViolationEvent event) {
        String check = event.checkName();
        Player player = event.detectedPlayer();

        if (!(check.equalsIgnoreCase("knockbackphysics") || check.equalsIgnoreCase("verticalphysics"))) {
            return;
        }

        if (knockbackMap.get(player) <= System.currentTimeMillis()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        if (!(damaged instanceof Player)) {
            return;
        }
        Player player = ((Player) damaged).getPlayer();
        knockbackMap.replace(player, System.currentTimeMillis() + 1000);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent event) {
        knockbackMap.put(event.getPlayer(), 0L);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent event) {
        knockbackMap.remove(event.getPlayer());
    }

}
