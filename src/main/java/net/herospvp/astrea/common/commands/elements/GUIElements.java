package net.herospvp.astrea.common.commands.elements;

import lombok.Getter;
import net.herospvp.astrea.common.core.CheckBuilder;
import net.herospvp.astrea.common.utils.CompactUtils;
import net.herospvp.astrea.common.utils.HardwareInfo;
import net.herospvp.heroscore.utils.inventory.GUIItem;
import net.herospvp.heroscore.utils.inventory.GUIWindow;
import net.herospvp.heroscore.utils.items.SkullCreator;
import net.herospvp.heroscore.utils.strings.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GUIElements {

    private final CompactUtils compactUtils;

    private final Map<Player, GUIWindow> playerInInv;

    private final ItemStack sysInfoHead, statisticsHead, checkListHead,
            checkEnabledHead, checkDisabledHead, glass, back;

    public GUIElements(CompactUtils compactUtils) {
        this.compactUtils = compactUtils;
        this.playerInInv = new HashMap<>();

        FileConfiguration config = compactUtils.getConfig();

        String path = "gui-heads.";
        this.sysInfoHead = SkullCreator.itemFromBase64(config.getString(path + "sys-info"));
        this.statisticsHead = SkullCreator.itemFromBase64(config.getString(path + "statistics"));
        path += "checks.";
        this.checkListHead = SkullCreator.itemFromBase64(config.getString(path + "list"));
        this.checkDisabledHead = SkullCreator.itemFromBase64(config.getString(path + "disabled"));
        this.checkEnabledHead = SkullCreator.itemFromBase64(config.getString(path + "enabled"));
        this.glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
        this.back = new ItemStack(Material.BARRIER);

        ItemMeta itemMeta;
        itemMeta = sysInfoHead.getItemMeta();
        itemMeta.setDisplayName(StringUtils.c("&7Informazioni di sistema"));
        sysInfoHead.setItemMeta(itemMeta);

        itemMeta = statisticsHead.getItemMeta();
        itemMeta.setDisplayName(StringUtils.c("&7Statistiche"));
        statisticsHead.setItemMeta(itemMeta);

        itemMeta = checkListHead.getItemMeta();
        itemMeta.setDisplayName(StringUtils.c("&7Checks"));
        checkListHead.setItemMeta(itemMeta);

        itemMeta = glass.getItemMeta();
        itemMeta.setDisplayName(" ");
        glass.setItemMeta(itemMeta);

        itemMeta = back.getItemMeta();
        itemMeta.setDisplayName(StringUtils.c("&c⇦ Indietro"));
        back.setItemMeta(itemMeta);
    }

    public void openInv(Player player) {
        GUIWindow window = new GUIWindow(StringUtils.c("&bAstrea"), 3);
        updateEveryLore(window);
        for (int i = 0; i < 27; i++) {
            window.setItem(i, new GUIItem(glass, event -> {}));
        }
        window.setItem(11, new GUIItem(sysInfoHead, event -> {}));
        window.setItem(13, new GUIItem(statisticsHead, event -> {}));
        window.setItem(15, new GUIItem(checkListHead, event -> openChecksInv(player)));

        window.setCloseEvent(event -> playerInInv.remove(player));
        playerInInv.put(player, window);

        window.show(player);
    }

    public void openChecksInv(Player player) {
        GUIWindow window = new GUIWindow(StringUtils.c("&bChecks"), 3);
        for (int i = 0; i < compactUtils.getChecks().size(); i++) {

            CheckBuilder checkBuilder = compactUtils.getChecks().get(i);

            ItemStack itemStack = checkEnabledHead;
            String color = "&a";
            if (!checkBuilder.isEnabled()) {
                color = "&c";
                itemStack = checkDisabledHead;
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(StringUtils.c(color + checkBuilder.getCheckName()));
            itemStack.setItemMeta(itemMeta);

            window.setItem(i, new GUIItem(itemStack, event -> {
                if (!player.hasPermission("astrea.admin")) {
                    player.sendMessage(ChatColor.RED + "Non puoi!");
                    return;
                }
                if (!checkBuilder.isEditableFromGame()) {
                    player.sendMessage(ChatColor.RED + "Non puoi editare questo check! (reload-unsafe)");
                    return;
                }
                compactUtils.getAstrea().getLogger().warning(
                        "Check \"" + checkBuilder.getCheckName()
                        + "\" impostato a \"" + (checkBuilder.isEnabled() ? "VERO": "FALSO")
                        + "\" da: " + player.getName());

                checkBuilder.setEnabled(!checkBuilder.isEnabled());
                openChecksInv(player);
            }));
        }
        window.setItem(22, new GUIItem(back, event -> openInv(player)));

        window.show(player);
    }

    public void updateEveryLore(GUIWindow window) {
        HardwareInfo hardware = compactUtils.getHardwareInfo();

        ItemMeta itemMeta;
        itemMeta = sysInfoHead.getItemMeta();

        if (itemMeta.getLore() != null && itemMeta.getLore().size() != 0) {
            List<String> lore = itemMeta.getLore();
            lore.set(4, StringUtils.c("&6▎ Utilizzo: &7"  + hardware.getCPUUsageTotal() + "%"));
            //lore.set(5, StringUtils.c("&6▎ Frequenza Attuale/Massima: &7" + hardware.getCPUFrequency() + "GHz/" + hardware.getCPUMaxFrequency() + "GHz"));
            lore.set(8, StringUtils.c("&6▎ Disponibile: &7" + hardware.getRAMAvailable() + "MB"));
            lore.set(18, StringUtils.c("&6▎ Giocatori Attuali/Massimi: &7" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers()));
            lore.set(19, StringUtils.c("&6▎ TPS: &7" + compactUtils.getNmsUtils().getTPS()));
            itemMeta.setLore(lore);
        } else {
            itemMeta.setLore(Arrays.asList(
                    " ",
                    StringUtils.c("&6CPU"),
                    StringUtils.c("&6▎ Nome: &7" + hardware.getCPUName()),
                    StringUtils.c("&6▎ Cores/Threads: &7" + hardware.getCPUCores() + "/" + hardware.getCPUThreads()),
                    StringUtils.c("&6▎ Utilizzo: &7"  + hardware.getCPUUsageTotal() + "%"),
                    //StringUtils.c("&6▎ Frequenza Attuale/Massima: &7" + hardware.getCPUFrequency() + "GHz/" + hardware.getCPUMaxFrequency() + "GHz"),
                    " ",
                    StringUtils.c("&6RAM"),
                    StringUtils.c("&6▎ Totale: &7" + hardware.getRAMTotal() + "MB"),
                    StringUtils.c("&6▎ Disponibile: &7" + hardware.getRAMAvailable() + "MB"),
                    " ",
                    StringUtils.c("&6VERSIONE"),
                    StringUtils.c("&6▎ Astrea: &7" + compactUtils.getAstrea().getVersion()),
                    StringUtils.c("&6▎ Intave: &7" + compactUtils.getAstrea().getIntaveVersion()),
                    StringUtils.c("&6▎ ProtocolLib: &7" + compactUtils.getAstrea().getProtocolLibVersion()),
                    StringUtils.c("&6▎ heros-core: &7" + compactUtils.getAstrea().getHerosCoreVersion()),
                    StringUtils.c("&6▎ database-lib: &7" + compactUtils.getAstrea().getDatabaseLibVersion()),
                    " ",
                    StringUtils.c("&6SERVER"),
                    StringUtils.c("&6▎ Giocatori Attuali/Massimi: &7" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers()),
                    StringUtils.c("&6▎ TPS: &7" + compactUtils.getNmsUtils().getTPS())
            ));
        }
        sysInfoHead.setItemMeta(itemMeta);

        itemMeta = statisticsHead.getItemMeta();
        itemMeta.setLore(Arrays.asList(
                " ",
                StringUtils.c("&6Violazioni totali: " + compactUtils.getBank().getTotalVl()),
                StringUtils.c("&6Punishments totali: " + compactUtils.getBank().getTotalPunishments())
        ));
        statisticsHead.setItemMeta(itemMeta);

        window.setItem(11, new GUIItem(sysInfoHead, event -> {}));
        window.setItem(13, new GUIItem(statisticsHead, event -> {}));
    }

}
