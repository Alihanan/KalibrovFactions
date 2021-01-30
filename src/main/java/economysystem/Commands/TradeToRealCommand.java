package economysystem.Commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import economysystem.MedievalEconomy;
import economysystem.Objects.Coinpurse;
import org.bukkit.ChatColor;

public class TradeToRealCommand {
	MedievalEconomy medievalEconomy = null;

	public TradeToRealCommand(MedievalEconomy plugin) {
		medievalEconomy = plugin;
	}

	public static Vector TradeToReal(int Coin) {
		int a = Coin % 10;
		int b = (Coin % 100) / 10;
		int c = Coin / 100;
		Vector CoinTrade = new Vector(a, b, c);
		return CoinTrade;
	}

	public void sendTradeToReal(CommandSender sender, int cauntcoin) {

		if (sender instanceof Player) {
			Vector returnet = TradeToReal(cauntcoin);
			Player player = (Player) sender;
			Coinpurse purse = medievalEconomy.utilities.getPlayersCoinPurse(player.getUniqueId());
			int playercoins = purse.getCoins();
			if (playercoins >= cauntcoin) {
				playercoins -= cauntcoin;
				purse.setCoins(playercoins);
				Inventory playerinv = player.getInventory();
				ItemStack nuggets = new ItemStack(Material.GOLD_NUGGET, returnet.getBlockX());
				ItemStack gingots = new ItemStack(Material.GOLD_INGOT, returnet.getBlockY());
				int cauntgblock = (int) returnet.getBlockZ();
				ItemStack gblocks = new ItemStack(Material.GOLD_BLOCK);
				for (int i = 0; i < cauntgblock; i++) {
					playerinv.addItem(gblocks);
				}
				playerinv.addItem(nuggets);
				playerinv.addItem(gingots);
			}
		}
	}

	public static void GiveCoins(Player player, int playercoins, int itempriceint) {
		if (playercoins >= itempriceint) {
			playercoins -= itempriceint;
			Vector returnet = TradeToReal(playercoins);
			Inventory playerinv = player.getInventory();
			ItemStack nuggets = new ItemStack(Material.GOLD_NUGGET, returnet.getBlockX());
			ItemStack gingots = new ItemStack(Material.GOLD_INGOT, returnet.getBlockY());
			int cauntgblock = (int) returnet.getBlockZ();
			ItemStack gblocks = new ItemStack(Material.GOLD_BLOCK);
			for (int i = 0; i < cauntgblock; i++) {
				playerinv.addItem(gblocks);
			}
			playerinv.addItem(nuggets);
			playerinv.addItem(gingots);
		}
	
	}
	
	public static void TakeCoins(Player player, int itempriceint) {
			Vector returnet = TradeToReal(itempriceint);
			Inventory playerinv = player.getInventory();
			ItemStack nuggets = new ItemStack(Material.GOLD_NUGGET, returnet.getBlockX());
			ItemStack gingots = new ItemStack(Material.GOLD_INGOT, returnet.getBlockY());
			int cauntgblock = (int) returnet.getBlockZ();
			ItemStack gblocks = new ItemStack(Material.GOLD_BLOCK);
			for (int i = 0; i < cauntgblock; i++) {
				playerinv.removeItem(gblocks);
			}
			playerinv.removeItem(nuggets);
			playerinv.removeItem(gingots);
	}
	
}
