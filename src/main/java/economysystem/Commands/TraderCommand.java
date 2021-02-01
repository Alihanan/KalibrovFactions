package economysystem.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Karavany.NPCConvoyTrait;
import economysystem.CitizensNPC.TraderTrait;
import factionsystem.Main;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCTraitCommandAttachEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;

public class TraderCommand {
	Main plugin;
	
	public TraderCommand(Main pl) {
		plugin = pl;
	}
	
	public void handle(Player sender, String[] args) {
		Location loc = sender.getLocation();
		
		NPC npc2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.RED 
    			+ "Торговец");
		
		//HashMap<String, Integer> cc = plugin.customconfig.currency;
		HashMap<Material, Integer> items = new  HashMap<Material, Integer>();
		items.put(Material.DIAMOND, 17);
		items.put(Material.DIAMOND_SWORD, 3);
		items.put(Material.WOOD, 80);
		items.put(Material.STONE, 80);
		
		
		/*
		for(String s : cc.keySet()) {
			Material m = Material.valueOf(s);
			if(m == null) continue;
			ItemStack is = new ItemStack(m);
			items.put(is, cc.get(s));
		}*/
    	npc2.addTrait(new TraderTrait(items, plugin));  
		Bukkit.getPluginManager().callEvent(new NPCTraitCommandAttachEvent(npc2, TraderTrait.class, null));
		
		npc2.getOrAddTrait(LookClose.class).setRandomLook(true);
		npc2.getOrAddTrait(LookClose.class).setRandomLookDelay(20);
		npc2.getOrAddTrait(LookClose.class).toggle();
		
		npc2.spawn(loc);
	}
	
}
