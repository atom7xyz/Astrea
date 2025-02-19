package net.herospvp.astrea.common.detections.unique;

import net.herospvp.astrea.common.core.CheckBuilder;
import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class CrasherCheck extends CheckBuilder {

    private final String magicAlgo;

    public CrasherCheck(
            @NotNull CompactUtils compactUtils,
            @NotNull String checkName,
            boolean registerEvents,
            boolean decreasing,
            boolean needMaps
    ) {
        super(compactUtils, checkName, registerEvents, decreasing, needMaps);
        this.magicAlgo = "for(i=0;i<256;i++)";
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void on(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();

        if (!(
                message.startsWith("/to") ||
                message.startsWith("/targetoffset") ||
                message.startsWith("//calculate") ||
                message.startsWith("//calc"))
        ) {
            return;
        }

        if (message.contains(magicAlgo)) {
            addVl(event.getPlayer(), 1, "Command", null);
        }

        event.setCancelled(true);
    }

}
