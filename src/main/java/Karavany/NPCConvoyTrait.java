package Karavany;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import factionsystem.*;
import factionsystem.Objects.Faction;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.livecar.nuttyworks.npc_destinations.api.Destination_Setting;
import net.livecar.nuttyworks.npc_destinations.api.Location_Added;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait;

public class NPCConvoyTrait extends Trait {
	
	ArrayList<Location> target;
	ArrayList<Location> path;
	int i = 0;
	final int TICKRATE_CHECK = 20;
	ArrayList<ConvoyFollowerTrait> followers = new ArrayList<>();
	Main plugin;
	Faction myFaction;
	NPCDestinationsTrait nct;
	
	public NPCConvoyTrait() {
		super("NPCConvoyTrait");
	}
	public NPCConvoyTrait(Main main, Faction f) {
		super("NPCConvoyTrait");
		plugin = main;
		myFaction = f;
	}
	public NPCConvoyTrait(Main main, ArrayList<Location> loc, Faction f) {
        this();
        target = loc;  
        plugin = main;
        myFaction = f;
        setNavigator();
    }
	public void setFaction(Faction f) {
		myFaction = f;
	}
	public Faction getFaction() {
		return myFaction;
	}
	
	@Override
	public void run(){
		
		if(!super.npc.isSpawned()) {
			return;
		}
		
		i++;
		Chunk c = npc.getStoredLocation().getChunk();
		if(!c.isLoaded()) {
			c.load();
		}	
		if(nct == null) {
			nct = npc.getTrait(NPCDestinationsTrait.class);
			if(nct == null) {
				System.out.println("I DONT HAVE A DESTINATIONS TRAIT");
				return;
			}
		}
		if(target == null) {
			return;
		}
		
		if(i%TICKRATE_CHECK != 0) {
			return;
		}
		/*
		int j = 0;
		for(Destination_Setting ds : nct.NPCLocations) {
			System.out.println(ChatColor.BLUE + "NPC:" + npc.getName() + ", " + j + ", " + ds.destination.toString());
			j++;
		}*/
		i = 0;
		if(target.size() == 0) {
			goalReached();
			return;
		}
		
		double dist_to_curr_target = Norm2Distance(npc.getStoredLocation(), target.get(0));
		if(dist_to_curr_target < 2.5) {
			Location last = target.get(0);
			target.remove(last);			
			if(target.size() == 0) {
				goalReached();
				return;
			}
			setTargetForNCT(target.get(0));
		}
		/*
		if(!npc.getNavigator().isNavigating()){
			if(target.size() == 0) {
				goalReached();
				return;
			}
			setTargetForNCT(target.get(0));
			return;
		}*/
		
    	if(target.size() != 0 && nct.NPCLocations.size() == 0) {
    		//npc.getNavigator().setTarget(target.get(0));
    		setTargetForNCT(target.get(0));
    	}
	}
	
	private void goalReached() {
		plugin.utilities.sendAllPlayersOnServerMessage(
				ChatColor.YELLOW + "Караван фракции " + myFaction.getName() + " успешно добрался до цели!");
		
		Inventory inv = npc.getTrait(Inventory.class);		
		
		if(inv != null) {
			Chunk lastTarget = path.get(path.size() - 1).getChunk();
			TradePoint lastPoint = plugin.allTradePosts.get(lastTarget);
			if(lastPoint != null) {
				Chest chest = lastPoint.chest;
				if(chest != null) {
					HashMap<Integer,ItemStack> remaining = new HashMap<Integer, ItemStack>();
					for(ItemStack is : inv.getContents()) {
						if(is != null) {
							HashMap<Integer,ItemStack> r = chest.getInventory().addItem(is);
							remaining.putAll(r);
						}
					}
					for(int i : remaining.keySet()) {
	            		ItemStack is = remaining.get(i);            		
	            		npc.getStoredLocation().getWorld().dropItemNaturally(npc.getStoredLocation(), is);
	            	}
				}else {
					for(ItemStack is : inv.getContents()) {
						if(is != null)
							npc.getStoredLocation().getWorld().dropItemNaturally(npc.getStoredLocation(), is);
					}
				}
			}
		}
		
		
		for(ConvoyFollowerTrait cft : followers) {
			cft.getNPC().destroy();
		}
		
		
		npc.destroy();
	}
	
	public static Location multiply(Location l, double scalar) {
		Location c = l.clone();
		c.setX(l.getX() * scalar);
		c.setY(l.getY() * scalar);
		c.setZ(l.getZ() * scalar);
		return c;
	}
	public static Location divide(Location l, double scalar) {
		return multiply(l, 1/scalar);
	}
	
	public static Location plus(Location a, Location b) {
		Location c = a.clone();
		c.setX(a.getX() + b.getX());
		c.setY(a.getY() + b.getY());
		c.setZ(a.getZ() + b.getZ());
		return c;
	}
	public static Location minus(Location a, Location b) {
		Location c = a.clone();
		c.setX(a.getX() - b.getX());
		c.setY(a.getY() - b.getY());
		c.setZ(a.getZ() - b.getZ());
		return c;
	}
	
	
	public static Location normalize(Location loc) {
		Location nloc = loc.clone();
		double n = Norm2(nloc);
		nloc.setX(nloc.getX()/n);
		nloc.setY(nloc.getY()/n);
		nloc.setZ(nloc.getZ()/n);
		return nloc;
	}
	public static double Norm2(Location start) {
		double f1 = start.getX();
		f1 = f1 * f1;
		double f2 = start.getY();
		f2 = f2 * f2;
		double f3 = start.getZ();
		f3 = f3 * f3;
		
		return Math.sqrt(f1 + f2 + f3);
	}
	
