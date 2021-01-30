package economysystem.Subsystems;

import economysystem.Commands.BalanceCommand;
import economysystem.Commands.ChunkCoinsCommand;
import economysystem.Commands.DepositCommand;
import economysystem.Commands.EconCommand;
import economysystem.Commands.GUICommand;
import economysystem.Commands.InfoCoinsCommand;
import economysystem.Commands.TradeToRealCommand;
import economysystem.Commands.WithdrawCommand;
import org.bukkit.ChatColor;
import economysystem.MedievalEconomy;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSubsystem {

	MedievalEconomy medievalEconomy = null;

	public CommandSubsystem(MedievalEconomy plugin) {
		medievalEconomy = plugin;
	}

	public boolean interpretCommand(CommandSender sender, String label, String[] args) {
		Player player = (Player) sender;
		if (label.equalsIgnoreCase("econ")) {
			EconCommand command = new EconCommand(medievalEconomy);
			command.run(sender, args);
			return true;
		}
		if (label.equalsIgnoreCase("balance")) {
			BalanceCommand command = new BalanceCommand(medievalEconomy);
			command.run(sender);
			return true;
		}
		if (label.equalsIgnoreCase("deposit")) {
			DepositCommand command = new DepositCommand(medievalEconomy);
			command.depositCoins(sender, args);
			return true;
		}
		if (label.equalsIgnoreCase("withdraw")) {
			WithdrawCommand command = new WithdrawCommand(medievalEconomy);
			command.withdrawCoins(sender, args);
			return true;
		}
		if (label.equalsIgnoreCase("infocoins")) {
			InfoCoinsCommand command = new InfoCoinsCommand(medievalEconomy);
			int playercoins = command.infoCoins(player);
			sender.sendMessage(ChatColor.GREEN+"Золота в инвентаре "+playercoins);
			return true;
		}
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
		return false;
	}
}
