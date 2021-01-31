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

	HashMap<Material, Integer> itemsToSell = new HashMap<Material, Integer>();
	public ItemStack[][] guis;	
	
	
	Main plugin;
	
	public TraderTrait() {
		super("TraderTrait");
	}
	
	public TraderTrait(HashMap<Material, Integer> items, Main plugin) {
		super("TraderTrait");
		setPrices(items);
		this.plugin = plugin;
	}
	
	@Override
	public void onSpawn() {		
		super.onSpawn();
		// Делает что то когда НПС заспавнится
	}
	
	@Override
	public void run() {
		// Каждый Тик делает что то
	}
	public HashMap<Material, Integer> getPrices(){
		return itemsToSell;
	}
	
	public void setPrices(HashMap<Material, Integer> items) {
		itemsToSell.putAll(items);
		
	}
	
	
	/**
	 * After player clicks in market inventory
	 * @param is = chosen stack
	 */
	public void buyItem(ItemStack is) {
		// Remove stack from stock
		// Add money
	}
	/**
	 * After player right clicks with item
	 * @param is
	 */
	public void sellItem(ItemStack is, Player player) {
		// Add stack to stocks
		int price = 25;
		
		//itemsToSell.put(is, 25);
		player.getInventory().remove(is);
		int playercoins = InfoCoinsCommand.infoCoins(player);
		// Give money
		TradeToRealCommand.TakeCoins(player);
		TradeToRealCommand.GiveCoins(player, playercoins + price * 2, price);
	}
	
	public void createGUI(Player player) {
		guis = new ItemStack[itemsToSell.size()/25+1][27];
		
		
    	for (int i=0;i<guis.length;i++) {   		
			Set<Material> keyitem = itemsToSell.keySet();
			ArrayList <Material> containkeyitem = new ArrayList<Material>(keyitem);
			for (int j = 0; j < 25;j++) {	
				if (i*25+j >= containkeyitem.size()) {break;}
				Material item = containkeyitem.get(i*25+j);
				System.out.println("ITEM:" + item.toString());
				System.out.println("ITEMTOSELL:" + itemsToSell.toString());
				for(Material is : itemsToSell.keySet())
					System.out.println(is.toString());
				
				int price = itemsToSell.get(item);
				ItemStack gItem = new ItemStack(item);
				// Set item price to meta
				ItemMeta temitem_meta = gItem.getItemMeta();				
				ArrayList <String> temitem_lore = new ArrayList<>();
				temitem_lore.add("Цена "+price);
				temitem_meta.setLore(temitem_lore);
				gItem.setItemMeta(temitem_meta);
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
		Player player = (Player)npc.getEntity();
		int itemPrice = 25;
		if(is == null || is.getType() == null || is.getType() == Material.AIR) {
			sender.sendMessage(ChatColor.RED + "<Торговец>: Чтобы узнать цену, возьми в руку какой то предмет!");
			return;
		}else {			
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
	
	
}

class ItemInfoTrader
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
	
	public ItemInfoTrader(int initialPrice, int base, long lastBuy, Material type) {
		currentPrice = initialPrice;
		lastBuyTime = lastBuy;
		ItemType = type;
		basePrice = base;
	}
	/**
	 * Should be called every N-th tick. <Optimized externally>
	 * @return Newly calculated price
	 */
	public int marketChange() {
		//int maxSize = new ItemStack(ItemType).getMaxStackSize();
		double decreasePercent = 100.0; // TODO calculated by magic
		// LastBuyTime + maxStackSize + basePrice + ...
		currentPrice = changePriceByPercent(currentPrice, decreasePercent);
		return currentPrice;
	}
	public void itemBought() {
		
	}
	/**
	 * Should be called after item sold
	 * @return newly calculated price
	 */
	public int itemSold() {
		setNewBuyTime();
		double increasePercent = 10.0; // TODO calculate by magic
		// LastBuyTime + maxStackSize + basePrice + ...
		currentPrice = changePriceByPercent(currentPrice, increasePercent);
		return currentPrice;
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



