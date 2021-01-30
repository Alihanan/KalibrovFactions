package Karavany;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import PacketWrappers.WrapperPlayServerRelEntityMove;

import factionsystem.Main;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerConnection;

public class CaravanCultureManager {
	Main main;
	public static ArrayList<Culture> allCultures = new ArrayList<Culture>();	
	private final String CONFIGFILE = "./plugins/MedievalFactions/textures.json";
	/*
   //16 = 14 + 2
	ArrayList<Biome> euroBiomes = new ArrayList<Biome>(Arrays.asList(new Biome[]{
			Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_HILLS, 
			Biome.MUTATED_BIRCH_FOREST, Biome.MUTATED_BIRCH_FOREST_HILLS,
			Biome.FOREST, Biome.FOREST_HILLS, Biome.PLAINS,
			Biome.EXTREME_HILLS,
			Biome.EXTREME_HILLS_WITH_TREES, Biome.MUTATED_EXTREME_HILLS,
			Biome.MUTATED_EXTREME_HILLS_WITH_TREES, Biome.SMALLER_EXTREME_HILLS,
			Biome.MUTATED_ROOFED_FOREST, Biome.ROOFED_FOREST,
			
			Biome.BEACHES, Biome.STONE_BEACH
	})) ;
	//20 = 15 + 5
	ArrayList<Biome> arabBiomes = new ArrayList<Biome>(Arrays.asList(new Biome[]{
			Biome.DESERT, Biome.DESERT_HILLS, Biome.MUTATED_DESERT,
			Biome.SAVANNA, Biome.SAVANNA_ROCK, Biome.MUTATED_SAVANNA,
			Biome.MUTATED_SAVANNA_ROCK, Biome.MESA,
			Biome.MESA_CLEAR_ROCK, Biome.MESA_ROCK, Biome.MUTATED_MESA,
			Biome.MUTATED_MESA_CLEAR_ROCK, Biome.MUTATED_MESA_ROCK,
			Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_ISLAND_SHORE,
			
			Biome.JUNGLE, Biome.JUNGLE_EDGE, Biome.JUNGLE_HILLS,
			Biome.MUTATED_JUNGLE, Biome.MUTATED_JUNGLE_EDGE
	})) ;
	//14
	ArrayList<Biome> russianBiomes = new ArrayList<Biome>(Arrays.asList(new Biome[]{
			Biome.TAIGA, Biome.TAIGA_COLD, Biome.TAIGA_COLD_HILLS, 
			Biome.TAIGA_HILLS, Biome.MUTATED_TAIGA, Biome.MUTATED_TAIGA_COLD,
			Biome.MUTATED_REDWOOD_TAIGA, Biome.REDWOOD_TAIGA_HILLS,
			Biome.COLD_BEACH, Biome.ICE_FLATS, Biome.ICE_MOUNTAINS, 
			Biome.MUTATED_ICE_FLATS, Biome.SWAMPLAND, Biome.MUTATED_ICE_FLATS,
			
	}));*/
	
	public CaravanCultureManager(Main plugin) {
		this.main = plugin;		
	}
		
	public static Culture getTypeByBiome(Biome biome) {
		for(Culture c : allCultures) {
			if(c.spawnBiomes.contains(biome)) {
				return c;
			}
		}
		return null;
	}
	
	
	public void save() {
		File f = new File(CONFIGFILE);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		ArrayList<Map<String, String>> saveFile = new ArrayList<Map<String,String>>();
		
		for(Culture c : allCultures) {
			saveFile.add(c.save());
		}
		try {
            f.createNewFile();
            FileWriter saveWriter = new FileWriter(f);
            saveWriter.write(gson.toJson(saveFile));
            saveWriter.close();
        } catch(IOException e) {
        	System.out.println(ChatColor.RED + "[KARAVANY] Can't create a file for Textures!");
        }
        System.out.println(ChatColor.AQUA + "[KARAVANY] cultures saved!");  
	}
	
	public void load() {
		File f = new File(CONFIGFILE);
		if(!f.exists()) {
			try {
				f.createNewFile();
				
				// If no textures -> copy default ones
				boolean isBackup = copyBackupFromResouces(f);
				if(!isBackup) return;
				System.out.println(ChatColor.AQUA + "[KARAVANY] Default cultures loading...");
			} catch (IOException e) {
				System.out.println(ChatColor.RED + "[KARAVANY] Can't create a file for Textures!");
				return;
			}
		}
		ArrayList<Map<String, String>> data = new ArrayList<Map<String,String>>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try{
            
            JsonReader reader = new JsonReader(new FileReader(CONFIGFILE));
            Type LIST_MAP_TYPE = new TypeToken<ArrayList<Map<String, String>>>(){}.getType();
            data = gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException e) {
            // Fail silently because this can actually happen in normal use
        	return;
        }		
		
		for(Map<String, String> cul : data) {
			try {
				allCultures.add(Culture.load(cul));
			}catch(Exception e) {
				
			}
			
		}
		System.out.println(ChatColor.AQUA + "[KARAVANY] Cultures loaded!");  
		
	}
	
