package net.herospvp.astrea.common.core;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

@Getter
public class Bank {

    private final LinkedList<Player> stafferList;
    private final LinkedList<CheckBuilder> checks;
    private final LinkedList<Profile> profiles;
    private int totalVl, totalPunishments;

    public Bank() {
        this.stafferList = new LinkedList<>();
        this.checks = new LinkedList<>();
        this.profiles = new LinkedList<>();
        this.totalVl = 0;
    }

    public synchronized void addCheck(
            @NotNull CheckBuilder checkBuilder
    ) {
        checks.add(checkBuilder);
    }

    public synchronized void removeCheck(
            @NotNull CheckBuilder checkBuilder
    ) {
        checks.remove(checkBuilder);
    }

    public synchronized void addStaffer(
            @NotNull Player player
    ) {
        stafferList.add(player);
    }

    public synchronized void removeStaffer(
            @NotNull Player player
    ) {
        stafferList.remove(player);
    }

    public synchronized void addVl(int amount) {
        totalVl += amount;
    }

    public synchronized void setVl(int amount) {
        totalVl = amount;
    }

    public synchronized void addPunishments(int amount) {
        totalPunishments += amount;
    }

    public synchronized void setPunishments(int amount) {
        totalPunishments = amount;
    }

    public synchronized <G> void addProfile(G generic) {
        profiles.add(generic instanceof Profile ? (Profile) generic : new Profile((Player) generic));
    }

    public synchronized <G> void removeProfile(G generic) {
        profiles.remove(generic instanceof Profile ? generic : getProfile((Player) generic));
    }

    public Profile getProfile(Player player) {
        return profiles.parallelStream()
                .filter(
                        profile -> profile.getPlayer() == player
                ).findFirst().orElse(null);
    }

}
