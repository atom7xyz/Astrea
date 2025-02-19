package net.herospvp.astrea.common.core;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.herospvp.astrea.Astrea;
import net.herospvp.astrea.common.core.elements.Action;
import net.herospvp.astrea.common.core.logging.TelegramLogger;
import net.herospvp.astrea.common.core.logging.elements.ActionsLogger;
import net.herospvp.astrea.common.core.logging.elements.DetectionsLogger;
import net.herospvp.astrea.common.utils.CompactUtils;
import net.herospvp.heroscore.utils.strings.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.Effect;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CheckBuilder implements Listener {

    private final CompactUtils compactUtils;

    private final String checkName;
    private final LinkedHashMap<Integer, LinkedList<String>> actionMap;

    private final boolean decreasing, needMaps;

    private boolean enabled;

    @Setter
    private boolean editableFromGame;

    private final ConcurrentHashMap<Player, Integer> violations;
    private final ConcurrentHashMap<Player, Long> latestViolation;

    private final LinkedHashMap<Player, Object> usableLinkedHashMap, otherUsableLinkedHashMap;
    private final LinkedList<Object> usableLinkedList, otherUsableLinkedList;

    private final TelegramLogger telegramLogger;

    public CheckBuilder(
            @NotNull CompactUtils compactUtils,
            @NotNull String checkName,
            boolean registerEvents,
            boolean decreasing,
            boolean needMaps
    ) {
        this.compactUtils = compactUtils;
        this.checkName = checkName;
        this.actionMap = new LinkedHashMap<>();

        FileConfiguration config = compactUtils.getAstrea().getConfig();
        String path = "detections." + checkName.toLowerCase() + ".vls";
        config.getConfigurationSection(path).getKeys(false)
                .forEach(
                        s -> addToActions(Integer.parseInt(s), new LinkedList<>(config.getStringList(path + "." + s)))
                );

        this.decreasing = decreasing;
        this.needMaps = needMaps;
        this.enabled = true;
        this.editableFromGame = config.getBoolean("detections." + checkName.toLowerCase() + ".editable");

        this.violations = new ConcurrentHashMap<>();
        this.latestViolation = new ConcurrentHashMap<>();

        this.telegramLogger = compactUtils.getAstrea().getTelegramLogger();

        compactUtils.getBank().addCheck(this);

        if (registerEvents) {
            compactUtils.getPluginManager().registerEvents(this, compactUtils.getAstrea());
        }

        if (!needMaps) {
            this.usableLinkedHashMap = null;
            this.otherUsableLinkedHashMap = null;

            this.usableLinkedList = null;
            this.otherUsableLinkedList = null;

            return;
        }

        this.usableLinkedHashMap = new LinkedHashMap<>();
        this.otherUsableLinkedHashMap = new LinkedHashMap<>();

        this.usableLinkedList = new LinkedList<>();
        this.otherUsableLinkedList = new LinkedList<>();
    }

    public void addVl(
            @NotNull Player player,
            int amount,
            @NotNull String checkVariant,
            @Nullable String anotherVariant
    ) {
        int vl = violations.get(player) + amount;
        violations.replace(player, vl);
        latestViolation.replace(player, System.currentTimeMillis());
        compactUtils.getBank().addVl(1);

        Optional<Map.Entry<Integer, LinkedList<String>>> result =
                actionMap
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getKey() == vl)
                        .findFirst();

        if (!result.isPresent()) {

            if (actionMap.size() < vl) {
                actionMap.get(actionMap.size()).forEach(s -> applyAction(player, s, checkVariant, anotherVariant));
            }

            return;
        }
        Map.Entry<Integer, LinkedList<String>> entry = result.get();

        LinkedList<String> list = entry.getValue();
        if (list.isEmpty()) {
            return;
        }
        list.forEach(s -> applyAction(player, s, checkVariant, anotherVariant));
    }

    private void applyAction(
            @NotNull Player player,
            @NotNull String actionStrings,
            @NotNull String checkVariant,
            @Nullable String anotherVariant
    ) {
        Astrea astrea = compactUtils.getAstrea();
        String[] split = actionStrings.split(" ");

        Action action;
        try {
            action = Action.valueOf(split[0]);
        } catch (Exception e) {
            astrea.getLogger().severe("Could not apply action: " + split[0]);
            astrea.getLogger().severe("Since is not a valid one!");
            return;
        }

        switch (action) {
            case STREAM: {
                stream(player, checkVariant, anotherVariant);
                return;
            }
            case RESET: {
                resetVl(player);
                return;
            }
            case WORSEN_TRUST_FACTOR: {
                // TODO
                return;
            }
            case IMPROVE_TRUST_FACTOR: {
                // TODO
                return;
            }
        }

        ConsoleCommandSender consoleSender = astrea.getServer().getConsoleSender();
        String playerName = player.getName();
        Server server = astrea.getServer();

        String generatedId = compactUtils.getEventUtils().generateId(playerName);
        String trimmedId = generatedId.substring(0, 12);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < split.length; i++) {
            stringBuilder.append(split[i]).append(" ");
        }
        String result = StringUtils.c(stringBuilder.toString()
                .replace("{id}", trimmedId)
                .replace("{player}", playerName));

        switch (action) {
            case BROADCAST: {
                server.broadcastMessage(" ");
                server.broadcastMessage(StringUtils.c("&b[&f✧&b] " + result));
                server.broadcastMessage(" ");
                return;
            }
            case IP_BAN: {
                server.getScheduler().runTask(astrea, () -> {
                    server.dispatchCommand(consoleSender, "ipban " + result);
                    player.getLocation().getWorld().playEffect(player.getLocation(), Effect.WITCH_MAGIC, 1);
                });
                break;
            }
            case BAN: {
                server.getScheduler().runTask(astrea, () -> {
                    server.dispatchCommand(consoleSender, "ban " + result);
                    player.getLocation().getWorld().playEffect(player.getLocation(), Effect.WITCH_MAGIC, 1);
                });
                break;
            }
            case KICK: {
                server.getScheduler().runTask(astrea, () -> {
                    server.dispatchCommand(consoleSender, "kick " + result);
                    player.getLocation().getWorld().playEffect(player.getLocation(), Effect.WITCH_MAGIC, 1);
                });
                break;
            }
            case TELEGRAM_NOTIFY: {
                telegramLogger.sendMessage(result);
                return;
            }

        }
        compactUtils.getBank().addPunishments(1);

        int playerPing = compactUtils.getNmsUtils().getLatency(player);
        double TPS = compactUtils.getNmsUtils().getTPS();
        new ActionsLogger(
                compactUtils,
                player,
                this,
                playerPing,
                TPS,
                System.currentTimeMillis(),
                trimmedId,
                checkName + " (" + checkVariant + (anotherVariant == null ? ")" : " <" + anotherVariant + ">)")
        ).register();
    }

    private void addToActions(
            int vl,
            @NotNull LinkedList<String> list
    ) {
        actionMap.put(vl, list);
    }

    public void removeVl(
            @NotNull Player player,
            int amount
    ) {
        int var = violations.get(player) - amount;
        violations.replace(player, Math.max(var, 0));
    }

    public void resetVl(
            @NotNull Player player
    ) {
        violations.replace(player, 0);
    }

    public int retrieveVl(
            @NotNull Player player
    ) {
        return violations.get(player);
    }

    public void addPlayer(
            @NotNull Player player
    ) {
        violations.put(player, 0);
        latestViolation.put(player, 0L);

        if (!needMaps) {
            return;
        }
        usableLinkedList.add(player);
        otherUsableLinkedList.add(player);

        usableLinkedHashMap.put(player, null);
        otherUsableLinkedHashMap.put(player, null);
    }

    public void removePlayer(
            @NotNull Player player
    ) {
        violations.remove(player);
        latestViolation.remove(player);

        if (!needMaps) {
            return;
        }
        usableLinkedList.remove(player);
        otherUsableLinkedList.remove(player);

        usableLinkedHashMap.remove(player);
        otherUsableLinkedHashMap.remove(player);
    }

    private void stream(
            @NotNull Player player,
            @NotNull String checkVariant,
            @Nullable String anotherVariant
    ) {

        int playerPing = compactUtils.getNmsUtils().getLatency(player);
        double serverTPS = compactUtils.getNmsUtils().getTPS();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append("&b[&f✧&b] &7[&b")
                .append(compactUtils.getNmsUtils().getLatency(player))
                .append("ms&7] &b")
                .append(player.getName())
                .append("&7 » &b")
                .append(checkName)
                .append(" &7(&b")
                .append(checkVariant);

        if (anotherVariant != null) {
            stringBuilder
                    .append(" &7<&b")
                    .append(anotherVariant)
                    .append("&7>) [&b");
        } else {
            stringBuilder.append("&7) [&b");
        }

        stringBuilder
            .append(retrieveVl(player))
            .append("&7]");

        compactUtils.getBank().getStafferList().parallelStream().forEach(p ->
                p.sendMessage(StringUtils.c(stringBuilder.toString()))
        );

        new DetectionsLogger(
                compactUtils,
                player,
                playerPing,
                serverTPS,
                System.currentTimeMillis(),
                checkName + " (" + checkVariant + " <" + anotherVariant + ">)"
        ).register();

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
