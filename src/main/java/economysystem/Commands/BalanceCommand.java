package economysystem.Commands;

import economysystem.Objects.Coinpurse;
import economysystem.MedievalEconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand {

    MedievalEconomy medievalEconomy = null;

    public BalanceCommand(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void run(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // permission check
            if (player.hasPermission("medievaleconomy.balance") || player.hasPermission("medievaleconomy.default")) {

                Coinpurse purse = medievalEconomy.utilities.getPlayersCoinPurse(player.getUniqueId());

                if (purse != null) {

                    int num = purse.getCoins();

                    player.sendMessage(ChatColor.GREEN + medievalEconomy.getConfig().getString("balanceTextStart") + num + medievalEconomy.getConfig().getString("balanceTextEnd"));

                }

            }
            else {
                player.sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("balanceNoPermission"));
            }

        }
    }

}
