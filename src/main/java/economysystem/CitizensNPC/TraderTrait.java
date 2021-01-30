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

import factionsystem.Main;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class TraderTrait extends Trait{

	HashMap<ItemStack, Integer> itemsToSell = new HashMap<ItemStack, Integer>();
	public ItemStack[][] guis;	
	Main plugin;
	
	public TraderTrait() {
		super("TraderTrait");
	}
	
	public TraderTrait(HashMap<ItemStack, Integer> items, Main plugin) {
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
	public HashMap<ItemStack, Integer> getPrices(){
		return itemsToSell;
	}
	
	public void setPrices(HashMap<ItemStack, Integer> items) {
		itemsToSell = items;
		guis = new ItemStack[items.size()/25+1][27];
    	
    	for (int i=0;i<guis.length;i++) {   		
			Set<ItemStack> keyitem = items.keySet();
			ArrayList <ItemStack> containkeyitem = new ArrayList(keyitem);
			for (int j = 0; j < 25;j++) {	
				if (i*25+j >= containkeyitem.size()) {break;}
				ItemStack item = containkeyitem.get(i*25+j);
				int price = items.get(item);
				ItemMeta temitem_meta = item.getItemMeta();
				//temitem_meta.setDisplayName(materialkey.toString());
				ArrayList <String> temitem_lore = new ArrayList<>();
				temitem_lore.add("Цена "+price);
				temitem_meta.setLore(temitem_lore);
				item.setItemMeta(temitem_meta);
				guis[i][j] = item;				
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
	}
	

	@EventHandler
	public void click(NPCRightClickEvent nre){
		System.out.println("CLICKED");
		NPC npc = nre.getNPC();
		TraderTrait trait = npc.getTrait(TraderTrait.class);
		Player player = nre.getClicker();
		Inventory gui = Bukkit.createInventory(player, 27,ChatColor.AQUA+ "Страница 1");
		gui.setContents(guis[0]);
		player.openInventory(gui);
		plugin.tradingCurrently.put(player, npc);
	}
	
	
}
