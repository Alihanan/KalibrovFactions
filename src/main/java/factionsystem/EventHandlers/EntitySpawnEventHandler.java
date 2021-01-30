package factionsystem.EventHandlers;

import factionsystem.Main;
import factionsystem.Subsystems.UtilitySubsystem;
import factionsystem.Objects.ClaimedChunk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntitySpawnEvent;

import static factionsystem.Subsystems.UtilitySubsystem.getPlayersFaction;

public class EntitySpawnEventHandler {

    Main main = null;

    public EntitySpawnEventHandler(Main plugin) {
        main = plugin;
    }

    public void handle(EntitySpawnEvent event) {

        int x = 0;
        int z = 0;

        x = event.getEntity().getLocation().getChunk().getX();
        z = event.getEntity().getLocation().getChunk().getZ();

        // check if land is claimed
        if (UtilitySubsystem.isClaimed(event.getLocation().getChunk(), main.claimedChunks))
        {
            if (event.getEntity() instanceof Monster && !main.getConfig().getBoolean("mobsSpawnInFactionTerritory")) {
                event.setCancelled(true);
            }
        }
    }

}
