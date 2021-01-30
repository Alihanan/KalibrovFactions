package economysystem.EventHandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import economysystem.MedievalEconomy;
import economysystem.Commands.InfoCoinsCommand;
import economysystem.Commands.TradeToRealCommand;
import economysystem.Objects.Coinpurse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class PlayerClickEventHandler {

	MedievalEconomy medievalEconomy = null;

	public PlayerClickEventHandler(MedievalEconomy plugin) {
		medievalEconomy = plugin;
	}

	public void GUIClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
//		Coinpurse purse = medievalEconomy.utilities.getPlayersCoinPurse(player.getUniqueId());
		int playercoins = InfoCoinsCommand.infoCoins(player);

		String page = event.getInventory().getTitle().split(" ")[0];

		if (page.equalsIgnoreCase(ChatColor.AQUA + "Страница")) {			
			String pagenum = event.getInventory().getTitle().split(" ")[1];
			int pageintnum = Integer.parseInt(pagenum);
		
			switch (event.getCurrentItem().getType()) {
			case STAINED_GLASS_PANE:
				event.setCancelled(true);
				String buttonname = event.getCurrentItem().getItemMeta().getDisplayName();
				if (buttonname.equals("Предыдущая страница")) {
					if (pageintnum-1<0) {pageintnum = medievalEconomy.customconfig.guis.length;}
					player.closeInventory();
					Inventory gui = Bukkit.createInventory(player, 27,ChatColor.AQUA+ "Страница "+(pageintnum-1));
					gui.setContents(medievalEconomy.customconfig.guis[pageintnum-2]);
					player.openInventory(gui);
				}
				if (buttonname.equals("Следующая страница")) {
					if (pageintnum>=medievalEconomy.customconfig.guis.length) {pageintnum = 1;}
					player.closeInventory();
					Inventory gui = Bukkit.createInventory(player, 27,ChatColor.AQUA+ "Страница "+(pageintnum+1));
					gui.setContents(medievalEconomy.customconfig.guis[pageintnum]);
					player.openInventory(gui);
				}
				break;
				
			default: 
				String itemprice = event.getCurrentItem().getItemMeta().getLore().get(0).split(" ")[1];
				int itempriceint = Integer.parseInt(itemprice);
				if (playercoins>=itempriceint) {
					TradeToRealCommand.TakeCoins(player, itempriceint);
					TradeToRealCommand.GiveCoins(player,playercoins,itempriceint);
					player.getInventory().addItem(new ItemStack (event.getCurrentItem().getType()));
					player.sendMessage(ChatColor.GREEN+"Операция выполнена, ваш баланс "+(playercoins-itempriceint));
				}
				else 
				{
					player.sendMessage(ChatColor.RED+"Недостаточно средств, ваш баланс "+playercoins);
				}
			}

			event.setCancelled(true);
		}
	}

}
