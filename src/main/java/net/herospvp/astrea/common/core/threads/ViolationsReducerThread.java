package net.herospvp.astrea.common.core.threads;

import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ViolationsReducerThread extends BukkitRunnable {

    private final CompactUtils compactUtils;
    private final int timeSinceLast;

    public ViolationsReducerThread(
            @NotNull CompactUtils compactUtils
    ) {
        this.compactUtils = compactUtils;

        FileConfiguration configuration = compactUtils.getConfig();
        this.timeSinceLast = configuration.getInt("vl-reducer.time-since-last") * 1000;
        int constantTimer = configuration.getInt("vl-reducer.constant-timer");

        this.runTaskTimerAsynchronously(compactUtils.getAstrea(), 20L * constantTimer, 20L * constantTimer);
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();

        compactUtils.getBank().getChecks().parallelStream().forEach(check -> {

            if (!check.isEnabled() || !check.isDecreasing()) {
                return;
            }

            check.getViolations().forEach((player, vl) -> {
                if (check.getLatestViolation().get(player) + timeSinceLast < time) {
                    check.removeVl(player, 1);
                }
            });

        });
    }

}
