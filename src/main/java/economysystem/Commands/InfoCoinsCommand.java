package economysystem.Commands;

import economysystem.Objects.Coinpurse;
import economysystem.MedievalEconomy;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InfoCoinsCommand {

	MedievalEconomy medievalEconomy = null;

	public InfoCoinsCommand(MedievalEconomy plugin) {
		medievalEconomy = plugin;
	}

	public static int infoCoins(Player player) {
		int totalnumber = 0;
		if (player.getInventory().contains(Material.GOLD_NUGGET)) {
			HashMap<Integer, ? extends ItemStack> nuggets = player.getInventory().all(Material.GOLD_NUGGET);
			for (int i = 0; i < nuggets.size(); i++) {
				ItemStack nugget1 = (ItemStack) nuggets.values().toArray()[i];
				int countnugget = nugget1.getAmount();
				totalnumber += countnugget;
			}
		}
		if (player.getInventory().contains(Material.GOLD_INGOT)) {
			HashMap<Integer, ? extends ItemStack> golds = player.getInventory().all(Material.GOLD_INGOT);
			for (int i = 0; i < golds.size(); i++) {
				ItemStack golds1 = (ItemStack) golds.values().toArray()[i];
				int countgolds = golds1.getAmount();
				totalnumber += countgolds * 10;
			}
		}
		if (player.getInventory().contains(Material.GOLD_BLOCK)) {
			HashMap<Integer, ? extends ItemStack> goldblocks = player.getInventory().all(Material.GOLD_BLOCK);
			for (int i = 0; i < goldblocks.size(); i++) {
				ItemStack goldblocks1 = (ItemStack) goldblocks.values().toArray()[i];
				int countgoldblocks = goldblocks1.getAmount();
				totalnumber += countgoldblocks * 100;
			}
		}
		return totalnumber;
	}

	public static int infoCoins(Player[] players) {
		int totalnumber = 0;
		for (int i = 0; i < players.length; i++) {
			if (players[i].getInventory().contains(Material.GOLD_NUGGET)) {
				HashMap<Integer, ? extends ItemStack> nuggets = players[i].getInventory().all(Material.GOLD_NUGGET);
				for (int a = 0; a < nuggets.size(); a++) {
					ItemStack nugget1 = (ItemStack) nuggets.values().toArray()[i];
					int countnugget = nugget1.getAmount();
					totalnumber += countnugget;
				}
			}
			if (players[i].getInventory().contains(Material.GOLD_INGOT)) {
				HashMap<Integer, ? extends ItemStack> golds = players[i].getInventory().all(Material.GOLD_INGOT);
				for (int a = 0; a < golds.size(); a++) {
					ItemStack golds1 = (ItemStack) golds.values().toArray()[i];
					int countgolds = golds1.getAmount();
					totalnumber += countgolds * 10;
				}
			}
			if (players[i].getInventory().contains(Material.GOLD_BLOCK)) {
				HashMap<Integer, ? extends ItemStack> goldblocks = players[i].getInventory().all(Material.GOLD_BLOCK);
				for (int a = 0; a < goldblocks.size(); a++) {
					ItemStack goldblocks1 = (ItemStack) goldblocks.values().toArray()[i];
					int countgoldblocks = goldblocks1.getAmount();
					totalnumber += countgoldblocks * 100;
				}
			}
		}
		return totalnumber;
	}

	public static int infoCoins(Chest chest) {
		int totalnumber = 0;
		if (chest.getInventory().contains(Material.GOLD_NUGGET)) {
			HashMap<Integer, ? extends ItemStack> nuggets = chest.getInventory().all(Material.GOLD_NUGGET);
			for (int i = 0; i < nuggets.size(); i++) {
				ItemStack nugget1 = (ItemStack) nuggets.values().toArray()[i];
				int countnugget = nugget1.getAmount();
				totalnumber += countnugget;
			}
		}
		if (chest.getInventory().contains(Material.GOLD_INGOT)) {
			HashMap<Integer, ? extends ItemStack> golds = chest.getInventory().all(Material.GOLD_INGOT);
			for (int i = 0; i < golds.size(); i++) {
				ItemStack golds1 = (ItemStack) golds.values().toArray()[i];
				int countgolds = golds1.getAmount();
				totalnumber += countgolds * 10;
			}
		}
		if (chest.getInventory().contains(Material.GOLD_BLOCK)) {
			HashMap<Integer, ? extends ItemStack> goldblocks = chest.getInventory().all(Material.GOLD_BLOCK);
			for (int i = 0; i < goldblocks.size(); i++) {
				ItemStack goldblocks1 = (ItemStack) goldblocks.values().toArray()[i];
				int countgoldblocks = goldblocks1.getAmount();
				totalnumber += countgoldblocks * 100;
			}
		}
		return totalnumber;
	}

}
