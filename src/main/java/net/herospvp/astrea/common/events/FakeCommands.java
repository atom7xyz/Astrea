package net.herospvp.astrea.common.events;

import net.herospvp.astrea.Astrea;
import net.herospvp.astrea.common.core.Bank;
import net.herospvp.astrea.common.utils.CompactUtils;
import net.herospvp.heroscore.utils.strings.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FakeCommands implements Listener {

    public FakeCommands(CompactUtils compactUtils) {
        Astrea astrea = compactUtils.getAstrea();
        astrea.getServer().getPluginManager().registerEvents(this, astrea);
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String string = event.getMessage().split(" ")[0].toLowerCase();

        switch (string) {
            case "/aac": {
                player.sendMessage(StringUtils.c("&6&l AAC &fLicensed to https://www.spigotmc.org/members/140687"));
                event.setCancelled(true);
                break;
            }
            case "/ncp": {
                player.sendMessage(
                        "Administrative commands overview:\n" +
                            "/ncp top (entries) (check/s...) (sort by...) NEW.\n" +
                            "/ncp info (player): Violation summary for a player.\n" +
                            "/ncp inspect (player): Status info for a player.\n" +
                            "/ncp removeplayer (player) [(check type)]: Remove data.\n" +
                            "/ncp reload: Reload the configuration.\n" +
                            "/ncp lag: Lag-related info.\n" +
                            "/ncp version: Version information.\n" +
                            "/ncp commands: List all commands, adds rarely used ones."
                );
                event.setCancelled(true);
                break;
            }
            case "/spartan": {
                player.sendMessage(StringUtils.c("&4Spartan Anti-Cheat &8[&7(&fVersion: Build 398&7)&8] &7(&fID: 346244/-394182457&7)"));
                event.setCancelled(true);
                break;
            }
            case "/vulcan":
            case "/alerts": {
                player.sendMessage(StringUtils.c("&cYou do not have permission to do this."));
                event.setCancelled(true);
                break;
            }
        }

    }

}
