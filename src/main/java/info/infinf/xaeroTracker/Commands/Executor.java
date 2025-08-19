package info.infinf.xaeroTracker.Commands;

import info.infinf.xaeroTracker.XaeroTracker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Executor implements TabExecutor {
    XaeroTracker plugin;

    public  Executor(XaeroTracker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args)
    {
        if (args.length < 1) {
            return false;
        }
        switch (args[0]) {
            case "toggleTracked" -> {
                if (args.length > 2) {
                    sender.sendMessage("Too many args");
                    return false;
                }
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        if (sender.hasPermission("xaerotracker.toggleTracked")) {
                            if (plugin.trackIgnoreList.toggle(sender.getName())) {
                                sender.sendMessage("Successfully toggle yourself not be tracked");
                            } else {
                                sender.sendMessage("Successfully toggle yourself be tracked");
                            }
                            return true;
                        }
                        sender.sendMessage("Permission denied");
                        return true;
                    }
                    sender.sendMessage("You must send this as a player");
                    return true;
                } else /* length == 2 */ {
                    if (sender.hasPermission("xaerotracker.toggleTracked.others")) {
                        if (plugin.trackIgnoreList.toggle(args[1])) {
                            sender.sendMessage("Successfully toggle " + args[1] + " not be tracked");
                        } else {
                            sender.sendMessage("Successfully toggle " + args[1] + " be tracked");
                        }
                        return true;
                    }
                    sender.sendMessage("Permission denied");
                    return true;
                }
            }
             case "toggleTrackEveryone" -> {
                 if (args.length > 2) {
                     sender.sendMessage("Too many args");
                     return false;
                 }
                 if (args.length == 1) {
                     if (sender instanceof Player) {
                         if (sender.hasPermission("xaerotracker.toggleTrackEveryone")) {
                             if (plugin.trackBypassList.toggle(sender.getName())) {
                                 sender.sendMessage("Successfully toggle yourself be able to track everyone");
                             } else {
                                 sender.sendMessage("Successfully toggle yourself be unable to track everyone");
                             }
                             return true;
                         }
                         sender.sendMessage("Permission denied");
                         return true;
                     }
                     sender.sendMessage("You must send this as a player");
                     return true;
                 } else /* length == 2 */ {
                     if (sender.hasPermission("xaerotracker.toggleTrackEveryone.others")) {
                         if (plugin.trackBypassList.toggle(args[1])) {
                             sender.sendMessage("Successfully toggle " + args[1] + " be able to track everyone");
                         } else {
                             sender.sendMessage("Successfully toggle " + args[1] + " not be able to track everyone");
                         }
                         return true;
                     }
                     sender.sendMessage("Permission denied");
                     return true;
                 }
             }
            default -> {
                sender.sendMessage("Unknown subcommand");
                return false;
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NotNull [] args)
    {
        if (args.length < 1) {
            return List.of();
        }
        switch (args[0]) {
            case "toggleTracked" -> {
                if (args.length == 2 && sender.hasPermission("xaerotracker.toggleTracked.others")) {
                    return null;
                }
                return List.of();
            }
            case "toggleTrackEveryone" -> {
                if (args.length == 2 && sender.hasPermission("xaerotracker.toggleTrackEveryone.others")) {
                    return null;
                }
                return List.of();
            }
            default -> {
                var res = new ArrayList<String>();
                if (sender.hasPermission("xaerotracker.toggleTracked") ||
                        sender.hasPermission("xaerotracker.toggleTracked.others")) {
                    res.add("toggleTracked");
                }
                if (sender.hasPermission("xaerotracker.toggleTrackEveryone") ||
                        sender.hasPermission("xaerotracker.toggleTrackEveryone.others")) {
                    res.add("toggleTrackEveryone");
                }
                return res;
            }
        }
    }
}
