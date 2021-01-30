package factionsystem.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import Karavany.Algorithm;
import Karavany.ConvoySubsystem;
import Karavany.NPCConvoyTrait;
import Karavany.TradePoint;
import factionsystem.Main;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.*;

import static factionsystem.Subsystems.UtilitySubsystem.findUUIDBasedOnPlayerName;
import static factionsystem.Subsystems.UtilitySubsystem.getPlayersPowerRecord;
import static org.bukkit.Bukkit.getServer;

public class Faction {

    // saved
    private ArrayList<UUID> members = new ArrayList<>();
    private ArrayList<String> enemyFactions = new ArrayList<>();
    private ArrayList<UUID> officers = new ArrayList<>();
    private ArrayList<String> allyFactions = new ArrayList<>();
    private ArrayList<String> laws = new ArrayList<>();
    private ArrayList<String> vassals = new ArrayList<>();
    
    private String name = "defaultName";
    private String description = "defaultDescription";
    private String liege = "none";
    private UUID owner = UUID.randomUUID();
    private int cumulativePowerLevel = 0;
    private Location factionHome = null;
    private ArrayList<Gate> gates = new ArrayList<>();
    
    // Reputation
    private int warReputation = 0;
    private int tradeReputation = 0;
    private WarReputationStates warReputationState = WarReputationStates.NEUTRAL;
    private long lastWarDeclarationTime = 0;
    //Tradepoints
    private ArrayList<String> allyTradeFactions = new ArrayList<>();
    
    // temporary
    int maxPower = 0;
    private ArrayList<UUID> invited = new ArrayList<>();
    private ArrayList<String> attemptedTruces = new ArrayList<>();
    private ArrayList<String> attemptedAlliances = new ArrayList<>();
    private ArrayList<String> attemptedVassalizations = new ArrayList<>();
    private ArrayList<String> attemptedTradeAlliances = new ArrayList<>();
    private boolean autoclaim = false;
    private Main main;


    // player constructor
    public Faction(String initialName, UUID creator, int max, Main main) {
        setName(initialName);
        setOwner(creator);
        maxPower = max;
        this.main = main;
        this.lastWarDeclarationTime = System.currentTimeMillis();
    }

    // server constructor
    public Faction(String initialName, int max, Main main) {
        setName(initialName);
        maxPower = max;
        this.main = main;
        this.lastWarDeclarationTime = System.currentTimeMillis();
    }

    public ArrayList<Gate> getGates()
    {
    	return gates;
    }    
    
    // Must recieve json data
    public Faction(Map<String, String> data, Main main) {
        this.main = main;
        this.load(data);
    } 
    
    
    public int getNumOfficers() {
        return officers.size();
    }

    public void addLaw(String newLaw) {
        laws.add(newLaw);
    }

    public boolean removeLaw(String lawToRemove) {
        if (main.utilities.containsIgnoreCase(laws, lawToRemove)) {
            laws.remove(lawToRemove);
            return true;
        }
        return false;
    }

    public boolean removeLaw(int i) {
        if (laws.size() > i) {
            laws.remove(i);
            return true;
        }
        return false;
    }

    public boolean editLaw(int i, String newString) {
        if (laws.size() > i) {
            laws.set(i, newString);
            return true;
        }
        return false;
    }

    public int getNumLaws() {
        return laws.size();
    }

    public ArrayList<String> getLaws() {
        return laws;
    }

    public void requestTruce(String factionName) {
        if (!main.utilities.containsIgnoreCase(attemptedTruces, factionName)) {
            attemptedTruces.add(factionName);
        }
    }

    public boolean isTruceRequested(String factionName) {
        return main.utilities.containsIgnoreCase(attemptedTruces, factionName);
    }

    public void removeRequestedTruce(String factionName) {
        main.utilities.removeIfContainsIgnoreCase(attemptedTruces, factionName);
    }

