package net.herospvp.astrea.common.utils;

import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EventUtils {

    public void callEvent(
            @NotNull Event event
    ) {
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    @SneakyThrows
    public String generateId(
            @NotNull String playerName
    ) {
        return DigestUtils.md5Hex(playerName + " " + System.currentTimeMillis());
    }

}
