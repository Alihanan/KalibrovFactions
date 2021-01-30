package Karavany;
import static factionsystem.Subsystems.UtilitySubsystem.getPlayersFaction;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mojang.authlib.GameProfile;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.line.HologramLine;

import factionsystem.Main;
import factionsystem.Objects.Faction;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCTraitCommandAttachEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.util.Messaging;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.trait.waypoint.WaypointProvider;
import net.citizensnpcs.trait.waypoint.Waypoints;
import net.citizensnpcs.util.Messages;
import net.livecar.nuttyworks.npc_destinations.citizens.NPCDestinationsTrait;

public class ConvoySubsystem extends BukkitRunnable {

    private final Main plugin;
    private HashMap<String, NPC> activeConvoys = new HashMap<>();
    boolean bool = false;
    public static final double CARAVAN_MAX_DISTANCE = 200.0;
    public static final long CARAVAN_COOLDOWN = 400000; 
    //public static final String CARAVAN_ARAB = "https://www.minecraftskins.com/uploads/skins/2020/09/18/sleeping-trader-15307512.png?v302";

    public static final String CARAVAN_EUROPEAN = "";
    
    // 300000 = 5 min
    // 60000 = 1 min
    // 86400000 = 24 hours
    
    public ConvoySubsystem(Main plugin) {//
        this.plugin = plugin;
    }

