package Professions;

import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import factionsystem.Main;

public class HorsePenaltyEvent {
	
	Main main = null;	
	
    public HorsePenaltyEvent(Main plugin) {
        main = plugin;
    }
	
	
	@EventHandler
	public void onPlayerInteractEntity (PlayerInteractEntityEvent event) {
	    Entity entity = event.getRightClicked();
	    Player player = event.getPlayer();

	    float pitch = player.getLocation().getPitch();
	    float yaw = player.getLocation().getYaw();

	    if (entity instanceof AbstractHorse) {
	        event.setCancelled(true);

	        Location loc = player.getLocation();
	        loc.setPitch(pitch);
	        loc.setYaw(yaw);
	        player.teleport(loc);

	        AbstractHorse horse = (AbstractHorse) event.getRightClicked();
	        player.sendMessage(horse.getName());
	    }
	}
}
