package economysystem.CitizensNPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import economysystem.commands.InfoCoinsCommand;
import economysystem.commands.TradeToRealCommand;
import factionsystem.Main;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class TraderTrait extends Trait{
	// How many items this trader has
	HashMap<Material, Integer> stock = new HashMap<Material, Integer>();
	// Prices 
	HashMap<Material, ItemInfoTrading> prices = new HashMap<Material, ItemInfoTrading>();
	//Current gui shop for active customer
	public ItemStack[][] guis;	
	// Money, calculated from gold from stock(gold not in stock!)
	int balance; 
	// How often we should update prices
	// 20 ~= 1 sec
	final int PRICE_UPDATE_TICKRATE = 20;
	
	Main plugin;
	
	public TraderTrait() {
		super("TraderTrait");
	}
	/**
	 * Create trait for autonomous trader
	 * @param items = initial stock of trader(! including gold !)
	 * @param plugin = main plugin
	 */
	public TraderTrait(HashMap<Material, Integer> items, Main plugin) {
		super("TraderTrait");		
		addToStock(items);		
		this.plugin = plugin;
		
		prices.put(Material.WOOD, new ItemInfoTrading(1, 1, System.currentTimeMillis(), Material.WOOD));
		prices.put(Material.DIAMOND_SWORD, new ItemInfoTrading(10, 10, System.currentTimeMillis(), Material.DIAMOND_SWORD));
		prices.put(Material.STONE, new ItemInfoTrading(2, 2, System.currentTimeMillis(), Material.STONE));
	}
	
	public void loadPrices(ArrayList<ItemInfoTrading> pricess) {
		for(ItemInfoTrading iit : pricess) {
			if(iit != null) {
				prices.put(iit.ItemType, iit);
			}
		}
		
	}
	
	public void addToStock(HashMap<Material, Integer> items) {
		for(Material m : items.keySet()) {
			int price = isMoney(m, items.get(m));
			if(price > 0) balance += price;
			else {
				if(stock.containsKey(m)) {
					int curr = stock.get(m);
					int added = items.get(m);
					stock.put(m, curr + added);
				}else {
					int added = items.get(m);
					stock.put(m, added);
					if(!prices.containsKey(m)) {
						// TODO load default ones
						ItemInfoTrading iit = new ItemInfoTrading(1, 1, System.currentTimeMillis(), m);
						prices.put(m, iit);
					}
				}
			}			
		}
	}
	public void addToStock(ItemStack is) {
		if(is == null) return;
		Material m = is.getType();
		int added = is.getAmount();
		if(stock.containsKey(m)) {
			int curr = stock.get(m);
			stock.put(m, curr + added);
		}else {
			stock.put(m, added);
		}
	}
	
	@Override
	public void onSpawn() {		
		super.onSpawn();
		// Делает что то когда НПС заспавнится
	}
	
	int timer = 0;
	@Override
	public void run() {
		// Каждый 20й Тик делает что то
		timer++;
		
		if(timer % PRICE_UPDATE_TICKRATE == 0) {			
			timer = 0;
			// Если торгуют - пока не меняем цены! 
			// Но проверяем не вышел ли игрок!
			if(plugin.tradingCurrently.containsValue(npc)) {
				HashMap<Player, NPC> copy = (HashMap<Player, NPC>) plugin.tradingCurrently.clone();
				for(Player p : copy.keySet()) {
					NPC n = copy.get(p);
					if(n.equals(npc) && !p.isOnline()) {
						plugin.tradingCurrently.remove(p);
					}else if(n.equals(npc)){
						return;
					}
				}
			}
			// Меняем
			for(ItemInfoTrading iit : prices.values()) {
				iit.marketChange();
			}
		}
	}
	
	
	/**
	 * After player clicks in market inventory
	 * @param is = chosen stack
	 */
	public void buyItem(ItemStack is, Player player) {
		Material m = is.getType();
		
		// Calculate selling number
		int maxSize = new ItemStack(m).getMaxStackSize();
		int quater = maxSize / 4;
		if(quater == 0) quater = 1;
		int amount = quater;
		
		
		// Remove stack from stock
		if(!stock.containsKey(m)) {
			player.sendMessage(ChatColor.RED+"Недостаточно товара на складе! Ошибка сервера поидее:)");
			player.closeInventory();
			return;
		}
		int curr = stock.get(m);
		if(curr < amount) {
			player.sendMessage(ChatColor.RED+"Недостаточно товара на складе! Ошибка сервера поидее:)");
			player.closeInventory();
			return;
		}

		int price = prices.get(m).getPrice() * amount;
		int playercoins = InfoCoinsCommand.infoCoins(player);
		if(playercoins < price) {
			player.sendMessage(ChatColor.RED+"Недостаточно средств, ваш баланс "+playercoins);
			return;
		}
		// Take money
		TradeToRealCommand.TakeCoins(player);
		TradeToRealCommand.GiveCoins(player, playercoins, price);
		stock.put(m, curr - amount);
		// Give item
		ItemStack toGive = new ItemStack(m, amount);
		player.getInventory().addItem(toGive);
		player.sendMessage(ChatColor.GREEN+"Операция выполнена, ваш баланс "+(playercoins-price));
		//prices.get(m).itemSold(); // Update prices
		
		// restart GUI
		player.closeInventory();
		createGUI(player);
	}
	/**
	 * After player right clicks with item
	 * @param is
	 */
	public void sellItem(ItemStack is, Player player) {
		// Add stack to stocks
		if(!prices.containsKey(is.getType())) {
			if(isMoney(is.getType(), is.getAmount()) > 0){
				player.sendMessage(ChatColor.RED+"<Торговец>: Я не знаю зачем вы пихаете мне золотые червонцы!");
			}else {
				player.sendMessage(ChatColor.RED + "<Торговец>: я не торгую этими товарами!");
			}
			return;
		}
		addToStock(is);
		int price = prices.get(is.getType()).getPrice() * is.getAmount() / 2;

		player.getInventory().removeItem(is);
		int playercoins = InfoCoinsCommand.infoCoins(player);
		// Give money
		TradeToRealCommand.TakeCoins(player);
		TradeToRealCommand.GiveCoins(player, playercoins + price * 2, price);
		
		prices.get(is.getType()).itemBought(); // Update prices
	}
	
	public void createGUI(Player player) {
		ArrayList<ItemStack> allItemsToSell = new ArrayList<ItemStack>();
		// Get all possible stocks
		for(Material m : stock.keySet()) {
			int amount = stock.get(m);
			int maxSize = new ItemStack(m).getMaxStackSize();
			int quater = maxSize / 4;
			if(quater == 0) quater = 1;
			
			if(amount < (quater + 1)) {
				continue;
			}
			if(!prices.containsKey(m)) continue;
			allItemsToSell.add(new ItemStack(m, quater));
		}
		
		guis = new ItemStack[allItemsToSell.size()/25+1][27];

    	for (int i=0;i<guis.length;i++) {   		
			
			for (int j = 0; j < 25;j++) {	
				if (i*25+j >= allItemsToSell.size()) {break;}
				ItemStack gItem = allItemsToSell.get(i*25+j);
				Material item = gItem.getType();	
				int price = prices.get(item).getPrice() * gItem.getAmount();
				
				// Set item price to meta
				ItemMeta temitem_meta = gItem.getItemMeta();				
				ArrayList <String> temitem_lore = new ArrayList<>();
				temitem_lore.add("Цена "+price);
				temitem_lore.add("Осталось на складе "+ stock.get(item));
				temitem_meta.setLore(temitem_lore);
				gItem.setItemMeta(temitem_meta);
				// add to gui
				guis[i][j] = gItem;				
			}
			
			
			ItemStack nextbutton = new ItemStack (Material.STAINED_GLASS_PANE,1,(short) 13);
			ItemMeta nextbutton_meta = nextbutton.getItemMeta();
			nextbutton_meta.setDisplayName("Следующая страница");
			nextbutton.setItemMeta(nextbutton_meta);
			guis[i][26] = nextbutton;
			
			ItemStack prevbutton = new ItemStack (Material.STAINED_GLASS_PANE,1,(short) 14);
			ItemMeta prevbutton_meta = prevbutton.getItemMeta();
			prevbutton_meta.setDisplayName("Предыдущая страница");
			prevbutton.setItemMeta(prevbutton_meta);
			guis[i][25] = prevbutton;
    	}
    	Inventory gui = Bukkit.createInventory(player, 27,ChatColor.AQUA+ "Страница 1");
		gui.setContents(guis[0].clone());
		player.openInventory(gui);
		plugin.tradingCurrently.put(player, npc);//TODO
	}
	/**
	 * After player left clicks with item
	 * @param nre
	 */
	public void getItemPrice(ItemStack is, Player sender) {		
		if(is == null || is.getType() == null || is.getType() == Material.AIR) {
			sender.sendMessage(ChatColor.RED + "<Торговец>: Чтобы узнать цену, возьми в руку какой то предмет!");
			return;
		}else {	
			if(!prices.containsKey(is.getType())) {
				if(isMoney(is.getType(), is.getAmount()) > 0){
					sender.sendMessage(ChatColor.RED+"<Торговец>: Я не знаю зачем вы пихаете мне золотые червонцы!");
				}else {
					sender.sendMessage(ChatColor.RED + "<Торговец>: я не торгую этими товарами!");
				}
				return;
			}
			int itemPrice = prices.get(is.getType()).getPrice() * is.getAmount() / 2;
			plugin.localeManager.sendMessage(sender, ChatColor.RED + "<Торговец>" + 
				ChatColor.AQUA + ": За " + ChatColor.AQUA + "<item>" + ChatColor.AQUA + " я дам " + itemPrice + " червонцев", is.getType(), (short) 0, null);
			return;
		}
		
	}

	@EventHandler
	public void leftClick(NPCLeftClickEvent nle){
		Player player = nle.getClicker();
		ItemStack is = player.getInventory().getItemInMainHand();
		getItemPrice(is, player);
	}
	
	@EventHandler
	public void rightClick(NPCRightClickEvent nre){
		System.out.println("CLICKED");
		NPC npc = nre.getNPC();
		Player player = nre.getClicker();
		ItemStack is = player.getInventory().getItemInMainHand();
		if(is == null || is.getType() == null || is.getType() == Material.AIR) {
			createGUI(player);
		}else {
			sellItem(is, player);
		}
	}
	
	public static int isMoney(Material m, int amount) {
		switch(m) {
		case GOLD_BLOCK:
			return amount * 100;
		case GOLD_INGOT:
			return amount * 10;
		case GOLD_NUGGET:
			return amount;
		default:
			return 0;
		}
	}
}
// TODO remove public
class ItemInfoTrading
{
	Material ItemType;
	long lastBuyTime;
	int currentPrice;
	int basePrice;
	ArrayList<Integer> savedNumberOfStocks = new ArrayList<Integer>();
	/**
	 * Object for price change mechanics
	 * @param initialPrice = current price at the moment of initialization
	 * @param base = base price, should be defined in config
	 * @param lastBuy = last time of this item type buying(from this trader)
	 * @param lastSoldNumber = number of item sold by last 5 sells
	 * @param type = type of item(Material)
	 */
	
