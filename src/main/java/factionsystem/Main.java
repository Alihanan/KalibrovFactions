package factionsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.nicknamer.NickNamerPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sainttx.holograms.api.HologramPlugin;
import Karavany.ConvoyDamageListener;
import Karavany.ConvoyFollowerTrait;
import Karavany.ConvoySubsystem;
import Karavany.NPCConvoyDeathHandler;
import Karavany.NPCConvoyTrait;
import Karavany.CaravanCultureManager;
import Karavany.TradePoint;
import Professions.CraftBlockingEventHandler;
import Professions.HorsePenaltyEvent;
import Professions.PlayerBreakBlockHandler;
import Professions.PlayerProfession;
import Professions.ProfessionProtocolManager;
import factionsystem.EventHandlers.AreaEffectCloudApplyEventHandler;
import factionsystem.EventHandlers.AsyncPlayerChatEventHandler;
import factionsystem.EventHandlers.BlockBreakEventHandler;
import factionsystem.EventHandlers.BlockPlaceEventHandler;
import factionsystem.EventHandlers.EntityDamageByEntityEventHandler;
import factionsystem.EventHandlers.EntitySpawnEventHandler;
import factionsystem.EventHandlers.LingeringPotionSplashEventHandler;
import factionsystem.EventHandlers.PlayerDeathEventHandler;
import factionsystem.EventHandlers.PlayerInteractEventHandler;
import factionsystem.EventHandlers.PlayerJoinEventHandler;
import factionsystem.EventHandlers.PlayerLeaveEventHandler;
import factionsystem.EventHandlers.PlayerMoveEventHandler;
import factionsystem.EventHandlers.PotionSplashEventHandler;
import factionsystem.Objects.ClaimedChunk;
import factionsystem.Objects.Duel;
import factionsystem.Objects.Faction;
import factionsystem.Objects.Gate;
import factionsystem.Objects.LockedBlock;
import factionsystem.Objects.PlayerActivityRecord;
import factionsystem.Objects.PlayerPowerRecord;
import factionsystem.Subsystems.CommandSubsystem;
import factionsystem.Subsystems.ConfigSubsystem;
import factionsystem.Subsystems.StorageSubsystem;
import factionsystem.Subsystems.UtilitySubsystem;
import factionsystem.Util.Pair;
import factionsystem.bStats.Metrics;


public class Main extends JavaPlugin implements Listener {

    // version
    public static String version = "v3.6";

    // subsystems
    public StorageSubsystem storage = new StorageSubsystem(this);
    public UtilitySubsystem utilities = new UtilitySubsystem(this);
    public ConfigSubsystem config = new ConfigSubsystem(this);

    // saved lists
    public ArrayList<Faction> factions = new ArrayList<>();
    public ArrayList<ClaimedChunk> claimedChunks = new ArrayList<>();
    public ArrayList<PlayerPowerRecord> playerPowerRecords = new ArrayList<>();
    public ArrayList<PlayerActivityRecord> playerActivityRecords = new ArrayList<>();
    public ArrayList<LockedBlock> lockedBlocks = new ArrayList<>();
    public ArrayList<Duel> duelingPlayers = new ArrayList<Duel>();

    // temporary lists
    public HashMap<UUID, Gate> creatingGatePlayers = new HashMap<>(); 
    public ArrayList<UUID> lockingPlayers = new ArrayList<>();
    public ArrayList<UUID> unlockingPlayers = new ArrayList<>();
    // Left user granting access, right user receiving access;
    public HashMap<UUID, UUID> playersGrantingAccess = new HashMap<>();
    public ArrayList<UUID> playersCheckingAccess = new ArrayList<>();
    // Left user granting access, right user receiving access;
    public HashMap<UUID, UUID> playersRevokingAccess = new HashMap<>();
    public ArrayList<UUID> playersInFactionChat = new ArrayList<>();
    public ArrayList<UUID> adminsBypassingProtections = new ArrayList<>();
    // List of players who made the cloud and the cloud itself in a pair
    public ArrayList<Pair<Player, AreaEffectCloud>> activeAOEClouds = new ArrayList<>();

    
    //// KARAVANY ADDON
    public HologramPlugin hologram;
    public HashMap<UUID, PlayerProfession> professions = new HashMap<>();
    public NickNamerPlugin nickNamer;
    public ProtocolManager protocolManager;
    public ProtocolLib protocolLib;
    public HashMap<Chunk, TradePoint> allTradePosts = new HashMap<>();
    public HashSet<Block> tradeChests = new HashSet<Block>();
    ProfessionProtocolManager ppm; // For professions
    public CaravanCultureManager scp; // For skin change
    
