package economysystem.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import factionsystem.Main;

public class ChunkCoinsCommand {
	Main plugin = null;

	public ChunkCoinsCommand(Main plugin) {
		this.plugin = plugin;
	}

	public static int chunkCoins(Chunk chunk) {
		ArrayList<Chest> chests = new ArrayList<Chest>();
		boolean flag = false;
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 255; y++) {
				for (int z = 0; z < 16; z++) {
					Block block = chunk.getBlock(x, y, z);
					if (block.getType() == Material.CHEST) {
						Chest chest = (Chest) block.getState();
						chests.add(chest);
						flag = true;
					}
				}
			}
		}
		int totalnumber = 0;
		if (flag == true) {
			for (int i = 0; i < chests.size(); i++) {
				if ( chests.get(i).getInventory().contains(Material.GOLD_NUGGET)) {
					HashMap<Integer, ? extends ItemStack> nuggets = chests.get(i).getInventory()
							.all(Material.GOLD_NUGGET);
					for (int a = 0; a < nuggets.size(); a++) {
						ItemStack nugget1 = (ItemStack) nuggets.values().toArray()[i];
						int countnugget = nugget1.getAmount();
						totalnumber += countnugget;
					}
				}
				if (chests.get(i).getInventory().contains(Material.GOLD_INGOT)) {
					HashMap<Integer, ? extends ItemStack> golds = chests.get(i).getInventory()
							.all(Material.GOLD_INGOT);
					for (int a = 0; a < golds.size(); a++) {
						ItemStack golds1 = (ItemStack) golds.values().toArray()[i];
						int countgolds = golds1.getAmount();
						totalnumber += countgolds * 9;
					}
				}
				if (chests.get(i).getInventory().contains(Material.GOLD_BLOCK)) {
					HashMap<Integer, ? extends ItemStack> goldblocks = chests.get(i).getInventory()
							.all(Material.GOLD_BLOCK);
					for (int a = 0; a < goldblocks.size(); a++) {
						ItemStack goldblocks1 = (ItemStack) goldblocks.values().toArray()[i];
						int countgoldblocks = goldblocks1.getAmount();
						totalnumber += countgoldblocks * 9 * 9;
					}
				}
			}
		}
		return totalnumber;
	}

}
