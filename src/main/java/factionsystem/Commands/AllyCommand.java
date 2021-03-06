package factionsystem.Commands;

import factionsystem.Main;
import factionsystem.Objects.Faction;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static factionsystem.Subsystems.UtilitySubsystem.*;

public class AllyCommand {

    Main main = null;

    public AllyCommand(Main plugin) {
        main = plugin;
    }

    public void requestAlliance(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (isInFaction(player.getUniqueId(), main.factions)) {
                Faction playersFaction = getPlayersFaction(player.getUniqueId(), main.factions);

                if (playersFaction.isOwner(player.getUniqueId()) || playersFaction.isOfficer(player.getUniqueId())) {

                    // player is able to do this command

                    if (args.length > 1) {
                        String targetFactionName = createStringFromFirstArgOnwards(args);
                        Faction targetFaction = getFaction(targetFactionName, main.factions);

                        if (!playersFaction.getName().equalsIgnoreCase(targetFactionName)) {

                            if (targetFaction != null) {

                                if (!playersFaction.isAlly(targetFactionName)) {
                                    // if not already ally

                                    if (!playersFaction.isRequestedAlly(targetFactionName)) {
                                        // if not already requested

                                        if (!playersFaction.isEnemy(targetFactionName)) {
                                        	long current = System.currentTimeMillis();
                                        	// No timer check if responds
                                        	if (!targetFaction.isRequestedAlly(playersFaction.getName())) {
                                                	long lastWar = playersFaction.getLastWarTime();                                               	
                                                	long elapsedTime = current - lastWar;
                                                	if(elapsedTime < 86400000) {
                                                		player.sendMessage(ChatColor.RED + "Вы не можете объявлять войны/предлагать союзы чаще чем раз в 24 часа!");
                                                        return;
                                                	}
                                                	
                                       		}          
                                        	playersFaction.setLastWarTime(current);
                                        	
                                            playersFaction.requestAlly(targetFactionName);
                                            player.sendMessage(ChatColor.GREEN + "Attempted to ally with " + targetFactionName);

                                            sendAllPlayersInFactionMessage(targetFaction,ChatColor.GREEN + "" + playersFaction.getName() + " has attempted to ally with " + targetFactionName + "!");

                                            if (playersFaction.isRequestedAlly(targetFactionName) && targetFaction.isRequestedAlly(playersFaction.getName())) {
                                                // ally factions
                                                playersFaction.addAlly(targetFactionName);
                                                playersFaction.signAllianceReputationChange();
                                                
                                                getFaction(targetFactionName, main.factions).addAlly(playersFaction.getName());
                                                playersFaction.signAllianceReputationChange();
                                                player.sendMessage(ChatColor.GREEN + "Your faction is now allied with " + targetFactionName + "!");
                                                sendAllPlayersInFactionMessage(targetFaction, ChatColor.GREEN + "Your faction is now allied with " + playersFaction.getName() + "!");
                                            }
                                        }
                                        else {
                                            player.sendMessage(ChatColor.RED + "That faction is currently your enemy! Make peace before trying to ally with them.");
                                        }

                                    }
                                    else {
                                        player.sendMessage(ChatColor.RED + "You've already requested an alliance with this faction!");
                                    }

                                }
                                else {
                                    player.sendMessage(ChatColor.RED + "That faction is already your ally!");
                                }
                            }
                            else {
                                player.sendMessage(ChatColor.RED + "That faction wasn't found!");
                            }
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "You can't ally with your own faction?");
                        }

                    }
                    else {
                        player.sendMessage(ChatColor.RED + "Usage: /mf ally (faction-name)");
                    }

                }
                else {
                    player.sendMessage(ChatColor.RED + "You need to be the owner of a faction or an officer of a faction to use this command.");
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "You need to be in a faction to use this command.");
            }
        }
    }
}