    public void requestAlly(String factionName) {
        if (!main.utilities.containsIgnoreCase(attemptedAlliances, factionName)) {
            attemptedAlliances.add(factionName);
        }
    }

    public boolean isRequestedAlly(String factionName) {
        return main.utilities.containsIgnoreCase(attemptedAlliances, factionName);
    }

    public void addAlly(String factionName) {
        if (!main.utilities.containsIgnoreCase(allyFactions, factionName)) {
            allyFactions.add(factionName);
        }
    }

    public void removeAlly(String factionName) {
        main.utilities.removeIfContainsIgnoreCase(allyFactions, factionName);
    }

    public boolean isAlly(String factionName) {
        return main.utilities.containsIgnoreCase(allyFactions, factionName);
    }

    public ArrayList<String> getAllies() {
        return allyFactions;
    }

    public void setFactionHome(Location l) {
        factionHome = l;
    }

    public Location getFactionHome() {
        return factionHome;
    }

    public int getCumulativePowerLevel() {
        int powerLevel = 0;
        for (UUID playerUUID : members){
            try
            {
            	powerLevel += getPlayersPowerRecord(playerUUID, main.playerPowerRecords).getPowerLevel();
            }
            catch (Exception e)
            {
            	System.out.println("ERROR: Player's Power Record for uuid " + playerUUID + " not found. Could not get cumulative power level.");
            }
        }
        return powerLevel;
    }

    public int calculateMaxOfficers(){
        int officersPerXNumber = main.getConfig().getInt("officerPerMemberCount");
        int officersFromConfig = members.size() / officersPerXNumber;
        return 1 + officersFromConfig;
    }

