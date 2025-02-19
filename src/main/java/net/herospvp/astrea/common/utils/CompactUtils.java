package net.herospvp.astrea.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.herospvp.astrea.Astrea;
import net.herospvp.astrea.common.commands.elements.GUIElements;
import net.herospvp.astrea.common.core.Bank;
import net.herospvp.astrea.common.core.CheckBuilder;
import net.herospvp.astrea.common.packets.CPacketHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;

import java.util.LinkedList;

@Getter
@AllArgsConstructor
public class CompactUtils {

    private final Astrea astrea;
    private final Bank bank;

    private final CPacketHandler cPacketHandler;
    private final HardwareInfo hardwareInfo;

    private final NMSUtils nmsUtils;
    private final EventUtils eventUtils;

    public FileConfiguration getConfig() {
        return astrea.getConfig();
    }

    public LinkedList<CheckBuilder> getChecks() {
        return bank.getChecks();
    }

    public PluginManager getPluginManager() {
        return astrea.getServer().getPluginManager();
    }

    public Messenger getMessenger() {
        return astrea.getServer().getMessenger();
    }

    public GUIElements getGUIElements() {
        return astrea.getGuiElements();
    }

}
