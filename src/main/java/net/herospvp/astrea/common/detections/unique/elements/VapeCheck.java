package net.herospvp.astrea.common.detections.unique.elements;

import net.herospvp.astrea.common.core.CheckBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class VapeCheck implements Listener, PluginMessageListener {

    private final CheckBuilder channelAnalyst;
    public VapeCheck(CheckBuilder channelAnalyst) {
        this.channelAnalyst = channelAnalyst;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void on(PlayerLoginEvent event) {
        event.getPlayer().sendMessage("§8 §8 §1 §3 §3 §7 §8 ");
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        channelAnalyst.addVl(player, 1, "Vape", null);
    }

}
