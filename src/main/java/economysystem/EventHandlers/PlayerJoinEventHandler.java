package economysystem.EventHandlers;

import economysystem.Objects.Coinpurse;
import economysystem.MedievalEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinEventHandler {

    MedievalEconomy medievalEconomy = null;

    public PlayerJoinEventHandler(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void handle(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "You wake up and find that you have some gold coins, some food and an empty book on your person.");
            event.getPlayer().getInventory().addItem(medievalEconomy.utilities.getCurrency(50));
            event.getPlayer().getInventory().addItem(new ItemStack(Material.BREAD, 10));
            event.getPlayer().getInventory().addItem(new ItemStack(Material.WRITTEN_BOOK));
        }

        if (!medievalEconomy.utilities.hasCoinpurse(event.getPlayer().getUniqueId())) {
            // assign coinpurse
            Coinpurse purse = new Coinpurse(medievalEconomy);
            purse.setPlayerUUID(event.getPlayer().getUniqueId());
            medievalEconomy.coinpurses.add(purse);
            event.getPlayer().sendMessage(ChatColor.GREEN + "You lay a hand at your side to reassure yourself your coinpurse is still there. (commands: /balance, /deposit, /withdraw)");
        }
    }

}