    public boolean addOfficer(UUID newOfficer) {
        if (officers.size() < calculateMaxOfficers() && !officers.contains(newOfficer)){
            officers.add(newOfficer);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeOfficer(UUID officerToRemove) {
        return officers.remove(officerToRemove);
    }

    public boolean isOfficer(UUID uuid) {
        return officers.contains(uuid);
    }

    public ArrayList<UUID> getMemberArrayList() {
        return members;
    }

    public void toggleAutoClaim() {
        autoclaim = !autoclaim;
    }

    public boolean getAutoClaimStatus() {
        return autoclaim;
    }

    public void addEnemy(String factionName) {
        if (!main.utilities.containsIgnoreCase(enemyFactions, factionName)) {
            enemyFactions.add(factionName);
        }
    }

    public void removeEnemy(String factionName) {
        main.utilities.removeIfContainsIgnoreCase(enemyFactions, factionName);
    }

    public boolean isEnemy(String factionName) {
        return main.utilities.containsIgnoreCase(enemyFactions, factionName);
    }

    public String getEnemiesSeparatedByCommas() {
        String enemies = "";
        for (int i = 0; i < enemyFactions.size(); i++) {
            enemies = enemies + enemyFactions.get(i);
            if (i != enemyFactions.size() - 1) {
                enemies = enemies + ", ";
            }
        }
        return enemies;
    }

    public String getAlliesSeparatedByCommas() {
        String allies = "";
        for (int i = 0; i < allyFactions.size(); i++) {
            allies = allies + allyFactions.get(i);
            if (i != allyFactions.size() - 1) {
                allies = allies + ", ";
            }
        }
        return allies;
    }

    public void invite(UUID playerName) {
        Player player = getServer().getPlayer(playerName);
        if (player != null) {
            UUID playerUUID = getServer().getPlayer(playerName).getUniqueId();
            invited.add(playerUUID);
        }
    }

    public void uninvite(UUID player) {
        invited.remove(player);
    }

    public boolean isInvited(UUID uuid) {
        return invited.contains(uuid);
    }

    public ArrayList<UUID> getMemberList() {
        return members;
    }

    public int getPopulation() {
        return members.size();
    }

    public void setOwner(UUID UUID) {
        owner = UUID;
    }

    public boolean isOwner(UUID UUID) {
        return owner.equals(UUID);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String newDesc) {
        description = newDesc;
    }

    public String getDescription() {
        return description;
    }

    public void addMember(UUID UUID, int power) {
        members.add(UUID);
        cumulativePowerLevel = cumulativePowerLevel + power;
    }

    public void removeMember(UUID UUID, int power) {
        members.remove(UUID);
        cumulativePowerLevel = cumulativePowerLevel - power;
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, String> saveMap = new HashMap<>();

        saveMap.put("members", gson.toJson(members));
        saveMap.put("enemyFactions", gson.toJson(enemyFactions));
        saveMap.put("officers", gson.toJson(officers));
        saveMap.put("allyFactions", gson.toJson(allyFactions));
        saveMap.put("laws", gson.toJson(laws));
        saveMap.put("name", gson.toJson(name));
        saveMap.put("vassals", gson.toJson(vassals));
        saveMap.put("description", gson.toJson(description));
        saveMap.put("owner", gson.toJson(owner));
        saveMap.put("cumulativePowerLevel", gson.toJson(cumulativePowerLevel));
        saveMap.put("location", gson.toJson(saveLocation(gson)));
        saveMap.put("liege", gson.toJson(liege));
        saveMap.put("warReputation", gson.toJson(warReputation));
        saveMap.put("tradeReputation", gson.toJson(tradeReputation));
        saveMap.put("lastWarDeclarationTime", gson.toJson(lastWarDeclarationTime));
        saveMap.put("allyTradeFactions", gson.toJson(allyTradeFactions));
        
        ArrayList<String> postList = new ArrayList<String>(); 
        ArrayList<String> mainPoint = new ArrayList<String>(); 
        // Save main point
        if(tradepoint != null) {
        	Map <String, String> mapT = tradepoint.save(main);
        	mainPoint.add(gson.toJson(mapT));
        	saveMap.put("tradePoint", gson.toJson(mainPoint));
        }
        
        
        // Save others
        for (TradePoint tp : tradePosts)
        {
        	Map <String, String> map = tp.save(main);
        	postList.add(gson.toJson(map));
        }
        saveMap.put("tradePosts", gson.toJson(postList));        
        
        // Gate
        ArrayList<String> gateList = new ArrayList<String>(); 
        for (Gate gate : gates)
        {
        	Map <String, String> map = gate.save();
        	gateList.add(gson.toJson(map));
        }
        saveMap.put("factionGates", gson.toJson(gateList));        
        return saveMap;
    }

    private Map<String, String> saveLocation(Gson gson) {
        Map<String, String> saveMap = new HashMap<>();

        if (factionHome != null && factionHome.getWorld() != null){
            saveMap.put("worldName", gson.toJson(factionHome.getWorld().getName()));
            saveMap.put("x", gson.toJson(factionHome.getX()));
            saveMap.put("y", gson.toJson(factionHome.getY()));
            saveMap.put("z", gson.toJson(factionHome.getZ()));
        }

        return saveMap;
    }
    
    Type arrayListTypeString = new TypeToken<ArrayList<String>>(){}.getType();
    Type arrayListTypeUUID = new TypeToken<ArrayList<UUID>>(){}.getType();
    Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();

    private void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        members = gson.fromJson(data.get("members"), arrayListTypeUUID);
        enemyFactions = gson.fromJson(data.get("enemyFactions"), arrayListTypeString);
        officers = gson.fromJson(data.get("officers"), arrayListTypeUUID);
        allyFactions = gson.fromJson(data.get("allyFactions"), arrayListTypeString);
        laws = gson.fromJson(data.get("laws"), arrayListTypeString);
        name = gson.fromJson(data.get("name"), String.class);
        description = gson.fromJson(data.get("description"), String.class);
        owner = UUID.fromString(gson.fromJson(data.get("owner"), String.class));
        cumulativePowerLevel = gson.fromJson(data.get("cumulativePowerLevel"), Integer.TYPE);
        factionHome = loadLocation(gson.fromJson(data.get("location"), mapType), gson);
        liege = gson.fromJson(data.getOrDefault("liege", "none"), String.class);
        vassals = gson.fromJson(data.getOrDefault("vassals", "[]"), arrayListTypeString);
        warReputation =  gson.fromJson(data.get("warReputation"), Integer.TYPE);
        setWarReputationState();
        tradeReputation =  gson.fromJson(data.get("tradeReputation"), Integer.TYPE);
        lastWarDeclarationTime = gson.fromJson(data.get("lastWarDeclarationTime"), Long.TYPE);
        allyTradeFactions = gson.fromJson(data.get("allyTradeFactions"), arrayListTypeString);
        if(allyTradeFactions == null) allyTradeFactions = new ArrayList<String>();
        
        System.out.println(ChatColor.AQUA + "Loading KARAVANY PARTS...");
        loadTradePoints(gson, data);
        
        System.out.println("Loading Fation Gates...");
        ArrayList<String> gateList = new ArrayList<String>();
        gateList = gson.fromJson(data.get("factionGates"), arrayListTypeString);
        if (gateList != null)
        {
	        for (String item : gateList)
	        {
	        	Gate g = Gate.load(item, main);
	        	gates.add(g);
	        }
        }
        else
        {
        	System.out.println("Could not load gates because the collection 'factionGates' did not exist in the factions JSON file. Are you upgrading from a previous version? Setting default.");
        }
    }
    
    public void loadTradePoints(Gson gson, Map<String, String> data){
    	tradepoint = null;
    	tradePosts.clear();
    	
    	// Load main
    	ArrayList<String> tradeList = new ArrayList<String>();
    	tradeList = gson.fromJson(data.get("tradePoint"), arrayListTypeString);
    	if(tradeList != null) {
    		for (String item : tradeList)
            {
            	TradePoint g = TradePoint.load(item, main);
            	tradepoint = g;
            	main.allTradePosts.put(g.getLocation().getChunk(), g);
            }
    	}
    	
    	
    	ArrayList<String> tradePostsS = new ArrayList<String>();
    	tradePostsS = gson.fromJson(data.get("tradePosts"), arrayListTypeString);
    	if(tradePostsS != null) {
    		for (String item : tradePostsS)
            {
            	TradePoint g = TradePoint.load(item, main);	        	
            	this.tradePosts.add(g);
            	main.allTradePosts.put(g.getLocation().getChunk(), g);
            }
    	}
    	
    	
    	computeNeighbours();
    	//Algorithm.CheckNetworkForHoles(this);
    }
    

    private void computeNeighbours() {
    	ArrayList<TradePoint> all = (ArrayList<TradePoint>) (tradePosts.clone());
    	all.add(tradepoint);
    	
    	for(TradePoint p : all) {
    		p.neightbours.clear();
    	}
    	for(TradePoint p : all) {
    		for(TradePoint n : all) {
    			if(!p.equals(n)) {
    				double dist = NPCConvoyTrait.Norm2Distance(p.getLocation(), n.getLocation());
    				if(dist <= ConvoySubsystem.CARAVAN_MAX_DISTANCE) {
    					n.neightbours.add(p);
    					p.neightbours.add(n);
    				}
    			}
    		}
    	}
    }
    
    private Location loadLocation(HashMap<String, String> data, Gson gson){
        if (data.size() != 0){
            World world = getServer().createWorld(new WorldCreator(gson.fromJson(data.get("worldName"), String.class)));
            double x = gson.fromJson(data.get("x"), Double.TYPE);
            double y = gson.fromJson(data.get("y"), Double.TYPE);
            double z = gson.fromJson(data.get("z"), Double.TYPE);
            return new Location(world, x, y, z);
        }
        return null;
    }

    public boolean legacyLoad(String filename) {
        try {
            File loadFile = new File("./plugins/MedievalFactions/" + filename);
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            if (loadReader.hasNextLine()) {
                setName(loadReader.nextLine());
            }
            if (loadReader.hasNextLine()) {
                String playerName = loadReader.nextLine();
                setOwner(findUUIDBasedOnPlayerName(playerName));
            }
            if (loadReader.hasNextLine()) {
                setDescription(loadReader.nextLine());
            }

            if (loadReader.hasNextLine()) {
                // Read legacy line and move along across Cumulative Power Record.
                loadReader.nextLine();
            }

            while (loadReader.hasNextLine()) {
                String temp = loadReader.nextLine();

                if (temp.equalsIgnoreCase("-")) {
                    break;
                }
                members.add(findUUIDBasedOnPlayerName(temp));
            }

            while (loadReader.hasNextLine()) {
                String temp = loadReader.nextLine();

                if (temp.equalsIgnoreCase("-")) {
                    break;
                }

                enemyFactions.add(temp);
            }

            while (loadReader.hasNextLine()) {
                String temp = loadReader.nextLine();

                if (temp.equalsIgnoreCase("-")) {
                    break;
                }

                allyFactions.add(temp);
            }

            while (loadReader.hasNextLine()) {
                String playerName = loadReader.nextLine();

                if (playerName.equalsIgnoreCase("-")) {
                    break;
                }

                officers.add(findUUIDBasedOnPlayerName(playerName));
            }

            String worldname;
            worldname = loadReader.nextLine();
            if (!worldname.equalsIgnoreCase("null")) {

                World world = null;
                double x = 0;
                double y = 0;
                double z = 0;

                try {
                    System.out.println("Attempting to load faction home location for " + name + "...");

                    // load faction home details
                    world = getServer().createWorld(new WorldCreator(worldname));
                    System.out.println("World successfully acquired.");

                    if (loadReader.hasNextLine()) {
//                    System.out.println("Parsing double...");
                        x = Double.parseDouble(loadReader.nextLine());
//                    System.out.println("X position successfully acquired.");
                    }
                    else {
                        System.out.println("X position not found in file!");
                    }
                    if (loadReader.hasNextLine()) {//
                        System.out.println("Parsing double...");
                        y = Double.parseDouble(loadReader.nextLine());
//                    System.out.println("Y position successfully acquired.");
                    }
                    else {
                        System.out.println("Y position not found in file!");
                    }
                    if (loadReader.hasNextLine()) {
                        System.out.println("Parsing double...");
                        z = Double.parseDouble(loadReader.nextLine());
//                    System.out.println("Z position successfully acquired.");
                    }
                    else {
                        System.out.println("Z position not found in file!");
                    }

                    // set location
                    if (world != null && x != 0 && y != 0 && z != 0) {
                        factionHome = new Location(world, x, y, z);
                        System.out.println("Faction home successfully set to " + x + ", " + y + ", " + z + ".");
                    }
                    else {
                        System.out.println("One of the variables the faction home location depends on wasn't loaded!");
                    }

                }
                catch(Exception e) {
                    System.out.println("An error occurred loading the faction home position.");
                }
            }

            while (loadReader.hasNextLine()) {
                String temp = loadReader.nextLine();

                if (temp.equalsIgnoreCase("-")) {
                    break;
                }

                laws.add(temp);
            }

            loadReader.close();
            System.out.println("Faction " + name + " successfully loaded.");
            return true;

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred loading the file " + filename + ".");
            return false;
        }
    }

    @Override
    public String toString() {
        return "Faction{" +
                "members=" + members +
                ", enemyFactions=" + enemyFactions +
                ", officers=" + officers +
                ", allyFactions=" + allyFactions +
                ", laws=" + laws +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", owner=" + owner +
                ", cumulativePowerLevel=" + cumulativePowerLevel +
                ", liege=" + liege +
                ", warRep=" + warReputationState.toString() +
                ", tradeRep=" + tradeReputation +
                '}';
    }

    public boolean isVassal(String faction) {
        return(main.utilities.containsIgnoreCase(vassals, faction));
    }

    public boolean hasLiege() {
        return !liege.equalsIgnoreCase("none"); // TODO: fix null error
    }

    public boolean isLiege(String faction) {
        return liege.equalsIgnoreCase(faction);
    }

    public void addVassal(String factionName) {
        if (!main.utilities.containsIgnoreCase(vassals, factionName)) {
            vassals.add(factionName);
        }
    }

    public void removeVassal(String faction) {
        main.utilities.removeIfContainsIgnoreCase(vassals, faction);
    }

    public void setLiege(String newLiege) {
        liege = newLiege;
    }

    public void addGate(Gate gate)
    {
    	gates.add(gate);
    }
    
    public void removeGate(Gate gate)
    {
    	gate.fillGate();
    	gates.remove(gate);
    }

    public boolean hasGateTrigger(Block block)
    {
    	for(Gate g : gates)
    	{
    		if (g.getTrigger().getX() == block.getX() && g.getTrigger().getY() == block.getY() && g.getTrigger().getZ() == block.getZ() &&
    				g.getTrigger().getWorld().equalsIgnoreCase(block.getWorld().getName()))
    		{
    			return true;
    		}
    	}
		return false;
    }
    
    public ArrayList<Gate> getGatesForTrigger(Block block)
    {
    	ArrayList<Gate> gateList = new ArrayList<>();
    	for(Gate g : gates)
    	{
    		if (g.getTrigger().getX() == block.getX() && g.getTrigger().getY() == block.getY() && g.getTrigger().getZ() == block.getZ() &&
    				g.getTrigger().getWorld().equalsIgnoreCase(block.getWorld().getName()))
    		{
    			gateList.add(g);
    		}
    	}
		return gateList;
    }

    public String getLiege() {
        return liege;
    }

    public boolean isLiege() {
        return vassals.size() != 0;
    }

    public String getVassalsSeparatedByCommas() {
        String toReturn = "";
        for (int i = 0; i < vassals.size(); i++) {
            toReturn = toReturn + vassals.get(i);
            if (i != vassals.size() - 1) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }

    public void addAttemptedVassalization(String factionName) {
        if (!main.utilities.containsIgnoreCase(attemptedVassalizations, factionName)) {
            attemptedVassalizations.add(factionName);
        }
    }

    public boolean hasBeenOfferedVassalization(String factionName) {
        return main.utilities.containsIgnoreCase(attemptedVassalizations, factionName);
    }

    public void removeAttemptedVassalization(String factionName) {
        main.utilities.removeIfContainsIgnoreCase(attemptedVassalizations, factionName);
    }
    
    ///// ADDING REPUTATION THINGs
    public enum WarReputationStates{
        FRIEND, HALFFRIEND, NEUTRAL, AGRESSIVE, WARLORD;
        
        @Override
        public String toString() {
          switch(this) {
            case FRIEND: return "миролюбивый ангел";
            case NEUTRAL: return "нейтральный незнакомец";
            case AGRESSIVE: return "зубастый сосед";
            case HALFFRIEND: return "дружелюбный сосед";
            case WARLORD: return "агрессивный завоеватель";
            default: throw new IllegalArgumentException();
          }
        }
    };

    public int getWarReputation() {
        return warReputation;
    }

    public int getTradeReputation() {
        return tradeReputation;
    }
    
    public void warDeclaredReputationChange() {
        warReputation -= 15;
        setWarReputationState();
    }
    public void truceAttemptedFromMySideReputationChange() {
        warReputation += 4;
        setWarReputationState();
    }
    public void truceSignedFromMySideReputationChange() {
        warReputation += 8;
        setWarReputationState();
    }
    public void vassalizeAttemptReputationChange() {
        warReputation -= 5;
        setWarReputationState();
    }
    public void vassalizeSuccessReputationChange() {
        warReputation -= 8;
        setWarReputationState();
    }
    public void grantIndepenceReputationChange() {
        warReputation += 10;
        setWarReputationState();
    }
    public void breakAllianceReputationChange(){
        warReputation -= 20;
        setWarReputationState();
    }
    public void neutralPlayerKilledReputationChange() {
    	warReputation -= 1;
    	setWarReputationState();
    }
    public void halffriendPlayerKilledReputationChange() {
    	warReputation -= 3;
    	setWarReputationState();
    }
    public void friendPlayerKilledReputationChange() {
    	warReputation -= 5;
    	setWarReputationState();
    }
    public void agressivePlayerKilledReputationChange() {
    	warReputation += 2;
    	setWarReputationState();
    }
    public void warlordPlayerKilledReputationChange() {
    	warReputation += 5;
    	setWarReputationState();
    }
    public void antiAgressiveWar() {
    	warReputation += 10;
    	setWarReputationState();
    }
    
    public void signAllianceReputationChange(){
        warReputation += 15;
        setWarReputationState();
    }
    
    
    
    private void setWarReputationState(){
    	if(warReputation >= 40){
            warReputationState = WarReputationStates.FRIEND;
            main.utilities.sendAllPlayersOnServerMessage(ChatColor.GREEN + this.name + " отныне считается дружелюбной фракцией! "
            		+ "Отныне за нападение на неё у вас будет отключаться приват!");
        }
    	else if(warReputation >= 20){
            warReputationState = WarReputationStates.HALFFRIEND;
        }
    	else if(warReputation <= -40){
            warReputationState = WarReputationStates.WARLORD;
            main.utilities.sendAllPlayersOnServerMessage(ChatColor.RED + this.name + " отныне считается воинствующей фракцией! "
            		+ "У неё отключен приват и блокировка, а за войны и убийство против неё вы будете получать репутацию!");
        }
        else if(warReputation <= -20){
            warReputationState = WarReputationStates.AGRESSIVE;
            
        }       
        else{
            warReputationState = WarReputationStates.NEUTRAL;
        }
    }
    
    public WarReputationStates getWarReputationState() {
    	return warReputationState;
    }
    
    public boolean hasFriendlyEnemy() {
    	for(Faction f : main.factions) {
    		if(f.getWarReputationState()==WarReputationStates.FRIEND && 
    				main.utilities.containsIgnoreCase(enemyFactions, f.getName())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public long getLastWarTime() {
    	return lastWarDeclarationTime;
    }
    
    public void setLastWarTime(long current) {
    	lastWarDeclarationTime = current;
    }
    /// ADDING TRADE THINGS
    private TradePoint tradepoint = null;
    private ArrayList<TradePoint> tradePosts = new ArrayList<TradePoint>();
    
    public void removeAfterUnclaim(TradePoint tp) {
    	if(tradepoint.equals(tp)) {
    		DeleteTradePoint();
    	}else {
    		removeTradePosts(tp.getLocation());
    	}
    }
    
    public TradePoint getTradePoint() {
		return tradepoint;    	
    }
    public void SetTradePoint(TradePoint tr) {
    	tradepoint = tr;
    	main.allTradePosts.put(tr.getLocation().getChunk(), tr);
    }
    public void DeleteTradePoint() {
    	if(tradepoint == null) {
    		return;
    	}
    	if(tradepoint.hologram != null) {
    		tradepoint.hologram.despawn();
        	main.hologram.getHologramManager().deleteHologram(tradepoint.hologram);
    	}
    	main.allTradePosts.remove(tradepoint.getLocation().getChunk());
    	if(tradepoint.chest != null && main.tradeChests.contains(tradepoint.chest.getBlock())) {
    		main.tradeChests.remove(tradepoint.getChest().getBlock());
    	}
    	tradepoint = null;
    	for(TradePoint tp : tradePosts) {
    		if(tp == null) continue;
    		if(tp.hologram != null) {
    			tp.hologram.despawn();
        		main.hologram.getHologramManager().deleteHologram(tp.hologram);
    		}    		
    	}
    	ArrayList<TradePoint> toDelete = (ArrayList<TradePoint>) tradePosts.clone();
    	for(TradePoint tp : toDelete) {
    		deletePostNoConnected(tp);
    	}
    	
    }       
    
    public void requestTradeAlly(String factionName) {
        if (!main.utilities.containsIgnoreCase(attemptedTradeAlliances, factionName)) {
        	attemptedTradeAlliances.add(factionName);
        }
    }

    public boolean isRequestedTradeAlly(String factionName) {
        return main.utilities.containsIgnoreCase(attemptedTradeAlliances, factionName);
    }

    public void addTradeAlly(String factionName) {
        if (!main.utilities.containsIgnoreCase(allyTradeFactions, factionName)) {
        	allyTradeFactions.add(factionName);
        }
    }

    public void removeTradeAlly(String factionName) {
        main.utilities.removeIfContainsIgnoreCase(allyTradeFactions, factionName);
    }

    public boolean isTradeAlly(String factionName) {
        return main.utilities.containsIgnoreCase(allyTradeFactions, factionName);
    }

    public ArrayList<String> getTradeAllies() {
        return allyTradeFactions;
    }
    
    public String getTradeAlliesSeparatedByCommas() {
        String allies = "";
        for (int i = 0; i < allyTradeFactions.size(); i++) {
            allies = allies + allyTradeFactions.get(i);
            if (i != allyTradeFactions.size() - 1) {
                allies = allies + ", ";
            }
        }
        return allies;
    }
    
    public ArrayList<TradePoint> getTradePosts() {
        return tradePosts;
    }
    
    public boolean addTradePost(TradePoint t) {
    	boolean b = tradePosts.add(t);
    	main.allTradePosts.put(t.getLocation().getChunk(), t);
    	return b;
    }
    public boolean removeTradePosts(Location loc) {
    	TradePoint post = null;   	
    	
    	for(TradePoint tp : tradePosts) {   
    		Chunk c = tp.getLocation().getChunk();
    		Chunk lc = loc.getChunk();
    		if(c.equals(lc)) {
    			/*
    			tradePosts.remove(tp);
    			main.allTradePosts.remove(tp.getLocation().getChunk());
    			Algorithm.RemoveUnconnectedTradePoints(tp, tradepoint, this);
    			tradepoint.neightbours.remove(tp);
    			
    			tp.hologram.despawn();
    			main.hologram.getHologramManager().deleteHologram(tp.hologram);
    			Algorithm.CheckNetworkForHoles(this);
    			return true;*/
    			post = tp;
    		}
    	}
    	if(post == null) {
    		return false;
    	}
    	
    	tradePosts.remove(post);
    	main.allTradePosts.remove(post.getLocation().getChunk());
    	for(TradePoint n : post.neightbours) {
    		n.neightbours.remove(post);
    	}
    	post.hologram.despawn();
		main.hologram.getHologramManager().deleteHologram(post.hologram);
    	return true;
    }
    
    public void deletePostNoConnected(TradePoint post) {
    	tradePosts.remove(post);
    	main.allTradePosts.remove(post.getLocation().getChunk());
    	for(TradePoint n : post.neightbours) {
    		n.neightbours.remove(post);
    	}
    	post.hologram.despawn();
		main.hologram.getHologramManager().deleteHologram(post.hologram);
    	
    	/*
    	if(tradepoint.neightbours == null) 
			tradepoint.neightbours = new ArrayList<TradePoint>();
    	if(post == null) return;
    	
    	
    	if(tradePosts.contains(post)) {
    		if(tradepoint != null) {			
    			tradepoint.neightbours.remove(post);
    		}  
			tradePosts.remove(post);
			main.allTradePosts.remove(post.getLocation().getChunk());
			post.hologram.despawn();
			main.hologram.getHologramManager().deleteHologram(post.hologram);
			for(TradePoint tp : tradePosts) {   
	    		if(tp == null) tradePosts.remove(tp);
	    		if(tp.neightbours.contains(post)) {
	    			tp.neightbours.remove(post);
	    		}
	    	}
		}*/
    }
    
    
}