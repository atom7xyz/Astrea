package net.herospvp.astrea.common.detections.unique.elements;

import net.herospvp.astrea.common.core.CheckBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class NobusWareCheck implements PluginMessageListener {

    private final CheckBuilder channelAnalyst;

    public NobusWareCheck(CheckBuilder channelAnalyst) {
        this.channelAnalyst = channelAnalyst;
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        channelAnalyst.addVl(player, 1, "NobusWare", null);
    }

}
