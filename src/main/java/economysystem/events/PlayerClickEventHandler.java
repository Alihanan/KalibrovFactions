package economysystem.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import economysystem.CitizensNPC.TraderTrait;
import economysystem.commands.InfoCoinsCommand;
import economysystem.commands.TradeToRealCommand;
import factionsystem.Main;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class PlayerClickEventHandler {

	Main plugin = null;

	public PlayerClickEventHandler(Main plugin) {
		this.plugin = plugin;
	}

	public void GUIClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
//		Coinpurse purse = medievalEconomy.utilities.getPlayersCoinPurse(player.getUniqueId());
		int playercoins = InfoCoinsCommand.infoCoins(player);

		String page = event.getInventory().getTitle().split(" ")[0];

		NPC npc = plugin.tradingCurrently.get(player);
		if(npc == null) return;
		TraderTrait trait = npc.getTrait(TraderTrait.class);
		
		if (page.equalsIgnoreCase(ChatColor.AQUA + "Страница")) {			
			String pagenum = event.getInventory().getTitle().split(" ")[1];
			int pageintnum = Integer.parseInt(pagenum);
			int index_page = pageintnum - 1;
			
			
			switch (event.getCurrentItem().getType()) {
			case STAINED_GLASS_PANE:
				event.setCancelled(true);
				String buttonname = event.getCurrentItem().getItemMeta().getDisplayName();
				if (buttonname.equals("Предыдущая страница")) {
					index_page--;
					if (index_page<0) {
						index_page = trait.guis.length - 1;
					}
					player.closeInventory();
					Inventory gui = Bukkit.createInventory(player, 27,ChatColor.AQUA+ "Страница "+ (index_page + 1));
					gui.setContents(trait.guis[index_page]);
					player.openInventory(gui);
					break;
				}
				if (buttonname.equals("Следующая страница")) {
					index_page++;
					if (index_page == trait.guis.length) {index_page = 0;}
					player.closeInventory();
					Inventory gui = Bukkit.createInventory(player, 27,ChatColor.AQUA+ "Страница "+(index_page+1));
					gui.setContents(trait.guis[index_page]);
					player.openInventory(gui);
					break;
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
