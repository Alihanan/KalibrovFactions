package economysystem;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import economysystem.commands.InfoCoinsCommand;
import economysystem.commands.TraderCommand;
import factionsystem.Main;

public class EconomyCommandHandler {

	Main plugin;
	
	public EconomyCommandHandler(Main main) {
		plugin = main;
	}
	
	public void handle(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(ChatColor.AQUA+"<ПОМОЩЬ>");
			return;
		}
		/**
		 *   /econ infocoins
		 */
		if (args[0].equalsIgnoreCase("infocoins")) {
			Player player;
			if(sender instanceof Player) {
				player = (Player)sender;
			}else {
				sender.sendMessage(ChatColor.RED+"Золото можно проверять только из игры!");
				return;
			}
			InfoCoinsCommand command = new InfoCoinsCommand(plugin);
			int playercoins = command.infoCoins(player);
			sender.sendMessage(ChatColor.GREEN+"Золота в инвентаре "+playercoins);
			return;
		}
		if (args[0].equalsIgnoreCase("trader")) {
			Player player;
			if(sender instanceof Player) {
				player = (Player)sender;
			}else {
				sender.sendMessage(ChatColor.RED+"Торговца можно создать только из игры!");
				return;
			}
			TraderCommand tc = new TraderCommand(plugin);
			tc.handle(player, args);
			
			return;
		}
		/*
		if (label.equalsIgnoreCase("chunkcoins")) {
			ChunkCoinsCommand command = new ChunkCoinsCommand(medievalEconomy);
			int amount = command.chunkCoins(player.getLocation().getChunk());
			sender.sendMessage(ChatColor.GREEN + "Всего золота в сундуках на чанк "+Integer.toString(amount));
			return true;
		}
		if (label.equalsIgnoreCase("tradetoreal")) {
			TradeToRealCommand command = new TradeToRealCommand(medievalEconomy);
			if (args.length > 0) {
			int cauntcoin = Integer.parseInt(args[0]);
			command.sendTradeToReal(sender, cauntcoin);
			return true;
			}
			else {
				sender.sendMessage(ChatColor.RED + "tradetoreal <количество> ");
				return true;
			}
		}
		if (label.equalsIgnoreCase("econgui")) {
			GUICommand command = new GUICommand(medievalEconomy);
			command.GUI(sender);
			return true;
			}
		 */
	}
	
	
}
