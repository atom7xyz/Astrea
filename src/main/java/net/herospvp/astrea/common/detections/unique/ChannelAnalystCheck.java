package net.herospvp.astrea.common.detections.unique;

import net.herospvp.astrea.Astrea;
import net.herospvp.astrea.common.core.CheckBuilder;
import net.herospvp.astrea.common.detections.unique.elements.NobusWareCheck;
import net.herospvp.astrea.common.detections.unique.elements.VapeCheck;
import net.herospvp.astrea.common.utils.CompactUtils;
import org.jetbrains.annotations.NotNull;

public class ChannelAnalystCheck extends CheckBuilder {

    private final VapeCheck vapeCheck;
    private final NobusWareCheck nobusWareCheck;

    public ChannelAnalystCheck(
            @NotNull CompactUtils compactUtils,
            @NotNull String checkName,
            boolean registerEvents,
            boolean decreasing,
            boolean needMaps
    ) {
        super(compactUtils, checkName, registerEvents, decreasing, needMaps);
        Astrea astrea = getCompactUtils().getAstrea();

        this.vapeCheck = new VapeCheck(this);
        compactUtils.getPluginManager().registerEvents(vapeCheck, astrea);
        compactUtils.getMessenger()
                .registerIncomingPluginChannel(
                        astrea, "LOLIMAHCKER", vapeCheck
                );

        this.nobusWareCheck = new NobusWareCheck(this);
        compactUtils.getMessenger()
                .registerIncomingPluginChannel(
                        astrea, "heroscheck", nobusWareCheck
                );
    }

    public void setEnabled(boolean value) {
        super.setEnabled(value);
        if (value) {
            return;
        }
        getCompactUtils().getMessenger().unregisterIncomingPluginChannel(
                getCompactUtils().getAstrea(), "LOLIMAHCKER", vapeCheck
        );
        getCompactUtils().getMessenger().unregisterIncomingPluginChannel(
                getCompactUtils().getAstrea(), "heroscheck", nobusWareCheck
        );
    }

}
