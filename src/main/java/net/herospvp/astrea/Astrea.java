package net.herospvp.astrea;

import lombok.Getter;
import net.herospvp.astrea.common.commands.AstreaCommand;
import net.herospvp.astrea.common.commands.elements.GUIElements;
import net.herospvp.astrea.common.core.Bank;
import net.herospvp.astrea.common.core.logging.CommonLogger;
import net.herospvp.astrea.common.core.logging.TelegramLogger;
import net.herospvp.astrea.common.core.threads.InventoryUpdaterThread;
import net.herospvp.astrea.common.core.threads.StatisticsSyncThread;
import net.herospvp.astrea.common.core.threads.ViolationsReducerThread;
import net.herospvp.astrea.common.detections.combat.AutoClickerCheck;
import net.herospvp.astrea.common.detections.intave.compatibility.v13.FlightPatch;
import net.herospvp.astrea.common.detections.intave.compatibility.v13.RSMovementPatch;
import net.herospvp.astrea.common.detections.misc.BugAbuseCheck;
import net.herospvp.astrea.common.detections.unique.ChannelAnalystCheck;
import net.herospvp.astrea.common.detections.unique.CrasherCheck;
import net.herospvp.astrea.common.events.FakeCommands;
import net.herospvp.astrea.common.events.IO_Handler;
import net.herospvp.astrea.common.packets.CPacketHandler;
import net.herospvp.astrea.common.utils.CompactUtils;
import net.herospvp.astrea.common.utils.EventUtils;
import net.herospvp.astrea.common.utils.HardwareInfo;
import net.herospvp.astrea.common.utils.NMSUtils;
import net.herospvp.database.lib.Musician;
import net.herospvp.heroscore.HerosCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Astrea extends JavaPlugin {

    private Astrea instance;
    private HerosCore herosCore;
    private Musician musician;

    private Bank bank;

    private CompactUtils compactUtils;
    private GUIElements guiElements;

    private ViolationsReducerThread violationsReducerThread;
    private CommonLogger commonLogger;

    private String version, protocolLibVersion, herosCoreVersion, databaseLibVersion, intaveVersion;

    private TelegramLogger telegramLogger;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        version = getDescription().getVersion();

        PluginManager manager = getServer().getPluginManager();

        protocolLibVersion = manager.getPlugin("ProtocolLib").getDescription().getVersion();
        herosCoreVersion = manager.getPlugin("heros-core").getDescription().getVersion();
        databaseLibVersion = manager.getPlugin("database-lib").getDescription().getVersion();
        intaveVersion = "N/A";

        //
        // Dependencies
        //
        herosCore = getPlugin(HerosCore.class);
        musician = herosCore.getMusician();
        telegramLogger = new TelegramLogger("-1001293009113", "1316821738:AAEbMiUbHhgeDMekkByUkXwE8dsTEyPWpOU");

        //
        // Datastructures
        //
        bank = new Bank();

        //
        // Utils
        //
        compactUtils = new CompactUtils(
                this,
                bank,
                new CPacketHandler(),
                new HardwareInfo(),
                new NMSUtils(this),
                new EventUtils()
        );
        guiElements = new GUIElements(compactUtils);
        //
        // Threads
        //
        commonLogger = new CommonLogger(compactUtils);
        commonLogger.startup();
        commonLogger.getTotal();

        violationsReducerThread = new ViolationsReducerThread(compactUtils);
        new InventoryUpdaterThread(compactUtils);
        new StatisticsSyncThread(compactUtils);

        //
        // Listeners and Detections
        //
        new IO_Handler(compactUtils);
        new FakeCommands(compactUtils);

        FileConfiguration config = getConfig();

        //new AuraCheck(compactUtils, "Aura", true, true, true)
        //        .setEnabled(config.getBoolean("detections.aura.enabled"));

        new AutoClickerCheck(compactUtils, "AutoClicker", true, true, true)
                .setEnabled(config.getBoolean("detections.autoclicker.enabled"));

        new BugAbuseCheck(compactUtils, "BugAbuse", true, true, true)
                .setEnabled(config.getBoolean("detections.bugabuse.enabled"));

        new CrasherCheck(compactUtils, "Crasher", true, false, false)
                .setEnabled(config.getBoolean("detections.crasher.enabled"));

        new ChannelAnalystCheck(compactUtils, "ChannelAnalyst", false, false, false)
                .setEnabled(config.getBoolean("detections.channelanalyst.enabled"));

        if (manager.isPluginEnabled("Intave")) {
            new RSMovementPatch(compactUtils).setEnabled(getConfig().getBoolean("patches.rs-movement"));
            new FlightPatch(compactUtils).setEnabled(getConfig().getBoolean("patches.flight-lag"));
            intaveVersion = manager.getPlugin("Intave").getDescription().getVersion();
        }

        //
        // Commands
        //
        new AstreaCommand(this, "astrea.command", "astrea", false, null, false, compactUtils);

    }

    @Override
    public void onDisable() {
        commonLogger.save();
        compactUtils.getChecks().forEach(check -> check.setEnabled(false));
    }

}