	private boolean copyBackupFromResouces(File f) {
		InputStream in = getClass().getResourceAsStream("/textures.json"); 
		//BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		//URL url = CaravanCultureManager.class.getClassLoader().getResource("textures.json");
		if(in == null) {
			System.out.println(ChatColor.RED + "[KARAVANY] Can't find a backup in inner part!");
			return false;
		}
		
		InputStream is = in;
	    OutputStream os = null;
	    try {
	        os = new FileOutputStream(f);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	        	//System.out.println(ChatColor.BLUE +  new String(buffer, StandardCharsets.UTF_8));
	            os.write(buffer, 0, length);
	        }
	        is.close();
	        os.close();
	    }catch(FileNotFoundException e) {
	    	System.out.println(ChatColor.RED + "[KARAVANY] Error during backuping!");
	    	return false;
	    }catch(IOException e) {
	    	System.out.println(ChatColor.RED + "[KARAVANY] Error during backuping!");
	    	return false;
	    }
	    return true;
	}
}
class TextureStructure
{
	public String uuid;
	public String data;
	public String signature;
	
	public TextureStructure(String data, String sign, String uuid) {
		this.data = data;
		this.signature = sign;
		this.uuid = uuid;
	}
	
	public String save() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, String> texture = new HashMap<String, String>();
		texture.put("uuid", uuid);
		texture.put("data", data);
		texture.put("sign", signature);
		return gson.toJson(texture);
	}
	
	public static TextureStructure load(String json) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Type LIST_MAP_TYPE = new TypeToken<Map<String, String>>(){}.getType();
		Map<String, String> texture = gson.fromJson(json, LIST_MAP_TYPE);
		
		
		String uuid = texture.get("uuid");
		String data = texture.get("data");
		String sign = texture.get("sign");

		return new TextureStructure(data, sign, uuid);
	}
}
class Culture{
	public TextureStructure caravanTexture;
	public TextureStructure guardTexture;
	public ArrayList<Biome> spawnBiomes = new ArrayList<Biome>();
	public String name;
	public Material sword;
	public float caravanSpeedModifier = 1.0f;
	public double caravanHP = 60.0f;
	public double guardHP = 30.0f;
	
	public Culture() {
		
	}
	
	public Map<String, String> save() {
		Map<String, String> mc = new HashMap<String, String>();
		mc.put("name", name);
		mc.put("sword", sword.toString());
		mc.put("caravanSpeedModifier", Float.toString(caravanSpeedModifier));
		mc.put("caravanHP", Double.toString(caravanHP));
		mc.put("guardHP", Double.toString(guardHP));
		
		mc.put("caravanTexture", caravanTexture.save());
		mc.put("guardTexture", guardTexture.save());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		ArrayList<String> bStr = new ArrayList<String>();
		for(Biome bi : spawnBiomes) {
			bStr.add(bi.toString());
		}
		
		mc.put("biomes", gson.toJson(bStr));
		return mc;
	}
	public static Culture load(Map<String, String> mc) {
		Culture c = new Culture();
		c.name = mc.get("name");
		
		String sword = mc.get("sword");		
		c.sword = Material.valueOf(sword);
		
		String csm = mc.get("caravanSpeedModifier");
		c.caravanSpeedModifier = Float.parseFloat(csm);
		
		String cHP = mc.get("caravanHP");
		c.caravanHP = Double.parseDouble(cHP);
		String gHP = mc.get("guardHP");
		c.guardHP = Double.parseDouble(gHP);
		
		String cTex = mc.get("caravanTexture");
		c.caravanTexture = TextureStructure.load(cTex);
		String gTex = mc.get("guardTexture");
		c.guardTexture = TextureStructure.load(gTex);
		
		String biomStr = mc.get("biomes");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Type LIST_MAP_TYPE = new TypeToken<ArrayList<String>>(){}.getType();
		ArrayList<String> bStr = gson.fromJson(biomStr, LIST_MAP_TYPE);
		c.spawnBiomes = new ArrayList<Biome>();	
		for(String b : bStr) {
			c.spawnBiomes.add(Biome.valueOf(b));
		}

		return c;
	}
	
	
}
