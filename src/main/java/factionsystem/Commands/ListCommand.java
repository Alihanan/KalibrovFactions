package factionsystem.Commands;

import factionsystem.Main;
import factionsystem.Objects.Faction;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import static factionsystem.Subsystems.UtilitySubsystem.getChunksClaimedByFaction;

public class ListCommand {

    Main main = null;

    public ListCommand(Main plugin) {
        main = plugin;
    }

    public boolean listFactions(CommandSender sender) {
        // if there aren't any factions
        if (main.factions.size() == 0) {
            sender.sendMessage(ChatColor.AQUA + "There are currently no factions.");
        }
        // factions exist, list them
        else {
            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + " == Factions" + " == ");
            listFactionsWithFormatting(sender);
        }
        return true;
    }

    public void listFactionsWithFormatting(CommandSender sender) {
//        sender.sendMessage(ChatColor.AQUA + String.format("%-20s %10s %12s %10s", "Name", "Power", "Population", "Land"));
        sender.sendMessage(ChatColor.AQUA + "P: power, M: members, L: land");
        sender.sendMessage(ChatColor.AQUA + "-----");
        for (Faction faction : main.utilities.getFactionsSortedByPower()) {
        	int rep = faction.getWarReputation();
        	ChatColor cc;
        	if(rep >= 40){
        		cc = ChatColor.GREEN;
        	}        		
        	else if(rep >= 20){
        		cc = ChatColor.DARK_GREEN;
        	}        		
        	else if(rep <= -40) {
        		cc = ChatColor.RED;
        	}       		
        	else if(rep <= -20){
        		cc = ChatColor.DARK_RED;
        	}       		
        	else {
        		cc = ChatColor.WHITE;
        	}       		
            sender.sendMessage(cc + String.format("%-25s %10s %10s %10s", faction.getName(), "Сила власти: " + faction.getCumulativePowerLevel(), "Кол-в членов: " + faction.getPopulation(), "Чанков Земли: " + getChunksClaimedByFaction(faction.getName(), main.claimedChunks)));
        }
    }
}
