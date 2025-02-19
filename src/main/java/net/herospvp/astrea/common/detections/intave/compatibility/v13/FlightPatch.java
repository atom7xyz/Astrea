package net.herospvp.astrea.common.detections.intave.compatibility.v13;

import de.jpx3.intave.api.external.linked.event.AsyncIntaveViolationEvent;
import net.herospvp.astrea.common.core.Patch;
import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.event.EventHandler;

public class FlightPatch extends Patch {

    public FlightPatch(CompactUtils compactUtils) {
        super(compactUtils);
    }

    @EventHandler
    public void on(AsyncIntaveViolationEvent event) {
        String check = event.checkName();

        if (getCompactUtils().getNmsUtils().getTPS() > 18) {
            return;
        }

        event.setCancelled(true);
    }

}
