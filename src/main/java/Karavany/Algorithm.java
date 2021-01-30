package Karavany;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;

import factionsystem.Main;
import factionsystem.Objects.Faction;
import net.citizensnpcs.commands.NPCCommands;

public class Algorithm {	
	
	public static void CheckNetworkForHoles(Faction f) {
		ArrayList<TradePoint> tps = new ArrayList<TradePoint>();
		tps.add(f.getTradePoint());
		tps.addAll(f.getTradePosts());
		
		
		ArrayList<TradePoint> connectedMain = new ArrayList<TradePoint>();	
		ArrayList<TradePoint> visited = new ArrayList<TradePoint>();
		TradePoint main = f.getTradePoint();
		connectedMain.add(main);
		visited.add(main);
		
		while(connectedMain.size() > 0) {
			TradePoint last = connectedMain.get(0);
			connectedMain.remove(last);
			for(TradePoint neight : last.neightbours) {
				if(!visited.contains(neight)) {
					connectedMain.add(neight);
					visited.add(neight);
				}
			}
		}
		
		tps.removeAll(visited);
		for(TradePoint rmv : tps) {
			f.deletePostNoConnected(rmv);
		}
		
	}
	
	
	
	public static void RemoveUnconnectedTradePoints(TradePoint removePoint, TradePoint mainPoint, Faction f) {		
		ArrayList<TradePoint> neights = new ArrayList<>();
		for(TradePoint tp : removePoint.neightbours) {
			neights.add(tp);
			tp.neightbours.remove(removePoint);
			mainPoint.neightbours.remove(tp);
		}			
		
		while(neights.size() > 0) {
			ArrayList<TradePoint> connected = new ArrayList<>();
			// POP
			TradePoint someTP = neights.get(0); 
			neights.remove(someTP);	
			
			connected.add(someTP);
			int i = 0;
			// Find all connected
			while(true) {
				if(i >= connected.size()) break;
				TradePoint last = connected.get(i);
				for(TradePoint n : last.neightbours) {
					if(!connected.contains(n)) {
						connected.add(n);
					}
					if(neights.contains(n)) {
						neights.remove(n);
					}
				}
				i++;
			}
			// If not connected to main - delete all
			if(!connected.contains(mainPoint)) {
				for(TradePoint p : connected) {
					f.deletePostNoConnected(p);
				}
			}
		}
	}
	
	public static ArrayList<TradePoint> findPath(Faction one, Faction two){
		ArrayList<TradePoint> path = new ArrayList<TradePoint>();
		TradePoint start_one = one.getTradePoint();
		TradePoint start_two = two.getTradePoint();
		
		ArrayList<TradePoint> neights_one = new ArrayList<TradePoint>();
		neights_one.addAll(one.getTradePosts());
		neights_one.add(start_one);
		Collections.shuffle(neights_one);
		
		ArrayList<TradePoint> neights_two = new ArrayList<TradePoint>();
		neights_two.addAll(two.getTradePosts());
		neights_two.add(start_two);
		
		Collections.shuffle(neights_two);
		
		for(TradePoint tp1 : neights_one) {
			for(TradePoint tp2 : neights_two) {
				if(tp1.equals(tp2)) {
					System.out.println(ChatColor.RED + "Found equal!");
					continue;
				}
				double dist = NPCConvoyTrait.Norm2Distance(tp1.getLocation(), tp2.getLocation());
				// There is a connection!
				if(dist <= ConvoySubsystem.CARAVAN_MAX_DISTANCE) {					
					// Find paths inside factions
					ArrayList<TradePoint> path_one = findPathInFaction(start_one, tp1);
					ArrayList<TradePoint> path_two = findPathInFaction(start_two, tp2);
					
					if(!path_one.contains(tp1)) {						
						//one.deleteAllTradePosts();
						//one.deletePostNoConnected(tp1);
						continue;
					}
					if(!path_two.contains(tp2)) {
						//two.deleteAllTradePosts();
						continue;
					}
					
					// Combine them into 1 path
					for(int i = 0; i < path_one.size(); i++) {
						path.add(path_one.get(i));
					}
					for(int i = 0; i < path_two.size(); i++) {
						path.add(path_two.get(path_two.size() - i - 1));
					}	
					return path;
				}
				
			}
		}				
		
		return path;
	}
	
	public static void printTP(TradePoint inter) {
		int x = inter.getLocation().getBlockX();
		int y = inter.getLocation().getBlockY();
		int z = inter.getLocation().getBlockZ();
		System.out.println(
				ChatColor.RED + "" + "[" + x + ", " + y + ", "+ z + "]");
	}
	
	public static ArrayList<TradePoint> findPathInFaction(TradePoint main, TradePoint target){
		ArrayList<TradePoint> path = new ArrayList<TradePoint>();
		
		if(main.equals(target)) {
			path.add(main);
			return path;
		}
		
		ArrayList<TradePoint> visited = new ArrayList<TradePoint>();
		ArrayList<TradePoint> current = new ArrayList<TradePoint>();
		visited.add(main);
		current.add(main);
		HashMap<TradePoint, TradePoint> backtrack = new HashMap<TradePoint, TradePoint>();
		while(current.size() > 0) {
			TradePoint tp = current.get(0);
			current.remove(tp);
			
			if(tp.equals(target)) {
				// Create path
				ArrayList<TradePoint> prevs = new ArrayList<TradePoint>();
				TradePoint curr = tp;

				while(true) {
					prevs.add(curr);
					if(curr.equals(main)) {
						break;
					}
					TradePoint prev = backtrack.get(curr);
					curr = prev;					
				}
				Collections.reverse(prevs);
				path.addAll(prevs);				
				
				break;
			}
			
			ArrayList<TradePoint> ns = tp.neightbours;
			for(TradePoint nt : ns) {
				if(!visited.contains(nt)) {
					visited.add(nt);
					current.add(nt);
					backtrack.put(nt, tp);
				}
			}
			
		}
		
		
		return path;
	}
}
