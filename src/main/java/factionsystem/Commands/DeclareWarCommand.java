package factionsystem.Commands;

import factionsystem.Main;
import factionsystem.Objects.Faction;
import factionsystem.Objects.Faction.WarReputationStates;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static factionsystem.Subsystems.UtilitySubsystem.*;

public class DeclareWarCommand {

    Main main = null;

    public DeclareWarCommand(Main plugin) {
        main = plugin;
    }

    public void declareWar(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean owner = false;
            for (Faction faction : main.factions) {
                // if player is the owner or officer
                if (faction.isOwner(player.getUniqueId()) || faction.isOfficer(player.getUniqueId())) {
                    owner = true;
                    // if there's more than one argument
                    if (args.length > 1) {

                        // get name of faction
                        String factionName = createStringFromFirstArgOnwards(args);

                        // check if faction exists
                        for (int i = 0; i < main.factions.size(); i++) {
                            if (main.factions.get(i).getName().equalsIgnoreCase(factionName)) {

                                if (!(faction.getName().equalsIgnoreCase(factionName))) {

                                    // check that enemy is not already on list
                                    if (!(faction.isEnemy(factionName))) {

                                        // if trying to declare war on a vassal
                                        if (main.factions.get(i).hasLiege()) {

                                            // if faction is vassal of declarer
                                            if (faction.isVassal(factionName)) {
                                                player.sendMessage(ChatColor.RED + "You can't declare war on your own vassal!");
                                                return;
                                            }

                                            // if lieges aren't the same
                                            if (!main.factions.get(i).getLiege().equalsIgnoreCase(faction.getLiege())) {
                                                player.sendMessage(ChatColor.RED + "You can't declare war on this faction as they are a vassal! You must declare war on their liege " + main.factions.get(i).getLiege() + " instead!");
                                                return;
                                            }

                                        }

                                        // disallow if trying to declare war on liege
                                        if (faction.isLiege(factionName)) {
                                            player.sendMessage(ChatColor.RED + "You can't declare war on your liege! Try '/mf declareindependence' instead!");
                                            return;
                                        }

                                        // check to make sure we're not allied with this faction
                                        if (!faction.isAlly(factionName)) {

                                        	long lastWar = faction.getLastWarTime();
                                        	long current = System.currentTimeMillis();
                                        	long elapsedTime = current - lastWar;
                                        	if(elapsedTime < 86400000) {
                                        		player.sendMessage(ChatColor.RED + "Вы не можете объявлять войны/предлагать союзы чаще чем раз в 24 часа!");
                                                return;
                                        	}
                                        	faction.setLastWarTime(current);
                                        	
                                        	
                                            // add enemy to declarer's faction's enemyList and the enemyLists of its allies
                                            faction.addEnemy(factionName);
                                            if(main.factions.get(i).getWarReputation() <= -20) {
                                            	player.sendMessage(ChatColor.GREEN + "За войну против агрессора вы получили уважение!");
                                            	faction.antiAgressiveWar();
                                            }
                                            if(main.factions.get(i).getWarReputationState() == WarReputationStates.FRIEND) {
                                            	main.utilities.sendAllPlayersOnServerMessage(ChatColor.RED + faction.getName() + " объявила войну дружелюбной фракции и поэтому у неё отключен приват!");
                                            }
                                            
                                            // add declarer's faction to new enemy's enemyList
                                            main.factions.get(i).addEnemy(faction.getName());

                                            for (int j = 0; j < main.factions.size(); j++) {
                                                if (main.factions.get(j).getName().equalsIgnoreCase(factionName)) {
                                                    main.utilities.sendAllPlayersOnServerMessage(ChatColor.RED + faction.getName() + " has declared war against " + factionName + "!");
                                                }
                                            }

                                            // invoke alliances
                                            invokeAlliances(main.factions.get(i).getName(), faction.getName(), main.factions);
                                        }
                                        else {
                                            player.sendMessage(ChatColor.RED + "You can't declare war on your ally!");
                                        }

                                    }
                                    else {
                                        player.sendMessage(ChatColor.RED + "Your faction is already at war with " + factionName);
                                    }

                                }
                                else {
                                    player.sendMessage(ChatColor.RED + "You can't declare war on your own faction.");
                                }
                            }
                        }

                    }
                    else {
                        player.sendMessage(ChatColor.RED + "Usage: /mf declarewar (faction-name)");
                    }
                }
            }
            if (!owner) {
                player.sendMessage(ChatColor.RED + "You have to own a faction or be an officer of a faction to use this command.");
            }
        }
    }
}