    @Override
    public void run() {    	
    	
    	
    	
    	/*Location loc = null;
    	Player player = null;
    	for(Player p : Bukkit.getOnlinePlayers()){
    		loc = p.getLocation();
    		player = p;
    		break;
    	}
    	
   
    	if(loc == null) return;
    	if(bool) return;
    	bool = true;

    	//
    	NPC convoy = createConvoyNPC(player.getLocation(), followers);
    	NPCConvoyTrait nct = convoy.getOrAddTrait(NPCConvoyTrait.class);
    	*/
    	
    	/*
    	for(NPC s : activeConvoys.values()) {
    		if(!s.isSpawned()) {    	
    			NPCConvoyTrait nnct = s.getOrAddTrait(NPCConvoyTrait.class);
    			Location trg = nnct.getTarget();
    			if(trg == null) {
    				System.out.println(ChatColor.RED + "Ошибка! У каравана нет цели в жизни!(Как и у всех)");
    				continue;
    			}
    			s.spawn(trg);
    		}
    	}*/
    	
    	

    	for(Faction f : plugin.factions) {
    		TradePoint tp = f.getTradePoint();
    		if(tp == null) continue;
    		
    		// Check texts
    		if(tp.hologram == null) {
    			reloadTradePoint(tp, "Торговая точка", tp.factionName);
    		}
    		if(!tp.hologram.isSpawned()) {
    			tp.hologram.spawn();
    		}
    		for(TradePoint tps : f.getTradePosts()) {
    			if(tps.hologram == null) {
        			reloadTradePoint(tps, "Аванпост", tps.factionName);
        		}
        		if(!tps.hologram.isSpawned()) {
        			tps.hologram.spawn();
        		}
    		}
    		
    		
    		long curr = System.currentTimeMillis();
    		
    		long elapsed = curr - tp.getLastConvoy();
			long lastConvTime = CARAVAN_COOLDOWN - elapsed;//300000
    		if(lastConvTime < 0) {
    			plugin.utilities.sendAllPlayersOnServerMessage(
    					ChatColor.RED + f.getName() + " отправляет свой караван в путь!");
    			tp.setLastConvoy(curr);
    			
    			/// DELETE PREVIOUS CONVOY IF NOT IN TARGET
    			String name = f.getName().toLowerCase();
    			if(activeConvoys.containsKey(name)) {
    				NPC npc1 = activeConvoys.get(name);
    				NPCConvoyTrait nct = npc1.getTrait(NPCConvoyTrait.class);  				
    				if(nct != null && npc1.isSpawned()) {
    					plugin.utilities.sendAllPlayersOnServerMessage(
    							ChatColor.RED + "Караван фракции " + nct.myFaction.getName() + " пропал! По слухам их вещи брошены на земле!");
    					nct.dieAndDrop();
    				}	
    				else
    					npc1.destroy();
    			}

    			if(tp.getChest() == null || 
    					tp.getChest().getType() != Material.CHEST || 
    							tp.getChest().getBlock().getType() != Material.CHEST) {
    				plugin.utilities.sendAllPlayersOnServerMessage(
        					ChatColor.RED + f.getName() + " не юмеют сундука в Торг.точке! Удаляем всю торговую сеть!");
    				f.DeleteTradePoint();
    				return;
    			}
    			
            	
    			
            	
            	
    			//CREATE NPC CONVOY AND 
    			Location tl = tp.getLocation();		    			
    			ArrayList<Location> targetLoc = searchForLocation(f);
    			if(targetLoc == null || targetLoc.size() == 0 || targetLoc.size() < 2) {
    				plugin.utilities.sendAllPlayersOnServerMessage(
        					ChatColor.RED + "У вас нет торговых партнеров/невозможно найти дорогу! Караван отменяется!");
    				continue;
    			}  
    			
    			
    			//ConvoyFollowerTrait[] followers = {};
    	    	
    			/*
    			for(int i = 0; i < targetLoc.size()-1; i++) {
    				ChunkLoadingForce(targetLoc.get(i) ,targetLoc.get(i+1));
    			}*/
    			Location loc = tl.clone();
    			NPC fnpc = createFollowerNPC();
    	    	ConvoyFollowerTrait ndt = fnpc.getOrAddTrait(ConvoyFollowerTrait.class);	
    	    	ndt.setFaction(f);
    	    	
    	    	loc = loc.clone();
    	    	loc.setX(loc.getX()+5.0);
    	    	NPC fnpc2 = createFollowerNPC();
    	    	ConvoyFollowerTrait ndt2 = fnpc2.getOrAddTrait(ConvoyFollowerTrait.class);		
    	    	ndt2.setFaction(f);
    	    	    	    	
    	    	loc = loc.clone();
    	    	loc.setZ(loc.getZ()+5.0);    	    	
    	    	NPC fnpc3 = createFollowerNPC();
    	    	ConvoyFollowerTrait ndt3 = fnpc3.getOrAddTrait(ConvoyFollowerTrait.class);		
    	    	ndt3.setFaction(f);
    	    	
    	    	ConvoyFollowerTrait[] followers = {ndt, ndt2, ndt3};    			
    			
    			NPC npc = createConvoyNPC(f, tl, followers, (ArrayList<Location>)(targetLoc.clone()));
    			if(npc == null) {
    				continue;
    			}
    			
    			
    			activeConvoys.put(name, npc);
    			
    			Chest chest = tp.getChest();

    			
    			/*HashMap<Integer,ItemStack> remaining = new HashMap<Integer, ItemStack>();*/
    			
    			net.citizensnpcs.api.trait.trait.Inventory invent =	npc.getOrAddTrait(
        				net.citizensnpcs.api.trait.trait.Inventory.class);    			
    			ItemStack[] items = new ItemStack[invent.getContents().length];
    			ItemStack[] left = new ItemStack[chest.getInventory().getContents().length];
    			int i = 0;
    			int j = 0;
            	for(ItemStack is : chest.getBlockInventory().getContents()) {
            		if(is == null) continue;
            		if(i < items.length) {
            			items[i] = is;
            			i++;
            		}else {
            			left[j] = is;
            			j++;
            		}
            		 		
 
            	}
    			/*
    			 * HashMap<Integer,ItemStack> r = invent.getInventoryView().addItem(is);
    			 * remaining.putAll(r);
        		System.out.println(ChatColor.AQUA + "Type:" + is.getType() 
        		+ ", amount:" + is.getAmount());*/
        		List<ItemStack> intList = Arrays.asList(items);

            	Collections.shuffle(intList);
            	items = (ItemStack[]) intList.toArray();
            	invent.setContents(items);
            	System.out.println(ChatColor.AQUA + npc.getOrAddTrait(Inventory.class).getContents().toString());
            	chest.getInventory().clear();
            	for(ItemStack is : left) {
            		if(is != null) {
            			chest.getInventory().addItem(is);
            		}
            	}
            	/*
            	for(int i : remaining.keySet()) {
            		ItemStack is = remaining.get(i);            		
            		chest.getInventory().addItem(is);
            	}*/
    			
    			
    			//addLocation(nTimeOfDay, targetLoc, ndt, npc);
    		}
    	}
    }
    private void reloadTradePoint(TradePoint tp, String first, String second) {
    	Location hLoc = tp.getLocation();
		Hologram holo = new Hologram("tradepoint", hLoc, true);
        HologramLine line = plugin.hologram.parseLine(holo, first);
        HologramLine line2 = plugin.hologram.parseLine(holo, second);
        holo.addLine(line);		
        holo.addLine(line2);
        holo.spawn();
		tp.hologram = holo;
    }
    private NPC createConvoyNPC(Faction f, Location loc, ConvoyFollowerTrait[] followers, ArrayList<Location> targetLoc) {
    	NPC npc2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.RED 
    			+ "Караван [" + f.getName() + "]");
    	
    	
    	npc2.addTrait(new NPCConvoyTrait(plugin, f));  
		Bukkit.getPluginManager().callEvent(new NPCTraitCommandAttachEvent(npc2, NPCConvoyTrait.class, null));
		
