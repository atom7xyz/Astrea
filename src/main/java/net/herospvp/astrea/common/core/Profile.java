package net.herospvp.astrea.common.core;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.LinkedList;

@Getter
public class Profile {

    private final Player player;
    private final LinkedList<Long> clicks;
    private final LinkedList<Integer> med, longMed;

    @Setter
    private boolean digging;
    @Setter
    private long immuneTime;
    @Setter
    private int latestClickValue;

    public Profile(Player player) {
        this.player = player;
        this.clicks = new LinkedList<>();
        this.med = new LinkedList<>();
        this.longMed = new LinkedList<>();
        this.digging = false;
        this.immuneTime = 0L;
        this.latestClickValue = 0;
    }

    public void addClick(long timing) {
        clicks.add(timing);
    }

    public void addMed(int value) {
        med.add(value);
        longMed.add(value);
    }

    public boolean isImmune(long value) {
        return immuneTime >= value;
    }

}
