package Professions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;

import factionsystem.Main;

public class ProfessionProtocolManager {
	Main main;
	static boolean isStarted = false;
	static DebufThread dt;
	
	public ProfessionProtocolManager(Main plugin) {
		this.main = plugin;
		if(!isStarted) {
			isStarted = true;
			dt = new DebufThread(plugin);	
			dt.runTaskTimer(plugin, 0, 10);
		}
	}
	
	public void handlePacket(PacketEvent event) {		
		PacketContainer packet = event.getPacket();		
		
		EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().getValues().get(0);
		BlockPosition bp = packet.getBlockPositionModifier().getValues().get(0);		
		
		String s = digType.name();
		Player p = event.getPlayer();
		if(!p.getGameMode().equals(GameMode.SURVIVAL)) {
			return;
		}
		Block b = p.getWorld().getBlockAt(new Location(p.getWorld(), bp.getX(), bp.getY(), bp.getZ()));
		
		
		switch(s) {
			case "START_DESTROY_BLOCK":
				if(isPunished(b, p)) {
					dt.addDamaging(p.getUniqueId());
					System.out.println("Dig started");
					System.out.println("Material 0: " + b.getType());
				}	
				else if(isDebuffed(b, p)) {
					dt.addWorking(p.getUniqueId());
					System.out.println("Dig started");
					System.out.println("Material 0: " + b.getType());
				}
				break;
			case "ABORT_DESTROY_BLOCK":
			case "STOP_DESTROY_BLOCK":
				dt.removeDamaging(p.getUniqueId());
				dt.removeWorking(p.getUniqueId());
				System.out.println("Dig stopped");
				System.out.println("Material 0: " + b.getType());
				break;
			default:	
				System.out.println("Unknown packet![" + s + "]");
		}
		// if by hands - damaging;
		
		// if by pickaxe working
	}	
	
	 private boolean isDebuffed(Block b, Player p) {
    	if(isStone(b.getType()) && 
    			main.professions.get(p.getUniqueId()).getProfession() == PlayerProfession.TYPE.MINER) {
    		return true;
    	}
    	if(isWood(b.getType()) && 
    			main.professions.get(p.getUniqueId()).getProfession() == PlayerProfession.TYPE.TIMBER) {
    		return true;
    	}  
    	return false;
    }
    private boolean isPunished(Block b, Player p) {
    	if(isStone(b.getType())){ // If stone
    		if(main.professions.get(p.getUniqueId()).getProfession() != PlayerProfession.TYPE.MINER) {
    			return true; // Non-miners are always dump
    		}
    		if(p.getInventory().getItemInMainHand() == null || 
    	    			p.getInventory().getItemInMainHand().getType() == null) {
    			// So are the miners with hands
    			return true;
    		}
    	}
    	if(isWood(b.getType())) {//If wood
    		if(main.professions.get(p.getUniqueId()).getProfession() != PlayerProfession.TYPE.TIMBER) {
    			// Non-timbers are always dump
    			return true;
    		}
        	if(p.getInventory().getItemInMainHand() == null || 
        			p.getInventory().getItemInMainHand().getType() == null) {
        		// So are the timbers with hands
				return true;
			}
    	}
    	return false;
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
    private boolean isWood(Material mainHand) {
    	final Material[] pickaxes = {
    			Material.LOG,
    			Material.LOG_2,
    			Material.WOOD,
    			Material.WOOD_DOOR,
    			Material.WOOD_DOUBLE_STEP,
    			Material.WOOD_PLATE,
    			Material.WOOD_STAIRS,
    			Material.WOOD_STEP,
    			//Material.WOOD_BUTTON,
    			Material.SPRUCE_DOOR,
    			Material.SPRUCE_DOOR_ITEM,
    			Material.SPRUCE_FENCE,
    			Material.SPRUCE_FENCE_GATE,
    			Material.SPRUCE_WOOD_STAIRS,
    			Material.JUNGLE_WOOD_STAIRS,
    			Material.JUNGLE_DOOR,
    			Material.JUNGLE_FENCE,
    			Material.JUNGLE_FENCE_GATE,
    			Material.JUNGLE_WOOD_STAIRS,
    			Material.ACACIA_DOOR,
    			Material.ACACIA_FENCE,
    			Material.ACACIA_FENCE_GATE,
    			Material.ACACIA_DOOR_ITEM,
    			Material.ACACIA_STAIRS,
    			Material.DARK_OAK_DOOR,
    			Material.DARK_OAK_DOOR_ITEM,
    			Material.DARK_OAK_FENCE,
    			Material.DARK_OAK_FENCE_GATE,
    			Material.DARK_OAK_STAIRS,
    			Material.BIRCH_DOOR,
    			Material.BIRCH_DOOR_ITEM,
    			Material.BIRCH_FENCE,
    			Material.BIRCH_FENCE_GATE,
    			Material.BIRCH_WOOD_STAIRS,
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
    
    
    private class DebufThread extends BukkitRunnable{

    	private ArrayList<UUID> damaging = new ArrayList<>();
    	private ArrayList<UUID> working = new ArrayList<>();
    	private Main main;
    	
    	public DebufThread(Main plugin) {
    		main = plugin;
    	}
    	public void addDamaging(UUID playerUUID) {
    		damaging.add(playerUUID);
    	}
    	public void addWorking(UUID playerUUID) {
    		working.add(playerUUID);
    	}
    	public void removeDamaging(UUID playerUUID) {
    		damaging.remove(playerUUID);
    	}
    	public void removeWorking(UUID playerUUID) {
    		working.remove(playerUUID);
    	}
		@Override
		public void run() {
			for(UUID uuid : damaging) {
				
				Player p = Bukkit.getPlayer(uuid);
				
				if(p == null) {
					damaging.remove(uuid);
					return;
				}
				if(!p.isOnline()) {
					damaging.remove(uuid);
					return;
				}
				p.damage(0.5);
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 100), true);
			}
			for(UUID uuid : working) {
				Player p = Bukkit.getPlayer(uuid);
				if(p == null) {
					working.remove(uuid);
					return;
				}
				if(!p.isOnline()) {
					working.remove(uuid);
					return;
				}				
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 20), true);
			}			
		}
    	
    }
}