		npc2.addTrait(new Inventory());  
		Bukkit.getPluginManager().callEvent(new NPCTraitCommandAttachEvent(npc2, Inventory.class, null));
		
		//npc2.addTrait(new NPCDestinationsTrait());  
		//Bukkit.getPluginManager().callEvent(new NPCTraitCommandAttachEvent(npc2, NPCDestinationsTrait.class, null));		
		
		Class<? extends Trait> npcDestClass = CitizensAPI.getTraitFactory().getTraitClass("NPCDestinations");
        if (npcDestClass == null) {
            // Failed to add the trait.. Odd
        	System.out.println(ChatColor.BLUE + "NPCDest ОШИБКА!");
            return null;
        } else if (!npc2.hasTrait(npcDestClass)) {
            // Add the trait, and signal other plugins we added the
            // trait incase they care.
            npc2.addTrait(npcDestClass);
            Bukkit.getPluginManager().callEvent(new NPCTraitCommandAttachEvent(npc2, npcDestClass, null));
        }
        // Setup the waypoint provider
        Waypoints waypoints = npc2.getTrait(Waypoints.class);
        boolean success = waypoints.setWaypointProvider("NPCDestinations");
        if(!success) {
        	System.out.println(ChatColor.BLUE + "Can't set waypoints!");
        }		
		
		NPCConvoyTrait nct = npc2.getOrAddTrait(NPCConvoyTrait.class);
		nct.setFaction(f);
		nct.setTarget(targetLoc);

		Location[] offsets = {
				new Location(loc.getWorld(), 1.0, 0.0, 1.0),
				new Location(loc.getWorld(), 1.0, 0.0, -1.0),
				new Location(loc.getWorld(), -1.0, 0.0, 1.0),
				new Location(loc.getWorld(), -1.0, 0.0, -1.0)
				};
		int i = 0;
		for(ConvoyFollowerTrait cft : followers) {
			cft.setOffset(offsets[i%4]);
			nct.AddFollower(cft);
			cft.setTargetNPC(npc2);			
			cft.getNPC().spawn(loc);
			i++;
		}		
    	   	
		boolean success2 = setNPCByCulture(nct, loc);
		if(!success2) {
			plugin.utilities.sendAllPlayersOnServerMessage(ChatColor.RED + "Торговая точка фракции " + f.getName() + " находилась в нежилом биоме. Они так и не нашли людей для каравана!");
			return null;
		}
		npc2.spawn(loc);
		
