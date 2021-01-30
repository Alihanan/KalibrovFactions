package factionsystem.EventHandlers;

import factionsystem.Main;
import factionsystem.Objects.ClaimedChunk;
import factionsystem.Objects.Duel;
import factionsystem.Objects.Faction;
import factionsystem.Objects.PlayerPowerRecord;
import factionsystem.Subsystems.UtilitySubsystem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import static factionsystem.Subsystems.UtilitySubsystem.*;

public class PlayerDeathEventHandler {

    Main main = null;

    public PlayerDeathEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(PlayerDeathEvent event) {
        event.getEntity();
        Player player = event.getEntity();
        
        // decrease dying player's power
        for (PlayerPowerRecord record : main.playerPowerRecords) {
            if (record.getPlayerUUID().equals(player.getUniqueId())) {
                record.decreasePowerByTenPercent();
                if (getPlayersPowerRecord(player.getUniqueId(), main.playerPowerRecords).getPowerLevel() > 0) {
                    player.sendMessage(ChatColor.RED + "Your power level has decreased!");
                }
            }
        }

        // if player's cause of death was another player killing them
        if (player.getKiller() != null) {
            Player killer = player.getKiller();

            PlayerPowerRecord record = UtilitySubsystem.getPlayersPowerRecord(killer.getUniqueId(), main.playerPowerRecords);
            if (record != null) {
                if (record.increasePowerByTenPercent()){
                    killer.sendMessage(ChatColor.GREEN + "Your power level has increased!");
                }
            }
            Faction myFaction = null;
            Faction enemyFaction = null;
            for (Faction faction : main.factions) {
                if (faction.isMember(player.getUniqueId())) {
                	myFaction = faction;
                }   
                if (faction.isMember(killer.getUniqueId())) {
                	myFaction = faction;
                } 
                if(myFaction != null && enemyFaction != null) {
                	break;
                }
            }
            if(myFaction == null) {
            	killer.sendMessage(ChatColor.RED + "Вы убили путешественника без фракции!");   
            	if(enemyFaction != null) {
            		killer.sendMessage(ChatColor.RED + "Репутация вашей фракции падает!");   
            		enemyFaction.neutralPlayerKilledReputationChange();
            	}
            }
            else if(myFaction.getWarReputation() <= -40) {
            	killer.sendMessage(ChatColor.GREEN + "Вы убили члена агрессивной фракции!");  
            	if(enemyFaction != null) {
            		killer.sendMessage(ChatColor.GREEN + "Репутация вашей фракции растёт!");   
            		enemyFaction.agressivePlayerKilledReputationChange();
            	}
            }
            else if(myFaction.getWarReputation() <= -20) {
            	killer.sendMessage(ChatColor.GREEN + "Вы убили члена воинствующей фракции!");  
            	if(enemyFaction != null) {
            		killer.sendMessage(ChatColor.GREEN + "Репутация вашей фракции растёт!");   
            		enemyFaction.warlordPlayerKilledReputationChange();
            	}
            }
            else if(myFaction.getWarReputation() >= 40) {
            	killer.sendMessage(ChatColor.RED + "Вы убили члена мирной фракции!"); 
            	if(enemyFaction != null) {
            		killer.sendMessage(ChatColor.RED + "Репутация вашей фракции падает!");   
            		enemyFaction.halffriendPlayerKilledReputationChange();
            	}
            	
            }else if(myFaction.getWarReputation() >= 20) {
            	killer.sendMessage(ChatColor.RED + "Вы убили члена дружелюбной фракции!");   
            	if(enemyFaction != null) {
            		killer.sendMessage(ChatColor.RED + "Репутация вашей фракции падает!");   
            		enemyFaction.friendPlayerKilledReputationChange();
            	}            	
            }
            else {
            	killer.sendMessage(ChatColor.RED + "Вы убили члена нейтральной фракции!");   
            	if(enemyFaction != null) {
            		killer.sendMessage(ChatColor.RED + "Репутация вашей фракции падает!");   
            		enemyFaction.neutralPlayerKilledReputationChange();
            	}
            }
            
            
        }

        // if player is in faction
        if (isInFaction(player.getUniqueId(), main.factions)) {

            // if player is in land claimed by their faction
            double[] playerCoords = new double[2];
            playerCoords[0] = player.getLocation().getChunk().getX();
            playerCoords[1] = player.getLocation().getChunk().getZ();

            // check if land is claimed
            if (UtilitySubsystem.isClaimed(player.getLocation().getChunk(), main.claimedChunks))
            {
            	ClaimedChunk chunk = UtilitySubsystem.getClaimedChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(),
            			player.getLocation().getWorld().getName(), main.claimedChunks);
                // if holder is player's faction
                if (chunk.getHolder().equalsIgnoreCase(getPlayersFaction(player.getUniqueId(), main.factions).getName()) && getPlayersFaction(player.getUniqueId(), main.factions).getAutoClaimStatus() == false) {

                    // if not killed by another player
                    if (!(player.getKiller() instanceof Player)) {

                        // player keeps items
                        // event.setKeepInventory(true); // TODO: fix this duplicating items

                    }

                }
            }

        }

    }
}
