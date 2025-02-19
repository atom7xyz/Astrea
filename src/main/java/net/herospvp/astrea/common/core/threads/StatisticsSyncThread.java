package net.herospvp.astrea.common.core.threads;

import net.herospvp.astrea.common.core.logging.CommonLogger;
import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class StatisticsSyncThread extends BukkitRunnable {

    private final CompactUtils compactUtils;

    public StatisticsSyncThread(
            @NotNull CompactUtils compactUtils
    ) {
        this.compactUtils = compactUtils;

        FileConfiguration configuration = compactUtils.getConfig();
        int constantTimer = configuration.getInt("gui-heads.stats-sync-freq");

        this.runTaskTimerAsynchronously(compactUtils.getAstrea(), 20L * constantTimer, 20L * constantTimer);
    }

    @Override
    public void run() {
        CommonLogger commonLogger = compactUtils.getAstrea().getCommonLogger();
        commonLogger.save();
        commonLogger.getTotal();
    }

}
