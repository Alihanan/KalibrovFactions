package economysystem.subsystems;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import factionsystem.Main;;

public class CustomConfingSubsystem {
	public HashMap <String,Integer> currency = new HashMap <String,Integer>();
	public ItemStack[][] guis;
	Main plugin = null;
	private FileConfiguration customConfig = null;
    private File customConfigFile = null;

    public CustomConfingSubsystem(Main plugin) {
        this.plugin = plugin;
        reloadCustomConfig();
        loadCurrency();
    }
    public void reloadCustomConfig() {
        if (customConfigFile == null) {
        customConfigFile = new File(plugin.getDataFolder(), "customConfig.yml");
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
     
    }
    public FileConfiguration getCustomConfig() {
        if (customConfig == null) {
            reloadCustomConfig();
        }
        return customConfig;
    }
    public void saveCustomConfig() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getCustomConfig().save(customConfigFile);
        } catch (IOException ex) {
        	plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }
    public void loadCurrency() {
    	if(customConfig.getConfigurationSection("currency") == null) {
    		return;
    	}
    	for (String key : customConfig.getConfigurationSection("currency").getKeys(false)) {
    		Object itemvalue = customConfig.get("currency."+key);
    		if (itemvalue.getClass().equals(Integer.class))
    			currency.put(key, (int) itemvalue );
    		else System.out.println(itemvalue.getClass());
    		
    	}
    	System.out.println(currency.toString());
    	guis = new ItemStack[currency.size()/25+1][27];
    	
    	for (int i=0;i<guis.length;i++) {   		
			Set<String> keyitem = currency.keySet();
			ArrayList <String> containkeyitem = new ArrayList(keyitem);
			for (int j = 0; j < 25;j++) {	
				if (i*25+j >= containkeyitem.size()) {break;}
				String key = containkeyitem.get(i*25+j);
				int price = currency.get(key);
				Material materialkey = null;
				try {			
					materialkey = Material.valueOf(key);
					if (materialkey == null) {continue;}
					ItemStack temitem = new ItemStack(materialkey);
					ItemMeta temitem_meta = temitem.getItemMeta();
					temitem_meta.setDisplayName(materialkey.toString());
					ArrayList <String> temitem_lore = new ArrayList<>();
					temitem_lore.add("Цена "+price);
					temitem_meta.setLore(temitem_lore);
					temitem.setItemMeta(temitem_meta);
					guis[i][j] = temitem;
				}
				catch(IllegalArgumentException e) {
					continue;
					
				}
				
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
    public void saveCurrency() {
    	currency.put(Material.GOLD_NUGGET.toString(), 1);
    	currency.put(Material.GOLD_INGOT.toString(), 10);
    	currency.put(Material.GOLD_BLOCK.toString(), 100);
    	
    	for (String key : currency.keySet()) {
    		customConfig.set("currency."+key, currency.get(key));
    		}
    	customConfig.options().copyDefaults(true);
    	saveCustomConfig();
    }

}
