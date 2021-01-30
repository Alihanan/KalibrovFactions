package Professions;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import Karavany.TradePoint;
import factionsystem.Main;
import factionsystem.Objects.Faction;

public class PlayerBreakBlockHandler {
	Main main = null;

    public PlayerBreakBlockHandler(Main plugin) {
        main = plugin;
    }
    
    @EventHandler
    public void handle(BlockBreakEvent event) {
    	Player player = event.getPlayer();
    	Block b = event.getBlock();   
    	// Convoys
    	if(main.tradeChests.contains(b)) {
    		if(b.getType() == Material.CHEST) {
    			player.sendMessage(ChatColor.RED + "Нельзя ломать сундуки торговой точки!");
    			event.setCancelled(true);
    		}else {
    			player.sendMessage(ChatColor.RED + "Поидее здесь должен быть сундук! Удаляем точку");
    			Chunk c = b.getChunk();
    			if(main.allTradePosts.containsKey(c)) {
    				TradePoint tp = main.allTradePosts.get(c);
    				Faction f = main.utilities.getFaction(tp.factionName, main.factions);
    				f.DeleteTradePoint();
    			}
    		}
    		
    		return;
    	}
    		
    	
    	ItemStack is = player.getInventory().getItemInMainHand();
    	if(	isStone(b.getType())
    			&& 
    		!isPickaxe(is.getType())) {
    		player.sendMessage(ChatColor.RED + "Камень можно добыть только киркой!");
    		event.setCancelled(true);
    		b.setType(Material.AIR);
    	}
    	if(b.getType() == Material.WOOD && !isAxe(is.getType())) {
    		player.sendMessage(ChatColor.RED + "Дерево можно добыть только топором!");
    		event.setCancelled(true);
    		b.setType(Material.AIR);
    	}
    }
    private boolean isStone(Material mainHand) {
    	final Material[] pickaxes = {
    			Material.STONE,
    			Material.IRON_ORE,
    			Material.GOLD_ORE,
    			Material.DIAMOND_ORE,
    			Material.COAL_ORE,
    			Material.EMERALD_ORE,
    	};
    	for(Material a : pickaxes) {
    		if(a == mainHand) return true;
    	}
    	return false;
    }
    private boolean isPickaxe(Material mainHand) {
    	final Material[] pickaxes = {
    			Material.WOOD_PICKAXE,
    			Material.STONE_PICKAXE,
    			Material.IRON_PICKAXE,
    			Material.GOLD_PICKAXE,
    			Material.DIAMOND_PICKAXE,
    	};
    	for(Material a : pickaxes) {
    		if(a == mainHand) return true;
    	}
    	return false;
    }
    private boolean isAxe(Material mainHand) {
    	final Material[] axes = {
    			Material.WOOD_AXE,
    			Material.STONE_AXE,
    			Material.IRON_AXE,
    			Material.GOLD_AXE,
    			Material.DIAMOND_AXE,
    	};
    	for(Material a : axes) {
    		if(a == mainHand) return true;
    	}
    	return false;
    }
}
