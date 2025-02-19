package net.herospvp.astrea.common.events;

import net.herospvp.astrea.Astrea;
import net.herospvp.astrea.common.core.Bank;
import net.herospvp.astrea.common.utils.CompactUtils;
import net.herospvp.heroscore.utils.strings.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class IO_Handler implements Listener {

    private final Bank bank;
    private final Astrea astrea;

    public IO_Handler(CompactUtils compactUtils) {
        this.bank = compactUtils.getBank();
        this.astrea = compactUtils.getAstrea();

        astrea.getServer().getPluginManager().registerEvents(this, astrea);
    }

    @EventHandler (priority = EventPriority.LOW)
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        bank.getChecks().forEach(check -> check.addPlayer(player));

        if (!player.hasPermission("astrea.command")) {
            return;
        }

        bank.addStaffer(player);
        player.sendMessage(StringUtils.c("\n&f✧ &bAstrea &n" + astrea.getVersion() + "&f ✧\n"));
    }

    @EventHandler (priority = EventPriority.LOW)
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        bank.getChecks().forEach(check -> check.removePlayer(player));
        bank.removeStaffer(player);
    }

}