	public ItemInfoTrading(int initialPrice, int base, long lastBuy, Material type) {
		currentPrice = initialPrice;
		lastBuyTime = lastBuy;
		ItemType = type;
		basePrice = base;
	}
	/**
	 * Should be called every N-th tick. <Optimized externally>
	 */
	public void marketChange() {
		//int maxSize = new ItemStack(ItemType).getMaxStackSize();
		double decreasePercent = 100.0; // TODO calculated by magic
		// LastBuyTime + maxStackSize + basePrice + ...
		currentPrice = changePriceByPercent(currentPrice, decreasePercent);
	}
	/**
	 * Should be called after item bought from player(- money, +stock)
	 */
	public void itemBought() {
		
	}
	/**
	 * Should be called after item sold to player(+ money, - stock)
	 */
	public void itemSold() {
		setNewBuyTime();
		double increasePercent = 10.0; // TODO calculate by magic
		// LastBuyTime + maxStackSize + basePrice + ...
		currentPrice = changePriceByPercent(currentPrice, increasePercent);
	}
	
	private int changePriceByPercent(int price, double percent) {
		double newPrice = price * percent / 100.0;
		int intPart = (int)newPrice;
		return intPart;
	}
	
	/**
	 * Get price from item info
	 * @return 
	 */
	public int getPrice() {
		return currentPrice;
	}
	
	/**
	 * Should be called after item buy
	 */
	public void setNewBuyTime() {
		lastBuyTime = System.currentTimeMillis();
	}
	
}



