package economysystem.EventHandlers;

import economysystem.Objects.Coinpurse;
import economysystem.MedievalEconomy;
import org.bukkit.ChatColor;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEventHandler {

    MedievalEconomy medievalEconomy = null;

    public PlayerDeathEventHandler(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void handle(PlayerDeathEvent event) {
        Coinpurse purse = medievalEconomy.utilities.getPlayersCoinPurse(event.getEntity().getUniqueId());

        if (purse.getCoins() != 0) {

            int amountToDrop = 0;

            // check if purse has at least 10 coins
            if (purse.containsAtLeast(10)) {
                amountToDrop = (int) (purse.getCoins() * 0.10);
            }
            else {
                amountToDrop = 1;
            }

            // remove coins from purse
            purse.removeCoins(amountToDrop);

            // drop coins on ground
            event.getDrops().add(medievalEconomy.utilities.getCurrency(amountToDrop));

            // inform player
            event.getEntity().sendMessage(ChatColor.RED + medievalEconomy.getConfig().getString("deathMessage"));
        }
    }

}
