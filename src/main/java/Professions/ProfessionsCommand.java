package Professions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import factionsystem.Main;
import net.md_5.bungee.api.ChatColor;

public class ProfessionsCommand {

	Main main = null;

    public ProfessionsCommand(Main plugin) {
        main = plugin;
    }

    public void interpret(CommandSender sender, String label, String[] args) {
    	switch (args.length) {
    		case 0:
    			helpProfession(sender);    			
    			break;
    		case 1:
    			if(args[0].equalsIgnoreCase("progress")) {
    				getProgress(sender);
    			}
    			else if(args[0].equalsIgnoreCase("info")) {
    				getProfession(sender);
    			}
    			break;
    	}   	
    }
    private void getProgress(CommandSender sender) {
    	if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerProfession pp = main.professions.get(player.getUniqueId());
            
    		player.sendMessage(ChatColor.GREEN + "Твой прогресс по профессиям: ");
    		player.sendMessage(ChatColor.GREEN + "---------------------------------");
    		for(int i = 1; i < PlayerProfession.PROFESSION_NUMBER; i++) {
    			ChatColor cc = (pp.getProfessionProgress()[i-1] >= pp.PROFESSION_THRESHOLD[i]) 
    					? ChatColor.GREEN : ChatColor.RED;
    			player.sendMessage(ChatColor.GREEN + ((PlayerProfession.TYPE.values()[i]).getName()) + ": " 
    		+ cc + (pp.getProfessionProgress()[i-1]) + ChatColor.GREEN + "/"  + pp.PROFESSION_THRESHOLD[i]);
    		}
    		player.sendMessage(ChatColor.GREEN + "---------------------------------");
    	}
    }
    private void getProfession(CommandSender sender) {
    	if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerProfession pp = main.professions.get(player.getUniqueId());
    		player.sendMessage(ChatColor.GREEN + "Твоя профессия это: " + pp.getProfession().getName());
    	}
    }
    
    private void helpProfession(CommandSender sender) {
    	if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.GREEN + "---------Помощь по профессиям----------");
            player.sendMessage(ChatColor.GREEN + "\\prof info         - узнать свою профессию");
            player.sendMessage(ChatColor.GREEN + "\\prof progress  - посмотреть прогресс по профессиям");
            player.sendMessage(ChatColor.GREEN + "---------------------------------");
    	}
    	else {
    		System.out.println(ChatColor.GREEN + "");
    	}
    }
	
}
