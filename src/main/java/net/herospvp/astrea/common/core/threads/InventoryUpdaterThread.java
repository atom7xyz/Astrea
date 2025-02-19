package net.herospvp.astrea.common.core.threads;

import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class InventoryUpdaterThread extends BukkitRunnable {

    private final CompactUtils compactUtils;

    public InventoryUpdaterThread(
            @NotNull CompactUtils compactUtils
    ) {
        this.compactUtils = compactUtils;
        int constantTimer = compactUtils.getConfig().getInt("gui-heads.inv-refresh-freq");

        this.runTaskTimer(compactUtils.getAstrea(), 20L * constantTimer, 20L * constantTimer);
    }

    @Override
    public void run() {
        compactUtils.getGUIElements().getPlayerInInv().forEach((player, guiWindow) -> {
            compactUtils.getGUIElements().updateEveryLore(guiWindow);
            player.updateInventory();
        });
    }

}