	public static double Norm2Distance(Location start, Location end) {
		double f1 = start.getX() - end.getX();
		f1 = f1 * f1;
		double f2 = start.getY() - end.getY();
		f2 = f2 * f2;
		double f3 = start.getZ() - end.getZ();
		f3 = f3 * f3;
		
		return Math.sqrt(f1 + f2 + f3);
	}
	
	public void setTarget(ArrayList<Location> loc) {
		target = (ArrayList<Location>) loc.clone();
		path = (ArrayList<Location>) loc.clone();
		
		setNavigator();
	}
	public Location getTarget() {
		return target.get(0).clone();
	}
	
	@Override
	public void onSpawn(){
		super.onSpawn();
		
		npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, false);
		npc.data().setPersistent(NPC.TARGETABLE_METADATA, false);
		npc.data().setPersistent(NPC.COLLIDABLE_METADATA, true);
	}
		
	public void AddFollower(ConvoyFollowerTrait cft) {
		followers.add(cft);
	}
	public void RemoveFollower(ConvoyFollowerTrait cft) {
		followers.remove(cft);
	}
	
	public void alertAllGuards(Player attacker) {
		for(ConvoyFollowerTrait cft : followers) {
			cft.defendYourself(attacker);
		}
	}
	private void setTargetForNCT(Location trgt) {
		if(nct == null) {
			nct = npc.getTrait(NPCDestinationsTrait.class);
			if(nct == null) {
				System.out.println(ChatColor.YELLOW + "I DONT HAVE A DESTINATIONS TRAIT");
				return;
			}
		}
		Destination_Setting oLoc = new Destination_Setting();
        oLoc.destination = trgt;
        oLoc.TimeOfDay = (int) (trgt.getWorld().getTime() + 20);
        oLoc.Probability = 100;
        //oLoc.Wait_Maximum = 0;
        //oLoc.Wait_Minimum = 0;
        //oLoc.setWanderingDistance(0);
        //oLoc.Time_Minimum = 0;
        oLoc.Time_Maximum = -1;
        oLoc.Alias_Name = "";
        
        
        oLoc.setMaxDistance(nct.MaxDistFromDestination * 2);//ConvoySubsystem.CARAVAN_MAX_DISTANCE * 5.0

        oLoc.LocationIdent = UUID.randomUUID();
        oLoc.arrival_Commands = new ArrayList<String>();

        oLoc.player_Skin_Name = "";
        oLoc.player_Skin_UUID = "";
        oLoc.player_Skin_ApplyOnArrival = false;
        oLoc.player_Skin_Texture_Metadata = "";
        oLoc.player_Skin_Texture_Signature = "";

        oLoc.Pause_Distance = 1;
        oLoc.Pause_Timeout = 1;
        oLoc.Pause_Type = "ALL";

        if (nct.NPCLocations == null) {
        	nct.NPCLocations = new ArrayList<Destination_Setting>();
        }
        nct.NPCLocations.clear();
        // V1.39 -- Event
        final Location_Added newLocation = new Location_Added(npc, oLoc);
        Bukkit.getServer().getPluginManager().callEvent(newLocation);
        if (newLocation.isCancelled()) {
            return;
        }
        
        nct.NPCLocations.add(oLoc);	
        nct.TeleportOnNoPath = false;
        nct.citizens_Swim = true;
        nct.TeleportOnFailedStartLoc = false;
        nct.OpensWoodDoors = true;
        nct.OpensGates = true;
        
        
		//nct.setCurrentLocation(oLoc);
        System.out.println(ChatColor.BLUE + "Setting target for NPCDest");
	}
	
	public void killKiller(Player player) {
		for(ConvoyFollowerTrait cft : followers) {
			cft.setDeathEnemy(player);
		}
	}
	
	public void dieAndDrop() {				
		Inventory inv = npc.getTrait(Inventory.class);

		if(inv != null) {
			for(ItemStack is : inv.getContents()) {
				if(is != null)
					npc.getStoredLocation().getWorld().dropItemNaturally(npc.getStoredLocation(), is);
			}
		}		
		
		for(ConvoyFollowerTrait cft : followers) {
			cft.getNPC().destroy();
		}				
		npc.destroy();
	}
	
	public ArrayList<ConvoyFollowerTrait> getFollowers(){
		return followers;
	}
	
	public void setNavigator() {
		if(nct == null) {
			nct = npc.getTrait(NPCDestinationsTrait.class);
			if(nct == null) {
				System.out.println("I DONT HAVE A DESTINATIONS TRAIT");
				return;
			}
		}
		
		setTargetForNCT(target.get(0));
		//nct.MaxDistFromDestination = (int)(ConvoySubsystem.CARAVAN_MAX_DISTANCE * 5.0f);
		//nct.maxProcessingTime = 6000;
		//nct.TeleportOnNoPath = false;
		
		//nct.
		
		//npc.getNavigator().getLocalParameters().useNewPathfinder(true);
		//float f = (float) ();
		//npc.getNavigator().getLocalParameters().range(f);
		//npc.getNavigator().getDefaultParameters().baseSpeed(0.7f);
		//npc.getNavigator().getLocalParameters().pathDistanceMargin(250.0f);
		//npc.getNavigator().getLocalParameters().distanceMargin(1.0);
		//npc.getNavigator().getLocalParameters().
	}
}
