package Karavany;

import static factionsystem.Subsystems.UtilitySubsystem.createStringFromFirstArgOnwards;
import static factionsystem.Subsystems.UtilitySubsystem.getClaimedChunk;
import static factionsystem.Subsystems.UtilitySubsystem.getFaction;
import static factionsystem.Subsystems.UtilitySubsystem.getPlayersFaction;
import static factionsystem.Subsystems.UtilitySubsystem.isClaimed;
import static factionsystem.Subsystems.UtilitySubsystem.isInFaction;
import static factionsystem.Subsystems.UtilitySubsystem.sendAllPlayersInFactionMessage;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramEntityController;
import com.sainttx.holograms.api.HologramPlugin;
import com.sainttx.holograms.api.entity.HologramEntity;
import com.sainttx.holograms.api.line.HologramLine;
import com.sainttx.holograms.commands.HologramCommands;
import com.sainttx.holograms.nms.v1_12_R1.HologramEntityControllerImpl;

import factionsystem.Main;
import factionsystem.Objects.ClaimedChunk;
import factionsystem.Objects.Faction;

public class TradePointCommand {
	Main main = null;

    public TradePointCommand(Main plugin) {
        main = plugin;
    }

    public void handleCommand(CommandSender sender, String[] args) {
    	if(args.length == 1) {
    		helpTradePoint(sender);
    	}else if(args.length >= 2) {
    		if (args[1].equalsIgnoreCase("create")) {
    			createTradePoint(sender, args);
    		}
    		else if (args[1].equalsIgnoreCase("remove")) {
    			removeTradePoint(sender, args);
    		}
    		else if (args[1].equalsIgnoreCase("info")) {
    			InfoTradePoint(sender, args);
    		}
    		else if (args[1].equalsIgnoreCase("ally")) {
    			allyTradePoint(sender, args);
    		}
    		else if (args[1].equalsIgnoreCase("removeally")) {
    			System.out.println("Removing trade ally");
    			removeTradeAlliance(sender, args);
    		}
    		else if(args[1].equalsIgnoreCase("addpost")) {
    			createPostPoint(sender);
    		}
    		else if(args[1].equalsIgnoreCase("removepost")) {
    			removePostPoint(sender);
    		}
    		else {
    			Player player = (Player) sender;
    			player.sendMessage(ChatColor.RED + "Неизвестная команда для торговых путей!");
    			helpTradePoint(sender);
    		}
    	}
    }
    private void helpTradePoint(CommandSender sender) {
    	if (sender instanceof Player) {
    		Player player = (Player) sender;
    		//+ "вы не можете объявить войну торговому партнеру!\n"
    		//Ставьте точку правильно! Караван может застрять!\n
        	player.sendMessage(ChatColor.AQUA + "=== Механика торговых путей ===\n");        	
        	player.sendMessage(ChatColor.RED + "Торговый альянс" + ChatColor.AQUA + " = позволяет отправлять караваны и торговать между фракциями.");
        	player.sendMessage(ChatColor.AQUA + "/mf trp ally   = предложить торговый альянс\n");
        	player.sendMessage(ChatColor.AQUA + "/mf trp removeally = разорвать торговый альянс\n");
        	player.sendMessage(ChatColor.AQUA + "/mf trp info   = указать текущее состояние торговой точки и аванпостов\n");      	
        	player.sendMessage(ChatColor.RED + "Торговая точка" + ChatColor.AQUA + " = место спавна караванов, "
        			+ "обязана быть в ваших владениях.");
        	player.sendMessage(ChatColor.AQUA + "При создании точки вы должны смотреть на сундук в 1 блоке от вас! Он содержит ресурсы, которые вы отправляете/получаете от караванов!");
        	player.sendMessage(ChatColor.AQUA + "/mf trp create = создать торговую точку вашей фракции\n");
        	player.sendMessage(ChatColor.AQUA + "/mf trp remove = удалить торговую точку и все аванпосты вашей фракции\n");
        	player.sendMessage(ChatColor.RED + "Аванпост" + ChatColor.AQUA + " = точка для маршрута каравана, может быть и в незанятых владениях(чанке). "
        			+ "Каждый каравана ходит от Точки к точке в радиусе " + (int)ConvoySubsystem.CARAVAN_MAX_DISTANCE + " блоков. Для дальних походов создавайте сеть точек!\n");
        	player.sendMessage(ChatColor.AQUA + "/mf trp addpost = создать торговый аванпост в текущем чанке.\n");
        	player.sendMessage(ChatColor.AQUA + "/mf trp removepost = удалить торговый аванпост в точке, где вы стоите сейчас.\n");
    	}   	   	
    }
    public void createPostPoint(CommandSender sender) {
    	if (sender instanceof Player)
		{
			Player player = (Player) sender;			
			if (isInFaction(player.getUniqueId(), main.factions)) {
				Faction playersFaction = getPlayersFaction(player.getUniqueId(), main.factions);
				
				Chunk chunk1 = player.getLocation().getChunk();
				
				if (playersFaction.getTradePoint() != null) {
					if (playersFaction.isOwner(player.getUniqueId()) || playersFaction.isOfficer(player.getUniqueId())) {
						boolean isFree = true;
						if(isClaimed(chunk1, main.claimedChunks)) {
							 ClaimedChunk chunk = getClaimedChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld().getName(), main.claimedChunks);
	                        if (!chunk.getHolder().equalsIgnoreCase(playersFaction.getName())) {
	                        	isFree = false;
	                        }
						}
						if (isFree) {
                        	
                        	int maxTPNum = playersFaction.getMemberList().size() / 2 + 3;
                        	if(playersFaction.getTradePosts().size() >= maxTPNum) {
                        		player.sendMessage(ChatColor.RED + "Максимум аванпостов поставлено! Добавьте еще членов во фракцию!");
                        		return;
                        	}     
                        	if(main.allTradePosts.containsKey(chunk1)) {
                        		player.sendMessage(ChatColor.RED + "Этот чанк содержит чей-то аванпост/Торг.точку!");
                        		return;
                        	}	
                        	
                        	
                        	TradePoint mainPoint = playersFaction.getTradePoint();
                        	Location pLoc = player.getLocation();
                        	TradePoint tp = new TradePoint(player.getLocation());
                        	tp.factionName = playersFaction.getName();
                        	boolean isNearAnything = false;
                        	double minDist = Double.MAX_VALUE;
                        	
                        	double distMain = NPCConvoyTrait.Norm2Distance(pLoc, mainPoint.getLocation());                        	
                        	if(distMain <= ConvoySubsystem.CARAVAN_MAX_DISTANCE) {
                        		isNearAnything = true;
                        		mainPoint.neightbours.add(tp);
                        		tp.neightbours.add(mainPoint);
                        	}
                        	if(distMain < minDist) minDist = distMain;
                        	ArrayList<TradePoint> otherTP = playersFaction.getTradePosts();
                        	for(TradePoint ttp : otherTP) {
                        		Location ttpLoc = ttp.getLocation();
                        		double dist = NPCConvoyTrait.Norm2Distance(pLoc, ttpLoc);                        	
                            	if(dist <= ConvoySubsystem.CARAVAN_MAX_DISTANCE) {
                            		isNearAnything = true;
                            		ttp.neightbours.add(tp);
                            		tp.neightbours.add(ttp);
                            	}
                            	if(dist < minDist) minDist = dist;
                        	}                        	
                        	if(!isNearAnything) {
                        		player.sendMessage(String.format(ChatColor.RED + "Слишком далеко от всех аванпостов! "
                        				+ "Ближайшая точка(ТП или Аван.) в %.1f блоках!", minDist));
                        		return;
                        	}                        	
                                            	
                        	
                            playersFaction.addTradePost(tp); 
                            
                            
                            player.sendMessage(String.format(ChatColor.GREEN + "Успешно создан аванпост! "
                            		+ "Ближайшая точка(ТП или Аван.) в %.1f блоках!", minDist));
                            
                            Location hLoc = tp.getLocation().clone();
                            hLoc.setY(hLoc.getY() + 1.5);
                            
                            Hologram holo = new Hologram("tPost_" + tp.factionName + "_" + playersFaction.getTradePosts().size(),
                            		hLoc, true);
                            HologramLine line = main.hologram.parseLine(holo, "Аванпост");
                            HologramLine line2 = main.hologram.parseLine(holo, playersFaction.getName());
                            holo.addLine(line);		
                            holo.addLine(line2);	
                            holo.spawn();
                            //main.allHolos.put(hLoc.getChunk(), holo);
                            
                            //main.hologram.getHologramManager().addActiveHologram(holo);
                            //main.hologram.getHologramManager().saveHologram(holo);	
                            
                            tp.hologram = holo;
	                    }
	                    else {
	                        player.sendMessage(ChatColor.RED + "Этот чанк не является вашими/свободными владениями!");
	                    }
					}
					else {
						player.sendMessage(ChatColor.RED + "Только владелец и офицер могут создавать аванпосты!");
					}
				}else {
					player.sendMessage(ChatColor.RED + "Ваша фракция не имеет торговую точку!");
				}
			}
			else {
				player.sendMessage(ChatColor.RED + "Вы не можете создать торговые точки без фракции!");
			}	    	
		}		
    	return;   	
    	
    }
    public void removePostPoint(CommandSender sender) {
    	if (sender instanceof Player)
		{
    		Player player = (Player) sender;			
			if (isInFaction(player.getUniqueId(), main.factions)) {
				Faction playersFaction = getPlayersFaction(player.getUniqueId(), main.factions);
				if (playersFaction.getTradePoint() != null) {
					if (playersFaction.isOwner(player.getUniqueId()) || playersFaction.isOfficer(player.getUniqueId())) {
						if(playersFaction.getTradePosts().size() == 0) {
							player.sendMessage(ChatColor.RED + "У вашей фракции нет аванпостов!");
							return;
						}
						
						Location loc = player.getLocation();
						boolean b = playersFaction.removeTradePosts(loc);
						if(!b) {
							player.sendMessage(ChatColor.RED + "В этом чанке нет аванпоста!");
							return;
						}
                        player.sendMessage(ChatColor.GREEN + "Успешно удалена торговая точка и все аванпосты!");
					}
					else {
						player.sendMessage(ChatColor.RED + "Только владелец и офицер могут удалять аванпосты!");
					}
				}else {
					player.sendMessage(ChatColor.RED + "Ваша фракция не имеет торговую точку и аванпосты!");
				}
			}
			else {
				player.sendMessage(ChatColor.RED + "Вы не можете удалить аванпост без фракции!");
			}
		}
    }
    
    public void createTradePoint(CommandSender sender, String[] args) {  
		if (sender instanceof Player)
		{
			Player player = (Player) sender;			
			if (isInFaction(player.getUniqueId(), main.factions)) {
				Faction playersFaction = getPlayersFaction(player.getUniqueId(), main.factions);
				if (playersFaction.getTradePoint() == null) {
					if (playersFaction.isOwner(player.getUniqueId()) || playersFaction.isOfficer(player.getUniqueId())) {
						 if (isClaimed(player.getLocation().getChunk(), main.claimedChunks)) {
		                        ClaimedChunk chunk = getClaimedChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld().getName(), main.claimedChunks);
		                        if (chunk.getHolder().equalsIgnoreCase(playersFaction.getName())) {
		                        	if(main.allTradePosts.containsKey(player.getLocation().getChunk())) {
		                        		player.sendMessage(ChatColor.RED + "Этот чанк содержит чей-то аванпост/Торг.точку!");
		                        		return;
		                        	}		                        	
		                        	
		                        	Location pLoc = player.getLocation();
		                        	TradePoint tp = new TradePoint(player.getLocation());
		                            
		                            		                            
		                            Block nextB = player.getTargetBlock(null, 1);

		                            if(nextB.getType() == Material.CHEST) {
		                            	Chest chest = (Chest)nextB.getState();
		                            	if(!chest.getChunk().equals(tp.getLocation().getChunk())) {
		                            		player.sendMessage(ChatColor.RED + "Сундук должен быть том же чанке что и Торг.точка!");
		                            		return;
		                            	}
		                            	/*for(ItemStack is : chest.getBlockInventory().getContents()) {
		                            		if(is == null) continue;
		                            		System.out.println(ChatColor.AQUA + "Type:" + is.getType() 
		                            		+ ", amount:" + is.getAmount());
		                            	}
		                            	*/
		                            	main.tradeChests.add(chest.getBlock());
		                            	tp.setChest(chest);
		                            	
		                            	
		                            	
		                            }
		                            else {
		                            	Block possibleB = player.getTargetBlock(null, 200);
		                            	if(possibleB.getType() == Material.CHEST) {
		                            		player.sendMessage(ChatColor.RED + "Сундук должен быть в досягаемости 1 блока от торг. точки!");
		                            	}else {
		                            		player.sendMessage(ChatColor.RED + "Вы должны смотреть на сундук для создания торговой точки!");
		                            	}	
		                        		return;
		                            }
		                            playersFaction.SetTradePoint(tp);
		                            tp.factionName = playersFaction.getName();
		                            player.sendMessage(ChatColor.GREEN + "Успешно создана торговая точка!");
		                            
		                            Biome biome = tp.getLocation().getBlock().getBiome();
		                            Culture cul = CaravanCultureManager.getTypeByBiome(biome);
		                            if(cul == null) {
		                            	player.sendMessage(ChatColor.RED + "В этом биоме не живут люди! Вы не сможете отправлять караваны, только получать!");
		                            }else {
		                            	player.sendMessage(ChatColor.GREEN + "В этом чанке живет культурная группа " 
		                            			+ cul.name + "!");
		                            }
		                            
		                            Location hLoc = tp.getLocation().clone();
		                            hLoc.setY(hLoc.getY() + 1.5);
		                            
		                            Hologram holo = new Hologram("tradepoint_" + tp.factionName, hLoc, true);
		                            HologramLine line = main.hologram.parseLine(holo, "Торговая Точка");
		                            HologramLine line2 = main.hologram.parseLine(holo, playersFaction.getName());
		                            holo.addLine(line);		
		                            holo.addLine(line2);
		                            holo.spawn();
		                            
		                            
		                            //main.hologram.getHologramManager().addActiveHologram(holo);
		                            //main.hologram.getHologramManager().saveHologram(holo);
		                            tp.hologram = holo;
		                        }
		                        else {
		                        	player.sendMessage(ChatColor.RED + "Этот чанк не является вашими владениями!");
		                        }
		                    }
		                    else {
		                        player.sendMessage(ChatColor.RED + "Этот чанк не является владениями!");
		                    }
					}
					else {
						player.sendMessage(ChatColor.RED + "Только владелец и офицер могут создавать торговые точки!");
					}
				}else {
					player.sendMessage(ChatColor.RED + "Ваша фракция уже имеет торговую точку!");
				}
			}
			else {
				player.sendMessage(ChatColor.RED + "Вы не можете создать торговые точки без фракции!");
			}	    	
		}		
    	return;   	
    }
    public void removeTradePoint(CommandSender sender, String[] args) {
    	if (sender instanceof Player)
		{
    		Player player = (Player) sender;			
			if (isInFaction(player.getUniqueId(), main.factions)) {
				Faction playersFaction = getPlayersFaction(player.getUniqueId(), main.factions);
				if (playersFaction.getTradePoint() != null) {
					if (playersFaction.isOwner(player.getUniqueId()) || playersFaction.isOfficer(player.getUniqueId())) {
						playersFaction.DeleteTradePoint();
                        player.sendMessage(ChatColor.GREEN + "Успешно удалена торговая точка и все аванпосты!");
					}
					else {
						player.sendMessage(ChatColor.RED + "Только владелец и офицер могут удалять торговые точки!");
					}
				}else {
					player.sendMessage(ChatColor.RED + "Ваша фракция не имеет торговую точку!");
				}
			}
			else {
				player.sendMessage(ChatColor.RED + "Вы не можете удалить торговые точки без фракции!");
			}
		}
    }
    public void InfoTradePoint(CommandSender sender, String[] args) {
    	if (sender instanceof Player) {
    		Player player = (Player) sender;			
			if (isInFaction(player.getUniqueId(), main.factions)) {
				Faction playersFaction = getPlayersFaction(player.getUniqueId(), main.factions);
				if (playersFaction.getTradePoint() != null) {
					TradePoint tp = playersFaction.getTradePoint();
					Location loc = tp.getLocation();
					player.sendMessage(ChatColor.GREEN + "=== Ваша фракция держит торговую точку ===\n");
					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					player.sendMessage(ChatColor.GREEN + "Позиция: [" 
					+ x + ", " + y + ", " + z + "]\n");
					long elapsed = System.currentTimeMillis() - tp.getLastConvoy();
					
					long lastConvTime = ConvoySubsystem.CARAVAN_COOLDOWN - elapsed;
					String lastConvoy;
					if(lastConvTime <= 0) {
						lastConvoy = "Вот вот отправиться!";
					}else {
						long hours = TimeUnit.MILLISECONDS.toHours(lastConvTime);
						long minutes = TimeUnit.MILLISECONDS.toMinutes(lastConvTime) -
								TimeUnit.HOURS.toMinutes(hours);
						long seconds = TimeUnit.MILLISECONDS.toSeconds(lastConvTime) - 
								- TimeUnit.HOURS.toSeconds(hours) 
								- TimeUnit.MINUTES.toSeconds(minutes);
						lastConvoy = String.format("%d часов, %d минут, %d секунд", 
				    		   hours, minutes, seconds
				    		);
					}
					player.sendMessage(ChatColor.GREEN + "Следующий караван через: " +
							lastConvoy + "\n");
					player.sendMessage(ChatColor.GREEN + "Ваши торговые партнёры: " +
							playersFaction.getTradeAlliesSeparatedByCommas() + "\n");
					int maxTPNum = playersFaction.getMemberList().size() / 2 + 1;
					player.sendMessage(ChatColor.GREEN + "Поставлено аванпостов: " +
							playersFaction.getTradePosts().size() + " / " + maxTPNum + "\n");
					int i = 0;
					for(TradePoint tpp : playersFaction.getTradePosts()) {
						int x1 = tpp.getLocation().getBlockX();
						int y1 = tpp.getLocation().getBlockY();
						int z1 = tpp.getLocation().getBlockZ();
						player.sendMessage(ChatColor.GREEN + "" + i + " Позиция: [" 
						+ x1 + ", " + y1 + ", " + z1 + "]\n");
						i++;
					}
					
				}else {
					player.sendMessage(ChatColor.RED + "Ваша фракция не имеет торговую точку!");
				}
			}
			else {
				player.sendMessage(ChatColor.RED + "Вы не состоите во фракции!");
			}	
    	}      	
    	return;   	
    }
    
    private void allyTradePoint(CommandSender sender, String[] args) {
    	//sendAllPlayersInFactionMessage(targetFaction,ChatColor.GREEN + "" + playersFaction.getName() + " has attempted to ally with " + targetFactionName + "!");
    	if (sender instanceof Player) {
		 Player player = (Player) sender;
		
		 if (isInFaction(player.getUniqueId(), main.factions)) {
		     Faction playersFaction = getPlayersFaction(player.getUniqueId(), main.factions);
		
		     if (playersFaction.isOwner(player.getUniqueId()) || playersFaction.isOfficer(player.getUniqueId())) {
		
		         // player is able to do this command
		
		         if (args.length > 2) {
		        	 String[] tmpArgs = {"", args[2]};
		             String targetFactionName = createStringFromFirstArgOnwards(tmpArgs);
		             Faction targetFaction = getFaction(targetFactionName, main.factions);
		
		             if (!playersFaction.getName().equalsIgnoreCase(targetFactionName)) {
		
		                 if (targetFaction != null) {
		
		                     if (!playersFaction.isTradeAlly(targetFactionName)) {
		                         // if not already ally
		
		                         if (!playersFaction.isRequestedTradeAlly(targetFactionName)) {
		                             // if not already requested
		
		                             if (!playersFaction.isEnemy(targetFactionName)) {
		                             	long current = System.currentTimeMillis();
		                             	// No timer check if responds
		                             	/*
		                             	if (!targetFaction.isRequestedTradeAlly(playersFaction.getName())) {
		                                     	long lastWar = playersFaction.getLastWarTime();                                               	
		                                     	long elapsedTime = current - lastWar;
		                                     	if(elapsedTime < 86400000) {
		                                     		player.sendMessage(ChatColor.RED + "Вы не можете объявлять войны/предлагать союзы чаще чем раз в 24 часа!");
		                                             return;
		                                     	}
		                                     	
		                            		}          
		                             	playersFaction.setLastWarTime(current);*/
		                             	
		                                 playersFaction.requestTradeAlly(targetFactionName);
		                                 player.sendMessage(ChatColor.GREEN + "Вы отправили приглашение в торговый альянс фракции " + targetFactionName);
		
		                                 sendAllPlayersInFactionMessage(targetFaction,ChatColor.GREEN + "" + playersFaction.getName() 
		                                 + " предложили торговый альянс!\n(Чтобы принять: /mf trp ally " 
		                                		 + playersFaction.getName() + ")");
		
		                                 if (playersFaction.isRequestedTradeAlly(targetFactionName) && targetFaction.isRequestedTradeAlly(playersFaction.getName())) {
		                                     // ally factions
		                                     playersFaction.addTradeAlly(targetFactionName);
		                                     playersFaction.signAllianceReputationChange();
		                                     
		                                     getFaction(targetFactionName, main.factions).addTradeAlly(playersFaction.getName());
		                                     playersFaction.signAllianceReputationChange();
		                                     player.sendMessage(ChatColor.GREEN + "Ваша фракция отныне является торговым партнером фракции " + targetFactionName + "!");
		                                     sendAllPlayersInFactionMessage(targetFaction, ChatColor.GREEN + "Ваша фракция отныне является торговым партнером фракции " + playersFaction.getName() + "!");
		                                 }
		                             }
		                             else {
		                                 player.sendMessage(ChatColor.RED + "Вы воюете с этой фракцией! Какой к черту торговый альянс!");
		                             }
		
		                         }
		                         else {
		                             player.sendMessage(ChatColor.RED + "Вы уже отправили им запрос на торговый альянс!");
		                         }
		
		                     }
		                     else {
		                         player.sendMessage(ChatColor.RED + "Вы уже являетесь членами торгового альянса!");
		                     }
		                 }
		                 else {
		                     player.sendMessage(ChatColor.RED + "Такой фракции не существует!");
		                 }
		             }
		             else {
		                 player.sendMessage(ChatColor.RED + "С самим собой нельзя иметь торговый альянс!");
		             }
		
		         }
		         else {
		             player.sendMessage(ChatColor.RED + "Используй: /mf trp ally (название-фракции)");
		         }
		
		     }
		     else {
		         player.sendMessage(ChatColor.RED + "Только лидер и офицеры могут заключить торговый альянс!");
		     }
		 }
		 else {
		     player.sendMessage(ChatColor.RED + "Для торгового альянса нужна фракция!");
	         }
	     }
	 }
    public void removeTradeAlliance(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Faction pf = main.utilities.getPlayersFaction(player.getUniqueId(), main.factions);
            if(pf == null) {
            	player.sendMessage(ChatColor.RED + "Вы должны быть членом фракции для этой команды.");
            	return;
            }
            else if(!pf.isOfficer(player.getUniqueId()) && !pf.isOwner(player.getUniqueId())) {
            	player.sendMessage(ChatColor.RED + "Вы должны быть лидером или офицером фракции для этой команды.");
            	return;
            }
            
            if (args.length > 2) {
            	String factionName = args[2];
            	Faction f = main.utilities.getFaction(factionName, main.factions);
            	if(f == null) {
            		player.sendMessage(ChatColor.RED + "Такой альянс не найден![" + factionName + "]");	
            		return;
            	}       
            	if(factionName.equalsIgnoreCase(pf.getName())) {
            		player.sendMessage(ChatColor.RED + "Вы не можете разорвать торговый альянс сами с собой.");
            		return;
            	}
            	if(!pf.isTradeAlly(factionName)) {
            		player.sendMessage(ChatColor.RED + "Вы не состоите в торговом альянсе с " + factionName);
            	}
            	
            	pf.removeTradeAlly(factionName);
            	pf.breakAllianceReputationChange();
                player.sendMessage(ChatColor.GREEN + "Вы разорвали торговый альянс с фракцией " + factionName + "!");

                // add declarer's faction to new enemy's enemyList
                f.removeTradeAlly(pf.getName());
               
                main.utilities.sendAllPlayersOnServerMessage(ChatColor.RED + pf.getName() + " разорвала торговый альянс с " + 
                f.getName() + "!");
                return;           	
            }else {
                player.sendMessage(ChatColor.RED + "Используй: /mf trp removeally (название-фракции)");
            }
        }
    }
}
