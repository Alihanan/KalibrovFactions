package Karavany;


import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import factionsystem.Objects.Faction;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class ConvoyFollowerTrait extends Trait{
	
	NPC targetNPC = null;
	Player attacker = null;
	int i = 0;
	final double NPC_FOLLOW_MINDIST = 0.0;
	final double NPC_FOLLOW_MAXDIST = 10.0;
	final int TICKRATE_CHECK = 10;
	Location offset;
	Faction myFaction;
	Player deathEnemy = null;
	
	public ConvoyFollowerTrait() {
		super("convoyFollowerTrait");
	}
	public ConvoyFollowerTrait(NPC np, Location offset, Faction f) {
		super("convoyFollowerTrait");
        targetNPC = np;    
        this.offset = offset;
        myFaction = f;
    }
	public void setFaction(Faction f) {
		myFaction = f;
	}
	public Faction getFaction() {
		return myFaction;
	}
	
	public void setDeathEnemy(Player enemy) {
		deathEnemy = enemy;
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		
		npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, false);
    	npc.data().setPersistent(NPC.TARGETABLE_METADATA, false);
    	npc.data().setPersistent(NPC.COLLIDABLE_METADATA, true);
	}
	
	
	@Override
	public void run(){
		if(!super.npc.isSpawned()) {
			return;
		}
		i++;
		Chunk c = npc.getStoredLocation().getChunk();
		if(!c.isLoaded()) {
			c.load();
		}	
		if(i%TICKRATE_CHECK != 0) {
			return;
		}
		i = 0;
		if(targetNPC == null) return;
		
		if(targetNPC.isSpawned()) {			
			/* IF CONVOY KILLED = BUG, SOME GUARDS COULDNT RECEIVE
			if(deathEnemy != null) {
				npc.getNavigator().setTarget(deathEnemy, true);
			}*/
			Location tNPCLoc = targetNPC.getEntity().getLocation().clone();
			if(targetNPC.getNavigator().isNavigating())
				tNPCLoc = targetNPC.getNavigator().getTargetAsLocation().clone();
			
			Location targetLoc = plus(tNPCLoc, offset);
			Location myLoc = npc.getEntity().getLocation();
			double dist = Norm2Distance(targetLoc, myLoc);
			
			if(dist > NPC_FOLLOW_MAXDIST) {
				attacker = null;
			}else if(attacker != null) {
				npc.getNavigator().setTarget(attacker, true);
				return;
			}
			
			
			Location finalLoc = null;
			if(dist > NPC_FOLLOW_MINDIST){
				Location direction = normalize(minus(targetLoc, myLoc));
				finalLoc = plus(myLoc, multiply(direction , dist - NPC_FOLLOW_MINDIST));
			}
			npc.getNavigator().setTarget(finalLoc);
		}
			
    	
	}
	private Location multiply(Location l, double scalar) {
		Location c = l;
		c.setX(l.getX() * scalar);
		c.setY(l.getY() * scalar);
		c.setZ(l.getZ() * scalar);
		return c;
	}
	private Location divide(Location l, double scalar) {
		return multiply(l, 1/scalar);
	}
	
	private Location plus(Location a, Location b) {
		Location c = a;
		c.setX(a.getX() + b.getX());
		c.setY(a.getY() + b.getY());
		c.setZ(a.getZ() + b.getZ());
		return c;
	}
	private Location minus(Location a, Location b) {
		Location c = a;
		c.setX(a.getX() - b.getX());
		c.setY(a.getY() - b.getY());
		c.setZ(a.getZ() - b.getZ());
		return c;
	}
	
	
	private Location normalize(Location loc) {
		Location nloc = loc;
		double n = Norm2(nloc);
		nloc.setX(nloc.getX()/n);
		nloc.setY(nloc.getY()/n);
		nloc.setZ(nloc.getZ()/n);
		return nloc;
	}
	private double Norm2(Location start) {
		double f1 = start.getX();
		f1 = f1 * f1;
		double f2 = start.getY();
		f2 = f2 * f2;
		double f3 = start.getZ();
		f3 = f3 * f3;
		
		return Math.sqrt(f1 + f2 + f3);
	}
	
	private double Norm2Distance(Location start, Location end) {
		double f1 = start.getX() - end.getX();
		f1 = f1 * f1;
		double f2 = start.getY() - end.getY();
		f2 = f2 * f2;
		double f3 = start.getZ() - end.getZ();
		f3 = f3 * f3;
		
		return Math.sqrt(f1 + f2 + f3);
	}
	
	public void setTargetNPC(NPC np) {
		targetNPC = np;
	}
	public void setTargetNPC(NPC np, Location offset) {
		targetNPC = np;
		this.offset = offset;
	}
	public void setOffset(Location offset) {
		this.offset = offset;
	}
	public Location getOffset() {
		return offset;
	}
	public NPC getTargetNPC() {
		return targetNPC;
	}
	
	public void defendYourself(Player damager) {
		if(attacker == null)
			attacker = damager;
	}	
	
	public Player getTargeted() {
		return attacker;
	}
	
}
