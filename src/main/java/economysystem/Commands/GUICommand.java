package economysystem.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import economysystem.MedievalEconomy;
import economysystem.Objects.Coinpurse;
import org.bukkit.ChatColor;

public class GUICommand {
	MedievalEconomy medievalEconomy = null;

	public GUICommand(MedievalEconomy plugin) {
		medievalEconomy = plugin;
	}

	public void GUI(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			
			Inventory gui = Bukkit.createInventory(player, 27,ChatColor.AQUA+ "Страница 1");
			gui.setContents(medievalEconomy.customconfig.guis[0]);
			player.openInventory(gui);
		}
	}

}
