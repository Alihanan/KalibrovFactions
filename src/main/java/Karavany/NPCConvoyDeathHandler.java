package Karavany;

import java.util.HashMap;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import factionsystem.Main;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.md_5.bungee.api.ChatColor;

public class NPCConvoyDeathHandler implements Listener{
	Main main;
	public NPCConvoyDeathHandler(Main main){
		this.main = main;
	}
	
	@EventHandler
    public void OnNPCDeath(NPCDeathEvent nde)
    {
		NPC npc = nde.getNPC();
		
		NPCConvoyTrait nct = npc.getTrait(NPCConvoyTrait.class);
		ConvoyFollowerTrait cft = npc.getTrait(ConvoyFollowerTrait.class);
		
		if(nct != null) {
			nct.dieAndDrop();
			main.utilities.sendAllPlayersOnServerMessage(
					ChatColor.RED + "Караван фракции " + nct.myFaction.getName() + " был ограблен!");					
		}else if(cft != null){
			Inventory inv = npc.getTrait(Inventory.class);
			if(inv != null) {
				inv.setContents(new ItemStack[inv.getContents().length]);
			}
		}
		
    }
}
	