    	return npc2;   	
    }
    private NPC createFollowerNPC() {
    	NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, ChatColor.RED + "Охрана Каравана");
    	    	    	
		npc.addTrait(new ConvoyFollowerTrait());  
		Bukkit.getPluginManager().callEvent(new NPCTraitCommandAttachEvent(npc, ConvoyFollowerTrait.class, null));  
		 						
		npc.addTrait(new Inventory());  
		Bukkit.getPluginManager().callEvent(new NPCTraitCommandAttachEvent(npc, Inventory.class, null));  	
    	// Set armor
    	/*
    	Equipment equipment = npc.getOrAddTrait(Equipment.class);
    	equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(org.bukkit.Material.IRON_HELMET));
    	equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(org.bukkit.Material.IRON_CHESTPLATE));
    	equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(org.bukkit.Material.IRON_LEGGINGS));
    	equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(org.bukkit.Material.IRON_BOOTS));*/
    	
    	return npc;
    }
    
    
    
    
	private ArrayList<Location> searchForLocation(Faction f) {
    	System.out.println(ChatColor.BLUE + "Searching target for faction: " + f.getName());
    	ArrayList<Location> path = new ArrayList<Location>();
    	ArrayList<String> al = (ArrayList<String>) f.getTradeAllies().clone();
    	Collections.shuffle(al);
    	for(String s : al) {
    		Faction fa = plugin.utilities.getFaction(s, plugin.factions);
    		if(fa == null) {
    			continue;
    		}
    		TradePoint tp = fa.getTradePoint();
    		if(tp != null) {
    			ArrayList<TradePoint> pathTP = Algorithm.findPath(f, fa);
    			
    			for(TradePoint pTP : pathTP) {
    				path.add(pTP.getLocation());
    			}
    			return path;
    		}
    	}
    	return null;  	
    }
    
    /*
    private void addLocation(int nTimeOfDay, Location targetLoc, NPCDestinationsTrait trait, NPC npc) {
    	Destination_Setting oLoc = new Destination_Setting();
        oLoc.destination = targetLoc;
        oLoc.TimeOfDay = nTimeOfDay + 20;
        oLoc.Probability = 100;
        oLoc.Wait_Maximum = 0;
        oLoc.Wait_Minimum = 0;
        oLoc.setWanderingDistance(0);
        oLoc.Time_Minimum = 0;
        oLoc.Time_Maximum = 1000;
        oLoc.Alias_Name = "";
        oLoc.setMaxDistance(1000);

        oLoc.LocationIdent = UUID.randomUUID();
        oLoc.arrival_Commands = new ArrayList<String>();

        oLoc.player_Skin_Name = "";
        oLoc.player_Skin_UUID = "";
        oLoc.player_Skin_ApplyOnArrival = false;
        oLoc.player_Skin_Texture_Metadata = "";
        oLoc.player_Skin_Texture_Signature = "";

        oLoc.Pause_Distance = -1;
        oLoc.Pause_Timeout = -1;
        oLoc.Pause_Type = "ALL";

        if (trait.NPCLocations == null) {
            trait.NPCLocations = new ArrayList<Destination_Setting>();
        }
        
        // V1.39 -- Event
        final Location_Added newLocation = new Location_Added(npc, oLoc);
        Bukkit.getServer().getPluginManager().callEvent(newLocation);
        if (newLocation.isCancelled()) {
            return;
        }
        trait.NPCLocations.add(oLoc);
        if(trait.AllowedPathBlocks == null) {
        	trait.AllowedPathBlocks = new ArrayList<Material>();
        }
        trait.AllowedPathBlocks.add(Material.GRASS_PATH);
        
    }
    */
    public boolean setNPCByCulture(NPCConvoyTrait npcTrait, Location tradeP) {
    	Culture c = plugin.scp.getTypeByBiome(tradeP.getBlock().getBiome());
    	if(c == null || c.caravanTexture == null || c.guardTexture == null) {
    		npcTrait.dieAndDrop();    		
    		return false;
    	}
    	
    	setNPCSkinFromRawData(npcTrait.getNPC(), c.caravanTexture);
    	npcTrait.getNPC().getNavigator().getLocalParameters().baseSpeed(c.caravanSpeedModifier);
    	HumanEntity he = (HumanEntity) npcTrait.getNPC().getEntity();
    	if(he != null) {
    		he.setMaxHealth(c.caravanHP);
    		he.setHealth(c.caravanHP);
    	}
    	
    	for(ConvoyFollowerTrait cft : npcTrait.getFollowers()) {
    		setNPCSkinFromRawData(cft.getNPC(), c.guardTexture);
        	HumanEntity he2 = (HumanEntity) cft.getNPC().getEntity();
        	if(he2 != null) {
        		he2.setMaxHealth(c.guardHP);
        		he2.setHealth(c.guardHP);        		
        	}
        	// Set inventory
        	Inventory inv = cft.getNPC().getOrAddTrait(Inventory.class);
        	if(inv != null && c.sword != null) {
        		int l = inv.getContents().length;   	
            	ItemStack[] newC = new ItemStack[l];
            	newC[0] =  new ItemStack(c.sword);
            	inv.setContents(newC);
        	}       	
    	}
    	
    	return true;
    }
	public void setNPCSkinFromRawData(NPC npc, TextureStructure ts) {
        SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
        //trait.clearTexture();
        //trait.setTexture(SkinChangePacketIntercepter.CARAVAN_ARAB_DATA, 
				//SkinChangePacketIntercepter.CARAVAN_ARAB_SIGN);
        if(ts == null) {
        	plugin.utilities.sendAllPlayersOnServerMessage(ChatColor.RED + "Ошибка текстур! Проверьте файлы!");
        	return;
        }
        String uuid = ts.uuid;
        String data = ts.data;
        String sign = ts.signature;

        trait.setSkinPersistent(uuid, sign, data);
        
        return;
	}
	
    public void ChunkLoadingForce(Location start, Location end) {
    	int x_start, x_end, x_move;
    	int z_start, z_end, z_move;
    	World world;
		if(!start.getWorld().equals(end.getWorld())) {
			System.err.println("GOT TWO POINTS FROM DIFFERENT WORLDS");
		}
		world = start.getWorld();
		x_start = (int)start.getX();
		z_start = (int)start.getZ();
		
		x_end = (int)end.getX();
		z_end = (int)end.getZ();
		
		x_move = bi(x_start > x_end) * -2 + 1;
		z_move = bi(z_start > z_end) * -2 + 1;
		for(int x = x_start; x <= x_end; x+=x_move) {
			for(int z = z_start; z <= z_end; z+=z_move) {
				Chunk c = world.getChunkAt(x, z);
				if(!c.isLoaded())
					c.load();
			}
		}
	}
	
	public int bi(boolean b) {
	    return b ? 1 : 0;
	}

}
