package net.herospvp.astrea.common.core;

import lombok.Getter;
import net.herospvp.astrea.common.utils.CompactUtils;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@Getter
public class Patch implements Listener {

    private final CompactUtils compactUtils;
    private boolean enabled;

    public Patch(CompactUtils compactUtils) {
        this.compactUtils = compactUtils;
        this.enabled = true;
        compactUtils.getPluginManager().registerEvents(this, compactUtils.getAstrea());
    }

    public void setEnabled(boolean value) {
        if (enabled == value) {
            return;
        }
        enabled = value;

        if (value) {
            compactUtils.getPluginManager().registerEvents(this, compactUtils.getAstrea());
        } else {
            HandlerList.unregisterAll(this);
        }
    }

}