    @Override
    public void onEnable() {
        System.out.println("Medieval Factions plugin enabling....");
        
        utilities.ensureSmoothTransitionBetweenVersions();

        // config creation/loading
        if (!(new File("./plugins/MedievalFactions/config.yml").exists())) {
            config.saveConfigDefaults();
        }
        else {
            // pre load compatibility checks
            if (!getConfig().getString("version").equalsIgnoreCase(Main.version)) {
                System.out.println("[ALERT] Version mismatch! Adding missing defaults and setting version!");
                config.handleVersionMismatch();
            }
            reloadConfig();
            
        }
        
        
        utilities.schedulePowerIncrease();
        utilities.schedulePowerDecrease();
        utilities.scheduleAutosave();
        this.getServer().getPluginManager().registerEvents(this, this);
        hologram = (HologramPlugin)getServer().getPluginManager().getPlugin("Holograms");
        // DELETE HOLOGRAMS
        /*
        hologram.getHologramManager().clear();
        if(hologram.getDataFolder().exists()) {
        	hologram.getDataFolder().delete();
        }        
        hologram.reloadConfig();
        hologram.getHologramManager().clear();*/
        storage.load();

        // post load compatibility checks
        if (!getConfig().getString("version").equalsIgnoreCase(Main.version)) {
            utilities.createActivityRecordForEveryOfflinePlayer(); // make sure every player experiences power decay in case we updated from pre-v3.5
        }

        int pluginId = 8929;
        new Metrics(this, pluginId);
        System.out.println("Basic Medieval Factions plugin enabled.");
        
        
        karavanyAddon();
        
        
    }
    
    private void karavanyAddon() {       
    	System.out.println(ChatColor.AQUA + "[KARAVANY] Starting KARAVANY addon.");
        //NMSUtils.registerEntity(NMSUtils.Type.HORSE, EntityHorseNPC.class, false);
    	System.out.println(ChatColor.AQUA + "[KARAVANY] Loading external plugins ...");
    	// LOAD EXTERNAL PLUGINS    	
        hologram = (HologramPlugin)getServer().getPluginManager().getPlugin("Holograms");        
        nickNamer = (NickNamerPlugin)getServer().getPluginManager().getPlugin("NickNamer");
        protocolLib = (ProtocolLib)getServer().getPluginManager().getPlugin("ProtocolLib");
        protocolManager = ProtocolLibrary.getProtocolManager();
        
        System.out.println(ChatColor.AQUA + "[KARAVANY] Setting Citizens ...");
        // SET CITIZENS
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(
        		net.citizensnpcs.api.trait.TraitInfo.create(ConvoyFollowerTrait.class).withName("convoyFollowerTrait"));
        net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(
        		net.citizensnpcs.api.trait.TraitInfo.create(NPCConvoyTrait.class).withName("NPCConvoyTrait"));
        new ConvoySubsystem(this).runTaskTimer(this, 20, 100);       
        getServer().getPluginManager().registerEvents(new ConvoyDamageListener(this), this);  
        getServer().getPluginManager().registerEvents(new NPCConvoyDeathHandler(this), this);  
        
        System.out.println(ChatColor.AQUA + "[KARAVANY] Enabling professions ...");
        // SET PROFESSIONS
        professions = PlayerProfession.load();
        
        ppm = new ProfessionProtocolManager(this);
        scp = new CaravanCultureManager(this);
        scp.load();
        // INTERCEPT PACKETS
        
        protocolManager.addPacketListener(
        		  new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Client.BLOCK_DIG) {
        		    @Override
        		    public void onPacketReceiving(PacketEvent event) {
        		        // Item packets (id: 0x29)
        		        ppm.handlePacket(event);
        		    }
        		    
        		    
        		});
        /*
        protocolManager.addPacketListener(
        		new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.REL_ENTITY_MOVE) {

            @Override
            public void onPacketSending(PacketEvent event) {
            	scp.handlePacket(event);
            	//System.out.print(ChatColor.DARK_PURPLE + "Sending...");
            }         
        });*/

