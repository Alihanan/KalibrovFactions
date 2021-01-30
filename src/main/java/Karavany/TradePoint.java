package Karavany;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.line.HologramLine;

import factionsystem.Main;
import net.md_5.bungee.api.ChatColor;


public class TradePoint {
	
	Location loc;
	long lastConvoySend;
	public ArrayList<TradePoint> neightbours = new ArrayList<>();
	public Hologram hologram;
	public String factionName = "";
	public Chest chest;
	
	public TradePoint() {
		
	}
	
	public TradePoint(Location l)
	{
		loc = l;
		lastConvoySend = System.currentTimeMillis();
	}
	
	public void setChest(Chest chest) {
		
		this.chest = chest;
	}
	public Chest getChest() {
		return chest;
	}
	
	public Location getLocation() {
		return loc.clone();
	}
	
	public long getLastConvoy() {
		return lastConvoySend;
	}
	
	public void setLastConvoy(long curr) {
		lastConvoySend = curr;
	}
	
	public void SetXYZ(double x, double y, double z) {
		loc.setX(x);
		loc.setY(y);
		loc.setZ(z);
	}	
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TradePoint)) {
			return false;
		}
		TradePoint tp = (TradePoint)obj;
		boolean isSamePoint = tp.getLocation().equals(this.loc);
		boolean isSameHolo = tp.hologram.equals(this.hologram);
		return isSamePoint && isSameHolo;
	}
	
	
	public Map<String, String> save(Main main) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();;
        Map<String, String> saveMap = new HashMap<>();
        String s = Long.toString(lastConvoySend);
        saveMap.put("lastConvoySend", s);
        saveMap.put("worldName", gson.toJson(loc.getWorld().getName()));
        saveMap.put("x", gson.toJson(loc.getX()));
        saveMap.put("y", gson.toJson(loc.getY()));
        saveMap.put("z", gson.toJson(loc.getZ()));       
        saveMap.put("factionName", gson.toJson(this.factionName));   
        
        // SaveChest
        if(chest != null) {
        	Location cloc = chest.getLocation();
            saveMap.put("chestWorld", gson.toJson(cloc.getWorld().getName()));
            saveMap.put("chestx", gson.toJson(cloc.getX()));
            saveMap.put("chesty", gson.toJson(cloc.getY()));
            saveMap.put("chestz", gson.toJson(cloc.getZ()));      
        }
         
        /*
        ArrayList<String> neightList = new ArrayList<String>(); 
        for (TradePoint tp : neightbours)
        {
        	String map = tp.getLocation().getChunk().toString();
        	neightList.add(map);
        }*/
        saveMap.put("firstHolo", hologram.getLine(0).getRaw());
        saveMap.put("secondHolo", hologram.getLine(1).getRaw());
        
        this.hologram.despawn();
    	main.hologram.getHologramManager().deleteHologram(this.hologram);
        // HOLOGRAM IS SAVED IN MAIN

        return saveMap;
	}
	
	public static TradePoint load(String input, Main main) {
		TradePoint tp = new TradePoint();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();;
		Type arrayListTypeString = new TypeToken<ArrayList<String>>(){}.getType();
		Type mapTypeString = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, String> tpInfo = gson.fromJson(input, mapTypeString);
		/*
		System.out.println(ChatColor.AQUA + " TPINFO:");
		System.out.println(input);*/
        try
        {        	
            tp.factionName = gson.fromJson(tpInfo.get("factionName"), String.class);
        	
        	double x = gson.fromJson(tpInfo.get("x"), Double.class);
        	double y = gson.fromJson(tpInfo.get("y"), Double.class);
        	double z = gson.fromJson(tpInfo.get("z"), Double.class);
        	String wname = gson.fromJson(tpInfo.get("worldName"), String.class);
        	World w = Bukkit.getWorld(wname);
        	
        	tp.loc = new Location(w, x, y, z);
        	if(main.allTradePosts.containsKey(tp.loc.getChunk())) {
        		tp = main.allTradePosts.get(tp.loc.getChunk());
        	}
        	else {
        		tp.lastConvoySend = gson.fromJson(tpInfo.get("lastConvoySend"), Long.class);
        		Location hLoc = tp.getLocation().clone();
                hLoc.setY(hLoc.getY() + 1.5);
                
                String first = tpInfo.get("firstHolo");
                String second = tpInfo.get("secondHolo");
                
                Hologram holo = new Hologram("tradepoint", hLoc, true);
                HologramLine line = main.hologram.parseLine(holo, first);
                HologramLine line2 = main.hologram.parseLine(holo, second);
                holo.addLine(line);		
                holo.addLine(line2);
                holo.spawn();
        		tp.hologram = holo;
        		try {
        			double cx = gson.fromJson(tpInfo.get("chestx"), Double.class);
                	double cy = gson.fromJson(tpInfo.get("chesty"), Double.class);
                	double cz = gson.fromJson(tpInfo.get("chestz"), Double.class);
                	String cwname = gson.fromJson(tpInfo.get("chestWorld"), String.class);
                	World cw = Bukkit.getWorld(cwname);
                	Location ccloc = new Location(cw, cx, cy, cz);
                	Block b = cw.getBlockAt(ccloc);
            		if(b.getType().equals(Material.CHEST)) {
            			Chest chest = (Chest)b.getState();
            			tp.chest = chest;
            			main.tradeChests.add(chest.getBlock());
            		}
        		} catch (Exception e) {
        			//System.out.println(ChatColor.AQUA + "Точка:" + first + " " + second + " не имеет сундука");
        		}
        		
        	}
        }
        catch (Exception e)
        {
        	System.out.println(ChatColor.RED + " #### ERROR: TRADEPOINT ERROR ######.\n");
        	e.printStackTrace();
        }
		return tp;
	}
	
}
