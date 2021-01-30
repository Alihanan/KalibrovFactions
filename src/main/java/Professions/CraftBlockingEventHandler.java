package Professions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import factionsystem.Main;

public class CraftBlockingEventHandler {
	Main main = null;
	//private const Material[] 
	
	
    public CraftBlockingEventHandler(Main plugin) {
        main = plugin;
    }
    
    @EventHandler
    public void handle(PrepareItemCraftEvent event) {
    	 Recipe rp = event.getRecipe();
    	 if(rp == null) return;
    	 Material itemType = rp.getResult().getType();
    	 ItemStack[] items = event.getInventory().getMatrix();
         Byte itemData = event.getRecipe().getResult().getData().getData();
         if(itemType==Material.ENDER_CHEST
        		 ||itemType==Material.HOPPER
        		 ||(itemType==Material.GOLDEN_APPLE&&itemData==1)
        		 ) {
        	 
             event.getInventory().setResult(new ItemStack(Material.AIR));
             
             for(HumanEntity he:event.getViewers()) {
                 if(he instanceof Player) {
                     ((Player)he).sendMessage(ChatColor.RED+"You cannot craft this!");
                 }
             }
         }
    }
    
    @EventHandler
    public void handle(InventoryClickEvent event) {
   	 	System.out.println(ChatColor.RED + "Action:" + event.getAction());
   	  
   	 	for(HumanEntity he : event.getViewers()) {
   	 		Player p = (Player)he;
   	 		System.out.println(ChatColor.DARK_RED + "SLOT:" +  event.getSlot());
   	 		if(event.getInventory() == he.getInventory()) {
   	 			System.out.println(ChatColor.DARK_RED + "SLOT:" +  event.getSlot());
   	 		}
   	 	}
   	 	

   }
}
