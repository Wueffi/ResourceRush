package wueffi.resourcerush.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import wueffi.resourcerush.utils.Holograms;
import wueffi.resourcerush.utils.ModManager;

import java.util.List;

public final class HologramCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hologram")) return handleHologram(sender, args);
        return false;
    }

    public static boolean handleHologram(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /playtime. Use /check <player> instead.");
            return true;
        }

        if (!ModManager.isModerator(player.getName())) {
            player.sendMessage("§cYou do not have perms for this command!");
            return true;
        }

        if (args.length <= 1) {
            player.sendMessage("§cWrong arguments!");
            return true;
        }

        if (args[0].equals("set")) Holograms.setHologram(player, args[1]);
        else if (args[0].equals("remove")) Holograms.removeHologram(args[1]);
        else {
            player.sendMessage("§cWrong argument " + args[0] + "!");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ((command.getName().equalsIgnoreCase("hologram")) && args.length <= 1) return List.of("set", "remove");
        if ((command.getName().equalsIgnoreCase("hologram")) && args.length > 1) return List.of("1", "2", "3");
        return List.of();
    }
}
