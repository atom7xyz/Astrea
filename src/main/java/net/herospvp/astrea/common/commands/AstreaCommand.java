package net.herospvp.astrea.common.commands;

import net.herospvp.astrea.common.utils.CompactUtils;
import net.herospvp.heroscore.utils.CommandsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class AstreaCommand extends CommandsHandler {

    private final CompactUtils compactUtils;

    public AstreaCommand(
            JavaPlugin instance,
            String permission,
            String command,
            boolean onlyPlayer,
            List<String> usage,
            boolean tabCompleteCustom,
            CompactUtils compactUtils
    ) {
        super(instance, permission, command, onlyPlayer, usage, tabCompleteCustom);
        this.compactUtils = compactUtils;
    }

    @Override
    public boolean command(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        compactUtils.getGUIElements().openInv(player);
        return false;
    }

    @Override
    public List<String> tabComplete(String[] strings) {
        return null;
    }

}
