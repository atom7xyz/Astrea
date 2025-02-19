package net.herospvp.astrea.common.core.logging.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class DetectionsLogger {

    private final CompactUtils compactUtils;

    private final Player player;
    private final int playerPing;
    private final double serverTPS;
    private final long time;
    private final String detection;

    public void register() {
        compactUtils.getAstrea().getCommonLogger().offer(this);
    }

}
