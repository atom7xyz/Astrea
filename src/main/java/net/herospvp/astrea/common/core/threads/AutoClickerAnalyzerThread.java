package net.herospvp.astrea.common.core.threads;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.herospvp.astrea.common.core.Bank;
import net.herospvp.astrea.common.core.Profile;
import net.herospvp.astrea.common.detections.combat.AutoClickerCheck;
import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.entity.Player;

@SuppressWarnings("BusyWait")
@Getter
public class AutoClickerAnalyzerThread extends Thread {

    private final AutoClickerCheck autoClickerCheck;
    private final Bank bank;
    @Setter
    private boolean running;

    public AutoClickerAnalyzerThread(CompactUtils compactUtils, AutoClickerCheck autoClickerCheck) {
        this.running = true;
        this.bank = compactUtils.getBank();
        this.autoClickerCheck = autoClickerCheck;
        this.setName("astrea-clicker");
        this.start();
    }

    public int getClicksPerSecond(Profile profile, long timing) {
        profile.getClicks().removeIf(aLong -> aLong < timing - 1050);
        return profile.getClicks().size();
    }

    @SneakyThrows
    @Override
    public void run() {
        while (running) {
            long timing = System.currentTimeMillis();

            bank.getProfiles().forEach(profile -> {
                if (profile.getClicks().isEmpty()) {
                    return;
                }

                Player player = profile.getPlayer();
                int cps = getClicksPerSecond(profile, timing);
                profile.addMed(cps);

                //
                // Abuse check
                //
                if (cps >= 17) {
                    autoClickerCheck.addVl(player, cps >= 20 ? 5 : 1, "Abuse", "~" + cps + "c/s");
                }

                //
                // Constant check
                //
                if (profile.getMed().size() >= 10) {
                    if (cps <= 7) {
                        return;
                    }
                    long rep = profile.getMed().stream().filter(m -> m == cps).count();

                    if (rep >= (profile.getMed().size() / 2)) {
                        autoClickerCheck.addVl(player, rep == profile.getMed().size() ? 5 : 1, "Constant", "~" + cps + "c/s");
                    }

                    profile.getMed().clear();
                }

                profile.setLatestClickValue(cps);
            });

            Thread.sleep(1000);
        }
    }

}
