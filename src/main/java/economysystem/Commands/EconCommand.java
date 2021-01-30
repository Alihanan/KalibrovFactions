package economysystem.Commands;

import economysystem.MedievalEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconCommand {

    MedievalEconomy medievalEconomy = null;

    public EconCommand(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("help")) {
                if (sender instanceof Player) {
                    medievalEconomy.utilities.sendHelpMessage((Player) sender);
                }
            }

            if (args[0].equalsIgnoreCase("createcurrency")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("medievaleconomy.createcurrency") || player.hasPermission("medievaleconomy.admin")) {

                        if (args.length == 1) {
                            medievalEconomy.utilities.addCurrencyToInventory(player, 1);
                        }
                        else {
                            medievalEconomy.utilities.addCurrencyToInventory(player, Integer.parseInt(args[1]));
                        }

                    }
                    else {
                        player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("createCurrencyNoPermission"));
                    }
                }
                else {
                    System.out.println(medievalEconomy.getConfig().getString("createCurrencyNoRunFromConsole"));
                }
            }
        }
        else {
            if (sender instanceof Player) {
                medievalEconomy.utilities.sendHelpMessage((Player) sender);
            }
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("medievaleconomy.reload") || player.hasPermission("medievaleconomy.admin")) {
                    medievalEconomy.reloadConfig();
                    player.sendMessage(ChatColor.GREEN + medievalEconomy.getConfig().getString("configReloadedText"));
                }
                else {
                    player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("reloadNoPermission"));
                }
            }
            else {
                medievalEconomy.reloadConfig();
                System.out.println(medievalEconomy.getConfig().getString("configReloadedText"));
            }

        }
    }

}
