package net.herospvp.astrea.common.utils;

import lombok.Getter;
import net.herospvp.astrea.Astrea;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@Getter
public class NMSUtils {

    private final Astrea astrea;

    private final String name, version;
    private Object serverInstance;
    private Field recentTPS;
    private Class<?> playerInstance;

    public NMSUtils(
            @NotNull Astrea astrea
    ) {
        this.astrea = astrea;
        name = Bukkit.getServer().getClass().getPackage().getName();
        version = name.substring(name.lastIndexOf('.') + 1);
        try {
            //
            // TPS
            //
            Field consoleField = astrea.getServer().getClass().getDeclaredField("console");
            consoleField.setAccessible(true);
            serverInstance = consoleField.get(astrea.getServer());

            recentTPS = serverInstance.getClass().getSuperclass().getDeclaredField("recentTps");
            recentTPS.setAccessible(true);

            //
            // PING
            //
            playerInstance = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
        } catch (Exception e) {
            e.printStackTrace();
            astrea.getLogger().severe("Could not load NMS utils via Reflections :(");
        }
    }

    private Class<?> getClass(
            @NotNull String className
    ) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public double getTPS() {
        try {
            return (double) Math.round(((double[]) recentTPS.get(serverInstance))[0] * 100) / 100;
        }
        catch (Exception e) {
            return 0;
        }
    }

    public int getLatency(
            @NotNull Player player
    ) {
        try {
            Object c = playerInstance.getMethod("getHandle").invoke(player);
            return (int) c.getClass().getDeclaredField("ping").get(c);
        }
        catch (Exception e) {
            return 0;
        }
    }

}
