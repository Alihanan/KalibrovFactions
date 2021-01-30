package Karavany;

import static factionsystem.Subsystems.UtilitySubsystem.getPlayersFaction;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.projectiles.ProjectileSource;

import factionsystem.Main;
import factionsystem.Objects.Faction;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.md_5.bungee.api.ChatColor;

public class ConvoyDamageListener implements Listener{
	
	Main main;
	public ConvoyDamageListener(Main main){
		this.main = main;
	}
	
	@EventHandler
    public void OnNPCDamage(NPCDamageByEntityEvent nde)
    {
		NPC npc = nde.getNPC();
		Player player;
				
		try {
			Object cause = nde.getDamager();
			if(cause instanceof Projectile) {
				if(cause == null) return;
				Projectile p = (Projectile)(cause);
				if(p == null) return;
				ProjectileSource ps = p.getShooter();
				if(ps == null) {
					return;
				}
				else if(ps instanceof Player) {
					player = (Player)ps;
				}else {
					return;
				}
			}
			else if(!nde.getDamager().getType().equals(EntityType.PLAYER)) {			
				return;
			}else {
				player = (Player)(nde.getDamager());
			}
		}catch(Exception npe) {
			System.out.println(ChatColor.GREEN + "[KARAVANY] NPC Damage error. Just another minecraft feature :)");
			return;
		}
		
		
		
		Faction playersFaction = getPlayersFaction(player.getUniqueId(), main.factions);
		
		for(Trait t : npc.getTraits()) {
			if(t.getClass().equals(NPCConvoyTrait.class)) {
				NPCConvoyTrait nct = (NPCConvoyTrait)t;
				Faction npcFaction = nct.myFaction;
				if(npcFaction == null) {}
				else if(playersFaction != null && (
						npcFaction.equals(playersFaction) 
						|| npcFaction.isAlly(playersFaction.getName())
						|| npcFaction.isTradeAlly(playersFaction.getName()))
						){
					player.sendMessage(ChatColor.RED + "Нельзя наносить урон караванам вашего/союзного клана!");
					nde.setCancelled(true);
					return;
				}
				else {
					player.sendMessage(ChatColor.RED + "Вы атаковали караван фракции " + nct.getFaction().getName());
				}
				nct.alertAllGuards(player);
				return;
			}else if(t.getClass().equals(ConvoyFollowerTrait.class)) {
				ConvoyFollowerTrait cft = (ConvoyFollowerTrait)t;
				if(cft.getFaction() == null) {}
				else if(playersFaction != null && (
						cft.getFaction().equals(playersFaction) 
						|| cft.getFaction().isAlly(playersFaction.getName()) 
						|| cft.getFaction().isTradeAlly(playersFaction.getName()))
						) {
					player.sendMessage(ChatColor.RED + "Нельзя наносить урон караванам вашего/союзного клана!");
					nde.setCancelled(true);
					return;
				}
				else {
					player.sendMessage(ChatColor.RED + "Вы атаковали караван фракции " + cft.getFaction().getName());					
				}
				cft.defendYourself(player);
				return;
			}
		}
    }
}