        new PeriodicSaveSubsystem(storage).runTaskTimer(this, 0, 6000);
        System.out.println(ChatColor.AQUA + "[KARAVANY] KARAVANY addon enabled.");
        // Turn on periodic saving        
        // 6000 = 5 min
        // 36000 = 30 min
        
        
    }    	
    @EventHandler()
    public void HorseEventHandler(PlayerInteractEntityEvent ice) {
    	HorsePenaltyEvent creh = new HorsePenaltyEvent(this);
    	creh.onPlayerInteractEntity(ice);
    } 
    @EventHandler()
    public void OnInventoryClickEvent(InventoryClickEvent ice) {
    	CraftBlockingEventHandler creh = new CraftBlockingEventHandler(this);
    	creh.handle(ice);
    } 
    @EventHandler()
    public void OnPrepareItemCraftEvent(PrepareItemCraftEvent pice) {
    	CraftBlockingEventHandler creh = new CraftBlockingEventHandler(this);
    	creh.handle(pice);
    }
    @EventHandler()
    public void OnBlockBreak(BlockBreakEvent bde) {
    	PlayerBreakBlockHandler pbbh = new PlayerBreakBlockHandler(this);
    	pbbh.handle(bde);
    }
    
    class PeriodicSaveSubsystem extends BukkitRunnable {
    	
    	StorageSubsystem ss;
    	public PeriodicSaveSubsystem(StorageSubsystem storage) {
			ss = storage;
		}
    	
		@Override
		public void run() {
			utilities.sendAllPlayersOnServerMessage(
					ChatColor.RED + "ВНИМАНИЕ ! Начинается сохранение! Сервер может подлагивать! ВНИМАНИЕ!");
			allTradePosts.clear();
			ss.save();
			scp.save();
			
			for(Faction f : factions) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				ArrayList<HashMap<String, String>> data = ss.GetData();
				
				for (Map<String, String> factionData : data){
					String name = gson.fromJson(factionData.get("name"), String.class);
					if(f.getName().equals(name)) {
						f.loadTradePoints(gson, factionData);
						break;
					}
				}
			}
			utilities.sendAllPlayersOnServerMessage(
					ChatColor.RED + "ВНИМАНИЕ ! Сохранение завершено! Лаги скоро прекратятся! ВНИМАНИЕ!");
		}
    	
    }
    
    @Override
    public void onDisable(){
        System.out.println("Medieval Factions plugin disabling....");
        storage.save();
        System.out.println("Medieval Factions plugin disabled.");
        PlayerProfession.save(professions);
        scp.save();
        System.out.println(ChatColor.AQUA + "KARAVANY ADDON disabled. Everything saved");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandSubsystem commandInterpreter = new CommandSubsystem(this);
        return commandInterpreter.interpretCommand(sender, label, args);
    }
    
    @EventHandler()
    public void onDamage(EntityDamageByEntityEvent event) {
        EntityDamageByEntityEventHandler handler = new EntityDamageByEntityEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onPlayerMove(PlayerMoveEvent event) {
        PlayerMoveEventHandler handler = new PlayerMoveEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onBlockBreak(BlockBreakEvent event) {
        BlockBreakEventHandler handler = new BlockBreakEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onBlockPlace(BlockPlaceEvent event) {
        BlockPlaceEventHandler handler = new BlockPlaceEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onRightClick(PlayerInteractEvent event) {
        PlayerInteractEventHandler handler = new PlayerInteractEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onDeath(PlayerDeathEvent event) {
        PlayerDeathEventHandler handler = new PlayerDeathEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onJoin(PlayerJoinEvent event) {
    	// Check if name constains numbers
    	Player player = event.getPlayer();
    	if(player.getName().matches(".*\\d.*")) {
    		Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Цифры в нике запрещены!", null , null );
        	player.kickPlayer("Цифры в нике запрещены!");
        	return;
    	}
    	
    	
        PlayerJoinEventHandler handler = new PlayerJoinEventHandler(this);
        handler.handle(event);
        
        // Create new profession file
        
        if(!professions.keySet().contains(player.getUniqueId())) {
        	PlayerProfession pp = new PlayerProfession(player);
        	professions.put(player.getUniqueId(), pp);
        }
        
        // Change nick by profession
        String nick = player.getName();
        PlayerProfession pp = professions.get(player.getUniqueId());
        int randomProf = (int)(7.0 * Math.random());
        pp.addProgress(PlayerProfession.TYPE.values()[randomProf + 1], 101);
        //String profession = pp.getProfession().getName();
        if(nick.length() > 14) {
        	nick = nick.substring(0, 14);
        }
        String final_nickname = pp.getProfession().getColor() + nick; // + "|" + profession
        final_nickname = ChatColor.translateAlternateColorCodes('&', final_nickname);
        if(nickNamer == null) {
        	nickNamer = (NickNamerPlugin)getServer().getPluginManager().getPlugin("NickNamer");
        }
        if(nickNamer.getAPI() == null) {
        	System.out.println("NICKNAMER API NULL");
        }
        nickNamer.getAPI().setNick(player.getUniqueId(), final_nickname);   
    }
    
    @EventHandler()
    public void onLeave(PlayerQuitEvent event)
    {
    	PlayerLeaveEventHandler handler = new PlayerLeaveEventHandler(this);
    	handler.handle(event);
    }

    @EventHandler()
    public void onJoin(EntitySpawnEvent event) {
        EntitySpawnEventHandler handler = new EntitySpawnEventHandler(this);
        handler.handle(event);
    }

    @EventHandler()
    public void onChat(AsyncPlayerChatEvent event) {
        AsyncPlayerChatEventHandler handler = new AsyncPlayerChatEventHandler(this);
        handler.handle(event);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        PotionSplashEventHandler handler = new PotionSplashEventHandler(this);
        handler.handle(event);
    }

    @EventHandler
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        LingeringPotionSplashEventHandler handler = new LingeringPotionSplashEventHandler(this);
        handler.handle(event);
    }

    @EventHandler
    public void onAreaOfEffectCloudApply(AreaEffectCloudApplyEvent event){
        AreaEffectCloudApplyEventHandler handler = new AreaEffectCloudApplyEventHandler(this);
        handler.handle(event);
    } 
    
}
