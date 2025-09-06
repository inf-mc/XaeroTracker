package info.infinf.xaeroTracker.Commands;

import info.infinf.xaeroTracker.XaeroTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Executor implements TabExecutor {
    public static Style FAILED_STYLE = Style.style(NamedTextColor.RED);
    public static Style SUCCEEDED_STYLE = Style.style(NamedTextColor.GREEN);

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
                    sender.sendMessage(Component.translatable("xaerotracker.command.excess_args", FAILED_STYLE));
                    return false;
                }
                if (args.length == 1) {
                    if (sender instanceof Player pl) {
                        if (sender.hasPermission("xaerotracker.toggleTracked")) {
                            plugin.trackerThread.submit(() -> {
                                if (plugin.trackIgnoreList.toggle(pl.getName())) {
                                    sender.sendMessage(Component.translatable(
                                            "xaerotracker.command.succeed_toggle_not_be_tracked", SUCCEEDED_STYLE));
                                } else {
                                    sender.sendMessage(Component.translatable(
                                            "xaerotracker.command.succeed_toggle_be_tracked", SUCCEEDED_STYLE));
                                }
                                var data = plugin.playerData.get(pl);
                                if (data != null) {
                                    plugin.track(pl, data);
                                }
                            });
                            return true;
                        }
                        sender.sendMessage(Component.translatable("xaerotracker.command.permission", FAILED_STYLE));
                        return true;
                    }
                    sender.sendMessage("You must send this as a player");
                    return true;
                } else /* length == 2 */ {
                    if (sender.hasPermission("xaerotracker.toggleTracked.others")) {
                        plugin.trackerThread.submit(() -> {
                            var name = args[1];
                            if (plugin.trackIgnoreList.toggle(name)) {
                                sender.sendMessage(Component.translatable(
                                        "xaerotracker.command.succeed_toggle_other_not_be_tracked",
                                        SUCCEEDED_STYLE,
                                        Component.text(name)));
                            } else {
                                sender.sendMessage(Component.translatable(
                                        "xaerotracker.command.succeed_toggle_other_be_tracked",
                                        SUCCEEDED_STYLE,
                                        Component.text(name)));
                            }
                            var pl = plugin.getServer().getPlayerExact(name);
                            var data = plugin.playerData.get(pl);
                            if (pl != null && data != null) {
                                plugin.track(pl, data);
                            }
                        });
                        return true;
                    }
                    sender.sendMessage(Component.translatable("xaerotracker.command.permission", FAILED_STYLE));
                    return true;
                }
            }
             case "toggleTrackEveryone" -> {
                 if (args.length > 2) {
                     sender.sendMessage(Component.translatable("xaerotracker.command.excess_args", FAILED_STYLE));
                     return false;
                 }
                 if (args.length == 1) {
                     if (sender instanceof Player pl) {
                         if (sender.hasPermission("xaerotracker.toggleTrackEveryone")) {
                             plugin.trackerThread.submit(() -> {
                                 var data = plugin.playerData.get(pl);
                                 if (plugin.trackBypassList.toggle(pl.getName())) {
                                     sender.sendMessage(Component.translatable(
                                             "xaerotracker.command.succeed_toggle_track_everyone", SUCCEEDED_STYLE));
                                     if (data != null) {
                                         plugin.trackOthers(pl, data.channel);
                                     }
                                 } else {
                                     sender.sendMessage(Component.translatable(
                                             "xaerotracker.command.succeed_toggle_not_track_everyone", SUCCEEDED_STYLE));
                                     if (data != null) {
                                         plugin.hideUntracked(pl);
                                     }
                                 }
                             });
                             return true;
                         }
                         sender.sendMessage(Component.translatable("xaerotracker.command.permission", FAILED_STYLE));
                         return true;
                     }
                     sender.sendMessage("You must send this as a player");
                     return true;
                 } else /* length == 2 */ {
                     var name = args[1];
                     if (sender.hasPermission("xaerotracker.toggleTrackEveryone.others")) {
                         plugin.trackerThread.submit(() -> {
                             var pl = plugin.getServer().getPlayerExact(name);
                             var data = plugin.playerData.get(pl);
                             if (plugin.trackBypassList.toggle(name)) {

                                 sender.sendMessage(Component.translatable(
                                         "xaerotracker.command.succeed_toggle_other_track_everyone",
                                         SUCCEEDED_STYLE,
                                         Component.text(name)));
                                 if (pl != null && data != null) {
                                     plugin.trackOthers(pl, data.channel);
                                 }
                             } else {
                                 sender.sendMessage(Component.translatable(
                                         "xaerotracker.command.succeed_toggle_other_not_track_everyone",
                                         SUCCEEDED_STYLE,
                                         Component.text(args[1])));
                                 if (pl != null && data != null) {
                                     plugin.hideUntracked(pl);
                                 }
                             }
                         });
                         return true;
                     }
                     sender.sendMessage(Component.translatable("xaerotracker.command.permission", FAILED_STYLE));
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
