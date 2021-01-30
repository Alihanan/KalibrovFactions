package Professions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import factionsystem.Objects.Faction;

public class PlayerProfession {
	public enum TYPE {
		NONE(0, "ЛОХ", ChatColor.BLACK, "LOH"), // In process of choosing/training
		MINER(1, "Шaхтер", ChatColor.DARK_GRAY, "Miner"), // Grind stone, 
		TIMBER(2, "Лесник", ChatColor.DARK_GREEN, "Timber"), // Grind wood
		BLACKSMITH(3, "Кузнец", ChatColor.DARK_RED, "Blacksmith"), // Craft weapon, melt ingots, 
		FARMER(4, "Крестьянин", ChatColor.GREEN, "Farmer"), // Make some food (animal + corn)
		HUNTER(5, "Охотник", ChatColor.RED, "Hunter"), // Get some food from hunting/fishing + Bow/Crossbow
		ENGINEER(6, "Инженер", ChatColor.YELLOW, "Engineer"), // Craft a lot of shit
		WIZARD(7, "Волшебник", ChatColor.AQUA, "Wizard"), // Enchant + potions (+ collect flowers, mushroom)
		HORSEMAN(8, "Наездник", ChatColor.WHITE, "Horseman"); // Enchant + potions (+ collect flowers, mushroom)
		
		private final int value;
		private final String name;
		private final ChatColor color;
		private final String encoding;
	    private TYPE(int value, String name, ChatColor color, String encoding) {
	        this.value = value;
	        this.name = name;
	        this.color = color;
	        this.encoding = encoding;
	    }
		public int getValue() {
	        return value;
	    }
		public String getName() {
			return name;
		}
		public ChatColor getColor() {
			return color;
		}
		public static int getByName(String n) {
			for(int i = 0; i < values().length; i++) {
				if(n.equals(values()[i].encoding)) {
					return values()[i].value;
				}
			}
			return -1;
		}
	}
	final static String CONFIG_SAVEFILE = "./plugins/MedievalFactions/professions.json";
	final static int PROFESSION_NUMBER = TYPE.values().length-1;
	final static int[] PROFESSION_THRESHOLD = new int[PROFESSION_NUMBER];	
	
	private TYPE playerType = TYPE.NONE;
	private Player player;
	private int[] profession_progress = new int[PROFESSION_NUMBER];		
	
	public static HashMap<UUID, PlayerProfession> load() {							
		File f = new File(CONFIG_SAVEFILE);
		ArrayList<HashMap<String, String>> data = null;
		HashMap<UUID, PlayerProfession> professes = new HashMap<>();
		if(!f.exists()) {
			for(int i = 0; i < PROFESSION_THRESHOLD.length; i++) {
				PROFESSION_THRESHOLD[i] = 100;
			}	
			return professes;
		}
		try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();;
            JsonReader reader = new JsonReader(new FileReader(CONFIG_SAVEFILE));
            Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();
            data = gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException e) {
            // Fail silently because this can actually happen in normal use
        	return professes;
        }		
		System.out.println(ChatColor.AQUA + "[KARAVANY] loaded file professions.");
		for (Map<String, String> factionData : data){
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			if(factionData.containsKey("PROFESSION_THRESHOLD")) {
				Object[] keys = (factionData.keySet().toArray());
				int size = keys.length;
				System.out.println(ChatColor.AQUA + "#################");
				for(int i = 0; i < size; i++) {
					Object key = keys[i];
					//System.out.println(ChatColor.BLUE + "i:" + i + "/" + (size-1));
					String s = factionData.get(key);
					int value = 0;
					try {
						value = Integer.parseInt(s);
					}catch(NumberFormatException e) {
						// ideally - only PROFESSION_THRESHOLD
						if(!key.toString().equals("PROFESSION_THRESHOLD")) {
							System.out.println("Non-number for key:" + key.toString());
						}						
					}
					
					
					int index = TYPE.getByName(key.toString());
					if(index < 1) {
						continue;
					}
					
					System.out.println(ChatColor.AQUA + "[Professions] Name:" + ChatColor.RED + key.toString());
					System.out.println(ChatColor.AQUA + "[Professions] Threshold:"  + ChatColor.RED + value);
					System.out.println(ChatColor.AQUA + "#################");
					
					PROFESSION_THRESHOLD[index-1] = value;
				}
				continue;
			}
			
			//Player p = Bukkit.getPlayer(UUID.fromString("123"));
            //PlayerProfession pp = new PlayerProfession(p);
            //professes.put(p.getUniqueId(), pp);
        }
		System.out.println(ChatColor.AQUA + "[KARAVANY] finished.");
        return professes;
	}
	private static void writeOutFiles(File file, List<Map<String, String>> saveData) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            file.createNewFile();
            FileWriter saveWriter = new FileWriter(file);
            saveWriter.write(gson.toJson(saveData));
            saveWriter.close();
        } catch(IOException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }
	public static void save(HashMap<UUID, PlayerProfession> professions) {
		// Prepare
		List<Map<String, String>> profess = new ArrayList<>();
		File f = new File(CONFIG_SAVEFILE);
		// Save thresholds
		Gson gson = new GsonBuilder().setPrettyPrinting().create();		
		Map<String, String> saveMap = new HashMap<>();
		saveMap.put("PROFESSION_THRESHOLD", "");
		for(int i = 0; i < PROFESSION_THRESHOLD.length; i++) {
			saveMap.put(TYPE.values()[i+1].encoding, Integer.toString(PROFESSION_THRESHOLD[i]));
		}
		
		profess.add(saveMap);
		// Save all players
		for(PlayerProfession pf : professions.values()) {
			profess.add(pf.save());						
		}		
		// Write
		writeOutFiles(f, profess);
	}
	
	public PlayerProfession(Player player) {
		for(int i = 0; i < PROFESSION_NUMBER; i++) {
			profession_progress[i] = 0;
		}
		this.player = player;
	}
	public PlayerProfession(int[] prof_prop, Player player) {
		for(int i = 0; i < PROFESSION_NUMBER; i++) {
			profession_progress[i] = prof_prop[i];
		}
		checkProgress();
		this.player = player;
	}
	
	public Map<String, String> save(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("playerUUID", gson.toJson(player.getUniqueId()));
        saveMap.put("playerProfID", gson.toJson(playerType.getValue()));
        saveMap.put("playerProgression", gson.toJson(profession_progress));
        return saveMap;
	}
	
	
	public int[] getProfessionProgress() {
		return profession_progress;
	}
	
	public TYPE getProfession() {
		return playerType;
	}
	
	public void addProgress(TYPE type, int progress) {
		if(type == TYPE.NONE) {
			return;
		}
		profession_progress[type.getValue()-1] += progress;
		checkProgress();
	}
	
	private void checkProgress() {
		for(int i = 0; i < PROFESSION_NUMBER; i++) {
			if(profession_progress[i] >= PROFESSION_THRESHOLD[i]) {
				playerType = TYPE.values()[i + 1];
			}
		}
	}
	
	public Player getPlayer() {
		return player;
	}
}
